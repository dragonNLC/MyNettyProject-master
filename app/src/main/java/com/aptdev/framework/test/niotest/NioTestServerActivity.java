package com.aptdev.framework.test.niotest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.ui.activity.base.BaseNetActivity;
import com.aptdev.common.utils.ToastUtils;
import com.aptdev.framework.R;
import com.aptdev.framework.test.adapter.RCVClientAdapter;
import com.aptdev.framework.test.nioserver.WorkStationServer;
import com.aptdev.framework.test.nioworkthread.ServerCallbackListener;
import com.dragondevl.clog.CLog;

/**
 * @ClassName TestActivity
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-07 10:09
 * @Version 1.0
 */
public class NioTestServerActivity extends BaseNetActivity implements ServerCallbackListener {

    private Button btnStartReceiver;
    private Button btnStopReceiver;
    private LinearLayout linearTopConnectLayout;
    private RecyclerView rcvConnectList;
    private RecyclerView rcvContentList;

    private WorkStationServer.ServerStationBinder myBinder;

    private RCVClientAdapter mAdapter;

    private boolean mBinging;

    private ServiceConnection mServerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CLog.e("onServiceConnected");
            mBinging = true;
            myBinder = (WorkStationServer.ServerStationBinder) service;
            if (myBinder != null) {
                myBinder.setServerCallbackListener(NioTestServerActivity.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            CLog.e("onServiceDisconnected");
            mBinging = false;
            if (myBinder != null) {
                myBinder.setServerCallbackListener(null);
            }
            myBinder = null;
        }
    };

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_nio_test_server_layout;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        btnStartReceiver = findViewById(R.id.btn_start_receiver);
        btnStopReceiver = findViewById(R.id.btn_stop_receiver);
        linearTopConnectLayout = findViewById(R.id.linear_top_connect_layout);
        rcvConnectList = findViewById(R.id.rcv_connect_list);
        rcvContentList = findViewById(R.id.rcv_content_list);

        btnStartReceiver.setOnClickListener(this);
        btnStopReceiver.setOnClickListener(this);
        btnStopReceiver.setEnabled(false);

        rcvConnectList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new RCVClientAdapter();
        rcvConnectList.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        startService(new Intent(this, WorkStationServer.class));
        bindService(new Intent(this, WorkStationServer.class), mServerConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void doBusiness() {
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBinging) {
            mBinging = false;
            unbindService(mServerConnection);
            if (myBinder != null) {
                myBinder.setServerCallbackListener(null);
            }
            myBinder = null;
        }
        stopService(new Intent(this, WorkStationServer.class));
    }

    @Override
    protected void viewClick(View view) {
        if (view == btnStartReceiver) {
            if (myBinder == null) {
                ToastUtils.longToastShort(this, "等待连接本地服务中，请稍候。。。");
                return;
            }
            myBinder.startReceiver();
        } else if (view == btnStopReceiver) {
            if (myBinder == null) {
                ToastUtils.longToastShort(this, "等待连接本地服务中，请稍候。。。");
                return;
            }
            myBinder.stopReceiver();
        }
    }

    @Override
    public void onStartSuccess() {
        btnStartReceiver.setEnabled(false);
        btnStopReceiver.setEnabled(true);
    }

    @Override
    public void onStartFail(String errorInfo) {
        onReceiverStop();
    }

    @Override
    public void onAlreadyStart() {
        onStartSuccess();
    }

    @Override
    public void onReceiverStop() {
        btnStartReceiver.setEnabled(true);
        btnStopReceiver.setEnabled(false);
        mAdapter.getData().clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onReceiverStopFail(String errorInfo) {
        onStartSuccess();
    }

    @Override
    public void onClientConnect(String ip) {
        mAdapter.addData(ip);
    }

    @Override
    public void onClientDisconnect(String ip) {
        mAdapter.getData().remove(ip);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClientSend(String content) {

    }

    @Override
    public void onSendToClientSuccess() {

    }

    @Override
    public void onSendToClientFail(String errorInfo) {

    }

    @Override
    public void onSuccess(Object data) {

    }

    @Override
    public void onFail(String errorInfo) {

    }

    @Override
    public void onCompleted() {

    }

}
