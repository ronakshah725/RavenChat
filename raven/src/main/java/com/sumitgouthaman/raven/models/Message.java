package com.sumitgouthaman.raven.models;

/**
 * Created by sumit on 16/3/14.
 */

/**
 * Class to hold properties of a message
 */
public class Message {
    public String messageText;
    public long timestamp;
    public boolean receivedMessage; //true for received messages, false for sent messages
}
