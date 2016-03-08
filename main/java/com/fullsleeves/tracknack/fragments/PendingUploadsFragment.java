package com.fullsleeves.tracknack.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.fullsleeves.tracknack.R;
import com.fullsleeves.tracknack.adapters.UploadItemsAdapter;
import com.fullsleeves.tracknack.entities.Media;
import com.fullsleeves.tracknack.utils.DataSource;

import java.util.List;

/**
 * Created by welcome on 1/9/2016.
 */
public class PendingUploadsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String POSITION_KEY = "FragmentPositionKey";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    private DataSource dataSource;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private UploadItemsAdapter mAdapter;

    public static PendingUploadsFragment newInstance(Bundle args) {
        PendingUploadsFragment fragment = new PendingUploadsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static PendingUploadsFragment newInstance(String param1, String param2) {
        PendingUploadsFragment fragment = new PendingUploadsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PendingUploadsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        dataSource=new DataSource(getActivity());
        dataSource.open();
        List<Media> mediaList=dataSource.getOfflineListFromDb();
        dataSource.close();
        mAdapter = new UploadItemsAdapter(mediaList,getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_item_grid_fragment, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(R.id.upload_items);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        //mListView.setOnItemClickListener(this);

        return view;
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
}
