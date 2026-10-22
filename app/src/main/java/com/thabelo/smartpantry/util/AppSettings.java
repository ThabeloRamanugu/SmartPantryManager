package com.thabelo.smartpantry.util;

import android.content.Context;
import android.content.SharedPreferences;

// I keep the user's settings here. SharedPreferences saves them on the phone,
// so they are still there after the app is closed.
public final class AppSettings {

    private static final String FILE = "smart_pantry_settings";
    private static final String KEY_ALERTS = "alerts_enabled";
    private static final String KEY_DAYS = "soon_days";

    private AppSettings() { }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    // is the red "expiring soon" highlight turned on? (default: yes)
    public static boolean alertsEnabled(Context context) {
        return prefs(context).getBoolean(KEY_ALERTS, true);
    }

    public static void setAlertsEnabled(Context context, boolean value) {
        prefs(context).edit().putBoolean(KEY_ALERTS, value).apply();
    }

    // how many days count as "soon" (default: 3)
    public static int soonDays(Context context) {
        return prefs(context).getInt(KEY_DAYS, 3);
    }

    public static void setSoonDays(Context context, int days) {
        prefs(context).edit().putInt(KEY_DAYS, days).apply();
    }
}