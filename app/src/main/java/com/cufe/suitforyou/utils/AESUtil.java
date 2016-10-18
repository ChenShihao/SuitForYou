package com.cufe.suitforyou.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Victor on 2016-08-30.
 */
public class AESUtil {

    public static String Encrypt(String content, String key) throws Exception {
        if (key == null || key.length() != 16) {
            return null;
        }
        byte[] raw = key.getBytes("ASCII");
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(content.getBytes());
        return byte2hex(encrypted).toLowerCase();
    }

    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            if (sKey == null || sKey.length() != 16) {
                return null;
            }
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] encrypted1 = hex2byte(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static byte[] hex2byte(String hexString) {
        if (hexString == null) {
            return null;
        }
        int length = hexString.length();
        if (length % 2 == 1) {
            return null;
        }
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i != length / 2; i++) {
            bytes[i] = (byte) Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    public static String byte2hex(byte[] b) {
        String hexString = "";
        String tmp;
        for (int n = 0; n < b.length; n++) {
            tmp = Integer.toHexString(b[n] & 0XFF);
            if (tmp.length() == 1) {
                hexString = hexString + "0" + tmp;
            } else {
                hexString = hexString + tmp;
            }
        }
        return hexString.toUpperCase();
    }
}
