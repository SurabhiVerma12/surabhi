package com.sample.weatherreport;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.sample.weatherreport.adapter.TabPagerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TabHostActivity extends FragmentActivity implements LocationFragment.LocationValueListener{
    private ActionBar actionBar;
    ActionBar.Tab Tab1, Tab2,Tab3,Tab4;
    ViewPager Tab;
    TabPagerAdapter TabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_host);
        TabAdapter = new TabPagerAdapter(getSupportFragmentManager());

        Tab = (ViewPager)findViewById(R.id.pager);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar = getActionBar();
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
        Tab.setAdapter(TabAdapter);
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener(){

            @Override
            public void onTabReselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {

            }

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

                Tab.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {


            }};
        Tab1 = actionBar.newTab().setText("LOCATION");
        Tab2 = actionBar.newTab().setText("NOW");
        Tab3 = actionBar.newTab().setText("DAILY");
        Tab4 = actionBar.newTab().setText("MAP");

        Tab1.setTabListener(tabListener);
        Tab2.setTabListener(tabListener);
        Tab3.setTabListener(tabListener);
        Tab4.setTabListener(tabListener);

        actionBar.addTab(Tab1);
        actionBar.addTab(Tab2);
        actionBar.addTab(Tab3);
        actionBar.addTab(Tab4);

        if(getIntent().getStringExtra("cityName")!=null){
            passData(getIntent().getStringExtra("cityName"));
        }
        this.setTitle(getString(R.string.daily_weather));
    }

    @Override
    public void passData(String data) {
        new PlacePreference(this).setCity(data);
        actionBar.selectTab(Tab2);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showNetworkAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.network_connectivity));
        alertDialog.setMessage(getString(R.string.server_error));
        alertDialog.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        alertDialog.show();
    }

    public class TCImageLoader implements ComponentCallbacks2 {
        private TCLruCache cache;

        public TCImageLoader(Context context) {
            ActivityManager am = (ActivityManager) context.getSystemService(
                    Context.ACTIVITY_SERVICE);
            int maxKb = am.getMemoryClass() * 1024;
            int limitKb = maxKb / 8;
            cache = new TCLruCache(limitKb);
        }

        public void display(String url, ImageView imageview) {
            Bitmap image = cache.get(url);
            if (image != null) {
                imageview.setImageBitmap(image);
            }
            else {
                new SetImageTask(imageview).execute(url);
            }
        }

        private class TCLruCache extends LruCache<String, Bitmap> {

            public TCLruCache(int maxSize) {
                super(maxSize);
            }

            @Override
            protected int sizeOf(String key, Bitmap value) {
                int kbOfBitmap = value.getByteCount() / 1024;
                return kbOfBitmap;
            }
        }

        private class SetImageTask extends AsyncTask<String, Void, Integer> {
            private ImageView imageview;
            private Bitmap bmp;

            public SetImageTask(ImageView imageview) {
                this.imageview = imageview;
            }

            @Override
            protected Integer doInBackground(String... params) {
                String url = params[0];
                try {
                    bmp = getBitmapFromURL(url);
                    if (bmp != null) {
                        cache.put(url, bmp);
                    }
                    else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
                return 1;
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (result == 1) {
                    imageview.setImageBitmap(bmp);
                }
                super.onPostExecute(result);
            }

            private Bitmap getBitmapFromURL(String src) {
                try {
                    URL url = new URL(src);
                    HttpURLConnection connection
                            = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
        }

        @Override
        public void onLowMemory() {
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onTrimMemory(int level) {
            if (level >= TRIM_MEMORY_MODERATE) {
                cache.evictAll();
            }
            else if (level >= TRIM_MEMORY_BACKGROUND) {
                cache.trimToSize(cache.size() / 2);
            }
        }
    }

}