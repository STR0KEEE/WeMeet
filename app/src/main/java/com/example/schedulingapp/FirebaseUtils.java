package com.example.schedulingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.schedulingapp.UserSide.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseUtils {

    private static FirebaseAuth firebaseAuth;

    // Initialize Firebase and log out the user
    public static void logout(Context context) {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }

        if (firebaseAuth != null) {
            firebaseAuth.signOut();
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();
            // After logging out, you might want to navigate back to the login screen or perform other actions
            // For example:
            context.startActivity(new Intent(context, LoginActivity.class));
            // Finish the current activity to prevent going back to it using the back button
            ((Activity) context).finish();
        } else {
            Toast.makeText(context, "Firebase not initialized", Toast.LENGTH_SHORT).show();
        }
    }
}
