package com.sample.weatherreport;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by surabhiv on 9/1/2015.
 */
public class PlacePreference {

    SharedPreferences prefs;

    public PlacePreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity(){
        return prefs.getString("city", "DELHI, IN");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }
}
