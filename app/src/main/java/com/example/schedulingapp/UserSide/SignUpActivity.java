package com.example.schedulingapp.UserSide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schedulingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextName, editTextUsername, editTextAge, editTextEmail, editTextPassword, editTextPhonenumber;
    private TextView textViewGender;
    private Button btnSignUp;
    private String selectedGender;
    private TextView genderTextView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Authentication and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Find Views
        editTextName = findViewById(R.id.editTextName);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextAge = findViewById(R.id.editTextAge);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhonenumber = findViewById(R.id.editTextPhoneNumber);
        textViewGender = findViewById(R.id.textViewGender);
        genderTextView = findViewById(R.id.textViewGender);

        btnSignUp = findViewById(R.id.btnSignUp);

        genderTextView.setOnClickListener(this::showGenderPopupMenu);

        // Set Click Listener for Sign Up Button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextName==null||editTextUsername==null||editTextAge==null||editTextEmail==null||editTextPassword==null||editTextPhonenumber==null){
                    Toast.makeText(SignUpActivity.this, "please fill all fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    signUp();
                }
            }
        });
    }

    private void showGenderPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(SignUpActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.gender_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.gender_male) {
                selectedGender = "Male";
            } else if (itemId == R.id.gender_female) {
                selectedGender = "Female";
            } else if (itemId == R.id.gender_prefer_not_to_say) {
                selectedGender = "Prefer not to say";
            }
            textViewGender.setText(selectedGender);
            return true;
        });

        popupMenu.show();
    }

    // Method to handle the sign-up process
    private void signUp() {
        String name = editTextName.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String phonenumber = editTextPhonenumber.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String gender = textViewGender.getText().toString();

        // Validate input fields here if needed

        // Firebase Authentication to create a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, save user profile to Firestore
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            UserProfile userProfile = new UserProfile(name, username, email, phonenumber, age, "drawable/default_profile_picture.png", uid, gender);

                            // Create a map to store the user data
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("id", uid);
                            userData.put("name", name);
                            userData.put("username", username);
                            userData.put("phonenumber", phonenumber);
                            userData.put("age", age);
                            userData.put("email", email);
                            userData.put("gender", gender);

                            saveUserProfile(uid, userData);

                            // Navigate to the main screen (NavBarActivity)
                            startActivity(new Intent(SignUpActivity.this, NavBarActivity.class));
                            finish();
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Failed to sign up. Please try again.", Toast.LENGTH_SHORT).show();
                            if (task.getException() != null) {
                                task.getException().printStackTrace();
                            }
                        }
                    }
                });
    }

    // Method to save user profile to Firestore
    private void saveUserProfile(String uid, Map<String, Object> userData) {
        firestore.collection("User").document(uid)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Profile created successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Failed to create profile.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
