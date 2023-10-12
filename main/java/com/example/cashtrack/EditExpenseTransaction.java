package com.example.cashtrack;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditExpenseTransaction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_expense_transaction);

        EditText dateEditText = findViewById(R.id.editdateEditText2);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditExpenseTransaction.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                        dateEditText.setText(selectedDate);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        EditText timeEditText = findViewById(R.id.edittimeEditText2);
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(EditExpenseTransaction.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar time = Calendar.getInstance();
                        time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        time.set(Calendar.MINUTE, minute);

                        SimpleDateFormat sdf;
                        if (android.text.format.DateFormat.is24HourFormat(EditExpenseTransaction.this)) {
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

        Intent intent = getIntent();
        double amount = intent.getDoubleExtra("amount", 0);
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        int position = intent.getIntExtra("position", 0);

        EditText amountEditText = findViewById(R.id.amountEditText2);
        EditText optionalEditText = findViewById(R.id.optionalEditText2);
        Spinner expenseSpinner = findViewById(R.id.editExpenseSpinner);
        Button editButton = findViewById(R.id.saveButton2);

        amountEditText.setText(String.valueOf(amount));
        dateEditText.setText(date);
        timeEditText.setText(time);

        ArrayAdapter<CharSequence> spinnerAdapter;
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.expense_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseSpinner.setAdapter(spinnerAdapter);

        expenseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedExpenseType = parent.getItemAtPosition(position).toString();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUsername = user.getDisplayName();

                DatabaseReference balanceList = FirebaseDatabase.getInstance().getReference("users").child(currentUsername).child("balancesList").child(String.valueOf(position));
                balanceList.child("expenseType").setValue(selectedExpenseType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no option is selected
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editedAmount = amountEditText.getText().toString().trim();
                String editedDate = dateEditText.getText().toString().trim();
                String editedTime = timeEditText.getText().toString().trim();
                double editedAmount1 = Double.parseDouble(editedAmount);
                double amountDifference = editedAmount1 - amount;

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUsername = user.getDisplayName();

                DatabaseReference balanceList = FirebaseDatabase.getInstance().getReference("users").child(currentUsername).child("balancesList");

                balanceList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot dataSnapshot = snapshot.child(String.valueOf(position));
                        Balance editedBalance = dataSnapshot.getValue(Balance.class);

                        if (editedBalance != null) {
                            editedBalance.setAmount(editedAmount1);
                            editedBalance.setDate(editedDate);
                            editedBalance.setTime(editedTime);
                            dataSnapshot.getRef().setValue(editedBalance);
                        }
                        TransactionAdapter adapter = new TransactionAdapter();
                        adapter.notifyItemChanged(position);

                        updateTotalExpenseInDatabase(amountDifference);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("editedAmount", editedAmount);
                        resultIntent.putExtra("editedDate", editedDate);
                        resultIntent.putExtra("editedTime", editedTime);
                        setResult(Activity.RESULT_OK, resultIntent);

                        Intent intent1 = new Intent(EditExpenseTransaction.this, MainInterface.class);
                        startActivity(intent1);

                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error
                    }
                });
            }
        });
    }

    private void updateTotalExpenseInDatabase(double amountDifference) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUsername = user.getDisplayName();

        DatabaseReference expenseRef = FirebaseDatabase.getInstance().getReference("users").child(currentUsername);

        expenseRef.child("total_expense").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                double currentTotalExpense = task.getResult().getValue(Double.class);

                double newTotalExpense = currentTotalExpense - amountDifference;

                expenseRef.child("total_expense").setValue(newTotalExpense);
            } else {
                // Handle the error
            }
        });

        expenseRef.child("total_balance").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                double currentTotalBalance = task.getResult().getValue(Double.class);

                double newTotalBalance = currentTotalBalance + amountDifference;

                expenseRef.child("total_balance").setValue(newTotalBalance);
            } else {
                // Handle the error
            }
        });
    }
}
