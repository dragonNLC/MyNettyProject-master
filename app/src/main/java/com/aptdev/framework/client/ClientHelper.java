package com.aptdev.framework.client;

import com.aptdev.framework.service.bean.LiveMessage;
import com.dragondevl.clog.CLog;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * @ClassName ClientHelper
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 11:33
 * @Version 1.0
 */
public class ClientHelper {

    public void run(int port) throws InterruptedException {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("192.168.11.50", port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                                //可以在这里保存channel
                                String content = "Hello!";
                                ByteBuf byteBuf = Unpooled.buffer();
                                byteBuf.writeByte(LiveMessage.TYPE_MESSAGE);
                                byteBuf.writeInt(content.getBytes().length);
                                byteBuf.writeBytes(content.getBytes());
                                ctx.writeAndFlush(byteBuf);
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                System.out.println("receiver = " + msg.toString(CharsetUtil.UTF_8));
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                super.channelInactive(ctx);
                                //可以在这里移除保存的channel
                            }

                            @Override
                            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                super.channelReadComplete(ctx);
                                System.out.println("channelReadComplete");
                                ctx.channel().close().addListener(ChannelFutureListener.CLOSE);
                                //当channel被关闭之后，整个serverBootStrap都会被关闭，而对于server来讲，之所以没有关闭，应该是socketServer在运行的原因
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                                cause.printStackTrace();
                            }
                        });
                    }
                });
        ChannelFuture future = bootstrap.connect().sync();//client使用connect连接，server使用bind绑定
        future.channel().closeFuture().sync();//阻塞当前线程，避免走完之后进程被关闭

        //future.channel().writeAndFlush()//使用channel直接写出数据，而channelContext则是包装了Channel的对象

        workGroup.shutdownGracefully().sync();
    }

}
