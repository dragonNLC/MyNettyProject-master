package com.aptdev.framework.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.aptdev.framework.iface.ServerCallbackListener;
import com.aptdev.framework.service.business.ReceiverDecoder;
import com.aptdev.framework.service.business.ReceiverEncoder;
import com.aptdev.framework.service.business.ReceiverHandler;
import com.dragondevl.clog.CLog;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

/**
 * @ClassName ServiceStationServer
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 8:58
 * @Version 1.0
 */
public class ServiceStationServer extends Service {

    private ServiceStationServer instance;

    private NioEventLoopGroup bossGroup;//ServerSocket线程
    private NioEventLoopGroup clientGroup;//client连接线程与io线程
    private ChannelFuture channelFuture;//用于存储bind之后返回的ChannelFuture

    private ServerCallbackListener serverCallbackListener;

    private Handler mUIHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        CLog.e("onBind");
        return new ServerStationBinder(instance);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    void startReceiver() {
        bossGroup = new NioEventLoopGroup(1);
        clientGroup = new NioEventLoopGroup();

        //引导服务，用于引导ServerSocket启动
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, clientGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(6601)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel ch) throws Exception {
                        CLog.e("remoteAddress = " + ch.remoteAddress().getAddress().getHostAddress());
                        CLog.e("localAddress = " + ch.localAddress().getAddress().getHostAddress());
                        //等待绑定结果
                        mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (serverCallbackListener != null) {
                                    serverCallbackListener.onClientConnect(ch.remoteAddress().getAddress().getHostAddress());
                                }
                            }
                        });
                        ch.pipeline().addLast(new ReceiverEncoder());
                        ch.pipeline().addLast(new ReceiverDecoder());
                        ch.pipeline().addLast(new ReceiverHandler(serverCallbackListener));
                    }
                });
        channelFuture = bootstrap.bind();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                //等待绑定结果
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (serverCallbackListener != null) {
                            if (future.isSuccess()) {
                                serverCallbackListener.onStartSuccess();
                            } else {
                                Throwable cause = future.cause();
                                cause.printStackTrace();
                                serverCallbackListener.onStartFail("启动失败，错误信息：" + cause.getMessage());
                            }
                        }
                    }
                });
            }
        });
    }

    void stopReceiver() {
        if (clientGroup != null) {
            //try {
            CLog.e(System.currentTimeMillis());
            try {
                clientGroup.shutdownGracefully().sync();//为组添加listener是不会执行的，关闭大概需要2s，如果有数据读写的话，可能时间会更长一点，所以关闭的话，要考虑是否以线程的形式关闭
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CLog.e(System.currentTimeMillis());
            //.sync();//阻塞，等待解绑完毕
            //} catch (InterruptedException e) {
            //     e.printStackTrace();
            //}
            clientGroup = null;
        }
        if (bossGroup != null) {
            //try {
            try {
                bossGroup.shutdownGracefully().sync();//关闭大概需要2s
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CLog.e(System.currentTimeMillis());
            //阻塞，等待解绑完毕，这个操作的是ServerSocket，所以下面的channelFuture是不用的了，
            // 所有操作都在EventLoopGroup中,为组添加listener是不会执行的，应该是表示，这是一个组，如果里面的channel有些没有关闭成功，有些关闭成功，那怎么算
            //所以所有操作都可以立即返回
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            // }
            bossGroup = null;
        }
        //等待解绑结果
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (serverCallbackListener != null) {
                    if (channelFuture.isSuccess()) {
                        serverCallbackListener.onReceiverStop();
                    } else {
                        Throwable cause = channelFuture.cause();
                        cause.printStackTrace();
                        serverCallbackListener.onReceiverStopFail("启动失败，错误信息：" + cause.getMessage());
                    }
                }
            }
        });
    }

    void setServerCallbackListener(ServerCallbackListener serverCallbackListener) {
        this.serverCallbackListener = serverCallbackListener;
    }

    public static class ServerStationBinder extends Binder {

        ServiceStationServer server;

        public ServerStationBinder(ServiceStationServer server) {
            this.server = server;
        }

        public void startReceiver() {
            this.server.startReceiver();
        }

        public void stopReceiver() {
            this.server.stopReceiver();
        }

        public void setServerCallbackListener(ServerCallbackListener serverCallbackListener) {
            this.server.setServerCallbackListener(serverCallbackListener);
        }

    }


}
