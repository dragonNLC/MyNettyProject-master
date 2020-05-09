package com.aptdev.framework.test.niotest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.ui.activity.base.BaseNetActivity;
import com.aptdev.common.utils.TimeUtils;
import com.aptdev.common.utils.ToastUtils;
import com.aptdev.framework.R;
import com.aptdev.framework.test.bean.LiveMessage;
import com.aptdev.framework.test.bean.SocketControlBean;
import com.aptdev.framework.test.constants.ServerConstant;
import com.aptdev.framework.test.eos.CommunicationPermissionUtil;
import com.aptdev.framework.test.eos.MyCipherUtil;
import com.aptdev.framework.test.nioserver.SingleClientServer;
import com.aptdev.framework.test.utils.GSonUtils;
import com.dragondevl.clog.CLog;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.SocketChannel;

/**
 * @ClassName TestActivity
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-07 10:09
 * @Version 1.0
 */
public class NioTestClientActivity extends BaseNetActivity implements SingleClientServer.ClientStatusListener {

    private Button btnSendHeartbeat;
    private Button btnRequestConsume;
    private LinearLayout linearTopConnectLayout;
    private RecyclerView rcvConnectList;
    private RecyclerView rcvContentList;
    private Button btnReconnect;
    private Button btnAuth;
    private Button btnStopConnect;

    private SingleClientServer.SingleClientBinder myBinder;

    private boolean mBinding;

    private ServiceConnection mServerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinding = true;
            myBinder = (SingleClientServer.SingleClientBinder) service;
            if (myBinder != null) {
                myBinder.setClientStatusListener(NioTestClientActivity.this);
                myBinder.startConnect();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinding = false;
            if (myBinder != null) {
                myBinder.setClientStatusListener(null);
            }
            myBinder = null;
        }
    };

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_nio_test_client_layout;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        btnAuth = findViewById(R.id.btn_auth);
        btnSendHeartbeat = findViewById(R.id.btn_send_heartbeat);
        btnRequestConsume = findViewById(R.id.btn_request_consume);
        linearTopConnectLayout = findViewById(R.id.linear_top_connect_layout);
        rcvConnectList = findViewById(R.id.rcv_connect_list);
        rcvContentList = findViewById(R.id.rcv_content_list);
        btnReconnect = findViewById(R.id.btn_reconnect);
        btnStopConnect = findViewById(R.id.btn_stop_connect);


        btnAuth.setOnClickListener(this);
        btnSendHeartbeat.setOnClickListener(this);
        btnRequestConsume.setOnClickListener(this);
        btnReconnect.setOnClickListener(this);
        btnStopConnect.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        startService(new Intent(this, SingleClientServer.class));
        bindService(new Intent(this, SingleClientServer.class), mServerConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void doBusiness() {
    }

    @Override
    protected void onStop() {
        super.onStop();
        CLog.e("onStop");
        if (mBinding) {
            mBinding = false;
            if (myBinder != null) {
                myBinder.setClientStatusListener(null);
                myBinder.stopConnect();
            }
            unbindService(mServerConnection);
            myBinder = null;
        }
    }

    @Override
    protected void viewClick(View view) {
        if (view == btnAuth) {
            if (myBinder != null) {
                String content = MyCipherUtil.encryptData(CommunicationPermissionUtil.SECRET_KEY, MyCipherUtil.DATA_KEY);
                myBinder.sendData(new LiveMessage(LiveMessage.TYPE_MESSAGE, content.getBytes().length, content));
            }
        } else if (view == btnSendHeartbeat) {
            if (myBinder == null) {
                ToastUtils.longToastShort(this, "等待连接本地服务中，请稍候。。。");
                return;
            }
            myBinder.sendData(new LiveMessage(LiveMessage.TYPE_HEART));
        } else if (view == btnRequestConsume) {
            if (myBinder == null) {
                ToastUtils.longToastShort(this, "等待连接本地服务中，请稍候。。。");
                return;
            }
            SocketControlBean scb = new SocketControlBean();
            scb.setDtype(ServerConstant.SEND_CODE_1);
            scb.setSerialNumber(System.currentTimeMillis());
            String content = GSonUtils.getInstance().toJson(scb);
            myBinder.sendData(new LiveMessage(LiveMessage.TYPE_MESSAGE, content.getBytes().length, content));
        } else if (view == btnReconnect) {
            btnReconnect.setVisibility(View.GONE);
            if (myBinder != null) {
                myBinder.startConnect();
            }
        } else if (view == btnStopConnect) {
            if (myBinder != null) {
                myBinder.stopConnect();
            }
        }
    }

    @Override
    public void connect(SocketChannel socketChannel) {
        btnReconnect.setVisibility(View.GONE);
        btnSendHeartbeat.setEnabled(true);
        btnRequestConsume.setEnabled(true);
        btnAuth.setEnabled(true);
        btnStopConnect.setEnabled(true);
        showToast("连接成功！", false);
    }

    @Override
    public void connectFail(String errorInfo) {
        disconnect();
        showToast(errorInfo, false);
    }

    @Override
    public void disconnect() {
        btnSendHeartbeat.setEnabled(false);
        btnRequestConsume.setEnabled(false);
        btnAuth.setEnabled(false);
        btnStopConnect.setEnabled(false);
        btnReconnect.setVisibility(View.VISIBLE);
        showToast("连接被关闭！", false);
    }

    @Override
    public void onWriteContent(String content) {

    }

    @Override
    public void onReadContent(String content) {

    }

}
