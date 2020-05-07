package com.aptdev.common.contracts.presenter.base;

import com.aptdev.common.contracts.view.base.BaseView;

/**
 * Created by lb on 2019/1/4.
 */

public class BasePresenter<V extends BaseView> {

    protected V mView;

    public void attach(V v) {
        this.mView = v;
    }

    public void detach() {
        this.mView = null;
    }

    protected boolean checkViewNoNull() {
        return mView != null;
    }

}
