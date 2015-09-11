package com.sample.weatherreport.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sample.weatherreport.DailyForecast;
import com.sample.weatherreport.R;
import com.sample.weatherreport.TabHostActivity;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by surabhiv on 9/3/2015.
 */
public class WeatherAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<DailyForecast> daysForecast;
    public WeatherAdapter(Context context, List<DailyForecast> daysForecast){
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.daysForecast=daysForecast;
    }
    @Override
    public int getCount() {
       return daysForecast.size();
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

            view = mLayoutInflater.inflate(R.layout.list_layout, null);
            holder.date = (TextView) view.findViewById(R.id.text_day);
            holder.clouds = (TextView) view.findViewById(R.id.text_clouds);
            holder.temp = (TextView) view.findViewById(R.id.text_temp);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

            holder.clouds.setText(daysForecast.get(position).weather.currentCondition.getCondition()+
                    "\n" + daysForecast.get(position).weather.currentCondition.getDescr());
            holder.temp.setText( String.format("%.0f",daysForecast.get(position).forecastTemp.min)+" "+"℃"+" / " +String.format("%.0f",daysForecast.get(position).forecastTemp.max)+" "+"℃");
            DateFormat df = DateFormat.getDateInstance();
            String updatedOn = df.format(new Date(daysForecast.get(position).timestamp * 1000));
            holder.date.setText(updatedOn);


        return view;

    }
    private static class ViewHolder {
        TextView date;
        TextView clouds;
        TextView temp;
    }

}
