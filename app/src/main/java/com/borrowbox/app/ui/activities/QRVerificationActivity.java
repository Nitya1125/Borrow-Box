package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class QRVerificationActivity extends AppCompatActivity {

    private Item item;

    private View layoutContainer, layoutSuccess;
    private ImageButton btnBack;
    private ImageView ivItemImage;
    private TextView tvItemName, tvOwnerName, tvTxId, tvInstructions;
    private MaterialButton btnMarkReturned, btnGoToRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        item = (Item) getIntent().getSerializableExtra("item");
        if (item == null) {
            item = DataManager.getInstance().getItems().get(0);
        }

        initViews();
        populateData();
    }

    private void initViews() {
        layoutContainer = findViewById(R.id.layoutQrContainer);
        layoutSuccess = findViewById(R.id.layoutReturnSuccess);
        btnBack = findViewById(R.id.btnQrBack);

        ivItemImage = findViewById(R.id.ivQrItemImage);
        tvItemName = findViewById(R.id.tvQrItemName);
        tvOwnerName = findViewById(R.id.tvQrOwnerName);
        tvTxId = findViewById(R.id.tvQrTxId);
        tvInstructions = findViewById(R.id.tvQrInstructions);

        btnMarkReturned = findViewById(R.id.btnMarkReturned);
        btnGoToRate = findViewById(R.id.btnGoToRate);

        btnBack.setOnClickListener(v -> finish());

        btnMarkReturned.setOnClickListener(v -> {
            DataManager.getInstance().markTransactionReturned("t1");
            layoutContainer.setVisibility(View.GONE);
            layoutSuccess.setVisibility(View.VISIBLE);
        });

        btnGoToRate.setOnClickListener(v -> {
            Intent intent = new Intent(QRVerificationActivity.this, RatingActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
            finish();
        });
    }

    private void populateData() {
        tvItemName.setText(item.getName());
        tvOwnerName.setText(item.getOwner());
        tvTxId.setText("Transaction ID: BB-" + item.getId() + "-2026");
        tvInstructions.setText("📋 Show this QR code to " + item.getOwner() + " during handover. Both parties need to be present.");

        Glide.with(this).load(item.getImageUrl()).into(ivItemImage);
    }
}
