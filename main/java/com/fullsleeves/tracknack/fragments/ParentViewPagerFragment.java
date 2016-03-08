package com.fullsleeves.tracknack.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fullsleeves.tracknack.R;

/**
 * Created by welcome on 1/8/2016.
 */
public class ParentViewPagerFragment extends Fragment {

    public static final String TAG = ParentViewPagerFragment.class.getName();

    public static ParentViewPagerFragment newInstance() {
        return new ParentViewPagerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.parent_viewpager_fragment, container, false);

        ViewPager viewPager = (ViewPager) root.findViewById(R.id.viewPager);
        /** Important: Must use the child FragmentManager or you will see side effects. */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        return root;
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            if(position==0) {
                args.putInt(UploadsFragment.POSITION_KEY, position);
                return UploadsFragment.newInstance(args);
            }else {
                args.putInt(UploadsFragment.POSITION_KEY, position);
                return PendingUploadsFragment.newInstance(args);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return "Uploads";
                case 1: return "Pending Uploads";
                default: return "";
            }
        }

    }
}
