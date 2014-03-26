package com.sumitgouthaman.raven;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sumitgouthaman.raven.models.MessageTypes;
import com.sumitgouthaman.raven.utils.MessageDispatcher;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DispatchGCMMessage extends ActionBarActivity {

    EditText regIdField;
    EditText messageField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_gcmmessage);

        regIdField = (EditText) findViewById(R.id.editText_debug_regId);
        messageField = (EditText) findViewById(R.id.editText_debug_message);

        Button sendButton = (Button) findViewById(R.id.button_debug_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(regIdField.getText().toString(), messageField.getText().toString());
            }
        });
    }

    public void sendMessage(final String regId, final String message) {
        new AsyncTask() {
            private ProgressDialog progressDialog;

            @Override
            protected Object doInBackground(Object[] objects) {
                return MessageDispatcher.dispatchMessage(getApplicationContext(), regId, MessageTypes.DEBUG_MESSAGE, message);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(DispatchGCMMessage.this);
                progressDialog.setMessage("Sending GCM message");
                progressDialog.show();

            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                progressDialog.dismiss();
                String res = (String) o;
                Toast.makeText(DispatchGCMMessage.this, res, Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dispatch_gcmmessage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
