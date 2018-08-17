package com.beidousat.libbns.model;

import android.support.v4.app.Fragment;

/**
 * Created by J Wong on 2016/6/14.
 */
public class FragmentModel {

    public Fragment fragment;
    public String tag;

    public FragmentModel(Fragment fragment) {
        this.fragment = fragment;
        this.tag = fragment.getClass().getName();
    }
}
