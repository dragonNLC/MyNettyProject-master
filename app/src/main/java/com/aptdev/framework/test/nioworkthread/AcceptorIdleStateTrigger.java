package com.aptdev.framework.test.nioworkthread;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @ClassName AcceptorIdleStateTrigger
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 17:58
 * @Version 1.0
 */
@ChannelHandler.Sharable
public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter {

    //检测心跳时间
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {//检测到也不能直接关闭channel，最好传exception给，每隔ns后都会被调用到
                throw new ReadIdleException("Read idle exception");//这个对象抛出的异常不会被非该channel的捕获，即，只有哪个Channel真正调用了IdleHandler，才会真正调用这个方法将异常抛出去
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    public static class ReadIdleException extends RuntimeException {

        public ReadIdleException(String message) {
            super(message);
        }
    }

}
