package com.sumitgouthaman.raven;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sumitgouthaman.raven.persistence.Persistence;


public class SelfDestructingMessageCompose extends ActionBarActivity {

    String targetSecretUsername;
    String targetUsername;
    String mySecretUsername;
    String targetRegId;

    TextView contactNameField;
    EditText messageField;
    RadioGroup durationRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sefl_destructing_message_compose);

        Intent thisIntent = getIntent();
        targetSecretUsername = thisIntent.getStringExtra("secretUsername");
        targetUsername = thisIntent.getStringExtra("username");
        mySecretUsername = Persistence.getSecretUsername(this);
        targetRegId = thisIntent.getStringExtra("registrationID");

        contactNameField = (TextView) findViewById(R.id.textView_contactName);
        durationRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_destroy_after);
        messageField = (EditText) findViewById(R.id.editText_message);

        contactNameField.setText(targetUsername);

        Button sendButton = (Button) findViewById(R.id.button_sendMessage);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String receiverSecretUsername = targetSecretUsername;
                final String mySecretUsernameStr = mySecretUsername;
                final String message = messageField.getText().toString();
                if (message.trim().equals("")) {
                    Toast.makeText(SelfDestructingMessageCompose.this, R.string.empty_message, Toast.LENGTH_SHORT).show();
                    return;
                }
                int duration;
                switch (durationRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.radioButton_20sec:
                        duration = 20;
                        break;
                    case R.id.radioButton_1min:
                        duration = 1 * 60;
                        break;
                    case R.id.radioButton_2min:
                        duration = 2 * 60;
                        break;
                    default:
                        duration = 20;
                }
                final int destroyAfter = duration;

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sefl_destructing_message_compose, menu);
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

}