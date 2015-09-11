package com.sample.weatherreport;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by surabhiv on 9/3/2015.
 */
public class DailyForecast {

    public Weather weather = new Weather();
    public TempratureForecast forecastTemp = new TempratureForecast();
    public long timestamp;

    public class TempratureForecast {
        public float day;
        public float min;
        public float max;

    }


}