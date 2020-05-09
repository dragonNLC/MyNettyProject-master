package com.aptdev.framework.test.nioworkthread;

import io.netty.channel.ChannelHandler;

/**
 * @ClassName ChannelHandlerHolder
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-09 10:20
 * @Version 1.0
 */
public interface ChannelHandlerHolder {

    ChannelHandler[] handlers();

}
