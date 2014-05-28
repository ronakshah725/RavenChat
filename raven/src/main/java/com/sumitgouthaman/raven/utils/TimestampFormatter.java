package com.sumitgouthaman.raven.utils;

import android.text.format.DateUtils;

/**
 * Created by sumit on 4/4/14.
 */
public class TimestampFormatter {
    public static String getAppropriateFormat(long timestamp) {
        String str = "";
        long now = System.currentTimeMillis();
        str = "" + DateUtils.getRelativeTimeSpanString(timestamp, now, 0);
        return str;
    }
}
