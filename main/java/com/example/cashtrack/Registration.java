package com.example.cashtrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class Registration extends AppCompatActivity {
    private TextInputEditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        usernameEditText = findViewById(R.id.UserName1);
        emailEditText = findViewById(R.id.EMail1);
        passwordEditText = findViewById(R.id.Password1);
        confirmPasswordEditText = findViewById(R.id.Password2);
        Button registerButton = findViewById(R.id.registerButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String username = usernameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the username already exists in the database
        usersRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username already exists, prompt the user to change it
                    Toast.makeText(Registration.this, "Username already exists. Please choose a different username.", Toast.LENGTH_SHORT).show();
                } else {
                    // Username is unique, create the user with email and password
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();

                                        if (user != null) {
                                            // Update user's display name
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(username)
                                                    .build();

                                            user.updateProfile(profileUpdates);
                                            // Store user information in the Realtime Database
                                            storeUserData(username, email);

                                            // Send verification email
                                            sendVerificationEmail(user);
                                        }
                                    } else {
                                        Toast.makeText(Registration.this, "Registration failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Registration.this, "Failed to check username availability", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendVerificationEmail(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Registration.this,
                                "Verification email sent. Please check your email.",
                                Toast.LENGTH_SHORT).show();
                        navigateToLoginPage();
                    } else {
                        Toast.makeText(Registration.this,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void navigateToLoginPage() {
        Intent intent = new Intent(Registration.this, LoginPage.class);
        startActivity(intent);
        finish();
    }

    private void storeUserData(final String username, final String email) {
        // Check if the username already exists in the database
        usersRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Username is unique, create the user
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        User newUser = new User(username, email);
                        usersRef.child(username).setValue(newUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Registration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Registration.this, "Failed to Register", Toast.LENGTH_SHORT).show();
                                    }
                                });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Registration.this, "Failed to check username availability", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
