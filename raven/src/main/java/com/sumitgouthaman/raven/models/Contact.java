package com.sumitgouthaman.raven.models;

/**
 * Created by sumit on 24/3/14.
 */

/**
 * Class to hold properties of a contact
 */
public class Contact {
    public String username;
    public String secretUsername;
    public String registrationID;
    public Message lastMessage = null; //null os interpreted as no message
    public String encKey = null; //null is interpreted as no encryption available
}
