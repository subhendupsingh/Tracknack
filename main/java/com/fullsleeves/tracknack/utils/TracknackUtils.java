package com.fullsleeves.tracknack.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.fullsleeves.tracknack.entities.Media;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by welcome on 1/2/2016.
 */
public class TracknackUtils {

    public static String getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        Float battery;

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return String.valueOf(50.0f);
        }

        battery= ((float)level / (float)scale) * 100.0f;
        return String.valueOf(battery.intValue());
    }

    public static String getAddress(Location location,Context context){
        StringBuilder finalAddress=new StringBuilder();
        Geocoder geocoder=new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(addresses!=null && addresses.size()>0){
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                if(null!=address && !address.isEmpty()){
                    finalAddress.append(address);
                    finalAddress.append(",");
                }
                if(null!=city && !city.isEmpty()){
                    finalAddress.append(city);
                    finalAddress.append(",");
                }
                if(null!=state && !state.isEmpty()){
                    finalAddress.append(state);
                    finalAddress.append(",");
                }
                if(null!=country && !country.isEmpty()){
                    finalAddress.append(country);
                    finalAddress.append(",");
                }
                if(null!=postalCode && !postalCode.isEmpty()){
                    finalAddress.append(postalCode);
                    finalAddress.append(",");
                }
                if(null!=knownName && !knownName.isEmpty()){
                    finalAddress.append(knownName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalAddress.toString();
    }

    public static String getDeviceImei(Context context){
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }

    public static void saveForFuture(Media media,Context context){
        DataSource ds = new DataSource(context);
        long id = 0l;
        try{
            ds.open();
            id = ds.addForOfflineUpload(media);
            Log.e("MA:saveForFuture:id", "" + id);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            ds.close();
        }
        if(id == -1){
            Toast.makeText(context, "Sqlite insertion error", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Image added for future upload", Toast.LENGTH_SHORT).show();
        }
    }

}
