package com.cufe.suitforyou.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Victor on 2016-08-30.
 */
public class MD5Util {

    /**
     * 使用MessageDigest类进行MD5加密
     *
     * @param password 目标字符串
     * @return 密文字符串
     */
    public static String encode(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] result = messageDigest.digest(password.getBytes());

        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : result) {
            int num = b & 0xff;
            String hexStr = Integer.toHexString(num);
            if (hexStr.length() == 1) {
                stringBuffer.append(0);
            }
            stringBuffer.append(hexStr);
        }
        return stringBuffer.toString();
    }
}
