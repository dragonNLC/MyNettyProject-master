package com.aptdev.framework.service.business;

/**
 * @ClassName StringUtils
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-04-30 17:29
 * @Version 1.0
 */
public class StringUtils {

    public static boolean isNullOrEmpty(String content) {
        return content == null || content.length() <= 0;
    }

    public static boolean isSpace(String string) {
        return string == null || string.trim().length() == 0;
    }

}
