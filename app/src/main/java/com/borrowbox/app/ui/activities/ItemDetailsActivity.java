package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.Item;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

public class ItemDetailsActivity extends AppCompatActivity {

    private Item item;

    private ImageView ivHero, ivOwnerAvatar;
    private ImageButton btnBack, btnBookmark, btnShare;
    private TextView tvStatusBadge, tvItemName, tvCategory, tvDistance;
    private TextView tvDescription, tvOwnerName, tvOwnerCollege, tvOwnerRating;
    private TextView tvMaxDuration, tvPickupMethod;
    private MaterialButton btnRequestBorrow, btnMessageOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        item = (Item) getIntent().getSerializableExtra("item");
        if (item == null) {
            item = DataManager.getInstance().getItems().get(0);
        }

        initViews();
        populateData();
    }

    private void initViews() {
        ivHero = findViewById(R.id.ivDetailHeroImage);
        ivOwnerAvatar = findViewById(R.id.ivDetailOwnerAvatar);
        btnBack = findViewById(R.id.btnDetailBack);
        btnBookmark = findViewById(R.id.btnDetailBookmark);
        btnShare = findViewById(R.id.btnDetailShare);

        tvStatusBadge = findViewById(R.id.tvDetailStatusBadge);
        tvItemName = findViewById(R.id.tvDetailItemName);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDistance = findViewById(R.id.tvDetailDistance);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvOwnerName = findViewById(R.id.tvDetailOwnerName);
        tvOwnerCollege = findViewById(R.id.tvDetailOwnerCollege);
        tvOwnerRating = findViewById(R.id.tvDetailOwnerRating);
        tvMaxDuration = findViewById(R.id.tvDetailMaxDuration);
        tvPickupMethod = findViewById(R.id.tvDetailPickupMethod);
        btnRequestBorrow = findViewById(R.id.btnRequestBorrow);
        btnMessageOwner = findViewById(R.id.btnMessageOwner);

        btnBack.setOnClickListener(v -> finish());

        boolean isSaved = DataManager.getInstance().isSaved(item.getId());
        updateBookmarkUI(isSaved);

        btnBookmark.setOnClickListener(v -> {
            DataManager.getInstance().toggleSaved(item.getId());
            boolean newState = DataManager.getInstance().isSaved(item.getId());
            updateBookmarkUI(newState);
            Toast.makeText(this, newState ? "Saved to Wishlist" : "Removed from Wishlist", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + item.getName() + " on BorrowBox!");
            startActivity(Intent.createChooser(shareIntent, "Share Item"));
        });

        btnMessageOwner.setOnClickListener(v -> {
            Toast.makeText(this, "Opening chat with " + item.getOwner() + "...", Toast.LENGTH_SHORT).show();
        });

        btnRequestBorrow.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailsActivity.this, BorrowRequestActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
        });
    }

    private void updateBookmarkUI(boolean saved) {
        if (saved) {
            btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
            btnBookmark.setColorFilter(ContextCompat.getColor(this, R.color.primary));
        } else {
            btnBookmark.setImageResource(R.drawable.ic_bookmark);
            btnBookmark.setColorFilter(ContextCompat.getColor(this, R.color.text_primary));
        }
    }

    private void populateData() {
        tvItemName.setText(item.getName());
        tvCategory.setText(item.getCategory());
        tvDistance.setText(item.getDistance() + " away");
        tvDescription.setText(item.getDescription());
        tvOwnerName.setText(item.getOwner());
        tvOwnerCollege.setText(item.getOwnerCollege());
        tvOwnerRating.setText(String.valueOf(item.getOwnerRating()));
        tvMaxDuration.setText(item.getMaxDuration());
        tvPickupMethod.setText(item.getPickupMethod());

        if (item.isAvailable()) {
            tvStatusBadge.setText("● Available to Borrow");
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.teal_success));
            tvStatusBadge.setBackgroundResource(R.drawable.badge_available);
            btnRequestBorrow.setEnabled(true);
            btnRequestBorrow.setText(R.string.request_to_borrow);
            btnRequestBorrow.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary));
        } else {
            tvStatusBadge.setText("● Currently Borrowed");
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.coral_error));
            tvStatusBadge.setBackgroundResource(R.drawable.badge_borrowed);
            btnRequestBorrow.setEnabled(false);
            btnRequestBorrow.setText(R.string.currently_unavailable);
            btnRequestBorrow.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.border_color));
        }

        Glide.with(this)
                .load(item.getImageUrl())
                .placeholder(R.drawable.bg_card)
                .into(ivHero);

        Glide.with(this)
                .load(item.getOwnerAvatar())
                .placeholder(R.drawable.ic_profile)
                .into(ivOwnerAvatar);
    }
}
