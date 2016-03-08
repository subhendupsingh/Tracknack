package com.fullsleeves.tracknack.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fullsleeves.tracknack.R;
import com.fullsleeves.tracknack.adapters.UploadItemsAdapter;
import com.fullsleeves.tracknack.entities.Media;
import com.fullsleeves.tracknack.fragments.dummy.DummyContent;
import com.fullsleeves.tracknack.utils.DataSource;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class UploadsFragment extends Fragment {

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

    public static UploadsFragment newInstance(Bundle args) {
        UploadsFragment fragment = new UploadsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static UploadsFragment newInstance(String param1, String param2) {
        UploadsFragment fragment = new UploadsFragment();
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
    public UploadsFragment() {
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
        List<Media> mediaList=dataSource.getUploadedImagesList();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
