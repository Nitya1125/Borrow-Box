package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.OnboardingPagerAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.OnboardingSlide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout layoutDots;
    private MaterialButton btnAction;
    private TextView btnSkip;
    private OnboardingPagerAdapter adapter;
    private List<OnboardingSlide> slides = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPagerOnboarding);
        layoutDots = findViewById(R.id.layoutDots);
        btnAction = findViewById(R.id.btnOnboardingAction);
        btnSkip = findViewById(R.id.btnSkip);

        initSlides();

        adapter = new OnboardingPagerAdapter(this, slides);
        viewPager.setAdapter(adapter);

        setupDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setupDots(position);
                if (position == slides.size() - 1) {
                    btnAction.setText(R.string.get_started);
                    btnSkip.setVisibility(View.GONE);
                } else {
                    btnAction.setText(R.string.next);
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSkip.setOnClickListener(v -> navigateToLogin());

        btnAction.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < slides.size() - 1) {
                viewPager.setCurrentItem(current + 1, true);
            } else {
                navigateToLogin();
            }
        });
    }

    private void initSlides() {
        slides.add(new OnboardingSlide(
                "Borrow what\nyou need.",
                "Find items from students near you and borrow them for free. No cost, just community.",
                R.drawable.ic_bb_logo,
                "#4F6AF0",
                "#EEF1FF",
                "#E8F3FF"
        ));
        slides.add(new OnboardingSlide(
                "Share what\nyou have.",
                "List your items in seconds and help fellow students when they need it most.",
                R.drawable.ic_bb_logo,
                "#3BB8A2",
                "#E6F7F4",
                "#EEF1FF"
        ));
        slides.add(new OnboardingSlide(
                "Return with\nease.",
                "QR-verified handovers make borrowing safe and trustworthy for everyone.",
                R.drawable.ic_bb_logo,
                "#9585E0",
                "#F0EDFC",
                "#FEF8E7"
        ));
    }

    private void setupDots(int activePosition) {
        layoutDots.removeAllViews();
        for (int i = 0; i < slides.size(); i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    i == activePosition ? 48 : 20, 20
            );
            params.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.badge_approved);
            if (i == activePosition) {
                dot.setBackgroundColor(Color.parseColor(slides.get(activePosition).getAccentColor()));
            } else {
                dot.setBackgroundColor(ContextCompat.getColor(this, R.color.border_color));
            }
            layoutDots.addView(dot);
        }
    }

    private void navigateToLogin() {
        DataManager.getInstance().setOnboardingCompleted(true);
        Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
