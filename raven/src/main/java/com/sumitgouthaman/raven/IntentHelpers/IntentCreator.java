package com.sumitgouthaman.raven.IntentHelpers;

import android.content.Context;
import android.content.Intent;

import com.sumitgouthaman.raven.ChatThreadActivity;

/**
 * Created by sumit on 27/5/14.
 */
public class IntentCreator {
    public static Intent getChatThreadIntent(Context context, String secretUsername, String prepopulatedMessage){
        Intent chatThreadIntent = new Intent(context, ChatThreadActivity.class);
        chatThreadIntent.putExtra("secretUsername", secretUsername);
        chatThreadIntent.putExtra("prepopulatedMessage", prepopulatedMessage);
        return chatThreadIntent;
    }
    public static Intent getChatThreadIntent(Context context, String secretUsername){
        return getChatThreadIntent(context, secretUsername, null);
    }
}
