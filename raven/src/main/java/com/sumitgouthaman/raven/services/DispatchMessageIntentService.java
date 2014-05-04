package com.sumitgouthaman.raven.services;

import android.app.IntentService;
import android.content.Intent;

import com.sumitgouthaman.raven.R;
import com.sumitgouthaman.raven.models.Message;
import com.sumitgouthaman.raven.persistence.Persistence;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DispatchMessageIntentService extends IntentService {

    public DispatchMessageIntentService() {
        super("DispatchMessageIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String regId = intent.getStringExtra("registrationID");
            int messageType = intent.getIntExtra("messageType", -1);
            String message = intent.getStringExtra("message");
            String secretUsername = intent.getStringExtra("targetSecretUsername");
            String messageText = intent.getStringExtra("messageText");

            String result = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("https://android.googleapis.com/gcm/send");
                httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.addHeader("Authorization", "key=" + getString(R.string.api_key));
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                JSONObject data = new JSONObject();
                data.put("messageType", messageType);
                data.put("messageText", message);
                nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
                nameValuePairs.add(new BasicNameValuePair("registration_id", regId));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, "UTF-8");
            } catch (Exception ie) {
                ie.printStackTrace();
            }

            Message messageOb = new Message();
            messageOb.messageText = messageText;
            messageOb.timestamp = System.currentTimeMillis();
            messageOb.receivedMessage = false;
            if(result==null){
                messageOb.timestamp = 0l;
            }
            Persistence.addMessage(this, secretUsername, messageOb);
        }
    }
}
