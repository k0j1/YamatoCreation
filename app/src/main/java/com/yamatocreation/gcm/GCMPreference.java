package com.yamatocreation.gcm;

import android.content.Context;
import android.content.SharedPreferences;

import com.yamatocreation.none.GCMFragment;

public class GCMPreference {
    //private static final String PREFERENCE_NAME = "KiiTest";
    private static final String PROPERTY_REG_ID = "GCMregId";

    /*
    static public String getRegistrationId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        return registrationId;
    }

    static public void setRegistrationId(Context context, String regId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.commit();
    }
    */

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        return registrationId;
    }

    public static void setRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.commit();
    }

    public static void clearRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.commit();
    }

    private static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(GCMFragment.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
}