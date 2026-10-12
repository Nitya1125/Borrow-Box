package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.ui.fragments.ActivityFragment;
import com.borrowbox.app.ui.fragments.AddItemFragment;
import com.borrowbox.app.ui.fragments.ExploreFragment;
import com.borrowbox.app.ui.fragments.HomeFragment;
import com.borrowbox.app.ui.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private final Fragment homeFragment = new HomeFragment();
    private final Fragment exploreFragment = new ExploreFragment();
    private final Fragment addFragment = new AddItemFragment();
    private final Fragment activityFragment = new ActivityFragment();
    private final Fragment profileFragment = new ProfileFragment();

    private Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Session check: If no session, redirect to Login
        if (!DataManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setItemActiveIndicatorEnabled(false);

        // Initialize fragments
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, profileFragment, "profile").hide(profileFragment)
                .add(R.id.fragmentContainer, activityFragment, "activity").hide(activityFragment)
                .add(R.id.fragmentContainer, addFragment, "add").hide(addFragment)
                .add(R.id.fragmentContainer, exploreFragment, "explore").hide(exploreFragment)
                .add(R.id.fragmentContainer, homeFragment, "home")
                .commit();

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_explore) {
                switchFragment(exploreFragment);
                return true;
            } else if (itemId == R.id.nav_add) {
                switchFragment(addFragment);
                return true;
            } else if (itemId == R.id.nav_activity) {
                switchFragment(activityFragment);
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(profileFragment);
                return true;
            }
            return false;
        });

        // Handle Back button: return to Home tab if on another tab
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (activeFragment != homeFragment) {
                    bottomNav.setSelectedItemId(R.id.nav_home);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void switchFragment(Fragment target) {
        if (activeFragment != target) {
            getSupportFragmentManager().beginTransaction()
                    .hide(activeFragment)
                    .show(target)
                    .commit();
            activeFragment = target;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!DataManager.getInstance().isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void navigateToHome() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    public void navigateToProfile() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_profile);
        }
    }
}
