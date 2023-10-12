package com.example.cashtrack;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UpdateProfile extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

//        Button updateUsernameButton = findViewById(R.id.updateUserNameButton);
//        updateUsernameButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Show a confirmation dialog
//                showDialogConfirmation();
//            }
//
//            private void showDialogConfirmation() {
//                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfile.this);
//                builder.setTitle("Confirm Username Update");
//                builder.setMessage("Are you sure you want to update your username?");
//                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Call a method to update the username
//                        updateUsername();
//                    }
//
//                    private void updateUsername() {
//                        // Get the new username from the EditText
//                        EditText usernameEditText = findViewById(R.id.usernameEditText);
//                        String newUsername = usernameEditText.getText().toString().trim();
//
//                        // Get the current user from Firebase Authentication
//                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                        String currentUsername = user.getDisplayName();
//                        if (user != null) {
//                            final String oldUsername = currentUsername; // Replace with the current username
//                             // Replace with the new desired username
//
//                            // Read data from the old user node
//                            mDatabase.child("users").child(oldUsername).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    // Check if the node exists
//                                    if (dataSnapshot.exists()) {
//                                        // Get the data
//                                        Object userData = dataSnapshot.getValue();
//
//                                        // Create a new user node with the data read from the old node
//                                        mDatabase.child("users").child(newUsername).setValue(userData)
//                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        if (task.isSuccessful()) {
//                                                            // Delete the old user node
//                                                            mDatabase.child("users").child(oldUsername).setValue(null)
//                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                        @Override
//                                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                                            if (task.isSuccessful()) {
//                                                                                Toast.makeText(UpdateProfile.this, "Username changed successfully", Toast.LENGTH_SHORT).show();
//                                                                            } else {
//                                                                                Toast.makeText(UpdateProfile.this, "Failed to change username", Toast.LENGTH_SHORT).show();
//                                                                            }
//                                                                        }
//                                                                    });
//                                                        } else {
//                                                            Toast.makeText(UpdateProfile.this, "Failed to change username", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    }
//                                                });
//                                    } else {
//                                        Toast.makeText(UpdateProfile.this, "User does not exist", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//                                    // Handle any errors if needed
//                                }
//                            });
//                        }
//                    }
//
//
//
//
//
//                });
//                builder.setNegativeButton("Cancel", null);
//                builder.show();
//            }
//        });

        Button updatePasswordButton = findViewById(R.id.updatePasswordButton);
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }

            private void updatePassword() {
                // Get the values from the EditText fields
                EditText oldPasswordEditText = findViewById(R.id.OldpasswordEditText);
                EditText newPasswordEditText = findViewById(R.id.passwordEditText);
                EditText confirmNewPasswordEditText = findViewById(R.id.ConfirmpasswordEditText);

                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();

                // Check if any field is empty
                if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPassword.length() < 6) {
                    Toast.makeText(getApplicationContext(), "New Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the old password matches the user's current password
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Check if the new password and confirm new password match
                                        if (newPassword.equals(confirmNewPassword)) {
                                            // Update the password
                                            user.updatePassword(newPassword)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), "Password updated successfully",
                                                                        Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Failed to update password",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(getApplicationContext(), "New password and confirm new password do not match",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Invalid old password",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

        });

    }

}