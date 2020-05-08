package com.aptdev.framework;

import com.aptdev.framework.client.ClientHelper;
import com.aptdev.framework.test.eos.MyCipherUtil;
import com.dragondevl.clog.CLog;

import org.junit.Test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void testNIO() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup parentGroup = new NioEventLoopGroup();

        ServerBootstrap sb = new ServerBootstrap();
        sb.group(bossGroup, parentGroup)
                .localAddress(8080)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                CLog.e("msg = " + msg.toString(CharsetUtil.UTF_8));
                            }
                        });
                    }
                });
        ChannelFuture future = sb.bind().sync();
        System.out.println(System.currentTimeMillis());
        bossGroup.shutdownGracefully().sync();//这个是阻塞的，停止所有线程比较耗时，主要是因为要等到释放完毕后唤醒本线程.
        System.out.println(System.currentTimeMillis());
        //只有当EventGroup被关闭时，channel才可以被关闭
        //从原理上来讲，表示的是，只有当所有的事件循环都被关闭了，对应的ServerSocketChannel才能被关闭，主要是因为要等到释放完毕后唤醒本线程.
        parentGroup.shutdownGracefully().sync();//这个是阻塞的，停止所有线程比较耗时
        System.out.println(System.currentTimeMillis());
        future.channel().closeFuture().sync();//这个是阻塞的，只是关闭Channel，很快
        System.out.println(System.currentTimeMillis());
        /*future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    CLog.e("绑定成功！");
                    future.channel().closeFuture().sync();
                } else {
                    Throwable throwable = future.cause();
                    if (throwable != null) {
                        throwable.printStackTrace();
                    }
                }
            }
        });*/
    }

    @Test
    public void testNioClient() throws InterruptedException {
        new ClientHelper().run(6601);
    }

    @Test
    public void printKey() {
        /*String key = MyCipherUtil.encryptData("aptdev", );
        System.out.println(key);*/
    }

}