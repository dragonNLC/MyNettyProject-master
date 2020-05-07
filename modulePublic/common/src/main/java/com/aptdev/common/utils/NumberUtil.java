package com.aptdev.common.utils;

/**
 * Created by Administrator on 2017/11/27.
 */

public class NumberUtil {

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            //System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
