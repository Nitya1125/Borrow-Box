package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.borrowbox.app.R;
import com.borrowbox.app.utils.AppExecutors;
import com.borrowbox.app.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;

public class CameraCaptureActivity extends AppCompatActivity {

    public static final String EXTRA_SAVED_IMAGE_PATH = "saved_image_path";
    public static final String EXTRA_IMAGE_PATH = EXTRA_SAVED_IMAGE_PATH;
    public static final String EXTRA_TARGET_DIR = "target_dir";

    private PreviewView previewView;
    private ImageView ivCapturedPreview;
    private ImageButton btnClose, btnFlash, btnSwitch, btnTake;
    private LinearLayout layoutCaptureControls, layoutConfirmControls;
    private MaterialButton btnRetake, btnConfirm;
    private ProgressBar progressBar;

    private ProcessCameraProvider cameraProvider;
    private ImageCapture imageCapture;
    private Camera camera;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private int flashMode = ImageCapture.FLASH_MODE_OFF;

    private File tempFile;
    private String finalProcessedPath;
    private String targetDir = "borrowbox_images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);

        String customDir = getIntent().getStringExtra(EXTRA_TARGET_DIR);
        if (customDir != null && !customDir.isEmpty()) {
            targetDir = customDir;
        }

        initViews();
        startCamera();
    }

    private void initViews() {
        previewView = findViewById(R.id.cameraPreviewView);
        ivCapturedPreview = findViewById(R.id.ivCapturedPhotoPreview);
        btnClose = findViewById(R.id.btnCameraClose);
        btnFlash = findViewById(R.id.btnCameraFlash);
        btnSwitch = findViewById(R.id.btnCameraSwitch);
        btnTake = findViewById(R.id.btnTakePhoto);
        layoutCaptureControls = findViewById(R.id.layoutCaptureControls);
        layoutConfirmControls = findViewById(R.id.layoutConfirmControls);
        btnRetake = findViewById(R.id.btnRetakePhoto);
        btnConfirm = findViewById(R.id.btnConfirmPhoto);
        progressBar = findViewById(R.id.progressCameraProcessing);

        btnClose.setOnClickListener(v -> finish());

        btnFlash.setOnClickListener(v -> {
            if (flashMode == ImageCapture.FLASH_MODE_OFF) {
                flashMode = ImageCapture.FLASH_MODE_ON;
                btnFlash.setImageResource(R.drawable.ic_flash_on);
            } else {
                flashMode = ImageCapture.FLASH_MODE_OFF;
                btnFlash.setImageResource(R.drawable.ic_flash_off);
            }
            if (imageCapture != null) {
                imageCapture.setFlashMode(flashMode);
            }
        });

        btnSwitch.setOnClickListener(v -> {
            lensFacing = (lensFacing == CameraSelector.LENS_FACING_BACK)
                    ? CameraSelector.LENS_FACING_FRONT
                    : CameraSelector.LENS_FACING_BACK;
            bindCameraUseCases();
        });

        btnTake.setOnClickListener(v -> takePhoto());

        btnRetake.setOnClickListener(v -> {
            ivCapturedPreview.setVisibility(View.GONE);
            previewView.setVisibility(View.VISIBLE);
            layoutConfirmControls.setVisibility(View.GONE);
            layoutCaptureControls.setVisibility(View.VISIBLE);
            if (tempFile != null) {
                tempFile.delete();
            }
        });

        btnConfirm.setOnClickListener(v -> {
            if (finalProcessedPath != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_SAVED_IMAGE_PATH, finalProcessedPath);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (Exception e) {
                Snackbar.make(previewView, "Unable to initialize camera.", Snackbar.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        if (cameraProvider == null) return;
        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setFlashMode(flashMode)
                .build();

        try {
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        } catch (Exception e) {
            Snackbar.make(previewView, "Error binding camera lens.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        tempFile = ImageUtils.createTempCameraImageFile(this);
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(tempFile).build();

        progressBar.setVisibility(View.VISIBLE);
        btnTake.setEnabled(false);

        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = Uri.fromFile(tempFile);
                        AppExecutors.getInstance().executeOnDiskIO(() -> {
                            try {
                                String processedPath = ImageUtils.processAndSaveImage(
                                        CameraCaptureActivity.this,
                                        savedUri,
                                        targetDir,
                                        "capture"
                                );
                                finalProcessedPath = processedPath;

                                AppExecutors.getInstance().postToMainThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    btnTake.setEnabled(true);
                                    previewView.setVisibility(View.GONE);
                                    ivCapturedPreview.setVisibility(View.VISIBLE);
                                    ivCapturedPreview.setImageURI(Uri.fromFile(new File(processedPath)));
                                    layoutCaptureControls.setVisibility(View.GONE);
                                    layoutConfirmControls.setVisibility(View.VISIBLE);
                                });
                            } catch (Exception e) {
                                AppExecutors.getInstance().postToMainThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    btnTake.setEnabled(true);
                                    Snackbar.make(previewView, "Error processing image: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        progressBar.setVisibility(View.GONE);
                        btnTake.setEnabled(true);
                        Snackbar.make(previewView, "Capture failed: " + exception.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }
}
