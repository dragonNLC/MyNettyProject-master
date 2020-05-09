package com.aptdev.framework.test.nioclientthread;


import com.aptdev.framework.test.bean.LiveMessage;
import com.aptdev.framework.test.eos.CommunicationPermissionUtil;
import com.aptdev.framework.test.eos.MyCipherUtil;
import com.aptdev.framework.test.nioserver.SingleClientServer;
import com.aptdev.framework.test.nioworkthread.ChannelHandlerHolder;
import com.dragondevl.clog.CLog;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

/**
 * @ClassName ConnectionWatchdog
 * @Description 重连检测，当发现当前的链路被关闭后，进行重连
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-09 10:05
 * @Version 1.0
 */
@ChannelHandler.Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {

    private static final ByteBuf AUTH_BYTE_BUF = Unpooled.unreleasableBuffer(Unpooled.buffer());

    static {
        AUTH_BYTE_BUF.writeByte(LiveMessage.TYPE_MESSAGE);
        String authContent = MyCipherUtil.encryptData(CommunicationPermissionUtil.SECRET_KEY, MyCipherUtil.DATA_KEY);
        AUTH_BYTE_BUF.writeInt(authContent.getBytes().length);
        AUTH_BYTE_BUF.writeBytes(authContent.getBytes());
    }

    private static final int DEFAULT_RECONNECT_MAX_TIME = 5;

    private final Bootstrap bootstrap;
    private final Timer timer;
    private final int port;

    private final String host;

    private volatile boolean reconnect;
    private int attempts;

    private SingleClientServer.ClientStatusListener clientStatusListener;

    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port, String host, boolean reconnect, SingleClientServer.ClientStatusListener clientStatusListener) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.port = port;
        this.host = host;
        this.reconnect = reconnect;
        this.clientStatusListener = clientStatusListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        CLog.e("当前链路已经激活，尝试重连次数重置为0");
        attempts = 0;
        ctx.writeAndFlush(AUTH_BYTE_BUF);//发送授权信息，每次重连都要
        ctx.fireChannelActive();//透传，内容不做改变，传给下一个
        if (clientStatusListener != null) {
            clientStatusListener.connect((SocketChannel) ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (clientStatusListener != null) {
            clientStatusListener.disconnect();
        }
        CLog.e("链路已经被关闭");
        if (reconnect) {
            CLog.e("链接关闭，将进行重连");
            if (attempts < DEFAULT_RECONNECT_MAX_TIME) {
                attempts++;
            }
            int timeout = 2 << attempts;//10000000
            CLog.e("timeout = " + timeout);
            timer.newTimeout(this, timeout, TimeUnit.SECONDS);//2秒重连，最大
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        ChannelFuture future;
        //bootstrap已经初始化完毕，只需要填入handler
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(host, port);
        }
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                boolean succeed = future.isSuccess();
                if (!succeed) {
                    CLog.e("重连失败！");
                    future.channel().pipeline().fireChannelInactive();//
                } else {
                    CLog.e("重连成功！");
                }
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CLog.e("好像有数据进来了！");
        super.channelRead(ctx, msg);
    }

}
