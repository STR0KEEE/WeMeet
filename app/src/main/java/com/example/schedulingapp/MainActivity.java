package com.example.schedulingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.schedulingapp.BusinessSide.BusinessNavBar;
import com.example.schedulingapp.UserSide.LoginActivity;
import com.example.schedulingapp.UserSide.NavBarActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            navigateToAppropriateActivity(user.getUid());
        } else {
            // Navigate to the login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void navigateToAppropriateActivity(String uid) {
        // First, check the User collection
        firestore.collection("User").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // User found in User collection
                            startActivity(new Intent(MainActivity.this, NavBarActivity.class));
                            finish();
                        } else {
                            // User not found in User collection, check Business collection
                            checkBusinessCollection(uid);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to retrieve user type", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    private void checkBusinessCollection(String uid) {
        firestore.collection("Business").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // User found in Business collection
                            startActivity(new Intent(MainActivity.this, BusinessNavBar.class));
                        } else {
                            // User not found in either collection
                            Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to retrieve user type", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }
}
