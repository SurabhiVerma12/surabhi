package com.sample.weatherreport;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.io.InputStream;

public class TabHostActivity extends FragmentActivity implements LocationFragment.LocationValueListener{
    public static String globaLData;
    private ActionBar actionBar;
    ActionBar.Tab Tab1, Tab2,Tab3;
    Fragment fragmentTab1 = new LocationFragment();
    Fragment fragmentTab2 = new WeatherActivityFragment();
    Fragment fragmentTab3 = new DailyWeatherActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tab_host);
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab1 = actionBar.newTab().setText("LOCATION");
        Tab2 = actionBar.newTab().setText("NOW");
        Tab3 = actionBar.newTab().setText("DAILY");

        Tab1.setTabListener(new TabListener(fragmentTab1));
        Tab2.setTabListener(new TabListener(fragmentTab2));
        Tab3.setTabListener(new TabListener(fragmentTab3));

        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);
        actionBar.addTab(Tab3);

        if(getIntent().getStringExtra("cityName")!=null){
           passData(getIntent().getStringExtra("cityName"));
        }
    }

    @Override
    public void passData(String data) {
        globaLData=data;
        actionBar.selectTab(Tab2);
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
