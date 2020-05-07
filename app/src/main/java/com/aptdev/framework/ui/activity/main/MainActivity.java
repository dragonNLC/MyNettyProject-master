package com.aptdev.framework.ui.activity.main;

import android.view.View;
import android.widget.Button;

import com.aptdev.common.ui.activity.base.BaseNetActivity;
import com.aptdev.framework.R;
import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.dragondevl.clog.CLog;

import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class MainActivity extends BaseNetActivity {

    private Button btnOpenNetty;
    private Button btnCloseNetty;

    private ChannelFuture future;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup parentGroup;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        btnOpenNetty = findViewById(R.id.btn_open_netty);
        btnCloseNetty = findViewById(R.id.btn_close_netty);
    }

    @Override
    protected void initData() {
        btnOpenNetty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bossGroup = new NioEventLoopGroup(1);
                parentGroup = new NioEventLoopGroup();

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
                future = sb.bind();
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            CLog.e("绑定成功！");
                        } else {
                            Throwable throwable = future.cause();
                            if (throwable != null) {
                                throwable.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
        btnCloseNetty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (future != null) {
                    try {
                        parentGroup.shutdownGracefully().sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        bossGroup.shutdownGracefully().sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        future.channel().closeFuture().sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    CLog.e("关闭成功！");
                }
            }
        });
    }

    @Override
    protected void doBusiness() {

    }

    @Override
    protected void viewClick(View view) {

    }

    @Override
    protected void enterScreenSaver() {
        super.enterScreenSaver();
        CLog.i("in screensaver!");
    }


}
