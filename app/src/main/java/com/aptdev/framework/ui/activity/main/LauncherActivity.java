package com.aptdev.framework.ui.activity.main;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;


import com.aptdev.common.constants.SimpleConstant;
import com.aptdev.common.contracts.presenter.base.BasePresenter;
import com.aptdev.common.ui.activity.base.BaseActivity;
import com.aptdev.framework.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by lb on 2018/7/6.
 */

public class LauncherActivity extends BaseActivity {

    private TextView tvEmptyDate;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launcher_layout;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        //默认不开启自动更新
        //startService(new Intent(this, UpgradeServer.class));
        init();
        check();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void doBusiness() {

    }

    @Override
    protected void viewClick(View view) {

    }

    private void init() {
        tvEmptyDate = (TextView) findViewById(R.id.tv_empty_date);
    }

    private void check() {
        SimpleDateFormat sdf = new SimpleDateFormat(SimpleConstant.SIMPLE_DATE_FORMAT_PATTERN, Locale.CHINESE);
        try {
            if (new Date().after(sdf.parse(SimpleConstant.DEFAULT_APP_USE_DATE))) {
                tvEmptyDate.setVisibility(View.VISIBLE);
            } else {
                Intent intent = new Intent(LauncherActivity.this, StartUpActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            tvEmptyDate.setVisibility(View.VISIBLE);
        }
    }


}
