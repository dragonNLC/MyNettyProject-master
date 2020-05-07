package com.aptdev.common.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import com.aptdev.common.ui.view.scroller.AutoVPScroller;
import com.dragondevl.clog.CLog;

import java.lang.reflect.Field;


/**
 * Created by Administrator on 2017/4/17.
 */

public class AutoRollingViewPager extends ViewPager {

    //是否进行自动滚动
    private boolean canAutoScroll = false;

    //是否允许手动滑动
    private boolean cannotTouch = false;

    private int autoScrollDelay = 0;

    protected static final int HANDLER_CAN_AUTO_SCROLL = 0x01;
    private static int HANDLER_DELAY_TIME = 1000 * 5;

    private AutoVPScroller scroller;
    private OnLastPageListener onLastPageListener;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //允许自动滑动
                case HANDLER_CAN_AUTO_SCROLL:
                    if (canAutoScroll) {
                        //当Item小于总Item-1个时，代表可以下一个
                        if (getAdapter() != null && getAdapter().getCount() > 0) {
                            if (getCurrentItem() < getAdapter().getCount() - 1) {
                                setCurrentItem(getCurrentItem() + 1);
                            } else {
                                //如果需要监听最后一页事件
                                if (onLastPageListener != null) {
                                    if (!onLastPageListener.onLast()) {//如果没有消费掉本次事件，则可以继续执行
                                        setCurrentItem(0, true);//重头开始，播放动画
                                    }
                                } else {
                                    setCurrentItem(0, true);//重头开始，播放动画
                                }
                            }
                        }
                        handler.removeMessages(HANDLER_CAN_AUTO_SCROLL);
                        handler.sendEmptyMessageDelayed(HANDLER_CAN_AUTO_SCROLL, HANDLER_DELAY_TIME);
                    }
                    break;
            }
            return false;
        }
    });


    public AutoRollingViewPager(Context context) {
        this(context, null);
    }

    public AutoRollingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        canAutoScroll = true;
        setViewPagerScroll();
    }

    public void startAutoScroll() {
        handler.sendEmptyMessageDelayed(HANDLER_CAN_AUTO_SCROLL, HANDLER_DELAY_TIME);
    }

    public void stopAutoScroll() {
        handler.removeMessages(HANDLER_CAN_AUTO_SCROLL);
    }

    private void setViewPagerScroll() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);
            scroller = new AutoVPScroller(getContext(),
                    (Interpolator) interpolatorField.get(null));
            scroller.setAnimTime(1000);
            scrollerField.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
    }

    public boolean isCanAutoScroll() {
        return canAutoScroll;
    }

    public void setCanAutoScroll(boolean canAutoScroll) {
        this.canAutoScroll = canAutoScroll;
    }

    public int getAutoScrollDelay() {
        return autoScrollDelay;
    }

    public void setAutoScrollDelay(int autoScrollDelay) {
        this.autoScrollDelay = autoScrollDelay;
    }

    public OnLastPageListener getOnEndListener() {
        return onLastPageListener;
    }

    public void setOnEndListener(OnLastPageListener onEndListener) {
        this.onLastPageListener = onEndListener;
    }

    public boolean isCannotTouch() {
        return cannotTouch;
    }

    public void setCannotTouch(boolean cannotTouch) {
        this.cannotTouch = cannotTouch;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (cannotTouch) return true;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            stopAutoScroll();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            startAutoScroll();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (cannotTouch) return true;
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (cannotTouch) return true;
        try {
            CLog.i(ev.getAction() + "");
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
        CLog.i("onDetachedFromWindow");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        CLog.i("keyCode = " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            resetAuto();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
        setFocusable(true);
        requestFocus();
        resetAuto();
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        setFocusable(true);
        requestFocus();
    }

    private void resetAuto() {
        stopAutoScroll();
        startAutoScroll();
    }

    public interface OnLastPageListener {
        boolean onLast();
    }

}
