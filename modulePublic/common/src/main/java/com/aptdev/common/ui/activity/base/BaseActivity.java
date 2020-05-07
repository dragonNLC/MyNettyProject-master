package com.aptdev.common.ui.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.contracts.view.base.BaseView;
import com.aptdev.common.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * Created by lb on 2019/1/4.
 */

public abstract class BaseActivity<V extends BaseView, T extends BasePresenter> extends AppCompatActivity implements View.OnClickListener {

    //用于隐藏顶部导航栏，底部虚拟按键栏
    public static final int UI_OPTIONS_HIDE_OTHERS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    //默认点击间隔
    public static final long CLICK_INTERVAL = 500;


    public static final int WITHOUT_FOR_RESULT = -0x001001;

    /**
     * 用户最后一次操作的时间
     **/
    protected Date mLastTouchTime;

    /**
     * 屏幕多久没有被操作，单位时间戳：ns
     **/
    protected long mNotTouchTime;

    /**
     * 静止超过n秒进入屏保操作，时间单位：s
     **/
    protected float mHoldStillTime = 5 * 2;

    /**
     * 标识当前是否已经进入屏保状态
     **/
    protected boolean isScreenSaver;

    /**
     * 检查线程的时间间隔
     **/
    protected long intervalScreenSaver = 1000;

    private Handler mHandler1 = new Handler();

    //点击间隔
    protected long mLastClick;

    //逻辑层对象
    protected T mPresenter;

    //获取layout xml文件的id
    protected abstract int getContentLayoutId();

    //初始化presenter，如果需要的话
    protected abstract T initPresenter();

    //初始化所有View，findViewById(?)
    protected abstract void initView();

    //初始化所有必须的数据
    protected abstract void initData();

    //处理业务逻辑
    protected abstract void doBusiness();

    //view的点击
    protected abstract void viewClick(View view);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutId());
        if (getIntent() != null && getIntent().getExtras() != null) {
            dealBundle(getIntent().getExtras());
        }
        mPresenter = initPresenter();
        if (null != mPresenter) {
            mPresenter.attach((V) this);
        }
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLastTouchTime = new Date(System.currentTimeMillis());
        mHandler1.postAtTime(mTask1, intervalScreenSaver);
        getWindow().getDecorView().setSystemUiVisibility(UI_OPTIONS_HIDE_OTHERS);
        try {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        doBusiness();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler1.removeCallbacks(mTask1);
        try {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detach();
            mPresenter = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (uniformClick()) {
            viewClick(v);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        updateUserActionTime();
        return super.dispatchTouchEvent(ev);
    }

    public <X extends BaseActivity> void startActivity(Class<X> clz) {
        startActivity(clz, null, WITHOUT_FOR_RESULT);
    }

    public <X extends BaseActivity> void startActivity(Class<X> clz, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (requestCode == WITHOUT_FOR_RESULT) {
            startActivity(intent);
        } else {
            startActivityForResult(intent, requestCode);
        }
    }

    public void showToast(String msg, boolean isLongTime) {
        if (isLongTime) {
            ToastUtils.longToastTime(this, msg);
        } else {
            ToastUtils.longToastShort(this, msg);
        }
    }

    /**
     * 通知处理传入的bundle数据
     *
     * @param bundle input param
     */
    protected void dealBundle(Bundle bundle) {

    }

    /**
     * 检查是否匀速点击，避免快速点击出现的问题
     *
     * @return is uniform or not
     */
    public boolean uniformClick() {
        if (System.currentTimeMillis() - mLastClick > CLICK_INTERVAL) {
            mLastClick = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public void updateUserActionTime() {
        Date touchNow = new Date(System.currentTimeMillis());
        mLastTouchTime.setTime(touchNow.getTime());
    }

    protected void enterScreenSaver() {

    }

    /**
     * 计时线程，不断判断是否已经到达显示屏保的时间
     */
    private Runnable mTask1 = new Runnable() {
        @Override
        public void run() {
            Date now = new Date(System.currentTimeMillis());
            //计算静止时间
            mNotTouchTime = now.getTime() - mLastTouchTime.getTime();
            float notTouchTimeSecond = mNotTouchTime / 1000f;
            if (notTouchTimeSecond > mHoldStillTime) {//静止时间大于最大静止时间
                if (!isScreenSaver) {//表示时间到了，但是还没有进入屏保
                    isScreenSaver = true;
                    enterScreenSaver();
                }
            } else {
                isScreenSaver = false;
            }
            mHandler1.postDelayed(mTask1, intervalScreenSaver);
        }
    };

}
