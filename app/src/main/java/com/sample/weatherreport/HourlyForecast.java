package com.sample.weatherreport;

/**
 * Created by surabhiv on 9/16/2015.
 */
public class HourlyForecast {

    public Weather weather = new Weather();
    public HourlyWeatherForecast forecastTemp = new HourlyWeatherForecast();

    public class HourlyWeatherForecast {
        public String dTime;
    }
}
