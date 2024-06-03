package com.example.schedulingapp.BusinessSide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schedulingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BusinessSignUpActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextType;
    private EditText editTextAbout;
    private Button btnSignUp;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_sign_up);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.editTextBusinessName);
        editTextType = findViewById(R.id.editTextBusinessType);
        editTextEmail = findViewById(R.id.editTextBusinessEmail);
        editTextPassword = findViewById(R.id.editTextBusinessPassword);
        editTextAbout = findViewById(R.id.editTextAbout);

        btnSignUp = findViewById(R.id.btnSignUp_Business);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        String name = editTextName.getText().toString().trim();
        String type = editTextType.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String about = editTextAbout.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Sign up with email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success
                            Toast.makeText(BusinessSignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();

                            // Store user information in Firestore
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                storeBusinessInfoInFirestore(user.getUid(), name, type, email, about);
                            }

                            // Navigate to the main activity or wherever needed
                            // For example:
                            startActivity(new Intent(BusinessSignUpActivity.this, BusinessNavBar.class));
                            finish();
                        } else {
                            // If sign up fails, display a message to the user.
                            Exception exception = task.getException();
                            if (exception != null) {
                                Toast.makeText(BusinessSignUpActivity.this, "Sign up failed: " + exception.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                // Log the error for more details
                                Log.e("FirebaseAuth", "Sign up failed", exception);
                            } else {
                                Toast.makeText(BusinessSignUpActivity.this, "Sign up failed: An unknown error occurred",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void storeBusinessInfoInFirestore(String uid, String name, String type, String email, String about) {
        // Map to store user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", uid);
        userData.put("name", name);
        userData.put("type", type);
        userData.put("email", email);
        userData.put("about", about);

        // Store user data in a common user collection
        firestore.collection("Business").document(uid)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // User data saved successfully
                            Toast.makeText(BusinessSignUpActivity.this, "User data saved to Firestore", Toast.LENGTH_SHORT).show();
                            saveBusinessIdInPreferences(uid);  // Save business ID in SharedPreferences
                        } else {
                            // Failed to save user data
                            Toast.makeText(BusinessSignUpActivity.this, "Failed to save user data to Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveBusinessIdInPreferences(String businessId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("BUSINESS_ID", businessId);
        editor.apply();
    }
}
