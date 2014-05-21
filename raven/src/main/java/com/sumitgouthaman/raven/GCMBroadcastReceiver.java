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

import java.util.UUID;

public class GCMBroadcastReceiver extends BroadcastReceiver {
    boolean inBackground;
    ChatThreadActivity chatThreadActivity;
    Context context;

    public GCMBroadcastReceiver() {
        inBackground = true;
    }

    public GCMBroadcastReceiver(boolean notify, ChatThreadActivity cta) {
        this.inBackground = notify;
        this.chatThreadActivity = cta;
    }

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
                    if (inBackground) {
                        SimpleNotificationMaker.sendNotification(context, "Raven: DEBUG MESSAGE", recdMessageText, contentIntent);
                    } else {
                        SimpleSoundNotificationMaker.sendNotification(context);
                    }
                } else if (recdMessageType == MessageTypes.PAIRING_MESSAGE) {
                    try {
                        JSONObject recdObject = new JSONObject(recdMessageText);
                        String cipherText = recdObject.optString("cipherText", null);
                        if(cipherText!=null){
                            //Encryption is supported
                            //Left to be handled....
                        }else{
                            //Not an encrypted connection
                            JSONObject pairingRequest = new JSONObject(recdMessageText);
                            Contact newContact = new Contact();
                            newContact.username = pairingRequest.getString("username");
                            newContact.secretUsername = pairingRequest.getString("secretUsername");
                            newContact.registrationID = pairingRequest.getString("registrationID");
                            Persistence.addNewContact(context, newContact);
                            Intent chatThreadIntent = new Intent(context, ChatThreadActivity.class);
                            chatThreadIntent.putExtra("secretUsername", newContact.secretUsername);
                            chatThreadIntent.putExtra("registrationID", newContact.registrationID);
                            chatThreadIntent.putExtra("contactName", newContact.username);
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                                    chatThreadIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                            String notifMessage = String.format(context.getString(R.string.contact_has_paired), newContact.username);
                            SimpleNotificationMaker.sendNotification(context, context.getString(R.string.contact_added), notifMessage, contentIntent);
                        }
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
                            if (!inBackground && chatThreadActivity != null && chatThreadActivity.secretUsername.equals(secretUsername)) {
                                SimpleSoundNotificationMaker.sendNotification(context);
                                chatThreadActivity.refreshThread();
                            } else {
                                SimpleNotificationMaker.sendNotification(context, context.getString(R.string.notif_title_messag_recd), username + ": " + message.messageText, contentIntent);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (recdMessageType == MessageTypes.REMOVE_CONTACT) {
                    String toBeRemoved = recdMessageText;
                    Contact user = Persistence.getUser(context, toBeRemoved);
                    if (user != null) {
                        String username = user.username;
                        String notifMessage = String.format(context.getString(R.string.contact_has_unpaired), username);
                        Intent messageListIntent = new Intent(context, MessageListActivity.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                                messageListIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        SimpleNotificationMaker.sendNotification(context, context.getString(R.string.contact_unpaired), notifMessage, contentIntent);
                        Persistence.clearContact(context, toBeRemoved);
                    }
                } else if (recdMessageType == MessageTypes.REGISTRATION_UPDATE) {
                    try {
                        JSONObject registrationUpdateOb = new JSONObject(recdMessageText);
                        String contactSecretUsername = registrationUpdateOb.getString("secretUsername");
                        String newRegId = registrationUpdateOb.getString("registrationID");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (recdMessageType == MessageTypes.SELF_DESTRUCTING_MESSAGE) {
                    try {
                        JSONObject selfDestructingMessageOb = new JSONObject(recdMessageText);
                        String contactSecretUsername = selfDestructingMessageOb.getString("secretUsername");
                        String message = selfDestructingMessageOb.getString("message");
                        int destryAfter = selfDestructingMessageOb.getInt("destroyAfter");
                        Intent selfDestructingMessageIntent = new Intent(context, SelfDestructingMessageDisplay.class);
                        selfDestructingMessageIntent.putExtra("secretUsername", contactSecretUsername);
                        selfDestructingMessageIntent.putExtra("destroyAfter", destryAfter);
                        selfDestructingMessageIntent.putExtra("message", message);
                        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                                selfDestructingMessageIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        SimpleNotificationMaker.sendNotification(context, context.getString(R.string.read_once_message), context.getString(R.string.can_read_only_once), contentIntent, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (recdMessageType == MessageTypes.USERNAME_UPDATE) {
                    try {
                        JSONObject updateUsernameMessageOb = new JSONObject(recdMessageText);
                        String contactSecretUsername = updateUsernameMessageOb.getString("secretUsername");
                        String newUsername = updateUsernameMessageOb.getString("username");
                        String oldUsername = Persistence.getUser(context, contactSecretUsername).username;
                        Persistence.updateContactUsername(context, contactSecretUsername, newUsername);
                        Contact user = Persistence.getUser(context, contactSecretUsername);
                        if (user != null) {
                            String username = user.username;
                            String userRegID = user.registrationID;
                            Intent chatThreadIntent = new Intent(context, ChatThreadActivity.class);
                            chatThreadIntent.putExtra("secretUsername", contactSecretUsername);
                            chatThreadIntent.putExtra("registrationID", userRegID);
                            chatThreadIntent.putExtra("contactName", username);
                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                                    chatThreadIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                            //String message = String.format(context.getString(R.string.contact_is_renamed), oldUsername, newUsername);
                            String message = oldUsername + " " + context.getString(R.string.is_now) + " " + newUsername;
                            SimpleNotificationMaker.sendNotification(context, context.getString(R.string.contact_renamed), message, contentIntent, true);
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
