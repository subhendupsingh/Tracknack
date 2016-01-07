package com.fullsleeves.tracknack.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.fullsleeves.tracknack.R;


public class ImagePreviewFragment extends Fragment {

    private ImageView previewImage;
    private String filePath;
    private Button nextButton;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle=this.getArguments();
        filePath=bundle.getString("filePath");
        previewMedia();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.image_preview_fragment, container, false);
        previewImage=(ImageView) v.findViewById(R.id.image_preview);
        nextButton=(Button) v.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("filePath",filePath);
                Fragment fragment=new DescriptionFragment();
                fragment.setArguments(bundle);
                FragmentTransaction txn=getFragmentManager().beginTransaction();
                txn.replace(R.id.content_frame,fragment);
                txn.setCustomAnimations(R.anim.fragment_slide_left_enter,R.anim.fragment_slide_left_exit);
                txn.addToBackStack(null);
                txn.commit();
            }
        });
        return  v;
    }

    private void previewMedia() {
        // Checking whether captured media is image or video

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        previewImage.setImageBitmap(bitmap);

    }

}
