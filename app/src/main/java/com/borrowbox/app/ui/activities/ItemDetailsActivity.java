package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.repository.ItemRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.User;
import com.borrowbox.app.utils.AvatarDrawable;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class ItemDetailsActivity extends AppCompatActivity {

    private Item item;
    private ItemRepository itemRepository;

    private ImageView ivHero, ivOwnerAvatar;
    private ImageButton btnBack, btnBookmark, btnShare;
    private TextView tvStatusBadge, tvItemName, tvCategory, tvDistance;
    private TextView tvDescription, tvOwnerName, tvOwnerCollege, tvOwnerRating;
    private TextView tvMaxDuration, tvPickupMethod;
    private MaterialButton btnRequestBorrow, btnMessageOwner;

    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        itemRepository = ItemRepository.getInstance(this);

        item = (Item) getIntent().getSerializableExtra("item");
        if (item == null) {
            finish();
            return;
        }

        initViews();
        populateData();
        checkBookmarkStatus();
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

        btnBookmark.setOnClickListener(v -> toggleBookmark());

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + item.getName() + " on BorrowBox!");
            startActivity(Intent.createChooser(shareIntent, "Share Item"));
        });

        btnMessageOwner.setOnClickListener(v -> {
            Snackbar.make(findViewById(android.R.id.content), "Direct chat with " + item.getOwner() + " available during active borrow.", Snackbar.LENGTH_SHORT).show();
        });

        btnRequestBorrow.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailsActivity.this, BorrowRequestActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
        });
    }

    private void checkBookmarkStatus() {
        User user = DataManager.getInstance().getCurrentUser();
        if (user == null) return;

        itemRepository.isItemSaved(user.getId(), item.getId(), new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean saved) {
                isSaved = saved;
                updateBookmarkUI(saved);
            }

            @Override
            public void onError(Throwable t) {
                // Ignore
            }
        });
    }

    private void toggleBookmark() {
        User user = DataManager.getInstance().getCurrentUser();
        if (user == null) return;

        btnBookmark.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(120)
                .withEndAction(() -> btnBookmark.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .start())
                .start();

        itemRepository.toggleSaved(user.getId(), item.getId(), new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean saved) {
                isSaved = saved;
                updateBookmarkUI(saved);
                Snackbar.make(findViewById(android.R.id.content),
                        saved ? R.string.saved_to_wishlist : R.string.removed_from_wishlist,
                        Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Could not update bookmark.", Snackbar.LENGTH_SHORT).show();
            }
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

        String pickup = item.getPickupLocation();
        tvDistance.setText(pickup != null && !pickup.isEmpty() ? pickup : "Campus Pickup");

        tvDescription.setText(item.getDescription());
        tvOwnerName.setText(item.getOwner());
        tvOwnerCollege.setText(item.getOwnerCollege());
        tvOwnerRating.setText(String.format(Locale.getDefault(), "%.1f", item.getOwnerRating()));

        tvMaxDuration.setText(item.getMaxDurationDays() + (item.getMaxDurationDays() == 1 ? " day" : " days"));
        tvPickupMethod.setText(pickup != null && !pickup.isEmpty() ? pickup : "Campus Pickup");

        User currentUser = DataManager.getInstance().getCurrentUser();
        boolean isOwnItem = (currentUser != null && currentUser.getId().equals(item.getOwnerId()));

        if (isOwnItem) {
            tvStatusBadge.setText("● Your Listed Item");
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.primary));
            tvStatusBadge.setBackgroundResource(R.drawable.badge_approved);
            btnRequestBorrow.setEnabled(false);
            btnRequestBorrow.setText("You own this item");
            btnRequestBorrow.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.border_color));
        } else if (item.isAvailable()) {
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

        String avatarUrl = item.getOwnerAvatar();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(new AvatarDrawable(item.getOwner()))
                    .error(new AvatarDrawable(item.getOwner()))
                    .into(ivOwnerAvatar);
        } else {
            ivOwnerAvatar.setImageDrawable(new AvatarDrawable(item.getOwner()));
        }
    }
}
