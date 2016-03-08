package com.fullsleeves.tracknack;

/**
 * Created by welcome on 12/31/2015.
 */
public class Constants {

    public enum DETECTED_ACTIVITY{
        IN_VEHICLE,ON_BICYCLE,ON_FOOT,WALKING,STILL,TILTING,RUNNING,UNKNOWN
    }

    public static final String IMAGE_DIRECTORY_NAME="Tracknack";
    public static final String FILE_UPLOAD_URL="http://23.94.21.18:4000/tracknack-rest-server/form/save";
    public static final int TYPE_FORM_FIELD=1;
    public static final int TYPE_IMAGE_FIELD=2;
    public static final int REQUEST_CODE_ASK_PERMISSIONS=10;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS=11;
}
