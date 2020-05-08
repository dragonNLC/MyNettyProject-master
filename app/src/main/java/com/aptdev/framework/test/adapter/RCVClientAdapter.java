package com.aptdev.framework.test.adapter;

import com.aptdev.framework.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * @ClassName RCVClientAdapter
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-07 17:56
 * @Version 1.0
 */
public class RCVClientAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public RCVClientAdapter() {
        super(R.layout.rcv_client_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_dev_ip, item);
    }

}
