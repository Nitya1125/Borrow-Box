package com.borrowbox.app.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddItemViewModel extends ViewModel {

    private static final String KEY_STEP = "wizard_step";
    private static final String KEY_PHOTOS = "wizard_photos";
    private static final String KEY_NAME = "wizard_name";
    private static final String KEY_CATEGORY = "wizard_category";
    private static final String KEY_DESC = "wizard_desc";
    private static final String KEY_DURATION = "wizard_duration";
    private static final String KEY_LOCATION = "wizard_location";

    private final SavedStateHandle savedState;

    public AddItemViewModel(SavedStateHandle savedStateHandle) {
        this.savedState = savedStateHandle;
        if (!savedState.contains(KEY_STEP)) {
            savedState.set(KEY_STEP, 0);
        }
        if (!savedState.contains(KEY_PHOTOS)) {
            savedState.set(KEY_PHOTOS, new ArrayList<String>());
        }
        if (!savedState.contains(KEY_CATEGORY)) {
            savedState.set(KEY_CATEGORY, "Calculators");
        }
        if (!savedState.contains(KEY_DURATION)) {
            savedState.set(KEY_DURATION, 3);
        }
        if (!savedState.contains(KEY_LOCATION)) {
            savedState.set(KEY_LOCATION, "Library Gate");
        }
    }

    public int getCurrentStep() {
        Integer step = savedState.get(KEY_STEP);
        return step != null ? step : 0;
    }

    public void setCurrentStep(int step) {
        savedState.set(KEY_STEP, step);
    }

    public List<String> getPhotoPaths() {
        List<String> list = savedState.get(KEY_PHOTOS);
        return list != null ? list : new ArrayList<>();
    }

    public void addPhotoPath(String path) {
        List<String> current = new ArrayList<>(getPhotoPaths());
        if (current.size() < 3 && !current.contains(path)) {
            current.add(path);
            savedState.set(KEY_PHOTOS, current);
        }
    }

    public void removePhotoPath(int index) {
        List<String> current = new ArrayList<>(getPhotoPaths());
        if (index >= 0 && index < current.size()) {
            current.remove(index);
            savedState.set(KEY_PHOTOS, current);
        }
    }

    public String getItemName() {
        String name = savedState.get(KEY_NAME);
        return name != null ? name : "";
    }

    public void setItemName(String name) {
        savedState.set(KEY_NAME, name);
    }

    public String getCategory() {
        String cat = savedState.get(KEY_CATEGORY);
        return cat != null ? cat : "Calculators";
    }

    public void setCategory(String category) {
        savedState.set(KEY_CATEGORY, category);
    }

    public String getCategoryId() {
        String id = savedState.get("wizard_category_id");
        if (id != null && !id.isEmpty()) return id;
        String cat = getCategory();
        return cat != null ? cat.toLowerCase().trim() : "others";
    }

    public void setCategoryId(String categoryId) {
        savedState.set("wizard_category_id", categoryId);
    }

    public String getDescription() {
        String desc = savedState.get(KEY_DESC);
        return desc != null ? desc : "";
    }

    public void setDescription(String desc) {
        savedState.set(KEY_DESC, desc);
    }

    public int getDurationDays() {
        Integer d = savedState.get(KEY_DURATION);
        return d != null ? d : 3;
    }

    public void setDurationDays(int days) {
        savedState.set(KEY_DURATION, days);
    }

    public String getPickupLocation() {
        String loc = savedState.get(KEY_LOCATION);
        return loc != null ? loc : "Library Gate";
    }

    public void setPickupLocation(String location) {
        savedState.set(KEY_LOCATION, location);
    }

    public void reset() {
        savedState.set(KEY_STEP, 0);
        savedState.set(KEY_PHOTOS, new ArrayList<String>());
        savedState.set(KEY_NAME, "");
        savedState.set(KEY_DESC, "");
        savedState.set(KEY_CATEGORY, "Calculators");
        savedState.set(KEY_DURATION, 3);
        savedState.set(KEY_LOCATION, "Library Gate");
    }
}
