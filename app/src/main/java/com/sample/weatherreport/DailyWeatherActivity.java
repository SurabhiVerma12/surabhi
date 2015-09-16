package com.sample.weatherreport;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.sample.weatherreport.adapter.WeatherAdapter;
import com.sample.weatherreport.parser.FetchWeather;
import com.sample.weatherreport.parser.JSONDailyWeatherParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DailyWeatherActivity extends Fragment {
    private Handler handler;
    private ListView myList;
    private TextView current_city;
    private TextView current_temp;
    private ImageView current_icon;
    private static String OPEN_WEATHER_MAP_DAILY_API = "http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&mode=json&units=metric&cnt=7";
    private static String IMG_URL = "http://openweathermap.org/img/w/";
    private ProgressDialog dialog = null;
    private TabHostActivity.TCImageLoader tcImageLoader = null;
    List<DailyForecast> WeaFore;

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
        dialog = new ProgressDialog(getActivity(),R.style.MyTheme);
        dialog.setTitle(getString(R.string.weather_details));
        dialog.setMessage(getString(R.string.weather_loading));
        dialog.setCancelable(false);
        dialog.show();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayDailyWeather(new PlacePreference(getActivity()).getCity());
    }

    private void displayDailyWeather(final String city){
        new Thread(){
            public void run(){
                final String json = FetchWeather.getJSON(getActivity(), city, OPEN_WEATHER_MAP_DAILY_API);
                Log.d("json value   ", json + "");
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            dialog.dismiss();
                            if(((TabHostActivity)getActivity()).isNetworkAvailable()==false){
                                ((TabHostActivity)getActivity()).showNetworkAlert();
                            }
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
            WeaFore  = JSONDailyWeatherParser.getForecastWeather(json);
            if(!(WeaFore.get(0).weather.location.getCod().equalsIgnoreCase("200"))){
                dialog.dismiss();
                Toast.makeText(getActivity(),getString(R.string.data_not_found),Toast.LENGTH_LONG).show();
                getActivity().finish();
            }else{
                String image_url =IMG_URL + new PlacePreference(getActivity()).getIcon().concat(".png");
                myList.setAdapter(new WeatherAdapter(getActivity(), WeaFore));
                current_city.setText(WeaFore.get(0).weather.location.getCity());
                current_temp.setText(new PlacePreference(getActivity()).getTemp()+" ℃");
                if (tcImageLoader==null){
                    tcImageLoader =  ((TabHostActivity) getActivity()).new TCImageLoader(getActivity());
                }
                tcImageLoader.display(image_url, current_icon);
                dialog.dismiss();
                myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if(position > 4){
                            Toast.makeText(getActivity(),getActivity().getString(R.string.data_not_available),Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent =new Intent(getActivity(),HourlyWeatherInformation.class);
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            String updatedOn = df.format(new Date(WeaFore.get(position).timestamp * 1000));
                            intent.putExtra("date",updatedOn);
                            intent.putExtra("city",new PlacePreference(getActivity()).getCity());
                            startActivity(intent);
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        };

    }

}
