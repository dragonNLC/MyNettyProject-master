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
import com.aptdev.framework.test.nioclientthread.ClientDecoder;
import com.aptdev.framework.test.nioclientthread.ClientHandler;
import com.aptdev.framework.test.nioclientthread.ConnectionWatchdog;
import com.aptdev.framework.test.nioclientthread.ConnectorIdleStateTrigger;
import com.dragondevl.clog.CLog;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.HashedWheelTimer;

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

    private ConnectorIdleStateTrigger idleStateTrigger = new ConnectorIdleStateTrigger();
    private HashedWheelTimer timer = new HashedWheelTimer();

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
            String ip = "192.168.11.50";
            int port = 6601;

            final ConnectionWatchdog watchdog = new ConnectionWatchdog(bootstrap, timer, port, ip, true, new ClientStatusListener() {
                @Override
                public void connect(final SocketChannel socketChannel) {
                    SingleClientServer.this.socketChannel = socketChannel;
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (clientStatusListener != null) {
                                clientStatusListener.connect(socketChannel);
                            }
                        }
                    });
                }

                @Override
                public void connectFail(final String errorInfo) {
                    SingleClientServer.this.socketChannel = null;
                            mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (clientStatusListener != null) {
                                clientStatusListener.connectFail(errorInfo);
                            }
                        }
                    });
                }

                @Override
                public void disconnect() {
                    SingleClientServer.this.socketChannel = null;
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
                public void onWriteContent(String content) {

                }

                @Override
                public void onReadContent(String content) {
                    CLog.e("数据读取成功：" + content);
                }
            }) {
                @Override
                public ChannelHandler[] handlers() {
                    return new ChannelHandler[]{
                            this,
                            new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS),
                            idleStateTrigger, new ClientDecoder(), new ClientHandler()};
                }
            };
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("192.168.11.50", 6601))
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(watchdog.handlers());
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
                                clientStatusListener.connect((SocketChannel) future.channel());
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
            workGroup = null;
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

        void connect(SocketChannel socketChannel);

        void connectFail(String errorInfo);

        void disconnect();

        void onWriteContent(String content);

        void onReadContent(String content);

    }

}
