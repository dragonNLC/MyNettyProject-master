package com.aptdev.common.ui.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.contracts.view.base.BaseView;
import com.aptdev.common.ui.activity.base.BaseFragmentActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lb on 2019/1/4.
 */

public abstract class BaseFragment<V extends BaseView, T extends BasePresenter> extends Fragment implements View.OnClickListener{

    //最外层view对象
    protected View mRooView;

    protected BaseFragmentActivity mActivity;

    protected T mPresenter;

    protected abstract int getContentLayoutId();

    protected abstract T initPresenter();

    protected abstract void initView(View rootView);

    protected abstract void initData();

    protected abstract void doBusiness();

    protected abstract void viewClick(View view);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = initPresenter();
        if (getArguments() != null) {
            dealBundle(getArguments());
        }
        try {
            mActivity = (BaseFragmentActivity) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mPresenter != null) {
            mPresenter.attach((V)this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRooView = LayoutInflater.from(mActivity).inflate(getContentLayoutId(), container, false);
        initView(mRooView);
        return mRooView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void onPause() {
        super.onPause();
        try {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mPresenter != null) {
            mPresenter.detach();
            mPresenter = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (mActivity != null) {
            if (mActivity.uniformClick()) {
                viewClick(v);
            }
        } else {
            viewClick(v);
        }
    }

    protected void dealBundle(Bundle bundle) {

    }

}
