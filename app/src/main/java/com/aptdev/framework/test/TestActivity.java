package com.aptdev.framework.test;

import android.Manifest;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.ui.activity.base.BaseNetActivity;
import com.aptdev.framework.MainApplication;
import com.aptdev.framework.R;
import com.aptdev.framework.test.niotest.NioTestClientActivity;
import com.aptdev.framework.test.niotest.NioTestServerActivity;
import com.dragondevl.clog.CLog;
import com.tbruyelle.rxpermissions.RxPermissions;

/**
 * @ClassName TestActivity
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-07 10:09
 * @Version 1.0
 */
public class TestActivity extends BaseNetActivity {

    private Button btnServerModel;
    private Button btnClientModel;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_test_layout;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        btnServerModel = findViewById(R.id.btn_server_model);
        btnClientModel = findViewById(R.id.btn_client_model);

        btnServerModel.setOnClickListener(this);
        btnClientModel.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void doBusiness() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        CLog.e("onStop");
    }

    @Override
    protected void viewClick(View view) {
        if (view == btnServerModel) {
            startActivity(NioTestServerActivity.class);
        } else if (view == btnClientModel) {
            startActivity(NioTestClientActivity.class);
        }
    }

}
