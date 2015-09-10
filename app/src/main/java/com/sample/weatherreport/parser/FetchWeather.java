package com.sample.weatherreport.parser;
import android.content.Context;
import android.graphics.Bitmap;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by surabhiv on 9/1/2015.
 */
public class FetchWeather {

    public static String getJSON (Context context , String place,String url1){
        URL url = null;
        try {
            url = new URL(String.format(url1,URLEncoder.encode(place, "utf8")));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();


            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

}
