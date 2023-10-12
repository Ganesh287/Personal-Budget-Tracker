package com.example.cashtrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashtrack.Balance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference database;
    TransactionAdapter transactionAdapter;
    ArrayList<Balance> List;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUsername = user.getDisplayName();

    private String selectedOption = "Select the Transactions TimeLine";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);

        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOption = parent.getItemAtPosition(position).toString();
                filterTransactions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no option is selected
            }
        });

        recyclerView = findViewById(R.id.transactionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(this, List);
        recyclerView.setAdapter(transactionAdapter);

        database = FirebaseDatabase.getInstance().getReference("users").child(currentUsername).child("balancesList");

        View home = findViewById(R.id.Home);
        View profileBtn = findViewById(R.id.ProfileBtn);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionsActivity.this, MainInterface.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionsActivity.this, Profile.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void filterTransactions() {
        List.clear();

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String todayDate = getFormattedDate(Calendar.getInstance().getTime(), "dd/MM/yyyy");
                Calendar sevenDaysBackCalendar = Calendar.getInstance();
                sevenDaysBackCalendar.add(Calendar.DAY_OF_MONTH, -7);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Balance balance = dataSnapshot.getValue(Balance.class);
                    String balanceDate = balance.getDate();
                    Calendar balanceCalendar = Calendar.getInstance();
                    balanceCalendar.setTime(getDateFromFormattedString(balanceDate, "dd/MM/yyyy"));

                    if (selectedOption.equals("Today") && isToday(balanceDate)) {
                        List.add(balance);
                    } else if (selectedOption.equals("This Week") && isWithinThisWeek(balanceCalendar, sevenDaysBackCalendar, Calendar.getInstance())) {
                        List.add(balance);
                    } else if (selectedOption.equals("This Month") && isSameMonth(balanceDate, todayDate)) {
                        List.add(balance);
                    } else if (selectedOption.equals("Select the Transactions TimeLine")) {
                        List.add(0, balance);
                    }
                }

                transactionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if any
            }
        });
    }

    private boolean isToday(String date) {
        String todayDate = getFormattedDate(Calendar.getInstance().getTime(), "dd/MM/yyyy");
        return date.equals(todayDate);
    }

    private boolean isSameMonth(String date1, String date2) {
        return date1.substring(3).equals(date2.substring(3));
    }

    private boolean isWithinThisWeek(Calendar target, Calendar start, Calendar end) {
        return target.after(start) && target.before(end);
    }

    private String getFormattedDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    private Date getDateFromFormattedString(String dateString, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            return sdf.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
