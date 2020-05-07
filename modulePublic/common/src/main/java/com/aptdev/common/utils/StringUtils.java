package com.aptdev.common.utils;

/**
 * Created by lb on 2018/12/6.
 */

public class StringUtils {

    public static boolean isEmpty(CharSequence string) {
        return string == null || string.length() == 0 || string.equals("null");
    }

    public static boolean isSpace(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static String nullLength0(String string) {
        return string == null ? "" : string;
    }

    public static int length(CharSequence sequence) {
        return sequence == null ? 0 : sequence.length();
    }

    public static String upperFirstLetter(String string) {
        return !isEmpty(string) && Character.isLowerCase(string.charAt(0)) ? (char)(string.charAt(0) - 32) + string.substring(1) : string;
    }

    public static String lowerFirstLetter(String string) {
        return !isEmpty(string) && Character.isUpperCase(string.charAt(0)) ? (char) (string.charAt(0) + 32) + string.substring(1) : string;
    }

}
