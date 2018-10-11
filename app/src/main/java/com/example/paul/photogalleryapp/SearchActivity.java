package com.example.paul.photogalleryapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

public class SearchActivity extends AppCompatActivity {

    private EditText fromDate;
    private EditText toDate;
    private EditText topLeftLat;
    private EditText topLeftLong;
    private EditText bottomRightLat;
    private EditText bottomRightLong;
    private EditText keywords;
    private Calendar fromCalendar;
    private Calendar toCalendar;
    private DatePickerDialog.OnDateSetListener fromListener;
    private DatePickerDialog.OnDateSetListener toListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fromDate = findViewById(R.id.search_fromDate);
        toDate   = findViewById(R.id.search_toDate);
        topLeftLat = findViewById(R.id.search_topLeftLat);
        topLeftLong = findViewById(R.id.search_topLeftLong);
        bottomRightLat = findViewById(R.id.search_bottomRightLat);
        bottomRightLong = findViewById(R.id.search_bottomRightLong);
        keywords = findViewById(R.id.search_keyword);
    }


    public void cancel(final View v) {
        finish();
    }

    public void search(final View v) {
        Intent i = new Intent();
        i.putExtra("STARTDATE", fromDate.getText().toString());
        i.putExtra("ENDDATE", toDate.getText().toString());
        i.putExtra("TOPLEFTLAT", topLeftLat.getText().toString());
        i.putExtra("TOPLEFTLONG", topLeftLong.getText().toString());
        i.putExtra("BOTTOMRIGHTLAT", bottomRightLat.getText().toString());
        i.putExtra("BOTTOMRIGHTLONG", bottomRightLong.getText().toString());
        i.putExtra("KEYWORDS", keywords.getText().toString());
        setResult(RESULT_OK, i);
        finish();
    }


}