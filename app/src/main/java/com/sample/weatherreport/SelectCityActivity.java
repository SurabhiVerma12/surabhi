package com.sample.weatherreport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SelectCityActivity extends AppCompatActivity {
    EditText edtText ;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        edtText = (EditText) findViewById(R.id.cityValue);
        button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtText.getText().toString()==null || (edtText.getText().toString().trim()).equals("")){
                    Toast.makeText(SelectCityActivity.this,getString(R.string.field_blank), Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(SelectCityActivity.this , TabHostActivity.class);
                    intent.putExtra("cityName",edtText.getText().toString());
                    startActivity(intent);
                    finish();
                }

            }
        });

    }






}
