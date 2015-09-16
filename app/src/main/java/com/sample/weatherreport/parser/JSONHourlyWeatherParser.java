package com.sample.weatherreport.parser;

import com.sample.weatherreport.DailyForecast;
import com.sample.weatherreport.HourlyForecast;
import com.sample.weatherreport.LocationTracking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by surabhiv on 9/16/2015.
 */
public class JSONHourlyWeatherParser {

    public static List<HourlyForecast> getForecastWeather(String data){
        List<HourlyForecast> hourForecast = new ArrayList<HourlyForecast>();
        LocationTracking loc = new LocationTracking();
        JSONObject jObj = null;
        JSONObject city=null;
        try {
            jObj = new JSONObject(data);
            city = jObj.getJSONObject("city");
            //loc.setCod(getString("cod", city));
            JSONArray jArr = jObj.getJSONArray("list");
            for (int i=0; i < jArr.length(); i++) {
                HourlyForecast df = new HourlyForecast();
                //df.weather.location = loc;
                JSONObject jHourForecast = jArr.getJSONObject(i);
                JSONObject mainObj = jHourForecast.getJSONObject("main");
                df.weather.currentCondition.setPressure((float) mainObj.getDouble("pressure"));
                df.weather.currentCondition.setHumidity((float) mainObj.getDouble("humidity"));
                df.weather.temperature.setTemp((float) mainObj.getDouble("temp"));
                JSONArray jWeatherArr = jHourForecast.getJSONArray("weather");
                JSONObject jWeatherObj = jWeatherArr.getJSONObject(0);
                df.weather.currentCondition.setWeatherId(getInt("id", jWeatherObj));
                df.weather.currentCondition.setDescr(getString("description", jWeatherObj));
                df.weather.currentCondition.setCondition(getString("main", jWeatherObj));
                df.weather.currentCondition.setIcon(getString("icon", jWeatherObj));
                df.forecastTemp.dTime = getString("dt_txt", jHourForecast);
                hourForecast.add(df);
            }
        } catch (JSONException e) {
            try {
                HourlyForecast df = new HourlyForecast();
                loc.setCod(getString("cod", city));
                df.weather.location = loc;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        return hourForecast;
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
