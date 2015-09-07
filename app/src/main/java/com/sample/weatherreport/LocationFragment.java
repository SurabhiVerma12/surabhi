package com.sample.weatherreport;

import android.app.Activity;
import android.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LocationFragment extends Fragment {
    EditText edtText ;
    Button button;
    LocationValueListener mCallback;
    public interface LocationValueListener{
        public void passData(String data);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_location_fragment, container, false);
        edtText = (EditText)rootView.findViewById(R.id.cityValue_fragment);

        button = (Button)rootView.findViewById(R.id.submit_fragment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if(edtText.getText().toString()==null || (edtText.getText().toString().trim()).equals("")){
                    Toast.makeText(getActivity(),getActivity().getString(R.string.field_blank),Toast.LENGTH_LONG).show();
                }
                else
                    mCallback.passData(edtText.getText().toString());
            }
        });
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

}

