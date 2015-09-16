package com.sample.weatherreport.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sample.weatherreport.HourlyForecast;
import com.sample.weatherreport.R;

import java.util.List;

/**
 * Created by surabhiv on 9/16/2015.
 */
public class HourlyWeatherAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<HourlyForecast> hourForecast;
    String date;
    public HourlyWeatherAdapter(Context context, List<HourlyForecast> hourForecast,String date){
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.hourForecast=hourForecast;
        this.date=date;
    }
    @Override
    public int getCount() {
        return hourForecast.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();

            view = mLayoutInflater.inflate(R.layout.list_hourly, null);
            holder.time = (TextView) view.findViewById(R.id.text_time);
            holder.clouds = (TextView) view.findViewById(R.id.text_clouds_hourly);
            holder.temp = (TextView) view.findViewById(R.id.text_temp_hourly);

            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }
        String timeUpdate =hourForecast.get(position).forecastTemp.dTime;
        String segments[] = timeUpdate.split(" ");
        String updatedOn=segments[segments.length-1];
        String dateUpdated=segments[0];
        if(dateUpdated.equals(date)) {
            holder.clouds.setText(hourForecast.get(position).weather.currentCondition.getCondition() +
                    "\n" + hourForecast.get(position).weather.currentCondition.getDescr());
            holder.temp.setText(String.format("%.0f", hourForecast.get(position).weather.temperature.getTemp()) + " " + "℃");
            holder.time.setText(updatedOn);

        }
        return view;

    }
    private static class ViewHolder {
        TextView time;
        TextView clouds;
        TextView temp;
    }

}
