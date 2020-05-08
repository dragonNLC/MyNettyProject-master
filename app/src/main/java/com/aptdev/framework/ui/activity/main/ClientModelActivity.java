package com.aptdev.framework.ui.activity.main;

import android.view.View;
import android.widget.Button;

import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.ui.activity.base.BaseNetActivity;
import com.aptdev.framework.R;

/**
 * @ClassName ClientModelActivity
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 8:46
 * @Version 1.0
 */
public class ClientModelActivity extends BaseNetActivity {

    private Button btnConnectServer;
    private Button btnDisconnectServer;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_client_model_layout;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        btnConnectServer = findViewById(R.id.btn_connect_server);
        btnDisconnectServer = findViewById(R.id.btn_disconnect_server);

        btnConnectServer.setOnClickListener(this);
        btnDisconnectServer.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void doBusiness() {

    }

    @Override
    protected void viewClick(View view) {
        if (view == btnConnectServer) {

        } else if (view == btnDisconnectServer) {

        }
    }

}
