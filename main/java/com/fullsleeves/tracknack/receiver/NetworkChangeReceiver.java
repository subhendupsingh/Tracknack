package com.fullsleeves.tracknack.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.fullsleeves.tracknack.Constants;
import com.fullsleeves.tracknack.entities.Media;
import com.fullsleeves.tracknack.utils.BackgroundUploader;
import com.fullsleeves.tracknack.utils.DataSource;
import com.fullsleeves.tracknack.utils.MultipartEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enigma-pc on 3/1/16.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    private Context context;

    public static NetworkChangeReceiver NetworkChangeReceiver;
    public static NetworkChangeReceiver getInstance() {
        if (NetworkChangeReceiver == null) {
            NetworkChangeReceiver = new NetworkChangeReceiver();
        }
        return NetworkChangeReceiver;
    }

    public NetworkChangeReceiver() {
        NetworkChangeReceiver = this;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("isConnectingToInternet:",""+isConnectingToInternet(context));
        if(isConnectingToInternet(context))
            uploadOfflineImages();

    }

    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet(Context context){
        this.context = context;
        if(context!=null) {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null){
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            return true;
                        }

            }
            return false;
        }
        return false;
    }

    public void uploadOfflineImages(){
        if(context!=null) {
            DataSource ds = new DataSource(context);
            try{
                ds.open();
                ArrayList<Media> arr = ds.getOfflineListFromDb();
                for (Media media : arr){
                    List<MultipartEntity> formFields=new ArrayList<MultipartEntity>();
                    if(media.getUri()!=null && !media.getUri().isEmpty()) {
                        MultipartEntity entity = new MultipartEntity();
                        entity.setType(Constants.TYPE_IMAGE_FIELD);
                        entity.setFileName("file");
                        entity.setFilePath(media.getUri());
                        formFields.add(entity);
                    }

                    if(media.getTitle()!=null && !media.getTitle().isEmpty()) {
                        MultipartEntity entity = new MultipartEntity();
                        entity.setType(Constants.TYPE_FORM_FIELD);
                        entity.setParamName("title");
                        entity.setParamValue(media.getTitle());
                        formFields.add(entity);
                    }

                    if(media.getDescription()!=null && !media.getDescription().isEmpty()) {
                        MultipartEntity entity = new MultipartEntity();
                        entity.setType(Constants.TYPE_FORM_FIELD);
                        entity.setParamName("description");
                        entity.setParamValue(media.getDescription());
                        formFields.add(entity);
                    }
                    new BackgroundUploader(context,formFields,false).execute();
                   // ds.removeFromOfflineUpload(temp);
                    Toast.makeText(context, "Successfully uploaded:", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                ds.close();
            }
        }
    }
}
