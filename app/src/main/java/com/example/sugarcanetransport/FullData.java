package com.example.sugarcanetransport;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FullData extends AppCompatActivity {

    TextView textDate, textOwners, textWeights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_data);

        textDate = findViewById(R.id.textDate);
        textOwners = findViewById(R.id.textOwners);
        textWeights = findViewById(R.id.textWeights);

        String date = getIntent().getStringExtra("date");
        String firstOwner = getIntent().getStringExtra("firstOwner");
        String secondOwner = getIntent().getStringExtra("secondOwner");
        String weight1 = getIntent().getStringExtra("weight1");
        String weight2 = getIntent().getStringExtra("weight2");

        textDate.setText("Date: " + date);
        textOwners.setText("Owners: " + firstOwner + " & " + secondOwner);
        textWeights.setText("Weights: " + weight1 + " kg, " + weight2 + " kg");
    }
}
