package com.fullsleeves.tracknack.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.fullsleeves.tracknack.Constants;
import com.fullsleeves.tracknack.R;
import com.fullsleeves.tracknack.entities.Media;
import com.fullsleeves.tracknack.fragments.ParentViewPagerFragment;
import com.fullsleeves.tracknack.receiver.NetworkChangeReceiver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by welcome on 1/6/2016.
 */
public class BackgroundUploader extends AsyncTask<Void, Integer, Void> implements DialogInterface.OnCancelListener {

    private ProgressDialog progressDialog;
    private String url;
    private File file;
    private Context context;
    private String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset="UTF-8";;
    private OutputStream outputStream;
    private PrintWriter writer;
    private List<MultipartEntity> formFields;
    private boolean showProgressDialog=false;
    private boolean isOffline;
    private Media workerMedia;
    private int mediaId;
    private DataSource dataSource;
    private FragmentTransaction ft;

    public BackgroundUploader(Context context, List<MultipartEntity> formFields,boolean isOffline,boolean showProgressDialog) {
        this.context=context;
        this.formFields=formFields;
        boundary = System.currentTimeMillis()+"";
        this.showProgressDialog=showProgressDialog;
        this.isOffline=isOffline;
        dataSource=new DataSource(context);
    }

    public BackgroundUploader(Context context, List<MultipartEntity> formFields,boolean isOffline,boolean showProgressDialog,FragmentTransaction ft) {
        this.context=context;
        this.formFields=formFields;
        boundary = System.currentTimeMillis()+"";
        this.showProgressDialog=showProgressDialog;
        this.isOffline=isOffline;
        dataSource=new DataSource(context);
        this.ft=ft;
    }

    public BackgroundUploader(Context context, List<MultipartEntity> formFields,boolean isOffline,Media media) {
        this.context=context;
        this.formFields=formFields;
        boundary = System.currentTimeMillis()+"";
        this.isOffline=isOffline;
        dataSource=new DataSource(context);
        this.mediaId=media.getId();
    }

    @Override
    protected void onPreExecute() {
        if(showProgressDialog) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(true);
            //progressDialog.setMax(100);
            progressDialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... v) {

        if(NetworkChangeReceiver.getInstance().isConnectingToInternet(context) && !isCancelled()) {
            URL url = null;
            try {
                url = new URL(Constants.FILE_UPLOAD_URL);
                httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setUseCaches(false);
                httpConn.setDoOutput(true); // indicates POST method
                httpConn.setDoInput(true);
                httpConn.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + boundary);
                httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
                httpConn.setRequestProperty("Test", "Bonjour");
                outputStream = httpConn.getOutputStream();
                writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                        true);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                workerMedia=new Media();
                if (formFields != null && formFields.size() > 0) {
                    for (MultipartEntity field : formFields) {
                        if (field.getType() == Constants.TYPE_FORM_FIELD) {
                            addFormField(field.getParamName(), field.getParamValue());
                            if(field.getParamName().equalsIgnoreCase("title")){
                                workerMedia.setTitle(field.getParamValue());
                            }
                            if(field.getParamName().equalsIgnoreCase("description")){
                                workerMedia.setDescription(field.getParamValue());
                            }
                        }

                        if (field.getType() == Constants.TYPE_IMAGE_FIELD) {
                            String filePath = field.getFilePath();
                            if (null != filePath && !filePath.isEmpty()) {
                                File file = new File(field.getFilePath());
                                addFilePart(field.getFileName(), file);
                                workerMedia.setUri(filePath);
                            }
                        }
                    }
                }

                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            cancel(true);
        }


        return null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        Media media=new Media();
        if (formFields != null && formFields.size() > 0) {
            for (MultipartEntity field : formFields) {
                if (field.getType() == Constants.TYPE_FORM_FIELD) {
                    if(field.getParamName().equalsIgnoreCase("title")){
                        media.setTitle(field.getParamValue());
                    }
                    if(field.getParamName().equalsIgnoreCase("description")){
                        media.setDescription(field.getParamValue());
                    }
                }

                if (field.getType() == Constants.TYPE_IMAGE_FIELD) {
                    String filePath = field.getFilePath();
                    media.setUri(filePath);
                }
            }
        }
        media.setIsUploadCompleted(0);
        TracknackUtils.saveForFuture(media, context);
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setProgress((int) (progress[0]));
    }

    @Override
    protected void onPostExecute(Void v) {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }

        if(!isOffline) {
            Toast.makeText(context, "Media Uploaded Successfully!", Toast.LENGTH_SHORT).show();

            if(ft!=null){
                ft.replace(R.id.content_frame, new ParentViewPagerFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        }

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        cancel(true);
        dialog.dismiss();
    }

    public void addFormField(String name, String value) {
        if(writer!=null) {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                    .append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=" + charset).append(
                    LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value).append(LINE_FEED);
            writer.flush();
        }
    }

    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {

        if(writer!=null) {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append(
                    "Content-Disposition: form-data; name=\"" + fieldName
                            + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append(
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            int progress = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                progress += bytesRead;
                if(showProgressDialog) {
                    publishProgress(progress);
                }
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
        }
    }

    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<String>();

        if(writer!=null) {
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            Log.d("REQUEST", LINE_FEED);
            writer.close();
        }

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            if(workerMedia!=null) {
                if (isOffline) {
                    dataSource.open();
                    dataSource.removeFromOfflineUpload(mediaId);
                    dataSource.close();
                } else {
                    workerMedia.setIsUploadCompleted(1);
                    dataSource.open();
                    dataSource.addForOfflineUpload(workerMedia);
                    dataSource.close();
                }
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }

        return response;
    }

}
