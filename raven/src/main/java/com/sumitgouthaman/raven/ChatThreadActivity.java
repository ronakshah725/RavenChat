package com.sumitgouthaman.raven;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumitgouthaman.raven.listadapters.ChatThreadAdapter;
import com.sumitgouthaman.raven.models.Message;
import com.sumitgouthaman.raven.models.MessageTypes;
import com.sumitgouthaman.raven.persistence.Persistence;
import com.sumitgouthaman.raven.utils.MessageDispatcher;

import org.json.JSONException;
import org.json.JSONObject;


public class ChatThreadActivity extends ActionBarActivity {

    public static String secretUsername;
    private static String targetRegistrationID;
    private static String contactName;
    private static String prepopulatedMessage;
    private static Message[] messages;
    private static ListView messagesList;
    private static ChatThreadAdapter cta;

    GCMBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_thread);
        secretUsername = getIntent().getStringExtra("secretUsername");
        targetRegistrationID = getIntent().getStringExtra("registrationID");
        contactName = getIntent().getStringExtra("contactName");
        prepopulatedMessage = getIntent().getStringExtra("prepopulatedMessage");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        receiver = new GCMBroadcastReceiver(false, this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.google.android.c2dm.intent.RECEIVE");
        filter.setPriority(2);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_thread, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == 0) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshThread() {
        messages = Persistence.getMessages(this, secretUsername);
        cta = new ChatThreadAdapter(this, messages);
        messagesList.setAdapter(cta);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chat_thread, container, false);

            messages = Persistence.getMessages(getActivity(), secretUsername);

            messagesList = (ListView) rootView.findViewById(R.id.listView_chatthread);
            cta = new ChatThreadAdapter(getActivity(), messages);
            messagesList.setAdapter(cta);

            final EditText newMessageField = (EditText) rootView.findViewById(R.id.editText_newMessageText);
            final ImageButton newMessageSendButton = (ImageButton) rootView.findViewById(R.id.button_newMessageSend);

            if (prepopulatedMessage != null) {
                newMessageField.setText(prepopulatedMessage);
            }

            newMessageSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String messageText = newMessageField.getText().toString().trim();
                    if (messageText.equals("")) {
                        return;
                    }
                    if (messageText.length() > 1000) {
                        Toast.makeText(getActivity(), R.string.message_length_exceeded, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String mySecretUsername = Persistence.getSecretUsername(getActivity());
                    final String toRegId = targetRegistrationID;
                    new AsyncTask() {
                        private ProgressDialog progressDialog;

                        @Override
                        protected Object doInBackground(Object[] objects) {
                            JSONObject messageJSON = new JSONObject();
                            String messageStr = "";
                            try {
                                messageJSON.put("secretUsername", mySecretUsername);
                                messageJSON.put("messageText", messageText);
                                messageStr = messageJSON.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return MessageDispatcher.dispatchMessage(getActivity(), toRegId, MessageTypes.MORNAL_MESSAGE, messageStr);
                        }

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
//                            progressDialog = new ProgressDialog(getActivity());
//                            progressDialog.setMessage(getString(R.string.sending));
//                            progressDialog.show();
                            //Toast.makeText(getActivity(), getString(R.string.sending), Toast.LENGTH_SHORT).show();
                            newMessageField.setText("");
                            TextView sendingMessage = (TextView) getActivity().findViewById(R.id.textView_sendingStatus);
                            sendingMessage.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            //progressDialog.dismiss();
                            //Toast.makeText(getActivity(), getString(R.string.sent), Toast.LENGTH_SHORT).show();
                            Message message = new Message();
                            message.messageText = messageText;
                            message.timestamp = System.currentTimeMillis();
                            message.receivedMessage = false;
                            Persistence.addMessage(getActivity(), secretUsername, message);
                            messages = Persistence.getMessages(getActivity(), secretUsername);
                            cta = new ChatThreadAdapter(getActivity(), messages);
                            messagesList.setAdapter(cta);
                            TextView sendingMessage = (TextView) getActivity().findViewById(R.id.textView_sendingStatus);
                            sendingMessage.setVisibility(View.INVISIBLE);
                        }
                    }.execute(null, null, null);
                }
            });

            ActionBar ab = getActivity().getActionBar();
            ab.setTitle(contactName);
            return rootView;
        }
    }
}
