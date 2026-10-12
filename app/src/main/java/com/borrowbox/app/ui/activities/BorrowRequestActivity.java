package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.repository.BorrowRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.models.User;
import com.borrowbox.app.utils.AvatarDrawable;
import com.borrowbox.app.utils.DateTimeUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class BorrowRequestActivity extends AppCompatActivity {

    private Item item;
    private int selectedDurationDays = 3;
    private boolean isSubmitting = false;

    private BorrowRepository borrowRepository;

    private View layoutFormContainer, layoutSuccess;
    private ImageButton btnBack;
    private ImageView ivItemImage;
    private ShapeableImageView ivOwnerAvatar, ivBorrowerAvatar;
    private TextView tvItemName, tvCategory, tvOwnerName;
    private ChipGroup chipGroupDurations;
    private TextView tvCalculatedReturnDate;
    private EditText etReason;
    private TextView tvBorrowerName, tvBorrowerCollege, tvBorrowerRating;
    private MaterialButton btnSend, btnViewActivity;
    private TextView tvBackHome, tvSuccessDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_request);

        borrowRepository = BorrowRepository.getInstance(this);

        item = (Item) getIntent().getSerializableExtra("item");
        if (item == null) {
            finish();
            return;
        }

        initViews();
        populateItemInfo();
        populateBorrowerInfo();
        setupDurationChips();
        updateReturnDate(selectedDurationDays);
    }

    private void initViews() {
        layoutFormContainer = findViewById(R.id.layoutBorrowFormContainer);
        layoutSuccess = findViewById(R.id.layoutBorrowSuccess);
        btnBack = findViewById(R.id.btnBorrowBack);

        ivItemImage = findViewById(R.id.ivBorrowItemImage);
        ivOwnerAvatar = findViewById(R.id.ivBorrowOwnerAvatar);
        tvItemName = findViewById(R.id.tvBorrowItemName);
        tvCategory = findViewById(R.id.tvBorrowCategory);
        tvOwnerName = findViewById(R.id.tvBorrowOwnerName);

        chipGroupDurations = findViewById(R.id.chipGroupBorrowDurations);
        tvCalculatedReturnDate = findViewById(R.id.tvCalculatedReturnDate);
        etReason = findViewById(R.id.etBorrowReason);
        btnSend = findViewById(R.id.btnSendBorrowRequest);

        ivBorrowerAvatar = findViewById(R.id.ivBorrowerAvatar);
        tvBorrowerName = findViewById(R.id.tvBorrowerName);
        tvBorrowerCollege = findViewById(R.id.tvBorrowerCollege);
        tvBorrowerRating = findViewById(R.id.tvBorrowerRating);

        btnViewActivity = findViewById(R.id.btnViewActivity);
        tvBackHome = findViewById(R.id.tvBorrowBackHome);
        tvSuccessDesc = findViewById(R.id.tvBorrowSuccessDesc);

        btnBack.setOnClickListener(v -> finish());
        tvBackHome.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> submitBorrowRequest());

        btnViewActivity.setOnClickListener(v -> {
            Intent intent = new Intent(BorrowRequestActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void populateItemInfo() {
        tvItemName.setText(item.getName());
        tvCategory.setText(item.getCategory());
        tvOwnerName.setText(item.getOwner());

        Glide.with(this)
                .load(item.getImageUrl())
                .placeholder(R.drawable.bg_card)
                .into(ivItemImage);

        String ownerAvatar = item.getOwnerAvatar();
        if (ownerAvatar != null && !ownerAvatar.isEmpty()) {
            Glide.with(this)
                    .load(ownerAvatar)
                    .placeholder(new AvatarDrawable(item.getOwner()))
                    .into(ivOwnerAvatar);
        } else {
            ivOwnerAvatar.setImageDrawable(new AvatarDrawable(item.getOwner()));
        }
    }

    private void populateBorrowerInfo() {
        User user = DataManager.getInstance().getCurrentUser();
        if (user == null) return;

        tvBorrowerName.setText(user.getName());
        tvBorrowerCollege.setText(user.getCollege() != null && !user.getCollege().isEmpty() ? user.getCollege() : "Campus");
        tvBorrowerRating.setText(String.format(Locale.getDefault(), "%.1f", user.getRating()));

        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatarUrl())
                    .placeholder(new AvatarDrawable(user.getName()))
                    .into(ivBorrowerAvatar);
        } else {
            ivBorrowerAvatar.setImageDrawable(new AvatarDrawable(user.getName()));
        }
    }

    private void setupDurationChips() {
        int maxDays = item.getMaxDurationDays() > 0 ? item.getMaxDurationDays() : 7;
        int[] availableDays = {1, 2, 3, 5, 7, 14, 30};

        chipGroupDurations.removeAllViews();
        boolean firstSet = false;

        for (int d : availableDays) {
            if (d <= maxDays) {
                Chip chip = new Chip(this);
                chip.setText(d + (d == 1 ? " day" : " days"));
                chip.setCheckable(true);
                chip.setClickable(true);
                chip.setChipBackgroundColorResource(R.color.surface);

                if (!firstSet || d == Math.min(3, maxDays)) {
                    chip.setChecked(true);
                    selectedDurationDays = d;
                    firstSet = true;
                }

                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedDurationDays = d;
                        updateReturnDate(d);
                    }
                });
                chipGroupDurations.addView(chip);
            }
        }
    }

    private void updateReturnDate(int days) {
        long dueTimestamp = DateTimeUtils.calculateDueTimestamp(System.currentTimeMillis(), days);
        tvCalculatedReturnDate.setText(DateTimeUtils.formatDate(dueTimestamp));
    }

    private void submitBorrowRequest() {
        if (isSubmitting) return;

        User user = DataManager.getInstance().getCurrentUser();
        if (user == null) return;

        // Business rule check
        if (item.getOwnerId() != null && item.getOwnerId().equals(user.getId())) {
            Snackbar.make(findViewById(android.R.id.content), "You cannot borrow your own listed item.", Snackbar.LENGTH_LONG).show();
            return;
        }

        isSubmitting = true;
        btnSend.setEnabled(false);

        String message = etReason.getText().toString().trim();

        borrowRepository.requestBorrow(item, user.getId(), user.getName(), selectedDurationDays, message, new RepositoryCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                isSubmitting = false;
                String ownerFirst = item.getOwner() != null ? item.getOwner().split(" ")[0] : "the owner";
                tvSuccessDesc.setText("Waiting for " + ownerFirst + " to respond. We'll notify you as soon as they approve.");
                layoutFormContainer.setVisibility(View.GONE);
                layoutSuccess.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable t) {
                isSubmitting = false;
                btnSend.setEnabled(true);
                Snackbar.make(findViewById(android.R.id.content), "Request failed: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
