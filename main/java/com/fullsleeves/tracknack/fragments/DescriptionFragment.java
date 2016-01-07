package com.fullsleeves.tracknack.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fullsleeves.tracknack.Constants;
import com.fullsleeves.tracknack.R;
import com.fullsleeves.tracknack.utils.BackgroundUploader;
import com.fullsleeves.tracknack.utils.MultipartEntity;
import com.fullsleeves.tracknack.utils.TracknackUtils;

import java.util.ArrayList;
import java.util.List;


public class DescriptionFragment extends Fragment {

    private Button button;
    private String filePath;
    private EditText titleEditText,descriptionEditText;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle=getArguments();
        filePath=bundle.getString("filePath");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.description_fragment,container,false);
        button=(Button) v.findViewById(R.id.submit);
        titleEditText=(EditText) v.findViewById(R.id.title);
        descriptionEditText=(EditText) v.findViewById(R.id.description);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<MultipartEntity> formFields=new ArrayList<MultipartEntity>();
                String title=titleEditText.getText().toString();
                String description=descriptionEditText.getText().toString();
                String imei=TracknackUtils.getDeviceImei(getActivity());

                if(null!=title && !title.isEmpty()){
                    MultipartEntity entity=new MultipartEntity();
                    entity.setType(Constants.TYPE_FORM_FIELD);
                    entity.setParamName("title");
                    entity.setParamValue(title);
                    formFields.add(entity);
                }

                if(null!=description && !description.isEmpty()){
                    MultipartEntity entity=new MultipartEntity();
                    entity.setType(Constants.TYPE_FORM_FIELD);
                    entity.setParamName("description");
                    entity.setParamValue(description);
                    formFields.add(entity);
                }

                if(null!=imei && !imei.isEmpty()){
                    MultipartEntity entity=new MultipartEntity();
                    entity.setType(Constants.TYPE_FORM_FIELD);
                    entity.setParamName("imei");
                    entity.setParamValue(imei);
                    formFields.add(entity);
                }

                if(null!=filePath && !filePath.isEmpty()){
                    MultipartEntity entity=new MultipartEntity();
                    entity.setType(Constants.TYPE_IMAGE_FIELD);
                    entity.setFileName("file");
                    entity.setFilePath(filePath);
                    Log.d("FILEPATH",filePath);
                    formFields.add(entity);
                }

                new BackgroundUploader(getActivity(),formFields,true).execute();

            }
        });
        return v;
    }
}
