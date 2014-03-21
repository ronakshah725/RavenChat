package com.sumitgouthaman.raven.persistence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.sumitgouthaman.raven.R;

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

    public static String getRegistrationID(Context context) {
        SharedPreferences shared = context.getSharedPreferences(key, 0);
        return shared.getString("REGISTRATION_ID", null);
    }

    public static void setRegistrationID(Context context, String registrationID) {
        SharedPreferences shared = context.getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("REGISTRATION_ID", registrationID);
        editor.commit();
    }

    public static String getSenderID(Context context) {
        return context.getString(R.string.sender_id);
    }

    public static String getAPIKey(Context context) {
        return context.getString(R.string.api_key);
    }
}
