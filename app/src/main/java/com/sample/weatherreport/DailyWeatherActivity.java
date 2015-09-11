package com.sample.weatherreport;


import android.app.Fragment;
import android.app.ProgressDialog;
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
import com.sample.weatherreport.parser.FetchWeather;
import com.sample.weatherreport.parser.JSONDailyWeatherParser;
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
            List<DailyForecast> WeaFore  = JSONDailyWeatherParser.getForecastWeather(json);
            if(!(WeaFore.get(0).weather.location.getCod().equalsIgnoreCase("200"))){
                dialog.dismiss();
                Toast.makeText(getActivity(),getString(R.string.data_not_found),Toast.LENGTH_LONG).show();
                getActivity().finish();
            }else{
                myList.setAdapter(new WeatherAdapter(getActivity(), WeaFore));
                current_city.setText(WeaFore.get(0).weather.location.getCity());
                current_temp.setText(new PlacePreference(getActivity()).getTemp()+" ℃");
                ((TabHostActivity)getActivity()).new DownloadImageTask(current_icon).execute(IMG_URL + new PlacePreference(getActivity()).getIcon().concat(".png"));
                dialog.dismiss();
            }

        } catch (Exception e) {
            e.printStackTrace();
        };

    }

}
