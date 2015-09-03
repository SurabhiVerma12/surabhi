package com.sample.weatherreport;

import android.app.ActionBar;
import android.os.Bundle;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

public class TabHostActivity extends FragmentActivity implements LocationFragment.LocationValueListener{
    public static String globaLData;
    private ActionBar actionBar;
    ActionBar.Tab Tab1, Tab2;
    Fragment fragmentTab1 = new LocationFragment();
    Fragment fragmentTab2 = new WeatherActivityFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_tab_host);
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab1 = actionBar.newTab().setText("LOCATION");
        Tab2 = actionBar.newTab().setText("NOW");

        Tab1.setTabListener(new TabListener(fragmentTab1));
        Tab2.setTabListener(new TabListener(fragmentTab2));

        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);

        if(getIntent().getStringExtra("cityName")!=null){
           passData(getIntent().getStringExtra("cityName"));
        }
    }

    @Override
    public void passData(String data) {
        globaLData=data;
        actionBar.selectTab(Tab2);
    }

}
