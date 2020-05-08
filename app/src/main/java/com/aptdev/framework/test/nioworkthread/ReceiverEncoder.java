package com.aptdev.framework.test.nioworkthread;


import android.text.TextUtils;

import com.aptdev.framework.test.bean.LiveMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @ClassName LivingEncoder
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-04-30 16:38
 * @Version 1.0
 */
public class ReceiverEncoder extends MessageToByteEncoder<LiveMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, LiveMessage msg, ByteBuf out) throws Exception {
        out.writeByte(msg.getType());
        out.writeInt(msg.getLength());
        if (!TextUtils.isEmpty(msg.getContent())) {
            out.writeBytes(msg.getContent().getBytes());
        }
    }

}
