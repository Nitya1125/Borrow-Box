package com.borrowbox.app.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.repository.BorrowRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.models.User;
import com.borrowbox.app.utils.AppExecutors;
import com.borrowbox.app.utils.QrUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class QRVerificationActivity extends AppCompatActivity {

    private Transaction transaction;
    private BorrowRepository borrowRepository;

    private View layoutContainer, layoutSuccess;
    private ImageButton btnBack;
    private ImageView ivItemImage, ivQrCode;
    private ProgressBar progressQr;
    private TextView tvItemName, tvOwnerName, tvTxId, tvInstructions, tvStatusBadge;
    private MaterialButton btnScanQr, btnMarkReturned, btnGoToRate;

    private ActivityResultLauncher<Intent> scanQrLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        borrowRepository = BorrowRepository.getInstance(this);

        transaction = (Transaction) getIntent().getSerializableExtra("transaction");
        if (transaction == null) {
            // Cannot proceed without valid transaction
            finish();
            return;
        }

        scanQrLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Transaction updated = (Transaction) result.getData().getSerializableExtra("transaction");
                        if (updated != null) {
                            transaction = updated;
                            populateData();
                        }
                        Snackbar.make(findViewById(android.R.id.content), R.string.handover_verified_success, Snackbar.LENGTH_LONG).show();
                    }
                }
        );

        initViews();
        populateData();
        generateQrBitmap();
    }

    private void initViews() {
        layoutContainer = findViewById(R.id.layoutQrContainer);
        layoutSuccess = findViewById(R.id.layoutReturnSuccess);
        btnBack = findViewById(R.id.btnQrBack);

        ivItemImage = findViewById(R.id.ivQrItemImage);
        ivQrCode = findViewById(R.id.ivQrCodeBitmap);
        progressQr = findViewById(R.id.progressQrGenerating);

        tvItemName = findViewById(R.id.tvQrItemName);
        tvOwnerName = findViewById(R.id.tvQrOwnerName);
        tvStatusBadge = findViewById(R.id.tvQrStatusBadge);
        tvTxId = findViewById(R.id.tvQrTxId);
        tvInstructions = findViewById(R.id.tvQrInstructions);

        btnScanQr = findViewById(R.id.btnLaunchScanner);
        btnMarkReturned = findViewById(R.id.btnMarkReturned);
        btnGoToRate = findViewById(R.id.btnGoToRate);

        btnBack.setOnClickListener(v -> finish());

        btnScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScanQrActivity.class);
            intent.putExtra(ScanQrActivity.EXTRA_TX_ID, transaction.getId());
            scanQrLauncher.launch(intent);
        });

        btnMarkReturned.setOnClickListener(v -> handleMarkReturned());

        btnGoToRate.setOnClickListener(v -> {
            Intent intent = new Intent(QRVerificationActivity.this, RatingActivity.class);
            intent.putExtra("transaction", transaction);
            startActivity(intent);
            finish();
        });
    }

    private void populateData() {
        String itemName = (transaction.getItem() != null) ? transaction.getItem().getName() : "--";
        tvItemName.setText(itemName);
        tvOwnerName.setText("Counterparty: " + transaction.getPerson());
        tvTxId.setText("Transaction ID: " + transaction.getId());

        String status = transaction.getStatus() != null ? transaction.getStatus().toUpperCase() : "PENDING";
        tvStatusBadge.setText(status);

        if ("borrowed".equalsIgnoreCase(transaction.getStatus())) {
            tvInstructions.setText("Physical handover verified! Return the item on or before the due date.");
            btnMarkReturned.setVisibility(View.VISIBLE);
        } else if ("returned".equalsIgnoreCase(transaction.getStatus())) {
            tvInstructions.setText("Item return has been verified.");
            btnMarkReturned.setVisibility(View.GONE);
            btnScanQr.setVisibility(View.GONE);
        } else {
            tvInstructions.setText("Show this QR verification code during handover. Both parties must be present.");
            btnMarkReturned.setVisibility(View.GONE);
        }

        String photo = (transaction.getItem() != null) ? transaction.getItem().getImageUrl() : "";
        Glide.with(this)
                .load(photo)
                .placeholder(R.drawable.bg_card)
                .into(ivItemImage);
    }

    private void generateQrBitmap() {
        if (progressQr != null) progressQr.setVisibility(View.VISIBLE);

        String payload = QrUtils.createHandoverPayload(transaction.getId(), transaction.getQrToken());

        AppExecutors.getInstance().executeOnDiskIO(() -> {
            Bitmap bitmap = QrUtils.generateQrCode(payload, 500);
            AppExecutors.getInstance().postToMainThread(() -> {
                if (progressQr != null) progressQr.setVisibility(View.GONE);
                if (bitmap != null && ivQrCode != null) {
                    ivQrCode.setImageBitmap(bitmap);
                }
            });
        });
    }

    private void handleMarkReturned() {
        borrowRepository.verifyReturn(transaction.getId(), new RepositoryCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                transaction = result;
                layoutContainer.setVisibility(View.GONE);
                layoutSuccess.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Return failed: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
