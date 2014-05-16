package com.sumitgouthaman.raven.utils.crypto;

import android.util.Base64;

/**
 * Created by sumit on 15/5/14.
 */
public class Base64Utils {
    public static String encode(byte[] arr) {
        return Base64.encodeToString(arr, Base64.DEFAULT);
    }

    public static byte[] decode(String str) {
        return Base64.decode(str.getBytes(), Base64.DEFAULT);
    }
}
