package com.sumitgouthaman.raven.persistence;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by sumit on 15/3/14.
 */
public class Persistence {

    private static final String key = "RAVEN";

    public static String getUsername(Activity activity) {
        SharedPreferences shared = activity.getSharedPreferences(key, 0);
        return shared.getString("USERNAME", null);
    }

    public static void setUsername(Activity activity, String username) {
        SharedPreferences shared = activity.getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("USERNAME", username);
        editor.commit();
    }
}
