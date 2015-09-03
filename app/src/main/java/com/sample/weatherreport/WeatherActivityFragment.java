package com.sample.weatherreport;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;


public class WeatherActivityFragment extends Fragment {
    TextView cityName;
    TextView detailed_value;
    TextView currentTemperature;
    ImageView weatherIcon;
    TextView maxMinTemp;
    TextView updatedField;
    Handler handler;
    private static String IMG_URL = "http://openweathermap.org/img/w/";

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
        weatherIcon=(ImageView)rootView.findViewById(R.id.weather_icon);
        maxMinTemp=(TextView)rootView.findViewById(R.id.max_min);
        updatedField =(TextView)rootView.findViewById(R.id.updated_field);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWeatherDetails(TabHostActivity.globaLData);

    }

    private void showWeatherDetails(final String city){
        new Thread(){
            public void run(){
                final String json = FetchWeather.getJSON(getActivity(), city);
                Log.d("json value   ",json+"" );
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

    private void updateWeatherValues(String json){

        Weather weather = new Weather();
        try {
            weather = JSONWeatherParser.getWeather(json);
            String url = IMG_URL+weather.currentCondition.getIcon().concat(".png");
            new DownloadImageTask(weatherIcon).execute(url);
            cityName.setText(weather.location.getCity() + "," + weather.location.getCountry());
            detailed_value.setText(
                    weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")" +
                            "\n" + "Humidity: " + weather.currentCondition.getHumidity() + "%" +
                            "\n" + "Pressure: " + weather.currentCondition.getPressure() + " hPa" +
                            "\n" + "WindSpeed:" +  weather.wind.getSpeed()+ "mps"
            );
            currentTemperature.setText(String.format("%.2f", weather.temperature.getTemp())+ " ℃");
            maxMinTemp.setText("Temprature varies from " + String.format("%.2f", weather.temperature.getMinTemp())+ " ℃" +" - " +String.format("%.2f", weather.temperature.getMaxTemp())+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(weather.location.getDate()*1000));
            updatedField.setText("Last update: " + updatedOn);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
