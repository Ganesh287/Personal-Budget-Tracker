package com.example.cashtrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cashtrack.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Limit extends AppCompatActivity {

    private EditText limitAmtEditText;
    private Button setLimitButton;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.limit_activity);

        limitAmtEditText = findViewById(R.id.LimitAmountEditText);
        setLimitButton = findViewById(R.id.SetLimit);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        setLimitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String limitAmtString = limitAmtEditText.getText().toString().trim();

                if (limitAmtString.isEmpty()) {
                    // Show alert if the limit amount is not entered
                    Toast.makeText(Limit.this, "Please enter a valid limit amount.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        // Parse the limit amount as a double
                        double limitAmt = Double.parseDouble(limitAmtString);

                        // Update the limit in the user's database
                        updateLimitInDatabase(limitAmt);
                    } catch (NumberFormatException e) {
                        // Show alert if the entered limit amount is not a valid number
                        Toast.makeText(Limit.this, "Please enter a valid number for the limit amount.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void updateLimitInDatabase(double limitAmt) {
        // Get the current user's ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUsername = user.getDisplayName();

        // Update the limit in the user's database node
        usersRef.child(currentUsername).child("limit").setValue(limitAmt)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Limit.this, "Limit updated successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(Limit.this,MainInterface.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Limit.this, "Failed to update the limit.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
