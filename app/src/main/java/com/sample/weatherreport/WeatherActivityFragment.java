package com.sample.weatherreport;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.Locale;


public class WeatherActivityFragment extends Fragment {
    TextView cityName;
    TextView detailed_value;
    TextView currentTemperature;
    Handler handler;

    public WeatherActivityFragment(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityName = (TextView)rootView.findViewById(R.id.city_field);
        detailed_value = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperature = (TextView)rootView.findViewById(R.id.current_temperature_field);
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
                final JSONObject json = FetchWeather.getJSON(getActivity(), city);
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
                            updateWeatherValues(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void updateWeatherValues(JSONObject json){
        try {
            cityName.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailed_value.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperature.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

        }catch(Exception e){
            Log.e("error message","error while parsing");
        }
    }

    public void enterCity(String city){
        showWeatherDetails(city);
    }
}
