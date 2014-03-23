package com.sumitgouthaman.raven;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sumitgouthaman.raven.persistence.Persistence;

import java.util.Set;


public class DebugActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        TextView registrationID = (TextView) findViewById(R.id.textView_RegistrationID);
        String regID = Persistence.getRegistrationID(this);
        if (regID != null) {
            registrationID.setText(regID);
        }
        String debugMessages = Persistence.getDebugMessages(this);
        debugMessages = "START:" + debugMessages + "\n:END";
        TextView debugMessagesField = (TextView) findViewById(R.id.textView_DebugMessages);
        debugMessagesField.setText(debugMessages);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.debug, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_clearDebugMessages) {
            Persistence.clearDebugMessages(this);
            Intent intent = new Intent(this, DebugActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
