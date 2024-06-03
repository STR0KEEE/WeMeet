package com.example.schedulingapp.BusinessSide;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulingapp.R;
import com.example.schedulingapp.UserSide.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BusinessLoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnSwitch;
    private TextView btnForgotPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.editTextUsername_Business);
        editTextPassword = findViewById(R.id.editTextPassword_Business);
        btnLogin = findViewById(R.id.btnLogin_Business);
        btnSignUp = findViewById(R.id.btnSignUp_Business);
        btnSwitch = findViewById(R.id.btnSwitch_Business);
        btnForgotPassword = findViewById(R.id.btnForgotPassword_Business);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessLoginActivity.this, BusinessSignUpActivity.class));
            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BusinessLoginActivity.this, LoginActivity.class));
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forgot password functionality here
                Toast.makeText(BusinessLoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            fetchAndSaveBusinessId(user.getUid());
                        }
                    } else {
                        String errorMessage = "Authentication failed";
                        if (task.getException() != null) {
                            errorMessage += ": " + task.getException().getMessage();
                        }
                        Toast.makeText(BusinessLoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchAndSaveBusinessId(String uid) {
        firestore.collection("Business").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String businessId = document.getString("id");
                            saveBusinessIdInPreferences(businessId);
                            Toast.makeText(BusinessLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(BusinessLoginActivity.this, BusinessNavBar.class));
                            finish();
                        } else {
                            Toast.makeText(BusinessLoginActivity.this, "Business document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BusinessLoginActivity.this, "Failed to fetch business data", Toast.LENGTH_SHORT).show();
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
