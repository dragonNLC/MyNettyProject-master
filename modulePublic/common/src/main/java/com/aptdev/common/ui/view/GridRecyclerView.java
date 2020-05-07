package com.aptdev.common.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;


/**
 * 用于支持item延时动画
 * Created by Administrator on 2017/4/6.
 */

public class GridRecyclerView extends RecyclerView implements GestureDetector.OnGestureListener {

    public GridRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public GridRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GridRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (layout instanceof GridLayoutManager) {
            super.setLayoutManager(layout);
        } else {
            throw new ClassCastException("You should only use a gridLayoutManager with GridRecyclerView");
        }
    }

    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count) {
        if (getAdapter() != null && getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutAnimationController.AnimationParameters animationParameters = (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;
            if (animationParameters == null) {

                animationParameters = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParameters;
            }
            int columns = ((GridLayoutManager) getLayoutManager()).getSpanCount();
            animationParameters.count = count;
            animationParameters.index = index;
            animationParameters.columnsCount = columns;
            animationParameters.rowsCount = count / columns;

            final int invertedIndex = count - 1 - index;
            animationParameters.column = columns - 1 - (invertedIndex % columns);
            animationParameters.row = animationParameters.rowsCount - 1 - invertedIndex / columns;
        } else {
            super.attachLayoutAnimationParameters(child, params, index, count);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }

}
