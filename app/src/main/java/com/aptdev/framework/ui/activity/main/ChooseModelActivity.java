package com.aptdev.framework.ui.activity.main;

import android.view.View;
import android.widget.Button;

import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.ui.activity.base.BaseNetActivity;
import com.aptdev.framework.R;

/**
 * @ClassName ChooseModelActivity
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 8:42
 * @Version 1.0
 */
public class ChooseModelActivity extends BaseNetActivity {

    private Button btnStartServerModel;
    private Button btnStartClientModel;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_choose_model_layout;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        btnStartServerModel = findViewById(R.id.btn_start_server_model);
        btnStartClientModel = findViewById(R.id.btn_start_client_model);

        btnStartServerModel.setOnClickListener(this);
        btnStartClientModel.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void doBusiness() {

    }

    @Override
    protected void viewClick(View view) {
        if (view == btnStartServerModel) {
            startActivity(ServerModelActivity.class);
        } else if (view == btnStartClientModel) {
            startActivity(ClientModelActivity.class);
        }
    }

}
