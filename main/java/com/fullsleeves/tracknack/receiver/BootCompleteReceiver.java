package com.fullsleeves.tracknack.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fullsleeves.tracknack.service.LocationService;

/**
 * Created by welcome on 12/31/2015.
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG=BootCompleteReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"Boot Completed!");
        Intent i=new Intent(context, LocationService.class);
        context.startService(i);
    }
}
