package com.sample.weatherreport;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.weatherreport.adapter.WeatherAdapter;

import org.json.JSONException;

import java.io.InputStream;
import java.util.List;

public class DailyWeatherActivity extends Fragment {
    Handler handler;
    private ListView myList;
    private TextView current_city;
    private TextView current_temp;
    ImageView current_icon;
    private static String OPEN_WEATHER_MAP_DAILY_API = "http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&mode=json&units=metric&cnt=7";
    private static String IMG_URL = "http://openweathermap.org/img/w/";

    public DailyWeatherActivity()
    {
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_daily_weather_fragment, container, false);
        myList=(ListView)rootView.findViewById(R.id.list);
        current_city=(TextView)rootView.findViewById(R.id.current_city);
        current_temp=(TextView)rootView.findViewById(R.id.current_temp);
        current_icon=(ImageView)rootView.findViewById(R.id.current_icon);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayDailyWeather(TabHostActivity.globaLData);

    }

    private void displayDailyWeather(final String city){
        new Thread(){
            public void run(){
                final String json = FetchWeather.getJSON(getActivity(),city,OPEN_WEATHER_MAP_DAILY_API);
                Log.d("json value   ", json + "");
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                           updateDailyForecast(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void updateDailyForecast(String json) {

        try {
            List<DailyForecast> WeaFore  = JSONDailyWeatherParser.getForecastWeather(json);
            myList.setAdapter(new WeatherAdapter(getActivity(), WeaFore));
            current_city.setText(WeaFore.get(0).weather.location.getCity());
            current_temp.setText(new PlacePreference(getActivity()).getTemp()+" ℃");
            ((TabHostActivity)getActivity()).new DownloadImageTask(current_icon).execute(IMG_URL+new PlacePreference(getActivity()).getIcon().concat(".png"));
        } catch (JSONException e) {
            e.printStackTrace();
        };

    }

}
