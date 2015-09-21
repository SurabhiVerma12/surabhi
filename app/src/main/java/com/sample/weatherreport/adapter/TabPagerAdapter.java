package com.sample.weatherreport.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sample.weatherreport.DailyWeatherActivity;
import com.sample.weatherreport.LocationFragment;
import com.sample.weatherreport.ShowMapFragment;
import com.sample.weatherreport.WeatherActivityFragment;

/**
 * Created by surabhiv on 9/16/2015.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new LocationFragment();
            case 1:
                return new WeatherActivityFragment();
            case 2:
                return new DailyWeatherActivity();
            case 3:
                return new ShowMapFragment();
        }
        return null;

    }

    @Override
    public int getCount() {
        return 4;
    }

}