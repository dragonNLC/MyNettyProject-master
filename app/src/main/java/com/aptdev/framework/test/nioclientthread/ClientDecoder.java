package com.aptdev.framework.test.nioclientthread;

import com.dragondevl.clog.CLog;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

/**
 * @ClassName ClientDecoder
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-09 16:03
 * @Version 1.0
 */
public class ClientDecoder extends ReplayingDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        CLog.e("好像有数据进来了!");
        out.add(in);
    }

}
