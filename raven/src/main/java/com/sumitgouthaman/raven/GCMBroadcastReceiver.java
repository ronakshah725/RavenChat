package com.sumitgouthaman.raven;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sumitgouthaman.raven.models.MessageTypes;
import com.sumitgouthaman.raven.persistence.Persistence;

public class GCMBroadcastReceiver extends BroadcastReceiver {
    public GCMBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Persistence.addDebugMessages(context, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Persistence.addDebugMessages(context, "Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                int recdMessageType = -1;
                try {
                    recdMessageType = Integer.parseInt(extras.getString("messageType"));
                } catch (NumberFormatException nfe) {
                    Persistence.addDebugMessages(context, "Coundn't parse message type");
                }
                String recdMessageText = extras.getString("messageText");
                if (recdMessageType == MessageTypes.DEBUG_MESSAGE) {
                    Persistence.addDebugMessages(context, "Received: " + "Message of type: " + recdMessageType + " => " + recdMessageText);
                }
            }
        }
    }
}
