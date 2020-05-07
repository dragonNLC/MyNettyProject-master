package com.aptdev.common.ui.view.scroller;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by Administrator on 2017/4/18.
 */

public class AutoVPScroller extends Scroller {

    private double scrollFactor = 1;

    private int animTime = 800;

    public AutoVPScroller(Context context) {
        super(context);
    }

    public AutoVPScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public AutoVPScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    public void setScrollFactor(double scrollFactor) {
        this.scrollFactor = scrollFactor;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, animTime);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, animTime);
    }

    public void setAnimTime(int animTime) {
        this.animTime = animTime;
    }

}
