package com.fullsleeves.tracknack.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.fullsleeves.tracknack.entities.Media;
import com.fullsleeves.tracknack.utils.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enigma-pc on 3/1/16.
 */
public class DataSource {

    private SQLiteDatabase db;
    private SqliteHelper sqliteHelper;

    public DataSource(Context context){
        sqliteHelper = new SqliteHelper(context);
    }

    public void open() throws SQLiteException{
        db = sqliteHelper.getWritableDatabase();
    }

    public void close(){
        sqliteHelper.close();
    }

    public long addForOfflineUpload(Media media){
        long id = 0;
        try{
            ContentValues values = new ContentValues();
            values.put(SqliteHelper.KEY_URI,media.getUri());
            values.put(SqliteHelper.KEY_TITLE,media.getTitle());
            values.put(SqliteHelper.KEY_DESCRIPTION,media.getDescription());
            values.put(SqliteHelper.KEY_UPLOAD_FLAG,media.getIsUploadCompleted());
            id = db.insert(SqliteHelper.TABLE_MEDIA, null, values);
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    public void removeFromOfflineUpload(int id){
        String querry = "update "+ SqliteHelper.TABLE_MEDIA + " set "+ SqliteHelper.KEY_UPLOAD_FLAG +" = '1' where "+SqliteHelper.KEY_ID+"='"+id+"'";
        try{
            db.execSQL(querry);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public ArrayList<Media> getOfflineListFromDb(){
        ArrayList<Media> arr = new ArrayList<Media>();
        String querry = "select * from "+ SqliteHelper.TABLE_MEDIA+" where "+SqliteHelper.KEY_UPLOAD_FLAG+"='0'";
        Cursor c = db.rawQuery(querry, null);
                try{
                    if(c.getCount() > 0){
                        c.moveToFirst();
                        int i = 0;
                        while (!c.isAfterLast()){
                            Media media=new Media();
                            media.setId(c.getInt(c.getColumnIndex(SqliteHelper.KEY_ID)));
                            media.setUri(c.getString(c.getColumnIndex(SqliteHelper.KEY_URI)));
                            media.setTitle(c.getString(c.getColumnIndex(SqliteHelper.KEY_TITLE)));
                            media.setDescription(c.getString(c.getColumnIndex(SqliteHelper.KEY_DESCRIPTION)));
                            media.setIsUploadCompleted(c.getInt(c.getColumnIndex(SqliteHelper.KEY_UPLOAD_FLAG)));
                            arr.add(media);
                            i++;
                            c.moveToNext();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(c!=null)
                        c.close();
                }

        return arr;
    }


    public List<Media> getUploadedImagesList(){
        List<Media> mediaList=new ArrayList<Media>();
        String query="select * from "+SqliteHelper.TABLE_MEDIA+" where "+SqliteHelper.KEY_UPLOAD_FLAG+"='1' order by id desc";
        Cursor c = db.rawQuery(query, null);
        try{
            if(c.getCount() > 0){
                c.moveToFirst();
                int i = 0;
                while (!c.isAfterLast()){
                    Media media=new Media();
                    media.setUri(c.getString(c.getColumnIndex(SqliteHelper.KEY_URI)));
                    media.setTitle(c.getString(c.getColumnIndex(SqliteHelper.KEY_TITLE)));
                    media.setDescription(c.getString(c.getColumnIndex(SqliteHelper.KEY_DESCRIPTION)));
                    media.setIsUploadCompleted(c.getInt(c.getColumnIndex(SqliteHelper.KEY_UPLOAD_FLAG)));
                    mediaList.add(media);
                    i++;
                    c.moveToNext();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(c!=null)
                c.close();
        }

        return mediaList;
    }
}
