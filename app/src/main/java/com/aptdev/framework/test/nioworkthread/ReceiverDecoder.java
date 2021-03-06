package com.aptdev.framework.test.nioworkthread;

import com.aptdev.framework.test.bean.LiveMessage;
import com.dragondevl.clog.CLog;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

/**
 * @ClassName ReceiverDecoder
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 11:13
 * @Version 1.0
 */
public class ReceiverDecoder extends ReplayingDecoder<ReceiverDecoder.LiveState> {

    public enum LiveState {
        TYPE,
        LENGTH,
        CONTENT
    }

    private LiveMessage message;

    public ReceiverDecoder() {
        super(LiveState.TYPE);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        LiveState state = state();
        CLog.e("state = " + state);
        switch (state) {
            case TYPE://还要判断，如果一开始没有找到包头，则直接不读取了
                message = new LiveMessage();
                byte type = in.readByte();
                CLog.e("type = " + type);
                message.setType(type);
                if (type == LiveMessage.TYPE_MESSAGE) {
                    checkpoint(LiveState.LENGTH);
                } else if (type == LiveMessage.TYPE_HEART) {
                    checkpoint(LiveState.TYPE);
                    out.add(message);
                } else {
                    CLog.e("数据错误，重置所有数据");
                    in.release();
                    checkpoint(LiveState.TYPE);
                }
                break;
            case LENGTH:
                int length = in.readInt();
                message.setLength(length);
                if (length > 0) {
                    checkpoint(LiveState.CONTENT);
                } else {
                    out.add(message);
                    checkpoint(LiveState.TYPE);
                }
                break;
            case CONTENT:
                byte[] bytes = new byte[message.getLength()];
                in.readBytes(bytes);
                String content = new String(bytes);
                message.setContent(content);
                out.add(message);
                checkpoint(LiveState.TYPE);
                break;
            default:
                throw new IllegalStateException("invalid state:" + state);//如果异常的话，要抛出异常
        }
    }

}
