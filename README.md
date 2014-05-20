RavenChat
=========
RavenChat or "Raven" is an attempt to build a server-less fully encrypted chat app.
###Screenshots
![Contact List](/screenshots/merged.png "Screenshots")
###Contacts Setup Process
It involves users adding each others to their contacts using a QR code based mechanism (or NFC if available). This pairing ideally happens when both users are physically together.
Once the pairing is done they can communicate like any other chat app.

###Important!
In order to successfully run the project, you need to supply the SENDER_ID and API_KEY for using GCM.
Follow the instructions given [here](http://developer.android.com/google/gcm/gs.html) to create the same.
Add them as string resources named "sender_id" and "api_key" to your project.

###License
This work is licensed under the [MIT License](LICENSE.md).
