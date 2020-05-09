package com.aptdev.framework.test.nioclientthread;

import com.aptdev.framework.test.bean.LiveMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * @ClassName ConnectorIdleStateTrigger
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-09 16:04
 * @Version 1.0
 */
@ChannelHandler.Sharable
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter {

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.buffer());

    static {
        HEARTBEAT_SEQUENCE.writeByte(LiveMessage.TYPE_HEART);
    }

    //超过4s没有发送数据，则发送心跳包
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE);//发送心跳数据
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
