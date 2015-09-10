package com.sample.weatherreport;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
   // public static String globaLData;
    private ActionBar actionBar;
    public String locationName;
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
        ActionBar bar = getActionBar();
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

        this.setTitle("Weather Report");
    }

    @Override
    public void passData(String data) {
        new PlacePreference(this).setCity(data);
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

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showNetworkAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Network Connectivity Problem");
        alertDialog.setMessage("Unable to connect to server , Please check your network connectivity");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        alertDialog.show();
    }

}
