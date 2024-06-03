package com.example.schedulingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileFragment extends Fragment {

    private EditText editName, editUsername, editEmail, editAge, editPhone;
    private Button saveButton;
    private TextView editGenderButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String currentUserUid;
    private String selectedGender;
    private LoadingFragment loadingFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserUid = currentUser.getUid();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editName = view.findViewById(R.id.edit_profile_name);
        editUsername = view.findViewById(R.id.edit_profile_username);
        editEmail = view.findViewById(R.id.edit_profile_email);
        editAge = view.findViewById(R.id.edit_profile_age);
        editPhone = view.findViewById(R.id.edit_profile_phone);
        editGenderButton = view.findViewById(R.id.edit_profile_gender_button);
        saveButton = view.findViewById(R.id.save_button);
        loadingFragment = new LoadingFragment();

        loadUserProfile();

        editGenderButton.setOnClickListener(this::showGenderPopupMenu);

        saveButton.setOnClickListener(v -> saveUserProfile());
    }

    private void loadUserProfile() {

        firestore.collection("User").document(currentUserUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editName.setText(documentSnapshot.getString("name"));
                        editUsername.setText(documentSnapshot.getString("username"));
                        editEmail.setText(documentSnapshot.getString("email"));
                        editAge.setText(documentSnapshot.getString("age"));
                        editPhone.setText(documentSnapshot.getString("phonenumber"));
                        selectedGender = documentSnapshot.getString("gender");
                        if (selectedGender != null) {
                            editGenderButton.setText(selectedGender);
                        } else {
                            editGenderButton.setText("Gender");
                        }
                    } else {
                        // Handle document not existing
                    }

                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    hideLoadingFragment();
                });
    }

    private void showGenderPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
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
            editGenderButton.setText(selectedGender);
            return true;
        });

        popupMenu.show();
    }

    private void saveUserProfile() {
        String name = editName.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String age = editAge.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

        if (!isValidEmail(email) || !isValidAge(age)) {
            // Handle validation errors (e.g., show a toast)
            return;
        }

        firestore.collection("User").document(currentUserUid)
                .update("name", name, "username", username, "email", email, "age", age, "phonenumber", phone, "gender", selectedGender)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated profile
                    // Navigate back to UserProfileFragment
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    // Failed to update profile
                    // Handle error (e.g., show a toast)
                });
    }

    private boolean isValidEmail(String email) {
        return email.contains("@"); // Simple validation example
    }

    private boolean isValidAge(String age) {
        try {
            int ageInt = Integer.parseInt(age);
            return ageInt > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showLoadingFragment() {
        if (getActivity() != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.loading_container, loadingFragment);
            transaction.commit();
        }
    }

    // TODO: 16/05/2024 fix loading screen
    private void hideLoadingFragment() {
        if (getActivity() != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(loadingFragment);
            transaction.commit();
        }
    }
}
