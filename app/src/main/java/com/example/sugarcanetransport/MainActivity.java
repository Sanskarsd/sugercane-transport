package com.example.sugarcanetransport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTractor, searchNumber;
    private Button buttonAdd, buttonSearch;
    private TextView textViewLine, textViewQueueNumber;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Line");

        editTextTractor = findViewById(R.id.editTextTractor);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonSearch = findViewById(R.id.buttonSearch);
        searchNumber = findViewById(R.id.searchNumber);
        textViewLine = findViewById(R.id.textViewLine);
        textViewQueueNumber = findViewById(R.id.textViewQueueNumber);

        // Add tractor
        buttonAdd.setOnClickListener(view -> addTractor());


        // Search for a tractor
        buttonSearch.setOnClickListener(view -> searchTractor());

        // Load and display line
        updateUI();
    }

    private void addTractor() {
        String input = editTextTractor.getText().toString().trim();
        if (!input.isEmpty()) {
            databaseReference.push().setValue(input); // Save as string
            editTextTractor.setText("");
            updateUI();
        } else {
            Toast.makeText(this, "Please enter a tractor number", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder lineText = new StringBuilder("Line: ");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Object value = snapshot.getValue();
                    if (value != null) {
                        String tractorNumber = value.toString(); // Handles both String and Long
                        lineText.append(tractorNumber).append(" ");
                    }
                }
                textViewLine.setText(lineText.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load line.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchTractor() {
        String searchInput = searchNumber.getText().toString().trim();
        if (!searchInput.isEmpty()) {
            String searchTractor = searchInput;

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int queueNumber = 1;
                    boolean found = false;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Object value = snapshot.getValue();
                        if (value != null && value.toString().equalsIgnoreCase(searchTractor)) {
                            found = true;
                            break;
                        }
                        queueNumber++;
                    }

                    if (found) {
                        textViewQueueNumber.setText("Queue Number: " + queueNumber);
                        textViewQueueNumber.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(MainActivity.this, "Tractor number not found!", Toast.LENGTH_SHORT).show();
                        textViewQueueNumber.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error searching tractor.", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, "Please enter a tractor number to search", Toast.LENGTH_SHORT).show();
        }
    }
}
