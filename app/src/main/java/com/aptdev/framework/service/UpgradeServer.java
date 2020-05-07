package com.aptdev.framework.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.aptdev.framework.R;
import com.aptdev.framework.ui.activity.main.StartUpActivity;
import com.dragondevl.clog.CLog;

import update.lb.cn.updateapk.bean.UpgradeRequestInfo;
import update.lb.cn.updateapk.retrofit.ApiServiceFactory;

/**
 * Created by lb on 2019/5/26.
 * 软件的更新服务
 */

public class UpgradeServer extends Service {

    private static final int HANDLER_UPDATE_APP = 0x00201;//1分钟检查是否需要更新app
    private static final int HANDLER_UPDATE_APP_DELAY = 60 * 1000;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (HANDLER_UPDATE_APP == msg.what) {
                handler.removeMessages(HANDLER_UPDATE_APP);
                handler.sendEmptyMessageDelayed(HANDLER_UPDATE_APP, HANDLER_UPDATE_APP_DELAY);
                upgrade();
            }
            return false;
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        handler.removeMessages(HANDLER_UPDATE_APP);
        handler.sendEmptyMessageDelayed(HANDLER_UPDATE_APP, 5 * 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void upgrade() {
        ApiServiceFactory.coverNewRequest(this, ApiServiceFactory.REQUEST_NEW_VERSION,
                new UpgradeRequestInfo("update", getString(R.string.app_name)),
                new ApiServiceFactory.ResultVersionCallBack() {

                    @Override
                    public void newVersion() {
                        forceUpdate();
                        CLog.i("发现新版本！");
                    }

                    @Override
                    public void normal() {
                        CLog.i("当前已经是最新的了！");
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void forceUpdate() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("updating", true);
        Intent intent = new Intent(this, StartUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        startActivity(intent);
        stopSelf();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
