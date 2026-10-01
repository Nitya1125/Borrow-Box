package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.Item;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BorrowRequestActivity extends AppCompatActivity {

    private Item item;
    private String selectedDuration = "3 days";

    private View layoutFormContainer, layoutSuccess;
    private ImageButton btnBack;
    private ImageView ivItemImage, ivOwnerAvatar;
    private TextView tvItemName, tvCategory, tvOwnerName;
    private ChipGroup chipGroupDurations;
    private TextView tvCalculatedReturnDate;
    private EditText etReason;
    private MaterialButton btnSend, btnViewActivity;
    private TextView tvBackHome, tvSuccessDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_request);

        item = (Item) getIntent().getSerializableExtra("item");
        if (item == null) {
            item = DataManager.getInstance().getItems().get(0);
        }

        initViews();
        populateItemInfo();
        setupDurationChips();
        updateReturnDate(3);
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

        btnViewActivity = findViewById(R.id.btnViewActivity);
        tvBackHome = findViewById(R.id.tvBorrowBackHome);
        tvSuccessDesc = findViewById(R.id.tvBorrowSuccessDesc);

        btnBack.setOnClickListener(v -> finish());
        tvBackHome.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> {
            String reason = etReason.getText().toString().trim();
            DataManager.getInstance().addBorrowRequest(item, selectedDuration, reason);

            tvSuccessDesc.setText("Waiting for " + item.getOwner().split(" ")[0] + " to respond. We'll notify you shortly.");
            layoutFormContainer.setVisibility(View.GONE);
            layoutSuccess.setVisibility(View.VISIBLE);
        });

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

        Glide.with(this).load(item.getImageUrl()).into(ivItemImage);
        Glide.with(this).load(item.getOwnerAvatar()).into(ivOwnerAvatar);
    }

    private void setupDurationChips() {
        String[] durations = {"1 day", "2 days", "3 days", "5 days", "7 days"};
        for (String d : durations) {
            Chip chip = new Chip(this);
            chip.setText(d);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColorResource(R.color.surface);
            chip.setChipStrokeColorResource(R.color.border_color);
            chip.setChipStrokeWidth(2f);

            if (d.equals(selectedDuration)) {
                chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedDuration = d;
                    int days = Integer.parseInt(d.split(" ")[0]);
                    updateReturnDate(days);
                }
            });
            chipGroupDurations.addView(chip);
        }
    }

    private void updateReturnDate(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        tvCalculatedReturnDate.setText(sdf.format(cal.getTime()));
    }
}
