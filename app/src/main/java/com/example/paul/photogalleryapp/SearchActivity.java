package com.example.paul.photogalleryapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private EditText fromDate;
    private EditText toDate;
    private EditText topLeft;
    private EditText bottomRight;
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
        topLeft = findViewById(R.id.search_topLeft);
        bottomRight = findViewById(R.id.search_bottomRight);
        keywords = findViewById(R.id.search_keyword);
    }


    public void cancel(final View v) {
        finish();
    }

    public void search(final View v) {
        Intent i = new Intent();
        i.putExtra("STARTDATE", fromDate.getText().toString());
        i.putExtra("ENDDATE", toDate.getText().toString());
        i.putExtra("TOPLEFT", topLeft.getText().toString());
        i.putExtra("BOTTOMRIGHT", bottomRight.getText().toString());
        i.putExtra("KEYWORDS", keywords.getText().toString());
        setResult(RESULT_OK, i);
        finish();
    }


}