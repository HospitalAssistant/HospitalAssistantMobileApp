package com.fmi.fcmtestapp.util;

public final class TimeUtil {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        long now = System.currentTimeMillis();
        if (time <= 0 || time > now) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "току-що";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "преди минута";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "преди " + diff / MINUTE_MILLIS + " минути";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "преди час";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "преди " + diff / HOUR_MILLIS + " часа";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "вчера";
        } else {
            return "преди " + diff / DAY_MILLIS + " дни";
        }
    }
}
