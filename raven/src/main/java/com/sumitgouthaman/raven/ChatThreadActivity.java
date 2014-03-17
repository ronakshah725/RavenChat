package com.sumitgouthaman.raven;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sumitgouthaman.raven.listadapters.ChatThreadAdapter;
import com.sumitgouthaman.raven.models.Message;


public class ChatThreadActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_thread);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
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

            Message[] messages = new Message[20];
            for (int i = 0; i < 20; i++) {
                messages[i] = new Message();
                if (i % 2 == 0) {
                    messages[i].messageText = "This is a received message....";
                    messages[i].timestamp = System.currentTimeMillis();
                    messages[i].receivedMessage = true;
                } else {
                    messages[i].messageText = "This is a sent message....";
                    messages[i].timestamp = System.currentTimeMillis();
                    messages[i].receivedMessage = false;
                }
            }

            ListView messagesList = (ListView) rootView.findViewById(R.id.listView_chatthread);
            ChatThreadAdapter cta = new ChatThreadAdapter(getActivity(), messages);
            messagesList.setAdapter(cta);

            return rootView;
        }
    }
}
