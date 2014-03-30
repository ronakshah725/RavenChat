package com.sumitgouthaman.raven.persistence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.sumitgouthaman.raven.R;
import com.sumitgouthaman.raven.models.Contact;
import com.sumitgouthaman.raven.models.Message;
import com.sumitgouthaman.raven.utils.RandomStrings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sumit on 15/3/14.
 */
public class Persistence {

    private static final String key = "RAVEN";
    private static final String[] persistenceKeys = {"USERNAME", "SECRET_USERNAME", "REGISTRATION_ID", "DEBUG_MESSAGES", "LAST_VERSION", "CONTACTS"};

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

    public static void addNewContact(Context context, Contact newContact) {
        SharedPreferences shared = context.getSharedPreferences(key, 0);
        String contactsStr = shared.getString("CONTACTS", "[]");
        try {
            JSONArray contactsArr = new JSONArray(contactsStr);
            JSONObject newContactOb = new JSONObject();
            newContactOb.put("username", newContact.username);
            newContactOb.put("secretUsername", newContact.secretUsername);
            newContactOb.put("registrationID", newContact.registrationID);
            contactsArr.put(newContactOb);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("CONTACTS", contactsArr.toString());
            editor.commit();
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public static Contact[] getContacts(Context context) {
        SharedPreferences shared = context.getSharedPreferences(key, 0);
        String contactsStr = shared.getString("CONTACTS", "[]");
        try {
            JSONArray contactsArr = new JSONArray(contactsStr);
            Contact[] contacts = new Contact[contactsArr.length()];
            for (int i = 0; i < contacts.length; i++) {
                contacts[i] = new Contact();
                JSONObject contactOb = contactsArr.getJSONObject(i);
                contacts[i].username = contactOb.getString("username");
                contacts[i].secretUsername = contactOb.getString("secretUsername");
                contacts[i].registrationID = contactOb.getString("registrationID");
            }
            return contacts;
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return new Contact[0];
    }

    public static Contact getUser(Context context, String secretUsername) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        Contact[] contacts = getContacts(context);
        for (Contact c : contacts) {
            if (c.secretUsername.equals(secretUsername)) {
                return c;
            }
        }
        return null;
    }

    public static void clearContacts(Context context) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Contact[] contacts = getContacts(context);
        for (Contact c : contacts) {
            clearContactMessages(context, c.secretUsername);
        }
        editor.putString("CONTACTS", "[]");
        editor.commit();
    }

    public static void clearContact(Context context, String secretUsername) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        String contactsStr = shared.getString("CONTACTS", "[]");
        try {
            JSONArray contactsArr = new JSONArray(contactsStr);
            JSONArray newContactsArr = new JSONArray();
            for (int i = 0; i < contactsArr.length(); i++) {
                JSONObject contactOb = contactsArr.getJSONObject(i);
                String targetSecretUsername = contactOb.getString("secretUsername");
                if (!targetSecretUsername.equals(secretUsername)) {
                    newContactsArr.put(contactOb);
                }
            }
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("CONTACTS", newContactsArr.toString());
            editor.commit();
        } catch (JSONException je) {
            je.printStackTrace();
        }
        cleanup(context);
    }

    public static Message[] getMessages(Context context, String secretUsername) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        try {
            JSONArray messagesArr = new JSONArray(shared.getString(secretUsername, "[]"));
            Message[] messages = new Message[messagesArr.length()];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = new Message();
                JSONObject messageOb = messagesArr.getJSONObject(i);
                messages[i].messageText = messageOb.getString("MESSAGE_TEXT");
                messages[i].receivedMessage = messageOb.getBoolean("RECD_MESSAGE");
                messages[i].timestamp = messageOb.getLong("TIMESTAMP");
            }
            return messages;
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return null;
    }

    public static void addMessage(Context context, String secretUsername, Message message) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        try {
            JSONArray messagesArr = new JSONArray(shared.getString(secretUsername, "[]"));
            JSONObject messageOb = new JSONObject();
            messageOb.put("MESSAGE_TEXT", message.messageText);
            messageOb.put("RECD_MESSAGE", message.receivedMessage);
            messageOb.put("TIMESTAMP", message.timestamp);
            messagesArr.put(messageOb);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(secretUsername, messagesArr.toString());
            editor.commit();
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public static void clearContactMessages(Context context, String secretUsername) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.remove(secretUsername);
        editor.commit();
    }

    public static String[] getAllKeys(Context context) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        String[] temp = shared.getAll().keySet().toArray(new String[0]);
        return temp;
    }

    public static void cleanup(Context context) {
        SharedPreferences shared = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        Contact[] contacts = getContacts(context);
        ArrayList<String> temp = new ArrayList<String>();
        for (Contact c : contacts) {
            temp.add(c.secretUsername);
        }
        String[] validSecrets = temp.toArray(new String[0]);
        String[] storedKeys = getAllKeys(context);
        temp.clear();
        String persistenceKeysStr = Arrays.toString(persistenceKeys);
        String validSecretsStr = Arrays.toString(validSecrets);
        for (String k : storedKeys) {
            if (!(persistenceKeysStr.contains(k) || validSecretsStr.contains(k))) {
                temp.add(k);
            }
        }
        for (String t : temp) {
            SharedPreferences.Editor editor = shared.edit();
            editor.remove(t);
            editor.commit();
        }
    }
}
