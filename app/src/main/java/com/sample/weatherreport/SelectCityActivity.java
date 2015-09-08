package com.sample.weatherreport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectCityActivity extends Activity implements AdapterView.OnItemClickListener ,LocationListener {
    private static final String LOG_TAG = "Google Places Autocomplete";
    Button button;
    AutoCompleteTextView autoCompView;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDouhHIrhBbI2HfbZ39q5zF24kgkuFREPA";
    private static final String API_KEY1= "AIzaSyDuD2zWnoTtr7YetUOGgC0Rybjd4O08nEk";
    GPSLocation gps;
    private Context context=null;
    private ProgressDialog dialog = null;
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
            button = (Button) findViewById(R.id.submit);
            new PlacePreference(this).setFirstRun(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gps = new GPSLocation(SelectCityActivity.this);
                    if (gps.canGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        dialog = ProgressDialog.show(SelectCityActivity.this, "", "Please wait..", true);
                        GetCurrentAddress currentadd = new GetCurrentAddress();
                        currentadd.execute();

                    } else {
                        gps.showSettingsAlert();
                    }
                }
            });
        }
        else
        {
            Intent intent = new Intent(SelectCityActivity.this, TabHostActivity.class);
            startActivity(intent);
            finish();

        }
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Intent intent = new Intent(SelectCityActivity.this, TabHostActivity.class);
        String segments[] = str.split(",");
        String cityName=segments[0];
        intent.putExtra("cityName", cityName);
        startActivity(intent);
        finish();
    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY1);
            sb.append("&types=(cities)");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("Error Reporting", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("Error Reporting", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e("Error Reporting", "Cannot process JSON results", e);
        }

        return resultList;
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

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).toString();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        resultList = autocomplete(constraint.toString());
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
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
            dialog.dismiss();
            Intent intent = new Intent(SelectCityActivity.this, TabHostActivity.class);
            intent.putExtra("cityName", address);
            startActivity(intent);
            finish();

        }
    }



}
