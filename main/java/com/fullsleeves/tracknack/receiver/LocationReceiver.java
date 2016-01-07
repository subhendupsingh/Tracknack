package com.fullsleeves.tracknack.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fullsleeves.tracknack.R;
import com.fullsleeves.tracknack.utils.SharedPreferencesManager;
import com.fullsleeves.tracknack.utils.TracknackUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

/**
 * Created by welcome on 12/30/2015.
 */
public class LocationReceiver extends BroadcastReceiver {

    private static final String TAG="LocationReceiver";
    static Random random = new Random();
    private SharedPreferencesManager prefs;


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        prefs=SharedPreferencesManager.getInstance(context);

        if (bundle == null) {
            return;
        }

        Location location = bundle.getParcelable("com.google.android.location.LOCATION");

        if (location == null) {
            return;
        }

        Toast.makeText(context," lat: " + location.getLatitude() + " lon: " + location.getLongitude(),Toast.LENGTH_SHORT).show();
        doGcmSendUpstreamMessage(context, location);
        Log.d("locationtesting", "accuracy: " + location.getAccuracy() + " lat: " + location.getLatitude() + " lon: " + location.getLongitude());

    }

    private void doGcmSendUpstreamMessage(final Context context,Location location) {
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        final String senderId = context.getString(R.string.gcm_defaultSenderId);
        final String msgId = getRandomMessageId();
        final String ttl = "";
        Long timestamp=System.currentTimeMillis() / 1000L;
        final Bundle data = new Bundle();
        String detectedActivity=prefs.getDetectedActivity();
        data.putString("latitude", String.valueOf(location.getLatitude()));
        data.putString("longitude",String.valueOf(location.getLongitude()));
        data.putString("address", TracknackUtils.getAddress(location, context));
        data.putString("deviceImei", TracknackUtils.getDeviceImei(context));
        data.putString("battery", TracknackUtils.getBatteryLevel(context));
        data.putString("timestamp", String.valueOf(timestamp.intValue()));
        if(detectedActivity!=null && !detectedActivity.isEmpty()){
            data.putString("activity", detectedActivity);
        }

        if (msgId.equals("")) {
            Toast.makeText(context, R.string.upstream_message_id_not_provided, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (ttl!=null && !ttl.isEmpty()) {
                        try {
                            gcm.send(senderId + "@gcm.googleapis.com", msgId,
                                    Long.parseLong(ttl), data);
                        } catch (NumberFormatException ex) {
                            Log.e(TAG,
                                    "Error sending upstream message: could not parse ttl", ex);
                            return "Error sending upstream message: could not parse ttl";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        gcm.send(senderId + "@gcm.googleapis.com", msgId, data);
                    }
                    Log.d(TAG, "Successfully sent upstream message");
                    return null;
                } catch (IOException ex) {
                    Log.e(TAG, "Error sending upstream message", ex);
                    return "Error sending upstream message:" + ex.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    Toast.makeText(context,
                            "send message failed: " + result,
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    public String getRandomMessageId() {
        return "m-" + Long.toString(random.nextLong());
    }
}
