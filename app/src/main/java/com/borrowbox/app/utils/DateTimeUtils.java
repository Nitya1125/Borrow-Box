package com.borrowbox.app.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateTimeUtils {

    private static final long SECOND_MILLIS = 1000;
    private static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;

    private DateTimeUtils() {}

    public static String formatTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 0) {
            return "Just now";
        }
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return (diff / MINUTE_MILLIS) + "m ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return (diff / HOUR_MILLIS) + "h ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else if (diff < 7 * DAY_MILLIS) {
            return (diff / DAY_MILLIS) + "d ago";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            return sdf.format(new Date(timestamp));
        }
    }

    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        return sdf.format(new Date(timestamp));
    }

    public static String formatMonthYear(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        return sdf.format(new Date(timestamp));
    }

    public static long calculateDueTimestamp(long startTimestamp, int durationDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTimestamp);
        cal.add(Calendar.DAY_OF_YEAR, durationDays);
        return cal.getTimeInMillis();
    }

    public static int getDaysRemaining(long dueTimestamp) {
        long diff = dueTimestamp - System.currentTimeMillis();
        if (diff <= 0) return 0;
        return (int) Math.ceil((double) diff / DAY_MILLIS);
    }

    public static int getDaysOverdue(long dueTimestamp) {
        long diff = System.currentTimeMillis() - dueTimestamp;
        if (diff <= 0) return 0;
        return (int) Math.ceil((double) diff / DAY_MILLIS);
    }

    public static boolean isOverdue(long dueTimestamp) {
        return System.currentTimeMillis() > dueTimestamp;
    }

    public static String getGreetingForCurrentTime() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 4 && hour < 12) {
            return "Good morning,";
        } else if (hour >= 12 && hour < 17) {
            return "Good afternoon,";
        } else {
            return "Good evening,";
        }
    }

    public static String getTimeOfDayGreeting() {
        return getGreetingForCurrentTime();
    }
}
