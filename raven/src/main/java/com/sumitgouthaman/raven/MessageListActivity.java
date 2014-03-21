package com.sumitgouthaman.raven;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sumitgouthaman.raven.listadapters.MessageListAdapter;
import com.sumitgouthaman.raven.models.MessageListItem;
import com.sumitgouthaman.raven.persistence.Persistence;
import com.sumitgouthaman.raven.utils.CheckPlayServices;

public class MessageListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        if (!CheckPlayServices.check(this)) {
            Toast.makeText(this, getString(R.string.play_services_not_supported), Toast.LENGTH_LONG).show();
            finish();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        initialCheck();
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_addContact) {
            Intent intent = new Intent(this, AddContactActivity.class);
            startActivity(intent);
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
            View rootView = inflater.inflate(R.layout.fragment_message_list, container, false);

            MessageListItem[] messages = new MessageListItem[10];
            for (int i = 0; i < 10; i++) {
                messages[i] = new MessageListItem();
                messages[i].contactName = "Contact " + (i + 1);
                messages[i].messagePreview = "This is a long message sent by contact " + (i + 1);
            }

            ListView messagesList = (ListView) rootView.findViewById(R.id.listView_MessageList);
            MessageListAdapter mla = new MessageListAdapter(getActivity(), messages);
            messagesList.setAdapter(mla);

            return rootView;
        }
    }

    public void initialCheck() {
        String username = Persistence.getUsername(this);
        if (username == null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Choose username");
            alert.setMessage(getResources().getString(R.string.message_username_not_set));
            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String chosenUsername = input.getText().toString();
                    Persistence.setUsername(MessageListActivity.this, chosenUsername);
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                }
            });
            alert.show();
        }
    }

}
