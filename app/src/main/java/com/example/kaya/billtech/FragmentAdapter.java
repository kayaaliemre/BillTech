package com.example.kaya.billtech;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by cagdas on 16.11.2017.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    Context mContext;
    private ArrayList<Fragment> fragmentList;

    public FragmentAdapter(FragmentManager supportFragmentManager, ArrayList<Fragment> fragmentList) {
        super(supportFragmentManager);
        this.fragmentList = fragmentList;
        
    }



    @Override
    public Fragment getItem(int position) {
        return this.fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
