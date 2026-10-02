package com.example.sugarcanetransport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TractorHome extends AppCompatActivity {

    private DatabaseReference databaseReference;
    TextView queueText;
    Button historybtn;
    FloatingActionButton btn;
    SharedPreferences sharedPreferences;
    String TractorNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tractor_home);
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Line");

        sharedPreferences = getSharedPreferences("TractorLoginData", MODE_PRIVATE);String tractorNumber = sharedPreferences.getString("tractorNumber", "N/A");
        String password = sharedPreferences.getString("password", "N/A");
        String fullName = sharedPreferences.getString("fullName", "N/A");
        String firstTroyleeNumber = sharedPreferences.getString("firstTroyleeNumber", "N/A");
        String secondTroyleeNumber = sharedPreferences.getString("secondTroyleeNumber", "N/A");
        String phone = sharedPreferences.getString("phone", "N/A");
        String tractorName = sharedPreferences.getString("tractorName", "N/A");
        TractorNumber = sharedPreferences.getString("tractorNumber", "N/A");
        Toast.makeText(TractorHome.this, "Tractor"+TractorNumber, Toast.LENGTH_SHORT).show();


        queueText=findViewById(R.id.queueText);
        btn=findViewById(R.id.enterQueue);
        historybtn=findViewById(R.id.historybtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTractor();
            }
        });
        historybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(TractorHome.this, TractorHistory.class));
            }
        });
        searchTractor();
    }

    public void openAcceptSugarData(View view) {
        Intent intent = new Intent(TractorHome.this, AcceptSugarcaneTractor.class);
        startActivity(intent);
    }

    private void searchTractor() {
        String searchTractor = TractorNumber.toString().trim();
        if (!searchTractor.isEmpty()) {

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
                        queueText.setText("Queue Number: " + queueNumber);
                    } else {
                        Toast.makeText(TractorHome.this, "Tractor number not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(TractorHome.this, "Error searching tractor.", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, "Please enter a tractor number to search", Toast.LENGTH_SHORT).show();
        }
    }
}