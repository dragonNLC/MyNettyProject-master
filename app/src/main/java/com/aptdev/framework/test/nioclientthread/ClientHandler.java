package com.aptdev.framework.test.nioclientthread;

import com.dragondevl.clog.CLog;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @ClassName ClientHandler
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-09 16:03
 * @Version 1.0
 */
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {


    /*@Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        //socketChannel = ctx.channel();
        //可以在这里保存channel
                                    *//*String content = MyCipherUtil.encryptData("aptdevc", MyCipherUtil.DATA_KEY);
                                    ByteBuf byteBuf = Unpooled.buffer();
                                    byteBuf.writeByte(LiveMessage.TYPE_MESSAGE);
                                    byteBuf.writeInt(content.getBytes().length);
                                    byteBuf.writeBytes(content.getBytes());
                                    ctx.writeAndFlush(byteBuf);*//*
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        //可以在这里移除保存的channel
        //socketChannel = null;//channel被关闭
        *//*mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (clientStatusListener != null) {
                    clientStatusListener.disconnect();
                }
            }
        });*//*
    }*/

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        CLog.e("好像有数据进来了!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("receiver = " + msg.toString(CharsetUtil.UTF_8));
        CLog.e("好像有数据进来了!");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        CLog.e("好像有数据进来了!");
        super.channelReadComplete(ctx);
        //ctx.channel().close().addListener(ChannelFutureListener.CLOSE);
        //当channel被关闭之后，整个serverBootStrap都会被关闭，而对于server来讲，之所以没有关闭，应该是socketServer在运行的原因
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

}
