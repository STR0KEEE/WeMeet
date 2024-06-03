package com.example.schedulingapp.BusinessSide;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.schedulingapp.R;
import com.example.schedulingapp.UserSide.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class BusinessNavBar extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private ImageButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_nav_bar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        // Set bottom navigation item selected listener
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        // Load the initial fragment (e.g., B_AppointmentsFragment)
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_task_list);
        }

        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle logout
                logout();
            }
        });
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        int itemId = item.getItemId();

        if (itemId == R.id.navigation_task_list) {
            fragment = new B_AppointmentsFragment();
        } else if (itemId == R.id.navigation_profile) {
            fragment = new B_ProfileFragment();
        } else if (itemId == R.id.navigation_add_task) {
            fragment = new B_ScheduleFragment();
        }

        // Replace the fragment with the selected one
        replaceFragment(fragment);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.business_fragment_container, fragment)
                    .commit();
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // For example, sign out the user and navigate to LoginActivity
        startActivity(new Intent(BusinessNavBar.this, LoginActivity.class));
        finish();
    }
}
