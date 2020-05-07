package com.aptdev.tools.screens;

import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenUtils {

    public static DisplayMetrics getScreenDM(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int getScreenWidth(Context context) {
        return getScreenDM(context).widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return getScreenDM(context).heightPixels;
    }

    public static int dp2px(int dp) {
        return 0;
    }

    public static int px2dp(int px) {
        return 0;
    }

}
