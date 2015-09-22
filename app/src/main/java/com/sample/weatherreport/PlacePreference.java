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

    String getTemp(){
        return prefs.getString("temp", "0.0");
    }

    void setTemp(String temp)
    {
        prefs.edit().putString("temp", temp).commit();
    }

    String getIcon(){
        return prefs.getString("icon", "10d");
    }

    void setIcon(String icon)
    {
        prefs.edit().putString("icon", icon).commit();
    }

    Boolean getFirstRun(){
        return prefs.getBoolean("firstRun", false);
    }

    void setFirstValue(Boolean firstValue)
    {
        prefs.edit().putBoolean("firstValue", firstValue).commit();
    }
    public Boolean getfirstValue(){
        return prefs.getBoolean("firstValue", true);
    }

    void setFirstRun(Boolean firstRun)
    {
        prefs.edit().putBoolean("firstRun", firstRun).commit();
    }

    float getLat(){
        return prefs.getFloat("lat", 0);
    }
    void setLat(float lat)
    {
        prefs.edit().putFloat("lat", lat).commit();
    }
    float getLong(){
        return prefs.getFloat("longitude",0);
    }
    void setLong(float longitude)
    {
        prefs.edit().putFloat("longitude",longitude).commit();
    }

}
