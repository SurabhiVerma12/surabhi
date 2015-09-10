package com.sample.weatherreport;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import com.sample.weatherreport.adapter.GooglePlacesAutocompleteAdapter;
import java.util.ArrayList;

public class LocationFragment extends Fragment implements AdapterView.OnItemClickListener{
    LocationValueListener mCallback;
    AutoCompleteTextView autoCompView;
    public interface LocationValueListener{
        public void passData(String data);
    }
    private static ListView lv;
    private static ArrayList<String> strArr= new ArrayList<String>();;
    private ArrayAdapter<String> adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_location_fragment, container, false);
        autoCompView = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView1);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
        autoCompView.setOnItemClickListener(this);
        lv = (ListView)rootView.findViewById(R.id.list_city);
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, strArr);
        lv.setAdapter(adapter);
        hideSoftkeyboard();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position,
                                    long id) {
                String str = (String) parent.getItemAtPosition(position);
                String segments[] = str.split(",");
                String cityName = segments[0];
                hideSoftkeyboard();
                mCallback.passData(cityName);
            }
        });
        if(getActivity().getIntent().getStringExtra("str")!=null &&  new PlacePreference(getActivity()).getfirstValue()) {
            String city=getActivity().getIntent().getStringExtra("str").toString();
            strArr.add(city);
            adapter.notifyDataSetChanged();
            new PlacePreference(getActivity()).setFirstValue(false);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (LocationValueListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DataPassListener");
        }
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        if(((TabHostActivity)getActivity()).isNetworkAvailable()==false)
        {
            ((TabHostActivity)getActivity()).showNetworkAlert();
        }
        else{
            String str = (String) adapterView.getItemAtPosition(position);
            String segments[] = str.split(",");
            String cityName=segments[0];
            if(strArr.contains(str)){

            }else
                strArr.add(str);
            adapter.notifyDataSetChanged();
            autoCompView.setText("");
            hideSoftkeyboard();
            mCallback.passData(cityName);
        }

    }

    public void hideSoftkeyboard()
    {
        if(autoCompView !=null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);
        }
    }


}

