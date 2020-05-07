package com.aptdev.common.utils;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by Administrator on 2018/3/5.
 */

public class ToastUtils {

    public static void longToastTime(Context context, String msg) {
        if (!"".equals(msg)) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    public static void longToastShort(Context context, String msg) {
        if (!"".equals(msg)) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

}
