package com.example.cashtrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginPage extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        emailEditText = findViewById(R.id.EMail);
        passwordEditText = findViewById(R.id.Password);
        Button loginButton = findViewById(R.id.Login);
        firebaseAuth = FirebaseAuth.getInstance();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    loginUser();
                } else {
                    showNetworkAlert();
                }
            }

            private void showNetworkAlert() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginPage.this);
                builder.setTitle("No Network Connection");
                builder.setMessage("Please check your internet connection and try again.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            private boolean isNetworkAvailable() {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    return networkInfo != null && networkInfo.isConnected();
                }
                return false;
            }
        });

        Button button = findViewById(R.id.Register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this,Registration.class);
                startActivity(intent);
            }
        });

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            navigateToMainInterface();
        }
    }


    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in user with email and password
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user != null) {
                                // Check if user's email is verified
                                if (user.isEmailVerified()) {
                                    // Navigate to main interface page after successful login
                                    navigateToMainInterface();
                                } else {
                                    Toast.makeText(LoginPage.this, "Please verify your email",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(LoginPage.this, "Login failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void navigateToMainInterface() {
        Intent intent = new Intent(LoginPage.this, MainInterface.class);
        startActivity(intent);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}

