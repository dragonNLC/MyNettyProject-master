package com.aptdev.framework.test.eos;

/**
 * @ClassName CommunicationPermissionUtils
 * @Description TODO
 * @SysUser Administrator
 * @Author dragon
 * @Date 2020-05-08 16:43
 * @Version 1.0
 */
public class CommunicationPermissionUtil {

    public static final String SECRET_KEY = "aptdev";

    public static boolean hasAuthorization(String content) {
        String secretKey = MyCipherUtil.decryptData(content, MyCipherUtil.DATA_KEY);
        return SECRET_KEY.equals(secretKey);
    }

}
