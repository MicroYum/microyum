package com.microyum.common.util;

import com.microyum.common.Constants;

import java.security.MessageDigest;

public class StringUtils {

    public String trim(String str) {

        if (str == null || str.length() == 0) {
            return "";
        }

        return str.trim();
    }

    private static String md5Encrypt(String str, boolean slat) {
        String result = "";
        try {
            if (slat) {
                str = str + Constants.COMMON_MD5_SLAT;
            }

            MessageDigest m = MessageDigest.getInstance(Constants.COMMON_MD5);
            m.update(str.getBytes(Constants.COMMON_DEFAULT_ENCODE));
            byte s[] = m.digest();

            for (int i = 0; i < s.length; i++) {
                result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
            }

        } catch (Exception e) {
            return null;
        }

        return result;
    }

    public static String md5EncryptSlat(String str) {

        return StringUtils.md5Encrypt(str, true);
    }

    public static String md5Encrypt(String str) {

        return StringUtils.md5Encrypt(str, false);
    }

    public static void main(String[] args) {
        System.out.println(StringUtils.md5EncryptSlat("password"));
        System.out.println(StringUtils.md5EncryptSlat(StringUtils.md5Encrypt("abcd.1234")));
    }
}
