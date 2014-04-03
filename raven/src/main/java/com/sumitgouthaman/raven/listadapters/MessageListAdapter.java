package com.sumitgouthaman.raven.listadapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sumitgouthaman.raven.ChatThreadActivity;
import com.sumitgouthaman.raven.R;
import com.sumitgouthaman.raven.models.Message;
import com.sumitgouthaman.raven.models.MessageListItem;
import com.sumitgouthaman.raven.models.MessageTypes;
import com.sumitgouthaman.raven.persistence.Persistence;
import com.sumitgouthaman.raven.utils.MessageDispatcher;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sumit on 10/3/14.
 */
public class MessageListAdapter extends ArrayAdapter<MessageListItem> {

    private final Context context;
    private final MessageListItem[] messageListItems;
    private final String mySecretUsername;
    private String prepopulatedMessage = null;

    public MessageListAdapter(Context context, MessageListItem[] messageListItems, String prepopulatedMessage) {
        this(context, messageListItems);
        this.prepopulatedMessage = prepopulatedMessage;
    }

    public MessageListAdapter(Context context, MessageListItem[] messageListItems) {
        super(context, R.layout.listitem_messagelist, messageListItems);
        this.context = context;
        this.messageListItems = messageListItems;
        this.mySecretUsername = Persistence.getSecretUsername(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.listitem_messagelist, parent, false);
        final TextView contactNameField = (TextView) rowView.findViewById(R.id.textView_ContactName);
        TextView messagePreviewField = (TextView) rowView.findViewById(R.id.textView_messagePreview);

        contactNameField.setText(messageListItems[position].contactName);
        messagePreviewField.setText(messageListItems[position].messagePreview);

        if (messageListItems[position].unread) {
            contactNameField.setTypeface(null, Typeface.BOLD);
        }
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatThreadActivity.class);
                intent.putExtra("secretUsername", messageListItems[position].secretUsername);
                intent.putExtra("registrationID", messageListItems[position].registrationID);
                intent.putExtra("contactName", messageListItems[position].contactName);
                if (prepopulatedMessage != null) {
                    intent.putExtra("prepopulatedMessage", prepopulatedMessage);
                }
                context.startActivity(intent);
            }
        });
        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.delete_contact) + ": " + messageListItems[position].contactName + "?")
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new AsyncTask() {
                                    private ProgressDialog progressDialog;

                                    @Override
                                    protected Object doInBackground(Object[] objects) {
                                        return MessageDispatcher.dispatchMessage(context, messageListItems[position].registrationID, MessageTypes.REMOVE_CONTACT, mySecretUsername);
                                    }

                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        progressDialog = new ProgressDialog(context);
                                        progressDialog.setMessage(context.getString(R.string.sending_unpairing));
                                        progressDialog.show();

                                    }

                                    @Override
                                    protected void onPostExecute(Object o) {
                                        super.onPostExecute(o);
                                        progressDialog.dismiss();
                                        rowView.setOnClickListener(null);
                                        contactNameField.setTextColor(context.getResources().getColor(R.color.color_messagePreview));
                                        contactNameField.setPaintFlags(contactNameField.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                        Persistence.clearContact(context, messageListItems[position].secretUsername);
                                    }
                                }.execute(null, null, null);
                            }
                        })
                        .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setIcon(context.getResources().getDrawable(R.drawable.ic_launcher))
                        .setTitle(R.string.dialogTitle_delete_contact);
                // Create the AlertDialog object and return it
                builder.show();
                return true;
            }
        });
        return rowView;
    }
}
