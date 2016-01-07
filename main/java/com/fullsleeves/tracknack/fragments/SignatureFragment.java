package com.fullsleeves.tracknack.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fullsleeves.tracknack.Constants;
import com.fullsleeves.tracknack.R;
import com.fullsleeves.tracknack.utils.DrawView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by welcome on 1/3/2016.
 */
public class SignatureFragment extends Fragment {

    private Button clear,next;
    private DrawView drawView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.signature_fragment,container,false);
        clear=(Button) v.findViewById(R.id.clear_button);
        next=(Button) v.findViewById(R.id.next_button);
        drawView=(DrawView) v.findViewById(R.id.draw_view);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.clear();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path=saveDrawing();
                Bundle bundle=new Bundle();
                bundle.putString("filePath",path);
                Fragment fragment=new DescriptionFragment();
                fragment.setArguments(bundle);
                FragmentTransaction txn=getFragmentManager().beginTransaction();
                txn.replace(R.id.content_frame,fragment);
                txn.setCustomAnimations(R.anim.fragment_slide_left_enter,R.anim.fragment_slide_left_exit);
                txn.addToBackStack(null);
                txn.commit();
            }
        });

        return v;

    }


    public String saveDrawing(){
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.IMAGE_DIRECTORY_NAME);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String path=mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        File file = new File(path);
        boolean success = false;


        if ( !file.exists() )
        {
            try {
                success = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream ostream = null;
        try
        {
            ostream = new FileOutputStream(file);

            System.out.println(ostream);
            Bitmap well = drawView.getBitmap();
            Bitmap save = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            Canvas now = new Canvas(save);
            now.drawRect(new Rect(0,0,320,480), paint);
            now.drawBitmap(well, new Rect(0,0,well.getWidth(),well.getHeight()), new Rect(0,0,320,480), null);

            if(save == null) {
                System.out.println("NULL bitmap save\n");
            }
            save.compress(Bitmap.CompressFormat.PNG, 100, ostream);

        }catch (NullPointerException e)
        {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Null error", Toast.LENGTH_SHORT).show();
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Toast.makeText(getActivity(), "File error", Toast.LENGTH_SHORT).show();
        }

        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(getActivity(), "IO error", Toast.LENGTH_SHORT).show();
        }

        return path;

    }




}
