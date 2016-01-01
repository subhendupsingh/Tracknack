package com.fullsleeves.tracknack.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fullsleeves.tracknack.Constants;
import com.fullsleeves.tracknack.receiver.LocationReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    protected static final String TAG = "location-updates-sample";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    // Flag that indicates if a request is underway.
    private boolean mInProgress;

    private Boolean servicesAvailable = false;

    private PendingIntent locationIntent;

    private BroadcastReceiver receiver;

    private String detectedActivity=null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInProgress = false;
        servicesAvailable = servicesConnected();
        mLocationRequest=createLocationRequest(detectedActivity);
        setUpLocationClientIfNeeded();

        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals("tracknack.detectedactivity")){
                    detectedActivity = intent.getExtras().getString("ACTIVITY");
                    mLocationRequest=createLocationRequest(detectedActivity);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("tracknack.detectedactivity");
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(!servicesAvailable || mGoogleApiClient.isConnected() || mInProgress) {
            return START_STICKY;
        }

        setUpLocationClientIfNeeded();

        if(!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting() && !mInProgress) {
            mInProgress = true;
            mGoogleApiClient.connect();
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        mInProgress = false;
        if(servicesAvailable && mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationIntent);
            mGoogleApiClient.disconnect();
        }

        super.onDestroy();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "inside onConnected");
        Intent intent = new Intent(this, LocationReceiver.class);
        locationIntent = PendingIntent.getBroadcast(getApplicationContext(), 14872, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent activityIntent=PendingIntent.getService(getApplicationContext(),0,new Intent(this,IntervalPendingIntentService.class),PendingIntent.FLAG_CANCEL_CURRENT);
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 1, 0) == PackageManager.PERMISSION_GRANTED
                || checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,1,0) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "inside checkperm");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationIntent);
        }

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient,0,activityIntent);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected LocationRequest createLocationRequest(String detectedActivity) {
        LocationRequest mLocationRequest = new LocationRequest();
        if(detectedActivity==null) {
            Log.d(TAG,"UNKNOWN HIGH ACCURACY MODE ON!");
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }else if(detectedActivity.equalsIgnoreCase(Constants.DETECTED_ACTIVITY.STILL.name())
                || detectedActivity.equalsIgnoreCase(Constants.DETECTED_ACTIVITY.TILTING.name())
                ||detectedActivity.equalsIgnoreCase(Constants.DETECTED_ACTIVITY.UNKNOWN.name())){
            Log.d(TAG,"STILL BALANCED POWER MODE ON!");
            //mLocationRequest.setInterval(30*1000);
            //mLocationRequest.setFastestInterval(15*1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
        }else if(detectedActivity.equalsIgnoreCase(Constants.DETECTED_ACTIVITY.ON_BICYCLE.name())
                || detectedActivity.equalsIgnoreCase(Constants.DETECTED_ACTIVITY.ON_FOOT.name())
                ||detectedActivity.equalsIgnoreCase(Constants.DETECTED_ACTIVITY.WALKING.name())){
            Log.d(TAG,"WALKING BALANCED POWER MODE ON!");
            mLocationRequest.setInterval(15*1000);
            mLocationRequest.setFastestInterval(10*1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }else if(detectedActivity.equalsIgnoreCase(Constants.DETECTED_ACTIVITY.RUNNING.name())
                || detectedActivity.equalsIgnoreCase(Constants.DETECTED_ACTIVITY.IN_VEHICLE.name())){
            Log.d(TAG,"VEHICLE HIGH ACCURACY MODE ON!");
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        return mLocationRequest;
    }

    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            return true;
        } else {

            return false;
        }
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
    public void onLocationChanged(Location location) {
        Log.d("Location received****",""+location.getLatitude());
        /*Intent i=new Intent();
        i.setAction("com.fullsleeves.location.intent");
        i.putExtra("location",location);
        sendBroadcast(i);*/
    }


}
