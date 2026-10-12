package com.borrowbox.app.ui.activities;

import androidx.annotation.OptIn;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.repository.BorrowRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.utils.QrUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScanQrActivity extends AppCompatActivity {

    public static final String EXTRA_TARGET_TX_ID = "target_tx_id";
    public static final String EXTRA_TX_ID = EXTRA_TARGET_TX_ID;

    private PreviewView previewView;
    private ImageButton btnClose;
    private ProgressBar progressBar;

    private ProcessCameraProvider cameraProvider;
    private BarcodeScanner barcodeScanner;
    private ExecutorService analysisExecutor;
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);
    private String expectedTxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        expectedTxId = getIntent().getStringExtra(EXTRA_TARGET_TX_ID);

        previewView = findViewById(R.id.scanPreviewView);
        btnClose = findViewById(R.id.btnScanClose);
        progressBar = findViewById(R.id.progressScanVerifying);

        btnClose.setOnClickListener(v -> finish());

        barcodeScanner = BarcodeScanning.getClient();
        analysisExecutor = Executors.newSingleThreadExecutor();

        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (Exception e) {
                Snackbar.make(previewView, "Failed to start camera for scanning.", Snackbar.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        if (cameraProvider == null) return;
        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(analysisExecutor, this::processImageProxy);

        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            Snackbar.make(previewView, "Error binding scanner camera.", Snackbar.LENGTH_SHORT).show();
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void processImageProxy(@NonNull ImageProxy imageProxy) {
        if (isProcessing.get() || imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        if (rawValue != null && rawValue.startsWith("borrowbox://handover?")) {
                            String txId = QrUtils.parseTransactionIdFromQr(rawValue);
                            String token = QrUtils.parseTokenFromQr(rawValue);

                            if (txId != null && token != null) {
                                if (expectedTxId == null || expectedTxId.equals(txId)) {
                                    if (isProcessing.compareAndSet(false, true)) {
                                        runOnUiThread(() -> verifyScannedCode(txId, token));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void verifyScannedCode(String txId, String token) {
        progressBar.setVisibility(View.VISIBLE);
        String currentUserId = DataManager.getInstance().getCurrentUserId();

        BorrowRepository.getInstance(this).verifyHandoverQr(txId, token, currentUserId, new RepositoryCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                progressBar.setVisibility(View.GONE);
                new MaterialAlertDialogBuilder(ScanQrActivity.this)
                        .setTitle("Handover Confirmed!")
                        .setMessage("QR verification succeeded. The item has been officially transferred.")
                        .setPositiveButton("Done", (dialog, which) -> {
                            Intent intent = new Intent();
                            intent.putExtra("verified_tx", result);
                            setResult(RESULT_OK, intent);
                            finish();
                        })
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onError(Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                new MaterialAlertDialogBuilder(ScanQrActivity.this)
                        .setTitle("Verification Failed")
                        .setMessage(throwable.getMessage() != null ? throwable.getMessage() : "Invalid verification token.")
                        .setPositiveButton("Try Again", (dialog, which) -> isProcessing.set(false))
                        .setNegativeButton("Cancel", (dialog, which) -> finish())
                        .show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (analysisExecutor != null) {
            analysisExecutor.shutdown();
        }
        if (barcodeScanner != null) {
            barcodeScanner.close();
        }
    }
}
