package com.sample.weatherreport;

import android.app.ActionBar;

import android.app.FragmentTransaction;
import android.app.Fragment;


/**
 * Created by surabhiv on 9/3/2015.
 */
public class TabListener implements ActionBar.TabListener {
    Fragment fragment;
    public TabListener(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        //ft.replace(R.id.fragment_container, fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
