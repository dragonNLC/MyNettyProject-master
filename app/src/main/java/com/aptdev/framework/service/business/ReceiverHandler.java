package com.aptdev.framework.service.business;

import com.aptdev.framework.iface.ServerCallbackListener;
import com.aptdev.framework.service.bean.LiveMessage;
import com.dragondevl.clog.CLog;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @ClassName ReceiverHandler
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 10:46
 * @Version 1.0
 */
@ChannelHandler.Sharable
public class ReceiverHandler extends SimpleChannelInboundHandler<LiveMessage> {

    private ServerCallbackListener serverCallbackListener;

    public ReceiverHandler(ServerCallbackListener serverCallbackListener) {
        this.serverCallbackListener = serverCallbackListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /*super.channelActive(ctx);*/
        //client被连接后，channel被添加，并正式处于活跃状态，可以读写数据
        CLog.e("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        /*super.channelInactive(ctx);*/
        //client关闭后，channel禁止读写数，并处于沉寂状态
        CLog.e("channelInactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /*super.exceptionCaught(ctx, cause);*/
        //出现异常的时候被调用
        cause.printStackTrace();
        ctx.close();
        CLog.e("exceptionCaught = " + cause.getMessage());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LiveMessage msg) throws Exception {
        CLog.e("channelRead0");
        //读取数据的时候被调用
        String content = msg.getContent();
        //ByteBuf bf = Unpooled.copiedBuffer(content.getBytes());
        //ctx.write(msg);
        CLog.e("content = " + content);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        /*super.channelReadComplete(ctx);*/
        CLog.e("channelReadComplete");
        //在读取数据结束的时候被调用
        ByteBuf bf = Unpooled.copiedBuffer("channelReadComplete".getBytes());
        ctx.writeAndFlush(bf);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        /*super.channelRegistered(ctx);*/
        CLog.e("channelRegistered");
        //client连接上来，并创建该handler，channel被注册
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        /*super.channelUnregistered(ctx);*/
        CLog.e("channelUnregistered");
        //client关闭连接，channel被移除
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        /*super.handlerAdded(ctx);*/
        CLog.e("handlerAdded");
        //handler被添加，属于最早被调用的方法
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        /*super.handlerRemoved(ctx);*/
        CLog.e("handlerRemoved");
        //handler被移除，属于最终被调用的方法
    }

}
