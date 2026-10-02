package com.example.sugarcanetransport;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class TractorOwnerInfo extends AppCompatActivity {

    private EditText editTextFullName, editTextPhone, editTextTractorName, editTextTractorNumber,
            editTextFirstTroyleeNumber, editTextSecondTroyleeNumber,editTextPassword;
    private Button buttonSubmit;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tractor_owner_info);

        // Initialize the Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("TractorUserData");

        // Initialize the views
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextTractorName = findViewById(R.id.editTextTractorName);
        editTextTractorNumber = findViewById(R.id.editTextTractorNumber);
        editTextFirstTroyleeNumber = findViewById(R.id.editTextFirstTroyleeNumber);
        editTextSecondTroyleeNumber = findViewById(R.id.editTextSecondTroyleeNumber);
        editTextPassword = findViewById(R.id.editTextPass);
        buttonSubmit = findViewById(R.id.buttonSubmit);


        // Set onClickListener to handle button click
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Collect data from the input fields
                String fullName = editTextFullName.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String tractorName = editTextTractorName.getText().toString().trim();
                String tractorNumber = editTextTractorNumber.getText().toString().trim();
                String firstTroylee = editTextFirstTroyleeNumber.getText().toString().trim();
                String secondTroylee = editTextSecondTroyleeNumber.getText().toString().trim();
                String passw = editTextPassword.getText().toString().trim();

                // Validation check to ensure all fields are filled
                if (fullName.isEmpty() || phone.isEmpty() || tractorName.isEmpty() || tractorNumber.isEmpty() ||
                        firstTroylee.isEmpty() || secondTroylee.isEmpty() || passw.isEmpty()) {
                    Toast.makeText(TractorOwnerInfo.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a Map to store the data
                    Map<String, Object> tractorUserData = new HashMap<>();
                    tractorUserData.put("fullName", fullName);
                    tractorUserData.put("phone", phone);
                    tractorUserData.put("tractorName", tractorName);
                    tractorUserData.put("firstTroyleeNumber", firstTroylee);
                    tractorUserData.put("secondTroyleeNumber", secondTroylee);
                    tractorUserData.put("password", passw);

                    // Store the data in Firebase under the unique tractor number
                    databaseReference.child(tractorNumber).setValue(tractorUserData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Data has been successfully added
                                    Toast.makeText(TractorOwnerInfo.this, "Data Submitted Successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle failure
                                    Toast.makeText(TractorOwnerInfo.this, "Failed to submit data", Toast.LENGTH_SHORT).show();
                                }
                            });

                    // Optionally, clear the fields after submission
                    clearFields();
                }
            }
        });
    }

    private void clearFields() {
        // Clear the input fields after submission
        editTextFullName.setText("");
        editTextPhone.setText("");
        editTextTractorName.setText("");
        editTextTractorNumber.setText("");
        editTextFirstTroyleeNumber.setText("");
        editTextSecondTroyleeNumber.setText("");
        editTextPassword.setText("");
    }
}
