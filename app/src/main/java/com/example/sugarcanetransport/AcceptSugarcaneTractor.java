package com.example.sugarcanetransport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AcceptSugarcaneTractor extends AppCompatActivity {

    // Firebase Database reference
    DatabaseReference SugarOwner;

    // UI elements
    TextInputEditText firstOwnerEditText, secondOwnerEditText, firstOwnerPhoneEditText, secondOwnerPhoneEditText;
     ExtendedFloatingActionButton submitButton;
    SharedPreferences sharedPreferences;
    String TractorNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accept_sugarcane_tractor);

        sharedPreferences = getSharedPreferences("TractorLoginData", MODE_PRIVATE);String tractorNumber = sharedPreferences.getString("tractorNumber", "N/A");
        String password = sharedPreferences.getString("password", "N/A");
        String fullName = sharedPreferences.getString("fullName", "N/A");
        String firstTroyleeNumber = sharedPreferences.getString("firstTroyleeNumber", "N/A");
        String secondTroyleeNumber = sharedPreferences.getString("secondTroyleeNumber", "N/A");
        String phone = sharedPreferences.getString("phone", "N/A");
        String tractorName = sharedPreferences.getString("tractorName", "N/A");
        TractorNumber = sharedPreferences.getString("tractorNumber", "N/A");
        Toast.makeText(AcceptSugarcaneTractor.this, "Tractor"+TractorNumber, Toast.LENGTH_SHORT).show();

        // Initialize Firebase reference
        SugarOwner = FirebaseDatabase.getInstance().getReference("SugarOwner");

        // Initialize UI elements
        firstOwnerEditText = findViewById(R.id.first_owner);
        secondOwnerEditText = findViewById(R.id.second_owner);
        firstOwnerPhoneEditText = findViewById(R.id.first_owner_phone);
        secondOwnerPhoneEditText = findViewById(R.id.second_owner_phone);
        submitButton = findViewById(R.id.submit);

        // Set OnClickListener for the Submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data from EditTexts
                String firstOwnerName = firstOwnerEditText.getText().toString().trim();
                String secondOwnerName = secondOwnerEditText.getText().toString().trim();
                String firstOwnerPhone = firstOwnerPhoneEditText.getText().toString().trim();
                String secondOwnerPhone = secondOwnerPhoneEditText.getText().toString().trim();


                // Check if the input is valid
                if (firstOwnerName.isEmpty() || secondOwnerName.isEmpty() || firstOwnerPhone.isEmpty() || secondOwnerPhone.isEmpty() || tractorNumber.isEmpty()) {
                    Toast.makeText(AcceptSugarcaneTractor.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a Map to store the data
                Map<String, String> sugarOwnerMap = new HashMap<>();
                sugarOwnerMap.put("firstOwner", firstOwnerName);
                sugarOwnerMap.put("secondOwner", secondOwnerName);
                sugarOwnerMap.put("firstOwnerPhone", firstOwnerPhone);
                sugarOwnerMap.put("secondOwnerPhone", secondOwnerPhone);

                    SugarOwner.child(TractorNumber).setValue(sugarOwnerMap)
                            .addOnSuccessListener(aVoid -> {
                                // Successfully added data to Firebase
                                Toast.makeText(AcceptSugarcaneTractor.this, "Data submitted successfully", Toast.LENGTH_SHORT).show();
                                // Clear EditTexts after submission
                                firstOwnerEditText.setText("");
                                secondOwnerEditText.setText("");
                                firstOwnerPhoneEditText.setText("");
                                secondOwnerPhoneEditText.setText("");

                            })
                            .addOnFailureListener(e -> {
                                // Failed to add data to Firebase
                                Toast.makeText(AcceptSugarcaneTractor.this, "Failed to submit data", Toast.LENGTH_SHORT).show();
                            });

            }
        });
    }
}
