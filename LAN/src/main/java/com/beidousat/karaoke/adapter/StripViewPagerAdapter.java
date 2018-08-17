package com.beidousat.karaoke.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.beidousat.karaoke.ui.fragment.BaseFragment;

import java.util.List;

/**
 * author: Hanson
 * date:   2017/3/30
 * describe:
 */

public class StripViewPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> mFragments;
    List<String> mTitles;

    public StripViewPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
