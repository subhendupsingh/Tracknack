package com.fullsleeves.tracknack.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fullsleeves.tracknack.Constants;
import com.fullsleeves.tracknack.utils.SharedPreferencesManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

/**
 * Created by welcome on 12/30/2015.
 */
public class IntervalPendingIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = IntervalPendingIntentService.class.getSimpleName();

    public IntervalPendingIntentService() {
        super("ActivityRecognitionIntentService");
    }

    private SharedPreferencesManager prefs;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onHandleIntent(Intent intent) {
        prefs=SharedPreferencesManager.getInstance(this);
        if(ActivityRecognitionResult.hasResult(intent)) {
            //Extract the result from the Response
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity detectedActivity = result.getMostProbableActivity();

            //Get the Confidence and Name of Activity
            int confidence = detectedActivity.getConfidence();
            String mostProbableName = getActivityName(detectedActivity.getType());
            /*Toast.makeText(this,mostProbableName,Toast.LENGTH_SHORT).show();*/


            //Fire the intent with activity name & confidence
            Intent i = new Intent("ImActive");
            i.setAction("tracknack.detectedactivity");
            i.putExtra("ACTIVITY", mostProbableName);
            prefs.setDetectedActivity(mostProbableName);

            Log.d(TAG, "Most Probable Name : " + mostProbableName);
            Log.d(TAG, "Confidence : " + confidence);

            //Send Broadcast to be listen in MainActivity
            this.sendBroadcast(i);

        }else {
            Log.d(TAG, "Intent had no data returned");
        }
    }

    //Get the activity name
    private String getActivityName(int type) {
        switch (type)
        {
            case DetectedActivity.IN_VEHICLE:
                return Constants.DETECTED_ACTIVITY.IN_VEHICLE.name();
            case DetectedActivity.ON_BICYCLE:
                return Constants.DETECTED_ACTIVITY.ON_BICYCLE.name();
            case DetectedActivity.ON_FOOT:
                return Constants.DETECTED_ACTIVITY.ON_FOOT.name();
            case DetectedActivity.WALKING:
                return Constants.DETECTED_ACTIVITY.WALKING.name();
            case DetectedActivity.STILL:
                return Constants.DETECTED_ACTIVITY.STILL.name();
            case DetectedActivity.TILTING:
                return Constants.DETECTED_ACTIVITY.TILTING.name();
            case DetectedActivity.RUNNING:
                return Constants.DETECTED_ACTIVITY.RUNNING.name();
            case DetectedActivity.UNKNOWN:
                return Constants.DETECTED_ACTIVITY.UNKNOWN.name();
        }
        return "N/A";
    }

    private void setUpLocationClientIfNeeded() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(ActivityRecognition.API)
                    .build();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
