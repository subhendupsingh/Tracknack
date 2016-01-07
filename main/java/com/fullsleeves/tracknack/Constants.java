package com.fullsleeves.tracknack;

/**
 * Created by welcome on 12/31/2015.
 */
public class Constants {

    public enum DETECTED_ACTIVITY{
        IN_VEHICLE,ON_BICYCLE,ON_FOOT,WALKING,STILL,TILTING,RUNNING,UNKNOWN
    }

    public static final String IMAGE_DIRECTORY_NAME="Tracknack";
    public static final String FILE_UPLOAD_URL="http://192.168.1.21:8080/tracknack-rest-server/form/save";
    public static final int TYPE_FORM_FIELD=1;
    public static final int TYPE_IMAGE_FIELD=2;
}
