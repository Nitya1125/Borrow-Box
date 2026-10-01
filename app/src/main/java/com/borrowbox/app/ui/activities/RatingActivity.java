package com.borrowbox.app.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.borrowbox.app.R;
import com.borrowbox.app.models.Item;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class RatingActivity extends AppCompatActivity {

    private int selectedRating = 5;
    private final String[] ratingLabels = {"", "Terrible", "Poor", "Okay", "Great", "Excellent!"};

    private View layoutRatingForm, layoutThankYou;
    private ImageButton btnBack;
    private ImageView ivItemImage;
    private TextView tvItemName, tvOwnerName, tvScoreLabel;
    private ImageButton star1, star2, star3, star4, star5;
    private EditText etReview;
    private ChipGroup chipGroupTags;
    private MaterialButton btnSubmit, btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        initViews();
        setupStars();
        setupTags();
    }

    private void initViews() {
        layoutRatingForm = findViewById(R.id.layoutRatingForm);
        layoutThankYou = findViewById(R.id.layoutRatingThankYou);
        btnBack = findViewById(R.id.btnRatingBack);

        ivItemImage = findViewById(R.id.ivRatingItemImage);
        tvItemName = findViewById(R.id.tvRatingItemName);
        tvOwnerName = findViewById(R.id.tvRatingOwnerName);
        tvScoreLabel = findViewById(R.id.tvRatingScoreLabel);

        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);

        etReview = findViewById(R.id.etReviewText);
        chipGroupTags = findViewById(R.id.chipGroupReviewTags);
        btnSubmit = findViewById(R.id.btnSubmitReview);
        btnDone = findViewById(R.id.btnRatingDone);

        btnBack.setOnClickListener(v -> finish());
        btnDone.setOnClickListener(v -> finish());

        // Default item image
        Glide.with(this)
                .load("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=300&fit=crop&auto=format")
                .into(ivItemImage);

        btnSubmit.setOnClickListener(v -> {
            if (selectedRating == 0) {
                Toast.makeText(this, "Please select a star rating", Toast.LENGTH_SHORT).show();
                return;
            }
            layoutRatingForm.setVisibility(View.GONE);
            layoutThankYou.setVisibility(View.VISIBLE);
        });
    }

    private void setupStars() {
        star1.setOnClickListener(v -> setRating(1));
        star2.setOnClickListener(v -> setRating(2));
        star3.setOnClickListener(v -> setRating(3));
        star4.setOnClickListener(v -> setRating(4));
        star5.setOnClickListener(v -> setRating(5));

        setRating(5);
    }

    private void setRating(int stars) {
        selectedRating = stars;
        star1.setImageResource(stars >= 1 ? R.drawable.ic_star_filled : R.drawable.ic_star);
        star2.setImageResource(stars >= 2 ? R.drawable.ic_star_filled : R.drawable.ic_star);
        star3.setImageResource(stars >= 3 ? R.drawable.ic_star_filled : R.drawable.ic_star);
        star4.setImageResource(stars >= 4 ? R.drawable.ic_star_filled : R.drawable.ic_star);
        star5.setImageResource(stars >= 5 ? R.drawable.ic_star_filled : R.drawable.ic_star);

        tvScoreLabel.setText(ratingLabels[stars]);
    }

    private void setupTags() {
        String[] tags = {"Good condition", "Punctual", "Friendly", "Accurate description", "Would borrow again"};
        for (String tag : tags) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColorResource(R.color.surface);
            chip.setChipStrokeColorResource(R.color.border_color);
            chip.setChipStrokeWidth(2f);
            chipGroupTags.addView(chip);
        }
    }
}
