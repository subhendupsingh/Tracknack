package com.fullsleeves.tracknack.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by welcome on 1/2/2016.
 */
public class SharedPreferencesManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    int PRIVATE_MODE=0;
    String PREF_NAME="TRACKNACK";
    private static final String KEY_DETECTED_ACTIVITY="detectedActivity";
    private static SharedPreferencesManager instance;

    public SharedPreferencesManager(Context context){
        this.context=context;
        prefs=context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor=prefs.edit();
    }

    public static SharedPreferencesManager getInstance(Context context){
        if(instance==null){
            instance=new SharedPreferencesManager(context);
        }

        return instance;
    }

    public void setDetectedActivity(String detectedActivity){
        editor.putString(KEY_DETECTED_ACTIVITY,detectedActivity);
        editor.commit();
    }

    public String getDetectedActivity(){
        return prefs.getString(KEY_DETECTED_ACTIVITY,null);
    }
}
