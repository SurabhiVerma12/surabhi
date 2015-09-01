package com.sample.weatherreport;

import org.json.JSONObject;
import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by surabhiv on 9/1/2015.
 */
public class FetchWeather {

    private static String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    public static JSONObject getJSON (Context context , String place){
        URL url = null;
        try {
            url = new URL(String.format(OPEN_WEATHER_MAP_API, place));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

}