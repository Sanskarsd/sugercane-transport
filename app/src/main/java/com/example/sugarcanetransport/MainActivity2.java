package com.example.sugarcanetransport;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    private FloatingActionButton buttonNext;
    private ExtendedFloatingActionButton send;

    private RecyclerView recyclerViewLine;
    private TextView textViewComplete;
    private TextInputEditText tractorNumberEditText, first_troylee_weightEdit, second_troylee_weightEdt;

    private ArrayList<String> lineList;
    private LineAdapter lineAdapter;

    private DatabaseReference databaseReference, sugarOwnerReference;
    private SmsManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        sugarOwnerReference = databaseReference.child("SugarOwner");

        buttonNext = findViewById(R.id.buttonNext);
        send = findViewById(R.id.send);
        recyclerViewLine = findViewById(R.id.recyclerViewLine);
        textViewComplete = findViewById(R.id.textViewComplete);
        tractorNumberEditText = findViewById(R.id.tractor_number);
        first_troylee_weightEdit = findViewById(R.id.first_troylee_weight);
        second_troylee_weightEdt = findViewById(R.id.second_troylee_weight);

        lineList = new ArrayList<>();
        lineAdapter = new LineAdapter(lineList);
        recyclerViewLine.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewLine.setAdapter(lineAdapter);

        buttonNext.setOnClickListener(view -> processNext());
        send.setOnClickListener(view -> processSend());

        fetchAndDisplayLineValues();
        fetchAndDisplayCompleteValues();
    }

    private void fetchAndDisplayLineValues() {
        DatabaseReference lineReference = databaseReference.child("Line");

        lineReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                lineList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Object value = child.getValue();
                    if (value != null) {
                        lineList.add(value.toString());
                    }
                }
                lineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity2.this, "Failed to load Line data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAndDisplayCompleteValues() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseReference completeReference = databaseReference.child("Complete").child(currentDate);

        completeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                StringBuilder completeText = new StringBuilder("Complete: ");
                for (DataSnapshot child : snapshot.getChildren()) {
                    completeText.append(child.getKey()).append(" ");
                }
                textViewComplete.setText(completeText.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity2.this, "Failed to load Complete data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processNext() {
        DatabaseReference lineReference = databaseReference.child("Line");

        lineReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot firstTractorSnapshot = snapshot.getChildren().iterator().next();
                    Object value = firstTractorSnapshot.getValue();
                    if (value != null) {
                        String tractorNumber = value.toString();
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        databaseReference.child("Complete").child(currentDate).child(tractorNumber).setValue(true);
                        firstTractorSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity2.this, "Failed to process Next", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processSend() {
        String tractorNumber = tractorNumberEditText.getText().toString().trim();
        String weight1 = first_troylee_weightEdit.getText().toString().trim();
        String weight2 = second_troylee_weightEdt.getText().toString().trim();

        if (tractorNumber.isEmpty()) {
            Toast.makeText(MainActivity2.this, "Please enter a tractor number", Toast.LENGTH_SHORT).show();
            return;
        }

        sugarOwnerReference.child(tractorNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstOwnerPhone = snapshot.child("firstOwnerPhone").getValue(String.class);
                    String secondOwnerPhone = snapshot.child("secondOwnerPhone").getValue(String.class);
                    String firstOwnerName = snapshot.child("firstOwner").getValue(String.class);
                    String secondOwnerName = snapshot.child("secondOwner").getValue(String.class);

                    Toast.makeText(MainActivity2.this,
                            "First Owner Name: " + firstOwnerName + "\nSecond Owner Name: " + secondOwnerName + "\nTractor Number: " + tractorNumber,
                            Toast.LENGTH_LONG).show();

                    if (firstOwnerPhone != null && secondOwnerPhone != null) {
                        sendMessage(weight1, weight2, firstOwnerPhone, secondOwnerPhone);

                        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                        DatabaseReference historyRef = databaseReference
                                .child("TractorHistory")
                                .child(tractorNumber)
                                .child(timestamp);

                        historyRef.child("weight1").setValue(weight1);
                        historyRef.child("weight2").setValue(weight2);
                        historyRef.child("firstOwnerName").setValue(firstOwnerName);
                        historyRef.child("secondOwnerName").setValue(secondOwnerName);
                        historyRef.child("firstOwnerPhone").setValue(firstOwnerPhone);
                        historyRef.child("secondOwnerPhone").setValue(secondOwnerPhone);
                    } else {
                        Toast.makeText(MainActivity2.this, "Phone numbers not found for this tractor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity2.this, "No data found for this tractor number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity2.this, "Failed to fetch tractor data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String weight1, String weight2, String firstOwnerPhone, String secondOwnerPhone) {
        try {
            manager = SmsManager.getDefault();
            manager.sendTextMessage(firstOwnerPhone, null, "Weight First Troylee: " + weight1 + " Ton", null, null);
            Toast.makeText(MainActivity2.this, "First SMS sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity2.this, "First SMS not sent", Toast.LENGTH_SHORT).show();
        }

        try {
            manager = SmsManager.getDefault();
            manager.sendTextMessage(secondOwnerPhone, null, "Weight Second Troylee: " + weight2 + " Ton", null, null);
            Toast.makeText(MainActivity2.this, "Second SMS sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity2.this, "Second SMS not sent", Toast.LENGTH_SHORT).show();
        }
    }

    // ============================== //
    //       INNER CLASS ADAPTER      //
    // ============================== //
    public class LineAdapter extends RecyclerView.Adapter<LineAdapter.LineViewHolder> {
        private ArrayList<String> lineList;

        public LineAdapter(ArrayList<String> lineList) {
            this.lineList = lineList;
        }

        @NonNull
        @Override
        public LineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line, parent, false);
            return new LineViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LineViewHolder holder, int position) {
            holder.textView.setText(lineList.get(position));
        }

        @Override
        public int getItemCount() {
            return lineList.size();
        }

        public class LineViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public LineViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textItem);
            }
        }
    }
}
