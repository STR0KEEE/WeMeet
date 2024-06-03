package com.example.schedulingapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManager {

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // Get the current user's username
    public static void getCurrentUserUsername(Callback<String> callback) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // If the user is signed in, retrieve the username
            callback.onSuccess(currentUser.getDisplayName());
        } else {
            // If no user is signed in, return null or handle accordingly
            callback.onFailure("No user signed in.");
        }
    }

    // Callback interface for handling asynchronous results
    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(String errorMessage);
    }
}

