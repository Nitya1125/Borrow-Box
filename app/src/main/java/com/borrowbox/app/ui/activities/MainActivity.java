package com.borrowbox.app.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.borrowbox.app.R;
import com.borrowbox.app.ui.fragments.ActivityFragment;
import com.borrowbox.app.ui.fragments.AddItemFragment;
import com.borrowbox.app.ui.fragments.ExploreFragment;
import com.borrowbox.app.ui.fragments.HomeFragment;
import com.borrowbox.app.ui.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Fragment homeFragment = new HomeFragment();
    private Fragment exploreFragment = new ExploreFragment();
    private Fragment addFragment = new AddItemFragment();
    private Fragment activityFragment = new ActivityFragment();
    private Fragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNavigation);

        // Initial default fragment
        loadFragment(homeFragment);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_explore) {
                loadFragment(exploreFragment);
                return true;
            } else if (itemId == R.id.nav_add) {
                loadFragment(addFragment);
                return true;
            } else if (itemId == R.id.nav_activity) {
                loadFragment(activityFragment);
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(profileFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
