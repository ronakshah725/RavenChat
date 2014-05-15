package com.sumitgouthaman.raven.utils.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

/**
 * Created by sumit on 15/5/14.
 */
public class KeyGeneratorUtils {
    public static String getNewKey(String secretUsername){
        String salt = "KNzFSWX7hCcG3qoZJx0V";
        String timestamp = System.currentTimeMillis()+"";
        String seed = salt+secretUsername+timestamp+salt;
        byte[] arr = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(seed.getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            arr = (kg.generateKey()).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if(arr==null){
            return null;
        }else{
            return Base64Utils.encode(arr);
        }
    }
}
