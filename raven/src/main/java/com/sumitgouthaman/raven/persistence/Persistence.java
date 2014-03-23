package com.sumitgouthaman.raven.persistence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.sumitgouthaman.raven.R;
import com.sumitgouthaman.raven.utils.RandomStrings;

import java.util.HashSet;
import java.util.Set;

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

    public static String getSecretUsername(Context context) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        String secretUsername = shared.getString("SECRET_USERNAME", null);
        if (secretUsername != null) {
            return secretUsername;
        } else {
            secretUsername = new RandomStrings(20).nextString();
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("SECRET_USERNAME", secretUsername);
            editor.commit();
            return secretUsername;
        }
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

    public static String getDebugMessages(Context context) {
        SharedPreferences shared = context.getSharedPreferences(key, 0);
        String debugMessages = shared.getString("DEBUG_MESSAGES", null);
        String debugTemp = "";
        if (debugMessages != null)
            debugTemp = debugMessages;
        return debugTemp;
    }

    public static void addDebugMessages(Context context, String dm) {
        SharedPreferences shared = context.getSharedPreferences(key, 0);
        String debugMessages = "";
        try {
            debugMessages = shared.getString("DEBUG_MESSAGES", "");
        } catch (ClassCastException cce) {
            debugMessages = "";
        }
        debugMessages += "\n\n" + (dm);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("DEBUG_MESSAGES", debugMessages);
        editor.commit();
    }

    public static void clearDebugMessages(Context context) {
        SharedPreferences shared = context.getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("DEBUG_MESSAGES", "");
        editor.commit();
    }

    public static int getLastVersionNumber(Context context) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return shared.getInt("LAST_VERSION", Integer.MIN_VALUE);
    }

    public static void setLastVersionNumber(Context context, int lastVersionNumber) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt("LAST_VERSION", lastVersionNumber);
        editor.commit();
    }
}
