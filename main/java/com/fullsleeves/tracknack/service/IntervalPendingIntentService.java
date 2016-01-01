package com.fullsleeves.tracknack.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.fullsleeves.tracknack.Constants;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by welcome on 12/30/2015.
 */
public class IntervalPendingIntentService extends IntentService {
    private static final String TAG = IntervalPendingIntentService.class.getSimpleName();

    public IntervalPendingIntentService() {
        super("ActivityRecognitionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            //Extract the result from the Response
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity detectedActivity = result.getMostProbableActivity();

            //Get the Confidence and Name of Activity
            int confidence = detectedActivity.getConfidence();
            String mostProbableName = getActivityName(detectedActivity.getType());

            //Fire the intent with activity name & confidence
            Intent i = new Intent("ImActive");
            i.setAction("tracknack.detectedactivity");
            i.putExtra("ACTIVITY", mostProbableName);

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

}
