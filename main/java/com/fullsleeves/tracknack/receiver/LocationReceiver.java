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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.IOException;
import java.util.Random;

/**
 * Created by welcome on 12/30/2015.
 */
public class LocationReceiver extends BroadcastReceiver {

    private static final String TAG="LocationReceiver";
    static Random random = new Random();


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (bundle == null) {
            return;
        }

        Location location = bundle.getParcelable("com.google.android.location.LOCATION");

        if (location == null) {
            return;
        }

        Toast.makeText(context," lat: " + location.getLatitude() + " lon: " + location.getLongitude(),Toast.LENGTH_SHORT).show();
        doGcmSendUpstreamMessage(context);
        Log.d("locationtesting", "accuracy: " + location.getAccuracy() + " lat: " + location.getLatitude() + " lon: " + location.getLongitude());

    }

    private void doGcmSendUpstreamMessage(final Context context) {
        final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        final String senderId = context.getString(R.string.gcm_defaultSenderId);
        final String msgId = getRandomMessageId();
        final String ttl = "";
        final Bundle data = new Bundle();
        data.putString("my_message", "Hello World");
        data.putString("my_action","SAY_HE'LLO");

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
