package com.example.schedulingapp.UserSide;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.schedulingapp.EditProfileFragment;
import com.example.schedulingapp.LoadingFragment;
import com.example.schedulingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileName, profileUsername, profileEmail, profileAge, profilePhoneNumber, profileGender;
    private Button editProfileButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String currentUserUid; // Store current user's UID
    private LoadingFragment loadingFragment; // Loading fragment instance
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the ActivityResultLauncher
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleGalleryResult);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Get the current user's UID
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserUid = currentUser.getUid();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileUsername = view.findViewById(R.id.profile_username);
        profileEmail = view.findViewById(R.id.profile_email);
        profileAge = view.findViewById(R.id.profile_age);
        profilePhoneNumber = view.findViewById(R.id.profile_phone);
        profileGender = view.findViewById(R.id.profile_gender);
        editProfileButton = view.findViewById(R.id.edit_profile_button);

        // Set up click listener for profile image
        profileImage.setOnClickListener(v -> openGallery());

        editProfileButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Show loading fragment while loading user profile data
        showLoadingFragment();

        // Load user profile data
        loadUserProfile();
    }

    // Method to open the gallery
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    // Handle the result from the gallery
    private void handleGalleryResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri imageUri = result.getData().getData();
            updateProfilePicture(imageUri);
        }
    }

    // Update profile picture
    private void updateProfilePicture(Uri imageUri) {
        // Check if the fragment is attached to an activity and has a valid context
        if (isAdded() && getContext() != null) {
            // Load the selected image into the profile image view
            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(profileImage);

            // Update profile picture in Firebase
            firestore.collection("User").document(currentUserUid)
                    .update("profilePicture", imageUri.toString())
                    .addOnSuccessListener(aVoid -> {
                        // Successfully updated profile picture
                        // You can show a toast or handle the success in any other way
                    })
                    .addOnFailureListener(e -> {
                        // Failed to update profile picture
                        // You can show a toast or handle the failure in any other way
                    });
        } else {
            // Fragment is not attached or context is null, handle this case as needed
        }
    }


    // Method to show loading fragment
    private void showLoadingFragment() {
        loadingFragment = new LoadingFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.loading_container, loadingFragment).commit();
    }

    // Method to hide loading fragment
    private void hideLoadingFragment() {
        if (isAdded() && getContext() != null && loadingFragment != null) {
            getChildFragmentManager().beginTransaction().remove(loadingFragment).commit();
        }
    }

    // Load user profile data
    private void loadUserProfile() {
        if (currentUserUid == null || !isAdded()) {
            // Handle the case where currentUserUid is null or fragment is not added to activity
            return;
        }

        // Retrieve user profile data from Firestore using the UID
        firestore.collection("User").document(currentUserUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && documentSnapshot.exists()) { // Check if fragment is added and document exists
                        // User document exists, retrieve data
                        String name = documentSnapshot.getString("name");
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");
                        String age = documentSnapshot.getString("age");
                        String phone = documentSnapshot.getString("phonenumber");
                        String profilePictureUrl = documentSnapshot.getString("profilePicture");
                        String gender = documentSnapshot.getString("gender");

                        // Set the user profile data to the views
                        profileName.setText(name);
                        profileUsername.setText(username);
                        profileEmail.setText(email);
                        profileAge.setText(age);
                        profilePhoneNumber.setText(phone);
                        profileGender.setText((gender));

                        if (isAdded() && profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            // Load profile picture only if fragment is added
                            Glide.with(this)
                                    .load(profilePictureUrl)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(profileImage);
                        }

                        // Hide loading fragment when data is loaded
                        hideLoadingFragment();
                    } else {
                        // User document does not exist or fragment is not added
                        // Handle this case as needed (e.g., show an error message)
                    }
                })
                .addOnFailureListener(e -> {
                    // Failed to retrieve user profile data
                    // Handle this case as needed (e.g., show an error message)

                    // Hide loading fragment when there's a failure
                    hideLoadingFragment();
                });
    }

}
