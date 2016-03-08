package com.fullsleeves.tracknack.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fullsleeves.tracknack.R;

/**
 * Created by welcome on 1/8/2016.
 */
public class ChildFragment extends Fragment implements View.OnClickListener {

    public static final String POSITION_KEY = "FragmentPositionKey";
    private int position;

    public static ChildFragment newInstance(Bundle args) {
        ChildFragment fragment = new ChildFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        position = getArguments().getInt(POSITION_KEY);

        View root = inflater.inflate(R.layout.signature_fragment, container, false);

        return root;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "Clicked Position: " + position, Toast.LENGTH_LONG).show();
    }
}
