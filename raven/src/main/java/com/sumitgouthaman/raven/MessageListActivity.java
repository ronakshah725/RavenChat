package com.sumitgouthaman.raven;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ListView;

import com.sumitgouthaman.raven.listadapters.MessageListAdapter;
import com.sumitgouthaman.raven.models.MessageListItem;

public class MessageListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
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
            View rootView = inflater.inflate(R.layout.fragment_message_list, container, false);

            MessageListItem[] messages = new MessageListItem[5];
            for (int i = 0; i < 5; i++) {
                messages[i] = new MessageListItem();
                messages[i].contactName = "Contact " + (i + 1);
                messages[i].messagePreview = "This is a long message sent by contact " + (i + 1);
            }

            ListView messagesList = (ListView)rootView.findViewById(R.id.listView_MessageList);
            MessageListAdapter mla = new MessageListAdapter(getActivity(), messages);
            messagesList.setAdapter(mla);

            return rootView;
        }
    }

}
