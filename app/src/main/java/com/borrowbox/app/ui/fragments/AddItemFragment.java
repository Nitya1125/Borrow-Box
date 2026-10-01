package com.borrowbox.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.Category;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.User;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class AddItemFragment extends Fragment {

    private int currentStep = 0;
    private final int TOTAL_STEPS = 4;

    // Data holder for new item
    private String selectedPhotoUrl = null;
    private String itemName = "";
    private String selectedCategory = "Calculators";
    private String itemDescription = "";
    private String selectedDuration = "3 days";
    private String selectedHandover = "Library gate";

    // Views
    private LinearLayout layoutWizardContainer, layoutAddSuccess;
    private TextView tvStepIndicator;
    private View barStep1, barStep2, barStep3, barStep4;
    private LinearLayout step1Photo, step2Details, step3Preferences, step4Preview;
    private MaterialButton btnWizardPrev, btnWizardNext, btnSuccessHome;

    // Step 1
    private View cardPhotoUploadArea, layoutUploadPlaceholder;
    private ImageView ivUploadedPhotoPreview;
    private TextView tvRemovePhoto;

    // Step 2
    private EditText etAddItemName, etAddItemDesc;
    private ChipGroup chipGroupCategories;

    // Step 3
    private ChipGroup chipGroupDurations;
    private RadioGroup rgHandoverLocations;

    // Step 4
    private ImageView ivPreviewImage;
    private TextView tvPreviewName, tvPreviewCategory, tvPreviewDescription, tvPreviewDuration, tvPreviewLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

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

        // Step 1 views
        cardPhotoUploadArea = view.findViewById(R.id.cardPhotoUploadArea);
        layoutUploadPlaceholder = view.findViewById(R.id.layoutUploadPlaceholder);
        ivUploadedPhotoPreview = view.findViewById(R.id.ivUploadedPhotoPreview);
        tvRemovePhoto = view.findViewById(R.id.tvRemovePhoto);

        // Step 2 views
        etAddItemName = view.findViewById(R.id.etAddItemName);
        etAddItemDesc = view.findViewById(R.id.etAddItemDesc);
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories);

        // Step 3 views
        chipGroupDurations = view.findViewById(R.id.chipGroupDurations);
        rgHandoverLocations = view.findViewById(R.id.rgHandoverLocations);

        // Step 4 views
        ivPreviewImage = view.findViewById(R.id.ivPreviewImage);
        tvPreviewName = view.findViewById(R.id.tvPreviewName);
        tvPreviewCategory = view.findViewById(R.id.tvPreviewCategory);
        tvPreviewDescription = view.findViewById(R.id.tvPreviewDescription);
        tvPreviewDuration = view.findViewById(R.id.tvPreviewDuration);
        tvPreviewLocation = view.findViewById(R.id.tvPreviewLocation);

        setupPhotoPicker();
        setupCategoryChips();
        setupDurationChips();
        setupNavigation();

        return view;
    }

    private void setupPhotoPicker() {
        cardPhotoUploadArea.setOnClickListener(v -> {
            selectedPhotoUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=300&fit=crop&auto=format";
            layoutUploadPlaceholder.setVisibility(View.GONE);
            ivUploadedPhotoPreview.setVisibility(View.VISIBLE);
            tvRemovePhoto.setVisibility(View.VISIBLE);
            Glide.with(requireContext()).load(selectedPhotoUrl).into(ivUploadedPhotoPreview);
        });

        tvRemovePhoto.setOnClickListener(v -> {
            selectedPhotoUrl = null;
            layoutUploadPlaceholder.setVisibility(View.VISIBLE);
            ivUploadedPhotoPreview.setVisibility(View.GONE);
            tvRemovePhoto.setVisibility(View.GONE);
        });
    }

    private void setupCategoryChips() {
        chipGroupCategories.removeAllViews();
        List<Category> categories = DataManager.getInstance().getCategories();
        for (Category cat : categories) {
            if ("all".equalsIgnoreCase(cat.getId())) continue;
            Chip chip = new Chip(getContext());
            chip.setText(cat.getLabel());
            chip.setChipIcon(ContextCompat.getDrawable(requireContext(), cat.getIconRes()));
            chip.setChipIconTint(ContextCompat.getColorStateList(requireContext(), R.color.primary));
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColorResource(R.color.surface);
            chip.setChipStrokeColorResource(R.color.border_color);
            chip.setChipStrokeWidth(2f);

            if (cat.getLabel().equalsIgnoreCase(selectedCategory)) {
                chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCategory = cat.getLabel();
                }
            });
            chipGroupCategories.addView(chip);
        }
    }

    private void setupDurationChips() {
        chipGroupDurations.removeAllViews();
        String[] durations = {"1 day", "2 days", "3 days", "5 days", "7 days", "2 weeks"};
        for (String d : durations) {
            Chip chip = new Chip(getContext());
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
                }
            });
            chipGroupDurations.addView(chip);
        }
    }

    private void setupNavigation() {
        btnWizardPrev.setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
                updateStepUI();
            }
        });

        btnWizardNext.setOnClickListener(v -> {
            if (currentStep == 0) {
                currentStep++;
                updateStepUI();
            } else if (currentStep == 1) {
                itemName = etAddItemName.getText().toString().trim();
                if (itemName.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter an item name", Toast.LENGTH_SHORT).show();
                    return;
                }
                itemDescription = etAddItemDesc.getText().toString().trim();
                currentStep++;
                updateStepUI();
            } else if (currentStep == 2) {
                int checkedId = rgHandoverLocations.getCheckedRadioButtonId();
                if (checkedId != -1) {
                    RadioButton rb = rgHandoverLocations.findViewById(checkedId);
                    selectedHandover = rb.getText().toString();
                }
                currentStep++;
                updateStepUI();
            } else if (currentStep == 3) {
                submitListing();
            }
        });

        btnSuccessHome.setOnClickListener(v -> {
            resetForm();
            if (getActivity() != null) {
                // Return to home tab
                getActivity().findViewById(R.id.nav_home).performClick();
            }
        });
    }

    private void updateStepUI() {
        tvStepIndicator.setText(String.format("Step %d of %d", currentStep + 1, TOTAL_STEPS));

        // Update progress bars
        int activeColor = ContextCompat.getColor(requireContext(), R.color.white);
        int inactiveColor = ContextCompat.getColor(requireContext(), R.color.white_overlay_40);
        barStep1.setBackgroundColor(currentStep >= 0 ? activeColor : inactiveColor);
        barStep2.setBackgroundColor(currentStep >= 1 ? activeColor : inactiveColor);
        barStep3.setBackgroundColor(currentStep >= 2 ? activeColor : inactiveColor);
        barStep4.setBackgroundColor(currentStep >= 3 ? activeColor : inactiveColor);

        // Show/hide step views
        step1Photo.setVisibility(currentStep == 0 ? View.VISIBLE : View.GONE);
        step2Details.setVisibility(currentStep == 1 ? View.VISIBLE : View.GONE);
        step3Preferences.setVisibility(currentStep == 2 ? View.VISIBLE : View.GONE);
        step4Preview.setVisibility(currentStep == 3 ? View.VISIBLE : View.GONE);

        btnWizardPrev.setVisibility(currentStep > 0 ? View.VISIBLE : View.GONE);

        if (currentStep == 3) {
            btnWizardNext.setText(R.string.list_item_cta);
            populatePreview();
        } else {
            btnWizardNext.setText(R.string.continue_text);
        }
    }

    private void populatePreview() {
        tvPreviewName.setText(itemName.isEmpty() ? "Item Name" : itemName);
        tvPreviewCategory.setText(selectedCategory);
        tvPreviewDescription.setText(itemDescription.isEmpty() ? "No description added." : itemDescription);
        tvPreviewDuration.setText("Max: " + selectedDuration);
        tvPreviewLocation.setText("Location: " + selectedHandover);

        if (selectedPhotoUrl != null) {
            Glide.with(requireContext()).load(selectedPhotoUrl).into(ivPreviewImage);
        } else {
            ivPreviewImage.setImageResource(R.drawable.bg_card);
        }
    }

    private void submitListing() {
        User currentUser = DataManager.getInstance().getCurrentUser();

        Item newItem = new Item(
                "item_" + System.currentTimeMillis(),
                itemName,
                selectedCategory,
                selectedPhotoUrl != null ? selectedPhotoUrl : "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=300&fit=crop&auto=format",
                currentUser.getName(),
                currentUser.getAvatarUrl(),
                currentUser.getCollege(),
                currentUser.getRating(),
                true,
                itemDescription,
                "0.1 km",
                selectedDuration,
                selectedHandover,
                "available",
                null,
                currentUser.getEmail()
        );

        // Store in SQLite DB
        DataManager.getInstance().addItem(newItem);

        layoutWizardContainer.setVisibility(View.GONE);
        layoutAddSuccess.setVisibility(View.VISIBLE);
    }

    private void resetForm() {
        currentStep = 0;
        selectedPhotoUrl = null;
        itemName = "";
        itemDescription = "";
        layoutWizardContainer.setVisibility(View.VISIBLE);
        layoutAddSuccess.setVisibility(View.GONE);
        if (etAddItemName != null) etAddItemName.setText("");
        if (etAddItemDesc != null) etAddItemDesc.setText("");
        if (layoutUploadPlaceholder != null) layoutUploadPlaceholder.setVisibility(View.VISIBLE);
        if (ivUploadedPhotoPreview != null) ivUploadedPhotoPreview.setVisibility(View.GONE);
        if (tvRemovePhoto != null) tvRemovePhoto.setVisibility(View.GONE);
        updateStepUI();
    }
}
