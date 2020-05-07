package com.aptdev.framework.ui.activity.main;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;


import com.aptdev.framework.R;
import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.ui.activity.base.BaseActivity;
import com.dragondevl.clog.CLog;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.functions.Action1;
import update.lb.cn.updateapk.download.DownLoadApk;

import static update.lb.cn.updateapk.download.DownLoadApk.DOWNLOAD_URL;


public class StartUpActivity extends BaseActivity {

    private static final int SLEEP_TIME = 2 * 1000;

    private ExecutorService ese;
    private TextView tvToast;
    private boolean updating;
    private int count;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_start_up;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void dealBundle(Bundle bundle) {
        super.dealBundle(bundle);
        updating = bundle.getBoolean("updating");
    }

    @Override
    protected void initView() {
        tvToast = (TextView) findViewById(R.id.tv_toast);
        ese = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void initData() {
        count = 0;
        if (updating) {
            tvToast.setText(getResources().getString(R.string.str_app_updating));
            String appName = getResources().getString(R.string.app_name);
            String updateTips = getResources().getString(R.string.str_app_update_name) + appName;
            DownLoadApk.download(this, DOWNLOAD_URL, updateTips);
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                ese.execute(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(SLEEP_TIME);
                        toMainActivity();
                    }
                });
            } else {
                requestPermissions();
            }
        }
    }

    @Override
    protected void doBusiness() {

    }

    @Override
    protected void viewClick(View view) {

    }

    private void toMainActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) return;
                startActivity(MainActivity.class);
                finish();
            }
        });
    }



    private void requestPermissions() {
        final String[] rqPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        RxPermissions rxPermission = new RxPermissions(StartUpActivity.this);
        rxPermission
                .requestEach(rqPermission)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        CLog.i("permission = " + permission);
                        if (permission.granted) {
                            // 用户已经同意该权限
                            count++;
                            if (count >= rqPermission.length) {
                                ese.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        SystemClock.sleep(2 * 1000);
                                        toMainActivity();
                                    }
                                });
                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            showToast(getResources().getString(R.string.app_not_allow_author), false);
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            showToast(getResources().getString(R.string.app_not_allow_author), false);
                        }
                    }
                });
    }

}
