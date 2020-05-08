package com.aptdev.framework;

import android.app.Application;


/**
 * Created by lb on 2019/4/2.
 */

public class MainApplication extends Application {

    private static MainApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MainApplication getInstance() {
        return instance;
    }

}
