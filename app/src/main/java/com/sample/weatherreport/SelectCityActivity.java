package com.sample.weatherreport;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.sample.weatherreport.adapter.GooglePlacesAutocompleteAdapter;

public class SelectCityActivity extends Activity implements AdapterView.OnItemClickListener ,LocationListener {
    Button mButton;
    AutoCompleteTextView autoCompView;
    GPSLocation gps;
    private ProgressDialog mDialog = null;
    double latitude;
    double longitude;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new PlacePreference(this).getFirstRun()==false) {
            setContentView(R.layout.activity_select_city);
            autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
            autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
            autoCompView.setOnItemClickListener(this);
            mButton = (Button) findViewById(R.id.submit);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkAvailable() == false) {
                        showSettingsAlert();
                    } else {
                        gps = new GPSLocation(SelectCityActivity.this);
                        if (gps.canGetLocation()) {
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            if (latitude == 0.0 || longitude == 0.0) {
                                Toast.makeText(SelectCityActivity.this, getString(R.string.gps_location), Toast.LENGTH_LONG).show();
                            } else {
                                mDialog = ProgressDialog.show(SelectCityActivity.this, "", getString(R.string.please_wait), true);
                                GetCurrentAddress currentadd = new GetCurrentAddress();
                                currentadd.execute();
                                new PlacePreference(SelectCityActivity.this).setFirstRun(true);
                            }

                        } else {
                            gps.showSettingsAlert();
                        }
                    }

                }
            });
            if(isNetworkAvailable()==false){
                showSettingsAlert();
            }
        }
        else
        {
            Intent intent = new Intent(SelectCityActivity.this, TabHostActivity.class);
            startActivity(intent);
            finish();

        }
    }
    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        if(isNetworkAvailable()==false){
            showSettingsAlert();
        }
        else{
            String str = (String) adapterView.getItemAtPosition(position);
            Intent intent = new Intent(SelectCityActivity.this, TabHostActivity.class);
            String segments[] = str.split(",");
            String cityName=segments[0];
            intent.putExtra("cityName", cityName);
            intent.putExtra("str",str);
            hideSoftkeyboard();
            new PlacePreference(SelectCityActivity.this).setFirstRun(true);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public  String getAddress(Context ctx, double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address>
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                String locality=address.getLocality();
                result.append(locality);
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    private class GetCurrentAddress extends AsyncTask<String,Void,String> {
        String address;
        @Override
        protected String doInBackground(String... urls) {
            address= getAddress(SelectCityActivity.this, latitude, longitude);
            return address;
        }

        @Override
        protected void onPostExecute(String resultString) {
            mDialog.dismiss();
            hideSoftkeyboard();
            Intent intent = new Intent(SelectCityActivity.this, TabHostActivity.class);
            intent.putExtra("cityName", address);
            intent.putExtra("str", address);
            startActivity(intent);
            finish();

        }
    }
    public void hideSoftkeyboard()
    {
        if(autoCompView !=null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showSettingsAlert(){
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
}
