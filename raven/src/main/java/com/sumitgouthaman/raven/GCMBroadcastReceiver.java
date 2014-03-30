package com.sumitgouthaman.raven;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sumitgouthaman.raven.models.Contact;
import com.sumitgouthaman.raven.models.Message;
import com.sumitgouthaman.raven.models.MessageTypes;
import com.sumitgouthaman.raven.persistence.Persistence;
import com.sumitgouthaman.raven.utils.SimpleNotificationMaker;
import com.sumitgouthaman.raven.utils.SimpleSoundNotificationMaker;

import org.json.JSONException;
import org.json.JSONObject;

public class GCMBroadcastReceiver extends BroadcastReceiver {
    boolean fromActivity;
    ChatThreadActivity chatThreadActivity;

    public GCMBroadcastReceiver() {
        fromActivity = true;
    }

    public GCMBroadcastReceiver(boolean notify, ChatThreadActivity cta) {
        this.fromActivity = notify;
        this.chatThreadActivity = cta;
    }

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        this.context = context;
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
                String recd = extras.getString("data");
                int recdMessageType = -1;
                String recdMessageText = "----";
                try {
                    JSONObject data = new JSONObject(recd);
                    recdMessageType = data.getInt("messageType");
                    recdMessageText = data.getString("messageText");
                } catch (JSONException nfe) {
                    Persistence.addDebugMessages(context, "Coundn't parse message type");
                }
                if (recdMessageType == MessageTypes.DEBUG_MESSAGE) {
                    Persistence.addDebugMessages(context, "Received: " + "Message of type: " + recdMessageType + " => " + recdMessageText);
                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                            new Intent(context, DebugActivity.class), 0);
                    if (fromActivity) {
                        SimpleNotificationMaker.sendNotification(context, "Raven: DEBUG MESSAGE", recdMessageText, contentIntent);
                    }else{
                        SimpleSoundNotificationMaker.sendNotification(context);
                    }
                } else if (recdMessageType == MessageTypes.PAIRING_MESSAGE) {
                    try {
                        JSONObject pairingRequest = new JSONObject(recdMessageText);
                        Contact newContact = new Contact();
                        newContact.username = pairingRequest.getString("username");
                        newContact.secretUsername = pairingRequest.getString("secretUsername");
                        newContact.registrationID = pairingRequest.getString("registrationID");
                        Persistence.addNewContact(context, newContact);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (recdMessageType == MessageTypes.MORNAL_MESSAGE) {
                    try {
                        JSONObject newMessage = new JSONObject(recdMessageText);
                        String secretUsername = newMessage.getString("secretUsername");
                        Message message = new Message();
                        message.messageText = newMessage.getString("messageText");
                        message.receivedMessage = true;
                        message.timestamp = System.currentTimeMillis();
                        Persistence.addMessage(context, secretUsername, message);
                        Contact user = Persistence.getUser(context, secretUsername);
                        if (user != null) {
                            String username = user.username;
                            String userRegID = user.registrationID;
                            Intent chatThreadIntent = new Intent(context, ChatThreadActivity.class);
                            chatThreadIntent.putExtra("secretUsername", secretUsername);
                            chatThreadIntent.putExtra("registrationID", userRegID);
                            chatThreadIntent.putExtra("contactName", username);
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                                    chatThreadIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                            if (fromActivity) {
                                SimpleNotificationMaker.sendNotification(context, context.getString(R.string.notif_title_messag_recd), username + ": " + message.messageText, contentIntent);
                            }else{
                                SimpleSoundNotificationMaker.sendNotification(context);
                                chatThreadActivity.refreshThread();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        abortBroadcast();
    }


}
