package com.aptdev.framework.ui.activity.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.ui.activity.base.BaseNetActivity;
import com.aptdev.framework.R;
import com.aptdev.framework.iface.ServerCallbackListener;
import com.aptdev.framework.service.ServiceStationServer;
import com.dragondevl.clog.CLog;

/**
 * @ClassName ServerModelActivity
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 8:46
 * @Version 1.0
 */
public class ServerModelActivity extends BaseNetActivity implements ServerCallbackListener {

    private Button btnStartReceiverServer;
    private Button btnStopReceiverServer;

    private boolean binding;

    private ServiceStationServer.ServerStationBinder serverStationBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binding = true;
            serverStationBinder = (ServiceStationServer.ServerStationBinder) service;
            if (serverStationBinder != null) {
                serverStationBinder.setServerCallbackListener(ServerModelActivity.this);
            }
            CLog.e("onServiceConnected");
        }

        //在正常关闭的时候是不会被调用的，只有当Server Crash或者被kill的时候，才会调用，正确来讲，应该是表示当server端被非显示关闭时调用该方法，表示服务已断开连接
        @Override
        public void onServiceDisconnected(ComponentName name) {
            binding = false;
            if (serverStationBinder != null) {
                serverStationBinder.setServerCallbackListener(null);
            }
            serverStationBinder = null;
            CLog.e("onServiceDisconnected");
        }
    };

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_server_model_layout;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        btnStartReceiverServer = findViewById(R.id.btn_start_receiver_server);
        btnStopReceiverServer = findViewById(R.id.btn_stop_receiver_server);

        btnStartReceiverServer.setOnClickListener(this);
        btnStopReceiverServer.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        startService(new Intent(this, ServiceStationServer.class));
        bindService(new Intent(this, ServiceStationServer.class), serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void doBusiness() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (binding) {
            binding = false;
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void viewClick(View view) {
        if (view == btnStartReceiverServer) {
            if (serverStationBinder == null) {
                showToast("等待服务启动。。。", false);
                return;
            }
            btnStartReceiverServer.setEnabled(false);
            btnStopReceiverServer.setEnabled(false);
            serverStationBinder.startReceiver();
        } else if (view == btnStopReceiverServer) {
            if (serverStationBinder == null) {
                showToast("等待服务启动。。。", false);
                return;
            }
            btnStartReceiverServer.setEnabled(false);
            btnStopReceiverServer.setEnabled(false);
            serverStationBinder.stopReceiver();
        }
    }

    @Override
    public void onStartSuccess() {
        btnStartReceiverServer.setEnabled(false);
        btnStopReceiverServer.setEnabled(true);
    }

    @Override
    public void onStartFail(String errorInfo) {
        showToast(errorInfo, false);
        btnStartReceiverServer.setEnabled(true);
        btnStopReceiverServer.setEnabled(false);
    }

    @Override
    public void onAlreadyStart() {
        onStartSuccess();
    }

    @Override
    public void onReceiverStop() {
        btnStartReceiverServer.setEnabled(true);
        btnStopReceiverServer.setEnabled(false);
    }

    @Override
    public void onReceiverStopFail(String errorInfo) {
        btnStartReceiverServer.setEnabled(true);
        btnStopReceiverServer.setEnabled(false);
    }

    @Override
    public void onClientConnect(String ip) {

    }

    @Override
    public void onClientDisconnect(String ip) {

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

