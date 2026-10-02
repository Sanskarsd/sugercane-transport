package com.example.sugarcanetransport;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TractorHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<TractorHistoryItem> historyList;
    private HistoryAdapter adapter;
    private String TractorNumber;

    EditText startDateInput, endDateInput;
    Button fetchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tractor_history);

        SharedPreferences sharedPreferences = getSharedPreferences("TractorLoginData", MODE_PRIVATE);
        TractorNumber = sharedPreferences.getString("tractorNumber", "N/A");

        recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);

        startDateInput = findViewById(R.id.startDate);
        endDateInput = findViewById(R.id.endDate);
        fetchButton = findViewById(R.id.fetchButton);

        // Show calendar on clicking EditTexts
        startDateInput.setOnClickListener(v -> showDatePickerDialog(startDateInput));
        endDateInput.setOnClickListener(v -> showDatePickerDialog(endDateInput));

        fetchButton.setOnClickListener(v -> {
            String start = startDateInput.getText().toString();
            String end = endDateInput.getText().toString();
            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show();
            } else {
                fetchDataBetweenDates(start, end);
            }
        });
    }

    private void showDatePickerDialog(EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    targetEditText.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void fetchDataBetweenDates(String startDateStr, String endDateStr) {
        historyList.clear();

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat firebaseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            Date startDate = inputFormat.parse(startDateStr);
            Date endDate = inputFormat.parse(endDateStr);

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("TractorHistory")
                    .child(TractorNumber);

            ref.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot entry : snapshot.getChildren()) {
                        String timestampKey = entry.getKey();

                        try {
                            Date entryDate = firebaseFormat.parse(timestampKey);
                            if (entryDate != null &&
                                    !entryDate.before(startDate) &&
                                    !entryDate.after(endDate)) {

                                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(entryDate);
                                String firstOwner = entry.child("firstOwnerName").getValue(String.class);
                                String secondOwner = entry.child("secondOwnerName").getValue(String.class);
                                String weight1 = entry.child("weight1").getValue(String.class);
                                String weight2 = entry.child("weight2").getValue(String.class);

                                historyList.add(new TractorHistoryItem(
                                        date,
                                        firstOwner != null ? firstOwner : "Unknown",
                                        secondOwner != null ? secondOwner : "Unknown",
                                        weight1 != null ? weight1 : "0",
                                        weight2 != null ? weight2 : "0"
                                ));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(TractorHistory.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Enter valid date format", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Model class
    public static class TractorHistoryItem {
        public String date;
        public String firstOwner;
        public String secondOwner;
        public String weight1;
        public String weight2;

        public TractorHistoryItem(String date, String firstOwner, String secondOwner, String weight1, String weight2) {
            this.date = date;
            this.firstOwner = firstOwner;
            this.secondOwner = secondOwner;
            this.weight1 = weight1;
            this.weight2 = weight2;
        }

        public int getTotalWeight() {
            try {
                return Integer.parseInt(weight1) + Integer.parseInt(weight2);
            } catch (Exception e) {
                return 0;
            }
        }
    }

    // Adapter
    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

        private final List<TractorHistoryItem> historyList;

        HistoryAdapter(List<TractorHistoryItem> historyList) {
            this.historyList = historyList;
        }

        class HistoryViewHolder extends RecyclerView.ViewHolder {
            TextView textDay, textWeight;

            HistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                textDay = itemView.findViewById(R.id.textDay);
                textWeight = itemView.findViewById(R.id.textWeight);
            }
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
            TractorHistoryItem item = historyList.get(position);
            holder.textDay.setText(item.date);
            holder.textWeight.setText("Total: " + item.getTotalWeight() + " Ton");

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(TractorHistory.this, FullData.class);
                intent.putExtra("date", item.date);
                intent.putExtra("firstOwner", item.firstOwner);
                intent.putExtra("secondOwner", item.secondOwner);
                intent.putExtra("weight1", item.weight1);
                intent.putExtra("weight2", item.weight2);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return historyList.size();
        }
    }
}
