package com.example.cashtrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class MainInterface extends AppCompatActivity {

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interface_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUsername = user.getDisplayName();


        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUsername);

        View transactions = findViewById(R.id.transactionBtn);
        View profileBtn = findViewById(R.id.ProfileBtn);
        Button addIncomeButton = findViewById(R.id.AddIncome);

        TextView income = findViewById(R.id.Income);
        TextView expense = findViewById(R.id.Expense);
        TextView balance = findViewById(R.id.TotalBalance);

        addIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainInterface.this, PopupAddIncome.class);
                startActivity(intent);
            }
        });

        Button addExpenseButton = findViewById(R.id.AddExpense);

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainInterface.this, PopupAddExpense.class);
                startActivity(intent);
            }
        });

        transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainInterface.this, TransactionsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainInterface.this, Profile.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        Button limitbtn=findViewById(R.id.Limit);
        limitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainInterface.this,Limit.class);
                startActivity(intent);
            }
        });
        // Retrieve and display the total income, total expense, and total balance
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    double totalIncome = user.getTotal_income();
                    double totalExpense = user.getTotal_expense();
                    double totalBalance = user.getTotal_balance();

                    income.setText(String.format(Locale.getDefault(), "%.2f", totalIncome));
                    expense.setText(String.format(Locale.getDefault(), "%.2f", totalExpense));
                    balance.setText(String.format(Locale.getDefault(), "%.2f", totalBalance));


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainInterface", "Database error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
