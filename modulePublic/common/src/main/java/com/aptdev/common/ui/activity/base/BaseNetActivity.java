package com.aptdev.common.ui.activity.base;

import android.Manifest;
import android.support.annotation.RequiresPermission;

import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.contracts.view.base.BaseView;
import com.aptdev.common.utils.NetUtil;

/**
 * Created by lb on 2019/1/4.
 */

public abstract class BaseNetActivity<V extends BaseView, T extends BasePresenter> extends BaseFragmentActivity<V, T> {

    protected int netMode = -1;

    @RequiresPermission(Manifest.permission.INTERNET)
    public boolean inspectNet() {
        this.netMode = NetUtil.getNetworkState(this);
        return inNetConnect();
    }

    private boolean inNetConnect() {
        return netMode == NetUtil.NETWORK_WIFI || netMode == NetUtil.NETWORK_ETHERNET || netMode == NetUtil.NETWORK_MOBILE;
    }

}
