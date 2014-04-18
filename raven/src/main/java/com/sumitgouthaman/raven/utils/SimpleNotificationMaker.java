package com.sumitgouthaman.raven.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.sumitgouthaman.raven.R;

import java.util.UUID;

/**
 * Created by sumit on 27/3/14.
 */
public class SimpleNotificationMaker {
    public static void sendNotification(Context context, String title, String msg, PendingIntent contentIntent) {
        sendNotification(context, title, msg, contentIntent, false);
    }

    public static void sendNotification(Context context, String title, String msg, PendingIntent contentIntent, boolean unique) {
        int NOTIFICATION_ID = 0;
        if (unique) {
            NOTIFICATION_ID = UUID.randomUUID().hashCode();
        }
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setLights(0xffff0000, 300, 10000)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
