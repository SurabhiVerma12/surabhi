package com.sample.weatherreport.parser;

import android.util.Log;

import com.sample.weatherreport.DailyForecast;
import com.sample.weatherreport.LocationTracking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by surabhiv on 9/3/2015.
 */
public class JSONDailyWeatherParser {

    public static List<DailyForecast> getForecastWeather(String data){
        List<DailyForecast> daysForecast = new ArrayList<DailyForecast>();
        LocationTracking loc = new LocationTracking();
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(data);
            JSONObject city = jObj.getJSONObject("city");
            loc.setCountry(getString("country", city));
            loc.setCity(getString("name", city));
            loc.setCod(getString("cod", jObj));
            JSONArray jArr = jObj.getJSONArray("list");
            for (int i=1; i < jArr.length(); i++) {

                DailyForecast df = new DailyForecast();
                df.weather.location = loc;
                JSONObject jDayForecast = jArr.getJSONObject(i);
                df.timestamp = jDayForecast.getLong("dt");
                JSONObject jTempObj = jDayForecast.getJSONObject("temp");
                df.forecastTemp.day = (float) jTempObj.getDouble("day");
                df.forecastTemp.min = (float) jTempObj.getDouble("min");
                df.forecastTemp.max = (float) jTempObj.getDouble("max");
                df.weather.currentCondition.setPressure((float) jDayForecast.getDouble("pressure"));
                df.weather.currentCondition.setHumidity((float) jDayForecast.getDouble("humidity"));
                JSONArray jWeatherArr = jDayForecast.getJSONArray("weather");
                JSONObject jWeatherObj = jWeatherArr.getJSONObject(0);
                df.weather.currentCondition.setWeatherId(getInt("id", jWeatherObj));
                df.weather.currentCondition.setDescr(getString("description", jWeatherObj));
                df.weather.currentCondition.setCondition(getString("main", jWeatherObj));
                df.weather.currentCondition.setIcon(getString("icon", jWeatherObj));
                daysForecast.add(df);
            }
        } catch (JSONException e) {
            try {
                DailyForecast df = new DailyForecast();
                loc.setCod(getString("cod", jObj));
                df.weather.location = loc;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        return daysForecast;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

}

