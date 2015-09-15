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
    TextView humidity_value_text;
    TextView pressure_value_text;
    TextView wind_value_text;
    TextView updatedField;
    Handler handler;
    private static String IMG_URL = "http://openweathermap.org/img/w/";
    private static String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    private ProgressDialog dialog = null;
    private TabHostActivity.TCImageLoader tcImageLoader = null;

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
        humidity_value_text = (TextView)rootView.findViewById(R.id.humid_field_text);
        pressure_value_text = (TextView)rootView.findViewById(R.id.pressure_field_text);
        wind_value_text = (TextView)rootView.findViewById(R.id.wind_field_text);
        currentTemperature = (TextView)rootView.findViewById(R.id.current_temperature_field);
        textViewNONVisibility();
        weatherIcon=(ImageView)rootView.findViewById(R.id.weather_icon);
        updatedField =(TextView)rootView.findViewById(R.id.updated_field);
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
        showWeatherDetails(new PlacePreference(getActivity()).getCity());
    }

    private void showWeatherDetails(final String city){
        new Thread(){
            public void run(){
                final String json =
                        FetchWeather.getJSON(getActivity(), city, OPEN_WEATHER_MAP_API);
                Log.d("json value   ",json+"" );
                if(json == null ){
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
                            updateWeatherValues(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void updateWeatherValues(String json){

        Weather weather;
        try {
            weather = JSONWeatherParser.getWeather(json);
            if(!(weather.location.getCod().equalsIgnoreCase("200"))){
                dialog.dismiss();
                Toast.makeText(getActivity(),getString(R.string.data_not_found),Toast.LENGTH_LONG).show();
                getActivity().finish();
            }else{
                String url = IMG_URL+weather.currentCondition.getIcon().concat(".png");
                if (tcImageLoader==null){
                    tcImageLoader =  ((TabHostActivity) getActivity()).new TCImageLoader(getActivity());
                }
                tcImageLoader.display(url, weatherIcon);
                cityName.setText(weather.location.getCity() + "," + weather.location.getCountry());
                textViewVisibility();
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
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void textViewNONVisibility(){
        wind_value_text.setVisibility(View.INVISIBLE);
        humidity_value_text.setVisibility(View.INVISIBLE);
        pressure_value_text.setVisibility(View.INVISIBLE);

    }

    public void textViewVisibility(){
        wind_value_text.setVisibility(View.VISIBLE);
        humidity_value_text.setVisibility(View.VISIBLE);
        pressure_value_text.setVisibility(View.VISIBLE);

    }

}
