package com.example.cashtrack;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class PopupAddExpense extends AppCompatActivity {

    private static final String CHANNEL_ID ="my_channel_id" ;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_add_expense);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUsername = user.getDisplayName();

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUsername);

        Spinner addExpenseSpinner = findViewById(R.id.addExpenseSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.expense_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addExpenseSpinner.setAdapter(adapter);

        
        createNotificationChannel();


        EditText dateEditText = findViewById(R.id.dateEditText2);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(PopupAddExpense.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                        dateEditText.setText(selectedDate);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        EditText timeEditText = findViewById(R.id.timeEditText2);
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(PopupAddExpense.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar time = Calendar.getInstance();
                        time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        time.set(Calendar.MINUTE, minute);

                        SimpleDateFormat sdf;
                        if (android.text.format.DateFormat.is24HourFormat(PopupAddExpense.this)) {
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


        Button okButton = findViewById(R.id.okButton2);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve values entered by the user
                EditText dateEditText = findViewById(R.id.dateEditText2);
                EditText timeEditText = findViewById(R.id.timeEditText2);
                Spinner addExpenseSpinner = findViewById(R.id.addExpenseSpinner);
                EditText optionalEditText = findViewById(R.id.optionalEditText2);

                String enteredDate = dateEditText.getText().toString();
                String enteredTime = timeEditText.getText().toString();
                String selectedType = addExpenseSpinner.getSelectedItem().toString();
                String enteredOptional = optionalEditText.getText().toString();

                // Validate the entered fields
                if (TextUtils.isEmpty(enteredDate)) {
                    Toast.makeText(PopupAddExpense.this, "Please enter a date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(enteredTime)) {
                    Toast.makeText(PopupAddExpense.this, "Please enter a time", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedType.equals("Select Expense Type")) {
                    Toast.makeText(PopupAddExpense.this, "Please select an expense type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedType.equals("Other") && TextUtils.isEmpty(enteredOptional)) {
                    Toast.makeText(PopupAddExpense.this, "Please enter an optional expense type", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update total balance by subtracting the entered amount
                EditText amountEditText = findViewById(R.id.amountEditText2);
                String enteredAmountString = amountEditText.getText().toString();

                if (TextUtils.isEmpty(enteredAmountString)) {
                    Toast.makeText(PopupAddExpense.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                double enteredAmount = Double.parseDouble(amountEditText.getText().toString());

                if (selectedType.equals("Other")) {
                    selectedType = enteredOptional;
                }

                Expense expense = new Expense(enteredAmount, enteredDate, enteredTime, selectedType);
                Balance balance = new Balance(-enteredAmount, enteredDate, enteredTime, selectedType);

                getUserFromDatabase(expense, balance, enteredAmount);
            }
        });

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void updateUserInDatabase(User presentUser, double enteredAmount) {
        databaseReference.setValue(presentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PopupAddExpense.this, "Expense got updated!!!", Toast.LENGTH_SHORT).show();

                // Pass the entered amount to the OtherPage activity
                Intent intent = new Intent(PopupAddExpense.this, MainInterface.class);
                intent.putExtra("enteredAmount", enteredAmount);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PopupAddExpense.this, "Expense is not updating!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserFromDatabase(Expense expense, Balance balance, double enteredAmount) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User presentUser = dataSnapshot.getValue(User.class);
                if (presentUser != null) {
                    // Update the user object
                    presentUser.setTotal_expense(presentUser.getTotal_expense() + enteredAmount);
                    presentUser.setTotal_balance(presentUser.getTotal_balance() - enteredAmount);
                    presentUser.getBalancesList().add(balance);
                    presentUser.getExpensesList().add(expense);

                    if (presentUser.getTotal_balance() < presentUser.getLimit()) {
                        // Build the notification
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(PopupAddExpense.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.notification_icon) // Replace notification_icon with your own icon
                                .setContentTitle("Balance Alert")
                                .setContentText("Your balance is below the limit.")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        // Show the notification
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(PopupAddExpense.this);
                        int notificationId = 1; // Unique ID for the notification
                        if (ActivityCompat.checkSelfPermission(PopupAddExpense.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        notificationManager.notify(notificationId, builder.build());
                    }



                    // Save the updated user to the database
                    updateUserInDatabase(presentUser, enteredAmount);
                } else {
                    // Handle the case where user data is not available
                    Toast.makeText(PopupAddExpense.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("TAG", "Failed to read value.", databaseError.toException());
                Toast.makeText(PopupAddExpense.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
