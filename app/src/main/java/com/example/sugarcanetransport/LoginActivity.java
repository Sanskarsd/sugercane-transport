package com.example.sugarcanetransport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextTractorNumber, editTextPassword;
    private Button loginButton;
    private DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextTractorNumber = findViewById(R.id.tractorNumber);
        editTextPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.submitS1);

        databaseReference = FirebaseDatabase.getInstance().getReference("TractorUserData");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tractorNumber = editTextTractorNumber.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (tractorNumber.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both fields", Toast.LENGTH_SHORT).show();
                }
                if(tractorNumber.toString().trim().equals("admin@123") )
                {
                    Toast.makeText(LoginActivity.this, "Admin Login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, TractorOwnerInfo.class);
                    startActivity(intent);
                }
                else if (tractorNumber.toString().trim().equals("emp@123"))
                {
                    Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                    startActivity(intent);
                }
                else if (tractorNumber.toString().trim().equals("gate@123"))
                {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                        loginTractorOwner(tractorNumber, password);
                }
            }
        });
    }

    private void loginTractorOwner(String tractorNumber, String password) {
        databaseReference.child(tractorNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Safe conversion with null checks
                    String storedPassword = String.valueOf(snapshot.child("password").getValue());
                    String name = String.valueOf(snapshot.child("fullName").getValue());
                    String firstTroyleeNumber = String.valueOf(snapshot.child("firstTroyleeNumber").getValue());
                    String secondTroyleeNumber = String.valueOf(snapshot.child("secondTroyleeNumber").getValue());
                    String phone = String.valueOf(snapshot.child("phone").getValue());
                    String tractorName = String.valueOf(snapshot.child("tractorName").getValue());

                    if (storedPassword.equals(password)) {
                        Toast.makeText(LoginActivity.this, "Login successful " + name, Toast.LENGTH_SHORT).show();

                        sharedPreferences = getSharedPreferences("TractorLoginData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("tractorNumber", tractorNumber);
                        editor.putString("password", password);
                        editor.putString("fullName", name);
                        editor.putString("firstTroyleeNumber", firstTroyleeNumber);
                        editor.putString("secondTroyleeNumber", secondTroyleeNumber);
                        editor.putString("phone", phone);
                        editor.putString("tractorName", tractorName);

                        editor.apply();

                        startActivity(new Intent(LoginActivity.this, TractorHome.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Tractor number not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
