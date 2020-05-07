package com.aptdev.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lb on 2018/7/31.
 */

public class MD5Utils {

    public static String getMD5(String path) {
        StringBuffer sb = new StringBuffer();
        MessageDigest md = null;
        FileInputStream fis = null;
        File md5File = new File(path);
        if (!md5File.exists()) {
            return "";
        }
        try {
            md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(md5File);
            byte[] buffer = new byte[(int) md5File.length()];
            int length = 0;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (md == null) {
            return "";
        } else {
            return str2HexStr(md.digest());
        }
    }

    /**
     * 字符串转换成十六进制字符串
     */
    private static String str2HexStr(byte[] request) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        int bit;
        for (int i = 0; i < request.length; i++) {
            bit = (request[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = request[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }
}
