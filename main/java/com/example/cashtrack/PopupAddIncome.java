package com.example.cashtrack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class PopupAddIncome extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_add_income);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUsername = user.getDisplayName();

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUsername);
        DatabaseReference balanceListRef = databaseReference.child("balancesList");

        Spinner addIncomeSpinner = findViewById(R.id.addIncomeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.income_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addIncomeSpinner.setAdapter(adapter);



        EditText dateEditText = findViewById(R.id.dateEditText);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(dateEditText);
            }


            private void showDatePicker(final EditText dateEditText) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(PopupAddIncome.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                        dateEditText.setText(selectedDate);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        EditText timeEditText = findViewById(R.id.timeEditText);
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(timeEditText);
            }

            private void showTimePicker(final EditText timeEditText) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                boolean is24HourFormat = false;

                TimePickerDialog timePickerDialog = new TimePickerDialog(PopupAddIncome.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar time = Calendar.getInstance();
                        time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        time.set(Calendar.MINUTE, minute);

                        SimpleDateFormat sdf;
                        if (android.text.format.DateFormat.is24HourFormat(PopupAddIncome.this)) {
                            sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        } else {
                            sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        }

                        String selectedTime = sdf.format(time.getTime());
                        timeEditText.setText(selectedTime);
                    }
                }, hour, minute, is24HourFormat);

                timePickerDialog.show();
            }
        });




        Button okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve values entered by the user
                EditText dateEditText = findViewById(R.id.dateEditText);
                EditText timeEditText = findViewById(R.id.timeEditText);
                Spinner addIncomeSpinner = findViewById(R.id.addIncomeSpinner);
                EditText optionalEditText = findViewById(R.id.optionalEditText);

                String enteredDate = dateEditText.getText().toString();
                String enteredTime = timeEditText.getText().toString();
                String selectedType = addIncomeSpinner.getSelectedItem().toString();
                String enteredOptional = optionalEditText.getText().toString();

                // Validate the entered fields
                if (TextUtils.isEmpty(enteredDate)) {
                    Toast.makeText(PopupAddIncome.this, "Please enter a date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(enteredTime)) {
                    Toast.makeText(PopupAddIncome.this, "Please enter a time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedType.equals("Select Income Type")) {
                    Toast.makeText(PopupAddIncome.this, "Please select an income type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedType.equals("Other") && TextUtils.isEmpty(enteredOptional)) {
                    Toast.makeText(PopupAddIncome.this, "Please enter an optional income type", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update total balance by adding the entered amount
                EditText amountEditText = findViewById(R.id.amountEditText);
                String enteredAmountString = amountEditText.getText().toString();

                if (TextUtils.isEmpty(enteredAmountString)) {
                    Toast.makeText(PopupAddIncome.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                double enteredAmount = Double.parseDouble(amountEditText.getText().toString());

                if (selectedType.equals("Other")) {
                    selectedType = enteredOptional;
                }

                Income income = new Income(enteredAmount, enteredDate, enteredTime, selectedType);
                Balance balance = new Balance(enteredAmount, enteredDate, enteredTime, selectedType);



                getUserFromDatabase(income, balance, enteredAmount);





            }
        });

    }

    private void updateUserInDatabase(User presentUser,double enteredAmount,String username) {
        databaseReference.setValue(presentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PopupAddIncome.this, "Income got updated!!!", Toast.LENGTH_SHORT).show();

                // Pass the entered amount to the OtherPage activity

                Intent intent = new Intent(PopupAddIncome.this, MainInterface.class);
                intent.putExtra("enteredAmount", enteredAmount);
                intent.putExtra("username",username);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PopupAddIncome.this, "Income is not updating!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserFromDatabase(Income income, Balance balance, double enteredAmount) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User presentUser = dataSnapshot.getValue(User.class);
                if (presentUser != null) {
                    // Update the user object
                    String username = presentUser.getUsername();
                    presentUser.total_income += enteredAmount;
                    presentUser.total_balance += enteredAmount;
                    presentUser.balancesList.add(balance);
                    presentUser.incomesList.add(income);

                    // Save the updated user to the database

                    updateUserInDatabase(presentUser,enteredAmount,username);
                } else {
                    // Handle the case where user data is not available
                    Toast.makeText(PopupAddIncome.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("TAG", "Failed to read value.", databaseError.toException());
                Toast.makeText(PopupAddIncome.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
