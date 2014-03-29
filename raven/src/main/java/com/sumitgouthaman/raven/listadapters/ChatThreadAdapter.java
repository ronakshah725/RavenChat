package com.sumitgouthaman.raven.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sumitgouthaman.raven.R;
import com.sumitgouthaman.raven.models.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sumit on 16/3/14.
 */
public class ChatThreadAdapter extends ArrayAdapter<Message> {
    private final Context context;
    private final Message[] messages;

    public ChatThreadAdapter(Context context, Message[] messages) {
        super(context, R.layout.listitem_receivedmessage, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if (messages[position].receivedMessage) {
            rowView = inflater.inflate(R.layout.listitem_receivedmessage, parent, false);
        } else {
            rowView = inflater.inflate(R.layout.listitem_sentmessage, parent, false);
        }
        TextView messageField = (TextView) rowView.findViewById(R.id.textView_message);
        //TextView timestampField = (TextView) rowView.findViewById(R.id.textView_timestamp);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(messages[position].timestamp);

        messageField.setText(messages[position].messageText);
        //timestampField.setText(formatter.format(calendar.getTime()));

        return rowView;
    }
}
