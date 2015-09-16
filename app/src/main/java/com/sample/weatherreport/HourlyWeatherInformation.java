package com.sample.weatherreport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.weatherreport.adapter.HourlyWeatherAdapter;
import com.sample.weatherreport.parser.FetchWeather;
import com.sample.weatherreport.parser.JSONHourlyWeatherParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by surabhiv on 9/15/2015.
 */
public class HourlyWeatherInformation extends Activity{
    private ListView listView;
    List<HourlyForecast> hourFore,exludedHourFore;
    private Handler handler;
    private TextView textview;
    String date,city;
    private ProgressDialog dialog = null;
    private static String OPEN_WEATHER_MAP_HOURLY_API="http://api.openweathermap.org/data/2.5/forecast?q=%s&mode=json&units=metric";
    public HourlyWeatherInformation()
    {
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourly_data);
        exludedHourFore = new ArrayList<>();
        date = getIntent().getStringExtra("date");
        city = getIntent().getStringExtra("city");
        listView = (ListView)findViewById(R.id.list_hourly);
        textview = (TextView)findViewById(R.id.hourly_date);
        textview.setText(date);
        dialog = new ProgressDialog(this,R.style.MyTheme);
        dialog.setTitle(getString(R.string.weather_details));
        dialog.setMessage(getString(R.string.weather_loading));
        dialog.setCancelable(false);
        dialog.show();
        this.setFinishOnTouchOutside(false);
        displayHourlyWeather(city);

    }

    public void displayHourlyWeather(final String city){
        new Thread(){
            public void run(){
               final String json = FetchWeather.getJSON(HourlyWeatherInformation.this, city, OPEN_WEATHER_MAP_HOURLY_API);
                Log.d("json value   ", json + "");
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            dialog.dismiss();
                            Toast.makeText(HourlyWeatherInformation.this, getString(R.string.data_not_found), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            hourFore= JSONHourlyWeatherParser.getForecastWeather(json);
                            for(int i=0;i<hourFore.size();i++)
                            {
                                String timeUpdate =hourFore.get(i).forecastTemp.dTime;
                                String segments[] = timeUpdate.split(" ");
                                String dateUpdated=segments[0];
                                if(dateUpdated.equals(date)){
                                   exludedHourFore.add(hourFore.get(i));
                                }
                            }
                            listView.setAdapter(new HourlyWeatherAdapter(HourlyWeatherInformation.this, exludedHourFore, date));
                            dialog.dismiss();
                        }
                    });
                }
            }
        }.start();

    }


}
