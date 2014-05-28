package com.sumitgouthaman.raven.listadapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sumitgouthaman.raven.R;
import com.sumitgouthaman.raven.models.Message;
import com.sumitgouthaman.raven.utils.TimestampFormatter;

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
        TextView timestampField = (TextView) rowView.findViewById(R.id.textView_timestamp);

        messageField.setText(messages[position].messageText);
        timestampField.setText(TimestampFormatter.getAppropriateFormat(messages[position].timestamp));
        if (messages[position].timestamp == 0l) {
            timestampField.setText(context.getString(R.string.not_sent));
            timestampField.setTextColor(context.getResources().getColor(R.color.red));
            timestampField.setTypeface(null, Typeface.ITALIC);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, R.string.message_not_sent, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (messages[position].timestamp == -1l) {
            timestampField.setText(context.getString(R.string.dispatched));
            timestampField.setTypeface(null, Typeface.ITALIC);
        }

        return rowView;
    }
}
