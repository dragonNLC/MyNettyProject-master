package com.aptdev.framework.test.nioworkthread;

import com.aptdev.framework.test.bean.LiveMessage;
import com.aptdev.framework.test.bean.SocketControlBean;
import com.aptdev.framework.test.eos.CommunicationPermissionUtil;
import com.aptdev.framework.test.utils.GSonUtils;
import com.dragondevl.clog.CLog;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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

    private ClientStatusListener clientStatusListener;

    private String remoteIPAddress;

    private boolean authorization;
    private String sessionId;

    public ReceiverHandler(String remoteIPAddress, ClientStatusListener clientStatusListener) {
        this.remoteIPAddress = remoteIPAddress;
        this.clientStatusListener = clientStatusListener;
        //channel连接上来了，延迟5s看有没有发送授权信息
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /*super.channelActive(ctx);*/
        //client被连接后，channel被添加，并正式处于活跃状态，可以读写数据
        CLog.e("channelActive！");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        /*super.channelInactive(ctx);*/
        //client关闭后，channel禁止读写数，并处于沉寂状态
        CLog.e("channelInactive！");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LiveMessage msg) throws Exception {
        //读取数据的时候被调用
        //我们读取到数据，还要判断是否是我们需要的机器，如果不是的，强制关闭它
        //ByteBuf bf = Unpooled.copiedBuffer(content.getBytes());
        //ctx.write(msg);
        /*CLog.e("content = " + msg.getType());
        if (msg.getType() == LiveMessage.TYPE_HEART) {
            CLog.e("这是心跳包！");
        } else {
            CLog.e("这是数据包： " + content);
        }*/
        if (!authorization) {//刚刚连接上来的，还没有进行授权，接收到的第一个信息必须是授权信息
            String authContent = msg.getContent();//content内容必须是加密内容，使用密钥解密出来后，看看是不是请求授权的内容，如果是的，授权成功
            if (CommunicationPermissionUtil.hasAuthorization(authContent)) {
                authorization = true;
                //已经授权成功了，下发一个sessionId
                sessionId = UUID.randomUUID().toString();//每一个连接都有一个sessionId，并返回给client
                writeAndFlushContent(ctx, sessionId);
                CLog.e("授权！");
            } else {//密钥不对，退出
                ctx.channel().close().sync();//强制退出
                CLog.e("没有授权！");
            }
        } else {//之前已经成功授权的，此时可以进行数据交换
            //将string的content转换成ctl bean类
            String msgContent = msg.getContent();
            SocketControlBean scb = GSonUtils.getInstance().fromJson(msgContent, SocketControlBean.class);
            if (scb != null) {//只有转换成功的，才可以进行处理
                int dtype = scb.getDtype();//查看是需要什么内容
                CLog.e("ctlType:" + dtype);
            } else {
                CLog.e("数据转换错误，原始数据：" + msgContent);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        /*super.channelReadComplete(ctx);*/
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
        if (clientStatusListener != null) {
            clientStatusListener.onClose(remoteIPAddress);
        }
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /*super.exceptionCaught(ctx, cause);*/
        //出现异常的时候被调用
        cause.printStackTrace();
        if (cause instanceof AcceptorIdleStateTrigger.ReadIdleException) {//看是不是读超时
            CLog.e("???");
            ctx.channel().close();
        }
        //
        //抛出异常
    }

    private void writeAndFlushContent(ChannelHandlerContext ctx, String content) {
        Channel channel = ctx.channel();
        ByteBuf buf = Unpooled.copiedBuffer(content.getBytes());
        channel.writeAndFlush(buf);
    }

}
