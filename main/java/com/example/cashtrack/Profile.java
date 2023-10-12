package com.example.cashtrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Profile extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        View logoutBtn=findViewById(R.id.logoutButton);
        View home=findViewById(R.id.Home);
        View transactionBtn=findViewById(R.id.transactionBtn);
        Button deleteActButton = findViewById(R.id.DeleteActButton);
        Button updateprofile = findViewById(R.id.updateProfileButton);
        TextView username= findViewById(R.id.usernameTextView);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        String currentUsername = user.getDisplayName();
        username.setText(currentUsername);
        updateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this,UpdateProfile.class);
                startActivity(intent);
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle("Confirm Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }

                            private void logout() {
                                // Perform logout operations here, such as clearing session data or credentials
                                FirebaseAuth.getInstance().signOut();
                                // Navigate to the login page
                                Intent intent = new Intent(Profile.this, LoginPage.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this,MainInterface.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

       transactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, TransactionsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
        deleteActButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a confirmation dialog
                showDialogConfirmation();
            }

            private void showDialogConfirmation() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle("Confirm Account Deletion");
                builder.setMessage("Are you sure you want to delete your account?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call a method to delete the account
                        deleteAccount();
                    }

                    private void deleteAccount() {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            String uid = user.getUid();
                            String currentUsername = user.getDisplayName();

                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Profile.this, "Successfully deleted user account from Firebase Authentication", Toast.LENGTH_SHORT).show();

                                                // Remove associated data from the Realtime Database
                                                DatabaseReference userReference = mDatabase.child("users").child(currentUsername);
                                                userReference.removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(Profile.this, "Successfully removed user data from the database", Toast.LENGTH_SHORT).show();
                                                                    redirectToLogin();
                                                                } else {
                                                                    Toast.makeText(Profile.this, "Error removing user data from the database", Toast.LENGTH_SHORT).show();
                                                                    // Log the database error for debugging purposes
                                                                    if (task.getException() != null) {
                                                                        Log.e("DeleteAccount", "Database Error: " + task.getException().getMessage());
                                                                    }
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(Profile.this, "Error deleting user account", Toast.LENGTH_SHORT).show();
                                                // Log the authentication error for debugging purposes
                                                if (task.getException() != null) {
                                                    Log.e("DeleteAccount", "Authentication Error: " + task.getException().getMessage());
                                                }
                                            }
                                        }
                                    });
                        }
                    }


                    private void redirectToLogin() {
                        // You can use an Intent to navigate to the login activity
                        Intent intent = new Intent(Profile.this, LoginPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }


                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
            
        });

       
        }

    }

