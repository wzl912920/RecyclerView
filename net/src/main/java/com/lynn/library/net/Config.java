package com.lynn.library.net;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Lynn.
 */

abstract class Config {
    public static final Gson GSON = new Gson();
    static boolean DEBUG = false;
    static String DOMAIN = "";
    static String TEST_DOMAIN = "";
    static String URL = Config.DEBUG ? TEST_DOMAIN : DOMAIN;

    protected static final String toMd5(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        byte[] bytes = str.getBytes();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(bytes);
            String result = toHexString(algorithm.digest());
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return toHexString(bytes);
        }
    }

    private synchronized static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int val = ((int) bytes[i]);
            if (val < 0) {
                val += 256;
            }
            if (val < 16) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(val));
        }
        return hexString.toString();
    }
}
