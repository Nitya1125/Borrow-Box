package com.borrowbox.app.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.repository.ItemRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Category;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.User;
import com.borrowbox.app.ui.activities.CameraCaptureActivity;
import com.borrowbox.app.ui.activities.MainActivity;
import com.borrowbox.app.ui.viewmodels.AddItemViewModel;
import com.borrowbox.app.utils.ImageUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.List;

public class AddItemFragment extends Fragment {

    private AddItemViewModel viewModel;
    private ItemRepository itemRepository;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraActivityLauncher;
    private ActivityResultLauncher<String> galleryPickerLauncher;

    // Wizard navigation views
    private LinearLayout layoutWizardContainer, layoutAddSuccess;
    private TextView tvStepIndicator;
    private View barStep1, barStep2, barStep3, barStep4;
    private LinearLayout step1Photo, step2Details, step3Preferences, step4Preview;
    private MaterialButton btnWizardPrev, btnWizardNext, btnSuccessHome;

    // Step 1: Photo
    private View cardPhotoUploadArea, layoutUploadPlaceholder;
    private ImageView ivUploadedPhotoPreview;
    private TextView tvRemovePhoto;

    // Step 2: Details
    private EditText etAddItemName, etAddItemDesc;
    private ChipGroup chipGroupCategories;

    // Step 3: Preferences
    private ChipGroup chipGroupDurations;
    private RadioGroup rgHandoverLocations;

    // Step 4: Preview
    private ImageView ivPreviewImage;
    private TextView tvPreviewName, tvPreviewCategory, tvPreviewDescription, tvPreviewDuration, tvPreviewLocation;

    private boolean isSubmitting = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemRepository = ItemRepository.getInstance(requireContext());
        viewModel = new ViewModelProvider(this).get(AddItemViewModel.class);

        // Register camera permission contract
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCamera();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            showCameraPermissionRationaleDialog();
                        } else {
                            showCameraPermissionSettingsDialog();
                        }
                    }
                }
        );

        // Register CameraCaptureActivity contract
        cameraActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String photoPath = result.getData().getStringExtra(CameraCaptureActivity.EXTRA_IMAGE_PATH);
                        if (photoPath != null && !photoPath.isEmpty()) {
                            viewModel.addPhotoPath(photoPath);
                            updatePhotoDisplay();
                            if (getView() != null) {
                                Snackbar.make(getView(), R.string.photo_captured, Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        // Register Gallery picker contract
        galleryPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && getContext() != null) {
                        try {
                            File saved = ImageUtils.saveBitmapFromUri(getContext(), uri, "borrowbox_item_" + System.currentTimeMillis() + ".jpg");
                            if (saved != null) {
                                viewModel.addPhotoPath(saved.getAbsolutePath());
                                updatePhotoDisplay();
                                if (getView() != null) {
                                    Snackbar.make(getView(), R.string.photo_selected_gallery, Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            if (getView() != null) {
                                Snackbar.make(getView(), "Error importing image: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        initViews(view);
        setupListeners(view);
        populateCategories();
        setupDurationChips();
        restoreStateToViews();
        renderStep(viewModel.getCurrentStep());
        return view;
    }

    private void initViews(View view) {
        layoutWizardContainer = view.findViewById(R.id.layoutWizardContainer);
        layoutAddSuccess = view.findViewById(R.id.layoutAddSuccess);
        tvStepIndicator = view.findViewById(R.id.tvStepIndicator);

        barStep1 = view.findViewById(R.id.barStep1);
        barStep2 = view.findViewById(R.id.barStep2);
        barStep3 = view.findViewById(R.id.barStep3);
        barStep4 = view.findViewById(R.id.barStep4);

        step1Photo = view.findViewById(R.id.step1Photo);
        step2Details = view.findViewById(R.id.step2Details);
        step3Preferences = view.findViewById(R.id.step3Preferences);
        step4Preview = view.findViewById(R.id.step4Preview);

        btnWizardPrev = view.findViewById(R.id.btnWizardPrev);
        btnWizardNext = view.findViewById(R.id.btnWizardNext);
        btnSuccessHome = view.findViewById(R.id.btnSuccessHome);

        cardPhotoUploadArea = view.findViewById(R.id.cardPhotoUploadArea);
        layoutUploadPlaceholder = view.findViewById(R.id.layoutUploadPlaceholder);
        ivUploadedPhotoPreview = view.findViewById(R.id.ivUploadedPhotoPreview);
        tvRemovePhoto = view.findViewById(R.id.tvRemovePhoto);

        etAddItemName = view.findViewById(R.id.etAddItemName);
        etAddItemDesc = view.findViewById(R.id.etAddItemDesc);
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories);

        chipGroupDurations = view.findViewById(R.id.chipGroupDurations);
        rgHandoverLocations = view.findViewById(R.id.rgHandoverLocations);

        ivPreviewImage = view.findViewById(R.id.ivPreviewImage);
        tvPreviewName = view.findViewById(R.id.tvPreviewName);
        tvPreviewCategory = view.findViewById(R.id.tvPreviewCategory);
        tvPreviewDescription = view.findViewById(R.id.tvPreviewDescription);
        tvPreviewDuration = view.findViewById(R.id.tvPreviewDuration);
        tvPreviewLocation = view.findViewById(R.id.tvPreviewLocation);
    }

    private void setupListeners(View root) {
        root.findViewById(R.id.btnAddBack).setOnClickListener(v -> {
            if (viewModel.getCurrentStep() > 0) {
                goToStep(viewModel.getCurrentStep() - 1);
            } else if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToHome();
            }
        });

        cardPhotoUploadArea.setOnClickListener(v -> showPhotoSourceDialog());

        tvRemovePhoto.setOnClickListener(v -> {
            List<String> paths = viewModel.getPhotoPaths();
            if (!paths.isEmpty()) {
                viewModel.removePhotoPath(paths.size() - 1);
                updatePhotoDisplay();
            }
        });

        btnWizardPrev.setOnClickListener(v -> {
            if (viewModel.getCurrentStep() > 0) {
                goToStep(viewModel.getCurrentStep() - 1);
            }
        });

        btnWizardNext.setOnClickListener(v -> {
            handleNextStep();
        });

        btnSuccessHome.setOnClickListener(v -> {
            viewModel.reset();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToHome();
            }
        });

        rgHandoverLocations.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = group.findViewById(checkedId);
            if (rb != null) {
                viewModel.setPickupLocation(rb.getText().toString());
            }
        });
    }

    private void showPhotoSourceDialog() {
        if (getContext() == null) return;
        String[] options = {getString(R.string.camera), getString(R.string.gallery)};
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.add_photo_title)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndLaunch();
                    } else {
                        galleryPickerLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        Intent intent = new Intent(requireContext(), CameraCaptureActivity.class);
        cameraActivityLauncher.launch(intent);
    }

    private void showCameraPermissionRationaleDialog() {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.camera_permission_title)
                .setMessage(R.string.camera_permission_rationale)
                .setPositiveButton(R.string.grant_permission, (d, w) -> requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showCameraPermissionSettingsDialog() {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.camera_permission_title)
                .setMessage(R.string.camera_permission_settings_msg)
                .setPositiveButton(R.string.open_settings, (d, w) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                    startActivity(intent);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void populateCategories() {
        itemRepository.getCategories(new RepositoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                if (!isAdded() || chipGroupCategories == null) return;
                chipGroupCategories.removeAllViews();
                for (Category cat : categories) {
                    if ("all".equalsIgnoreCase(cat.getId())) continue;
                    Chip chip = new Chip(requireContext());
                    chip.setText(cat.getLabel());
                    chip.setCheckable(true);
                    chip.setClickable(true);
                    chip.setChipBackgroundColorResource(R.color.surface);
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
                    chip.setCheckedIconVisible(true);
                    if (cat.getLabel().equalsIgnoreCase(viewModel.getCategory()) || cat.getId().equalsIgnoreCase(viewModel.getCategoryId())) {
                        chip.setChecked(true);
                    }
                    chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            viewModel.setCategory(cat.getLabel());
                            viewModel.setCategoryId(cat.getId());
                        }
                    });
                    chipGroupCategories.addView(chip);
                }
            }

            @Override
            public void onError(Throwable t) {
                // Ignore
            }
        });
    }

    private void setupDurationChips() {
        int[] durations = {1, 3, 7, 14, 30};
        chipGroupDurations.removeAllViews();
        for (int d : durations) {
            Chip chip = new Chip(requireContext());
            chip.setText(d + (d == 1 ? " day" : " days"));
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColorResource(R.color.surface);
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
            if (d == viewModel.getDurationDays()) {
                chip.setChecked(true);
            }
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    viewModel.setDurationDays(d);
                }
            });
            chipGroupDurations.addView(chip);
        }
    }

    private void restoreStateToViews() {
        updatePhotoDisplay();
        if (etAddItemName != null) etAddItemName.setText(viewModel.getItemName());
        if (etAddItemDesc != null) etAddItemDesc.setText(viewModel.getDescription());

        // Restore location
        for (int i = 0; i < rgHandoverLocations.getChildCount(); i++) {
            View child = rgHandoverLocations.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton rb = (RadioButton) child;
                if (rb.getText().toString().equalsIgnoreCase(viewModel.getPickupLocation())) {
                    rb.setChecked(true);
                    break;
                }
            }
        }
    }

    private void updatePhotoDisplay() {
        List<String> photos = viewModel.getPhotoPaths();
        if (photos.isEmpty()) {
            if (layoutUploadPlaceholder != null) layoutUploadPlaceholder.setVisibility(View.VISIBLE);
            if (ivUploadedPhotoPreview != null) ivUploadedPhotoPreview.setVisibility(View.GONE);
            if (tvRemovePhoto != null) tvRemovePhoto.setVisibility(View.GONE);
        } else {
            String latest = photos.get(photos.size() - 1);
            if (layoutUploadPlaceholder != null) layoutUploadPlaceholder.setVisibility(View.GONE);
            if (ivUploadedPhotoPreview != null) {
                ivUploadedPhotoPreview.setVisibility(View.VISIBLE);
                Glide.with(this).load(latest).into(ivUploadedPhotoPreview);
            }
            if (tvRemovePhoto != null) {
                tvRemovePhoto.setVisibility(View.VISIBLE);
                tvRemovePhoto.setText("Remove photo (" + photos.size() + "/3)");
            }
        }
    }

    private void handleNextStep() {
        int step = viewModel.getCurrentStep();

        if (step == 0) {
            // Require at least 1 photo
            if (viewModel.getPhotoPaths().isEmpty()) {
                if (getView() != null) {
                    Snackbar.make(getView(), R.string.add_photo_required_error, Snackbar.LENGTH_SHORT).show();
                }
                return;
            }
            goToStep(1);
        } else if (step == 1) {
            // Validate name & description
            String name = etAddItemName.getText().toString().trim();
            String desc = etAddItemDesc.getText().toString().trim();

            if (name.length() < 3) {
                etAddItemName.setError(getString(R.string.item_name_min_chars));
                etAddItemName.requestFocus();
                return;
            }
            if (desc.isEmpty()) {
                etAddItemDesc.setError(getString(R.string.item_desc_required));
                etAddItemDesc.requestFocus();
                return;
            }

            viewModel.setItemName(name);
            viewModel.setDescription(desc);
            goToStep(2);
        } else if (step == 2) {
            // Preferences validated via chip & radio defaults
            bindPreviewData();
            goToStep(3);
        } else if (step == 3) {
            // Submit item
            submitItem();
        }
    }

    private void bindPreviewData() {
        List<String> photos = viewModel.getPhotoPaths();
        if (!photos.isEmpty()) {
            Glide.with(this).load(photos.get(0)).into(ivPreviewImage);
        }
        tvPreviewName.setText(viewModel.getItemName());
        tvPreviewCategory.setText(viewModel.getCategory());
        tvPreviewDescription.setText(viewModel.getDescription());
        tvPreviewDuration.setText("Max: " + viewModel.getDurationDays() + (viewModel.getDurationDays() == 1 ? " day" : " days"));
        tvPreviewLocation.setText(viewModel.getPickupLocation());
    }

    private void submitItem() {
        if (isSubmitting) return;
        isSubmitting = true;
        btnWizardNext.setEnabled(false);

        User user = DataManager.getInstance().getCurrentUser();
        if (user == null) {
            isSubmitting = false;
            btnWizardNext.setEnabled(true);
            return;
        }

        String joinedPhotos = String.join(";", viewModel.getPhotoPaths());
        Item item = new Item();
        item.setOwnerId(user.getId());
        item.setName(viewModel.getItemName());
        item.setCategoryId(viewModel.getCategoryId());
        item.setDescription(viewModel.getDescription());
        item.setPhotoPath(joinedPhotos);
        item.setStatus("available");
        item.setMaxDurationDays(viewModel.getDurationDays());
        item.setPickupLocation(viewModel.getPickupLocation());

        itemRepository.insertItem(item, new RepositoryCallback<Item>() {
            @Override
            public void onSuccess(Item result) {
                isSubmitting = false;
                if (!isAdded()) return;
                layoutWizardContainer.setVisibility(View.GONE);
                layoutAddSuccess.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable t) {
                isSubmitting = false;
                if (!isAdded()) return;
                btnWizardNext.setEnabled(true);
                if (getView() != null) {
                    Snackbar.make(getView(), "Failed to list item: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goToStep(int step) {
        viewModel.setCurrentStep(step);
        renderStep(step);
    }

    private void renderStep(int step) {
        step1Photo.setVisibility(step == 0 ? View.VISIBLE : View.GONE);
        step2Details.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        step3Preferences.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        step4Preview.setVisibility(step == 3 ? View.VISIBLE : View.GONE);

        btnWizardPrev.setVisibility(step > 0 ? View.VISIBLE : View.GONE);
        btnWizardNext.setText(step == 3 ? R.string.publish_listing : R.string.continue_text);

        tvStepIndicator.setText(getString(R.string.step_indicator_fmt, step + 1, 4));

        barStep1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        barStep2.setBackgroundColor(ContextCompat.getColor(requireContext(), step >= 1 ? R.color.white : R.color.white_overlay_40));
        barStep3.setBackgroundColor(ContextCompat.getColor(requireContext(), step >= 2 ? R.color.white : R.color.white_overlay_40));
        barStep4.setBackgroundColor(ContextCompat.getColor(requireContext(), step >= 3 ? R.color.white : R.color.white_overlay_40));
    }
}
