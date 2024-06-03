package com.example.schedulingapp.UserSide;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.schedulingapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class NavBarActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_bar);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        fragmentManager = getSupportFragmentManager();

        // Set bottom navigation item selected listener
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        // Load the initial fragment (e.g., HomeFragment)
        replaceFragment(new HomeFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(NavBarActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        int itemId = item.getItemId();

        if (itemId == R.id.menu_home) {
            fragment = new HomeFragment();
        }
        else if (itemId == R.id.menu_search) {
            fragment = new SearchFragment();
        }
        else if (itemId == R.id.menu_schedule){
            fragment = new ScheduleFragment();
        }
        else if (itemId == R.id.menu_profile) {
            fragment = new UserProfileFragment();
        }

        // Replace the fragment with the selected one
        replaceFragment(fragment);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss();
        }
    }
}
