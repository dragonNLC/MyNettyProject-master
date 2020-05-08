package com.aptdev.framework.test.nioserver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;


import com.aptdev.framework.test.bean.LiveMessage;
import com.aptdev.framework.test.eos.MyCipherUtil;
import com.dragondevl.clog.CLog;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @ClassName ClientServer
 * @Description 负责核销服务中的client端，专门去获取用户核销数据，并将核销数据发往服务器，接收服务器核销结果
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-07 15:52
 * @Version 1.0
 */
public class SingleClientServer extends Service {

    private SingleClientServer instance;

    private NioEventLoopGroup workGroup;
    private Bootstrap bootstrap;

    private Channel socketChannel;

    private ClientStatusListener clientStatusListener;

    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SingleClientBinder(instance);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    void startConnect() {
        if (workGroup == null) {
            workGroup = new NioEventLoopGroup();

            bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("192.168.11.50", 6601))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {

                                @Override
                                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                    socketChannel = ctx.channel();
                                    //可以在这里保存channel
                                    /*String content = MyCipherUtil.encryptData("aptdevc", MyCipherUtil.DATA_KEY);
                                    ByteBuf byteBuf = Unpooled.buffer();
                                    byteBuf.writeByte(LiveMessage.TYPE_MESSAGE);
                                    byteBuf.writeInt(content.getBytes().length);
                                    byteBuf.writeBytes(content.getBytes());
                                    ctx.writeAndFlush(byteBuf);*/
                                }

                                @Override
                                public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                                    //可以在这里移除保存的channel
                                    socketChannel = null;//channel被关闭
                                    mUIHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (clientStatusListener != null) {
                                                clientStatusListener.disconnect();
                                            }
                                        }
                                    });
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                    System.out.println("receiver = " + msg.toString(CharsetUtil.UTF_8));
                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    super.channelReadComplete(ctx);
                                    //ctx.channel().close().addListener(ChannelFutureListener.CLOSE);
                                    //当channel被关闭之后，整个serverBootStrap都会被关闭，而对于server来讲，之所以没有关闭，应该是socketServer在运行的原因
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                }
                            });
                        }
                    });
        }
        ChannelFuture future = bootstrap.connect();//client使用connect连接，server使用bind绑定
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (clientStatusListener != null) {
                            if (future.isSuccess()) {
                                clientStatusListener.connect();
                            } else {
                                Throwable cause = future.cause();
                                cause.printStackTrace();
                                clientStatusListener.connectFail("连接服务器失败，错误信息：" + cause.getMessage());
                            }
                        }
                    }
                });
            }
        });
    }

    void stopConnect() {
        try {
            workGroup.shutdownGracefully().sync();//阻塞当前线程，避免走完之后进程被关闭
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void sendData(LiveMessage liveMessage) {
        if (socketChannel != null) {
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeByte(liveMessage.getType());
            if (liveMessage.getLength() > 0) {
                byteBuf.writeInt(liveMessage.getLength());
                byteBuf.writeBytes(liveMessage.getContent().getBytes());
            }
            socketChannel.writeAndFlush(byteBuf).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {//检测是否发送成功
                        CLog.e("数据发送成功！");
                    } else {
                        Throwable cause = future.cause();
                        cause.printStackTrace();
                    }
                }
            });
        }
    }

    void setClientStatusListener(ClientStatusListener clientStatusListener) {
        this.clientStatusListener = clientStatusListener;
    }

    public static class SingleClientBinder extends Binder {

        SingleClientServer server;

        public SingleClientBinder(SingleClientServer server) {
            this.server = server;
        }

        public void startConnect() {
            this.server.startConnect();
        }

        public void stopConnect() {
            this.server.stopConnect();
        }

        public void sendData(LiveMessage liveMessage) {
            this.server.sendData(liveMessage);
        }

        public void setClientStatusListener(ClientStatusListener clientStatusListener) {
            this.server.setClientStatusListener(clientStatusListener);
        }
    }

    public interface ClientStatusListener {

        void connect();

        void connectFail(String errorInfo);

        void disconnect();

        void onWriteContent(String content);

        void onReadContent(String content);

    }

}
