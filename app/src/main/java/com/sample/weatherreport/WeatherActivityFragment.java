package com.sample.weatherreport;
import android.app.ProgressDialog;
import android.os.Handler;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.weatherreport.parser.FetchWeather;
import com.sample.weatherreport.parser.JSONWeatherParser;

import org.json.JSONException;

import java.text.DateFormat;
import java.util.Date;


public class WeatherActivityFragment extends Fragment {
    TextView cityName;
    TextView detailed_value;
    TextView currentTemperature;
    ImageView weatherIcon;
    TextView humidity_value;
    TextView pressure_value;
    TextView wind_value;
    TextView updatedField;
    Handler handler;
    private static String IMG_URL = "http://openweathermap.org/img/w/";
    private static String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    private ProgressDialog dialog = null;

    public WeatherActivityFragment(){

        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityName = (TextView)rootView.findViewById(R.id.city_field);
        detailed_value = (TextView)rootView.findViewById(R.id.details_field);
        humidity_value = (TextView)rootView.findViewById(R.id.humid_field);
        pressure_value = (TextView)rootView.findViewById(R.id.pressure_field);
        wind_value = (TextView)rootView.findViewById(R.id.wind_field);
        currentTemperature = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon=(ImageView)rootView.findViewById(R.id.weather_icon);
        updatedField =(TextView)rootView.findViewById(R.id.updated_field);
        dialog = new ProgressDialog(getActivity(),R.style.MyTheme);
        dialog.setTitle("WEATHER DETAILS");
        dialog.setMessage("Please wait ...Weather Details are being loaded.");
        dialog.show();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWeatherDetails(new PlacePreference(getActivity()).getCity());
    }

    private void showWeatherDetails(final String city){
        new Thread(){
            public void run(){
                final String json = FetchWeather.getJSON(getActivity(), city, OPEN_WEATHER_MAP_API);
                Log.d("json value   ",json+"" );
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            updateWeatherValues(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void updateWeatherValues(String json){

        Weather weather = new Weather();
        try {
            weather = JSONWeatherParser.getWeather(json);
            String url = IMG_URL+weather.currentCondition.getIcon().concat(".png");
            ((TabHostActivity)getActivity()).new DownloadImageTask(weatherIcon).execute(url);
            cityName.setText(weather.location.getCity() + "," + weather.location.getCountry());
            detailed_value.setText(
                    (weather.currentCondition.getCondition()).toUpperCase() + "(" + weather.currentCondition.getDescr() + ")");
            humidity_value.setText( weather.currentCondition.getHumidity() + "%");
            pressure_value.setText(weather.currentCondition.getPressure() + " hPa");
            wind_value.setText(weather.wind.getSpeed()+ "mps");
            currentTemperature.setText(String.format("%.2f", weather.temperature.getTemp())+ " ℃");
            new PlacePreference(getActivity()).setTemp(weather.temperature.getTemp() + "");
            new PlacePreference(getActivity()).setIcon(weather.currentCondition.getIcon()+"");
            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(weather.location.getDate()*1000));
            updatedField.setText("Last update: " + updatedOn);
            dialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
