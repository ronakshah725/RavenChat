package com.sumitgouthaman.raven.listadapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sumitgouthaman.raven.ChatThreadActivity;
import com.sumitgouthaman.raven.R;
import com.sumitgouthaman.raven.models.MessageListItem;

/**
 * Created by sumit on 10/3/14.
 */
public class MessageListAdapter extends ArrayAdapter<MessageListItem> {

    private final Context context;
    private final MessageListItem[] messageListItems;

    public MessageListAdapter(Context context, MessageListItem[] messageListItems) {
        super(context, R.layout.listitem_messagelist, messageListItems);
        this.context = context;
        this.messageListItems = messageListItems;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listitem_messagelist, parent, false);
        TextView contactNameField = (TextView) rowView.findViewById(R.id.textView_ContactName);
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
                context.startActivity(intent);
            }
        });
        return rowView;
    }
}
