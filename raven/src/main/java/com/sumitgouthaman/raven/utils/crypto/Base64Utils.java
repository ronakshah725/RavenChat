package com.sumitgouthaman.raven.utils.crypto;

import android.util.Base64;

/**
 * Created by sumit on 15/5/14.
 */

/**
 * Class to abstract and simply converting from byte array to string and other way around
 */
public class Base64Utils {
    /**
     * Converts byte array to String
     * @param arr - The array to be encoded
     * @return - The returned String
     */
    public static String encode(byte[] arr) {
        return Base64.encodeToString(arr, Base64.DEFAULT);
    }

    /**
     * Converts from String to byte array
     * @param str - The String to be decoded
     * @return - The returned byte array
     */
    public static byte[] decode(String str) {
        return Base64.decode(str.getBytes(), Base64.DEFAULT);
    }
}
