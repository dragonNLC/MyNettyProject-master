package com.aptdev.framework.test.eos;

import com.dragondevl.base64.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyCipherUtil {

    public static String encroptMode = "ECB";
    public static String paddingMode = "PKCS5Padding";
    public static String algorithm = "DESede";
    public static String charset = "UTF-8";

    public static final String DATA_KEY = "ASJPVogQQDgoJXlRy91VZncpdJgCIjbi";

    public static final byte[] DATA_KEY_BYTES = { 0x01, 0x22, 0x4F, 0x56, (byte) 0x88, 0x10,
            0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD,
            0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x02, 0x22, 0x36,
            (byte) 0xE2 };

    public static String generateKey(byte[] keyBuf) {
        return Base64.encodeBase64String(keyBuf);
    }

    /**
     * 加密
     */
    public static String encryptData(String oriData, String keyStr) {
        try {
            byte[] keyBytes = Base64.decodeBase64(keyStr);
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            Cipher cipher = Cipher.getInstance(algorithm + "/" + encroptMode + "/" + paddingMode);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptBytes = cipher.doFinal(oriData.getBytes(charset));
            return Base64.encodeBase64String(encryptBytes).replace(" ", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 解密
     */
    public static String decryptData(String encData, String keyStr) {
        try {
            byte[] keyBytes = Base64.decodeBase64(keyStr);
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            Cipher cipher = Cipher.getInstance(algorithm + "/" + encroptMode + "/" + paddingMode);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decBytes = cipher.doFinal(Base64.decodeBase64(encData));
            return new String(decBytes, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
