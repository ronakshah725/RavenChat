package com.sumitgouthaman.raven;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sumitgouthaman.raven.persistence.Persistence;


public class SettingsActivity extends ActionBarActivity {

    EditText usernameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        usernameField = (EditText) findViewById(R.id.editText_settings_username);
        String username = Persistence.getUsername(this);
        usernameField.setText(username);

        Button clearContactsButton = (Button) findViewById(R.id.button_clearContacts);
        clearContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Persistence.clearContacts(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, getString(R.string.contacts_cleared), Toast.LENGTH_SHORT).show();
            }
        });

        Button cleanupButton = (Button)findViewById(R.id.button_cleanup);
        cleanupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Persistence.cleanup(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, getString(R.string.ran_cleanup), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings_save) {

            String username = usernameField.getText().toString().trim();
            if (username.equals("")) {
                Toast.makeText(this, getResources().getString(R.string.message_username_blank), Toast.LENGTH_SHORT).show();
            } else {
                Persistence.setUsername(this, username);
                Toast.makeText(this, getResources().getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();
                finish();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
