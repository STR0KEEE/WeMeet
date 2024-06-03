package com.example.schedulingapp.UserSide;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schedulingapp.BusinessSide.BusinessLoginActivity;
import com.example.schedulingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button btnLogin, btnSignUp, btnSwitch;
    private TextView btnForgotPassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if the user is already signed in
        if (firebaseAuth.getCurrentUser() != null) {
            // User is already signed in, navigate to the main activity or wherever needed
            startActivity(new Intent(LoginActivity.this, NavBarActivity.class));
            finish();
        }

        // Initialize Views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSwitch = findViewById(R.id.btnSwitch);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        // Set click listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to BusinessLoginActivity
                startActivity(new Intent(LoginActivity.this, BusinessLoginActivity.class));
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forgot password, for example, show a dialog or navigate to a forgot password activity
                Toast.makeText(LoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login() {
        // Get user input
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            return;
        }

        // Sign in with email and password
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Navigate to the main activity or wherever needed
                            startActivity(new Intent(LoginActivity.this, NavBarActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            String errorMessage = "Login failed";
                            if (task.getException() != null) {
                                errorMessage += ": " + task.getException().getMessage();
                            }
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
