package com.fullsleeves.tracknack.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fullsleeves.tracknack.R;
import com.fullsleeves.tracknack.entities.Media;
import com.fullsleeves.tracknack.utils.ImageLoader;

import java.util.List;

/**
 * Created by welcome on 1/8/2016.
 */
public class UploadItemsAdapter extends BaseAdapter {

    List<Media> mediaItemsList;
    ImageLoader imageLoader;
    Context context;

    public UploadItemsAdapter(List<Media> mediaItemsList,Context context){
        this.mediaItemsList=mediaItemsList;
        this.context=context;
        imageLoader=new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return mediaItemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView==null){
            LayoutInflater inflater=LayoutInflater.from(parent.getContext());
            convertView=inflater.inflate(R.layout.upload_list_item,parent,false);
            holder=new ViewHolder();
            holder.firstLine=(TextView) convertView.findViewById(R.id.first_line);
            holder.secondLine=(TextView) convertView.findViewById(R.id.second_line);
            holder.listViewImage=(ImageView) convertView.findViewById(R.id.list_view_image);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }

        if(mediaItemsList!=null && mediaItemsList.size()>0) {
            Media media = mediaItemsList.get(position);
            imageLoader.DisplayImage(media.getUri(),holder.listViewImage);
            holder.firstLine.setText(media.getTitle());
            holder.secondLine.setText(media.getDescription());
        }

        return convertView;
    }

    private static class ViewHolder {
        public TextView firstLine,secondLine;
        public ImageView listViewImage;
    }
}
