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
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.SessionManager;
import com.borrowbox.app.data.repository.BorrowRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.data.repository.UserRepository;
import com.borrowbox.app.models.User;
import com.borrowbox.app.ui.activities.CameraCaptureActivity;
import com.borrowbox.app.ui.activities.HistoryActivity;
import com.borrowbox.app.ui.activities.LoginActivity;
import com.borrowbox.app.ui.activities.MyItemsActivity;
import com.borrowbox.app.ui.activities.RatingActivity;
import com.borrowbox.app.utils.AvatarDrawable;
import com.borrowbox.app.utils.AppExecutors;
import com.borrowbox.app.utils.ImageUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvName, tvCollege, tvRating, tvStatLent, tvStatBorrowed, tvStatPoints;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraActivityLauncher;
    private ActivityResultLauncher<String> galleryPickerLauncher;

    private UserRepository userRepository;
    private BorrowRepository borrowRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = UserRepository.getInstance(requireContext());
        borrowRepository = BorrowRepository.getInstance(requireContext());

        // Camera permission launcher
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCamera();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            showCameraRationale();
                        } else {
                            showCameraSettings();
                        }
                    }
                }
        );

        // CameraCaptureActivity launcher
        cameraActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String photoPath = result.getData().getStringExtra(CameraCaptureActivity.EXTRA_IMAGE_PATH);
                        if (photoPath != null) {
                            updateAvatarPath(photoPath);
                        }
                    }
                }
        );

        // Gallery picker launcher
        galleryPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && getContext() != null) {
                        AppExecutors.getInstance().executeOnDiskIO(() -> {
                            try {
                                File saved = ImageUtils.saveBitmapFromUri(requireContext(), uri, "avatar_" + System.currentTimeMillis() + ".jpg");
                                if (saved != null) {
                                    AppExecutors.getInstance().postToMainThread(() -> updateAvatarPath(saved.getAbsolutePath()));
                                } else {
                                    AppExecutors.getInstance().postToMainThread(() -> {
                                        if (getView() != null) {
                                            Snackbar.make(getView(), "Failed to save selected image", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                AppExecutors.getInstance().postToMainThread(() -> {
                                    if (getView() != null) {
                                        Snackbar.make(getView(), "Failed to update avatar: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivAvatar = view.findViewById(R.id.ivProfileAvatar);
        tvName = view.findViewById(R.id.tvProfileName);
        tvCollege = view.findViewById(R.id.tvProfileCollege);
        tvRating = view.findViewById(R.id.tvProfileRating);
        tvStatLent = view.findViewById(R.id.tvStatLent);
        tvStatBorrowed = view.findViewById(R.id.tvStatBorrowed);
        tvStatPoints = view.findViewById(R.id.tvStatPoints);

        bindUserData();

        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> showAvatarSourceDialog());
        }
        View layoutAvatar = view.findViewById(R.id.layoutAvatarContainer);
        if (layoutAvatar != null) {
            layoutAvatar.setOnClickListener(v -> showAvatarSourceDialog());
        }

        // Edit profile on clicking profile info area
        view.findViewById(R.id.tvProfileName).setOnClickListener(v -> showEditProfileDialog());
        view.findViewById(R.id.tvProfileCollege).setOnClickListener(v -> showEditProfileDialog());

        view.findViewById(R.id.menuMyItems).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyItemsActivity.class));
        });

        view.findViewById(R.id.menuHistory).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), HistoryActivity.class));
        });

        view.findViewById(R.id.menuWishlist).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), com.borrowbox.app.ui.activities.WishlistActivity.class));
        });

        view.findViewById(R.id.menuReviews).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RatingActivity.class));
        });

        view.findViewById(R.id.menuSettings).setOnClickListener(v -> {
            Snackbar.make(view, "Campus Notifications and Privacy: All active.", Snackbar.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.menuHelp).setOnClickListener(v -> {
            Snackbar.make(view, "Campus Support: Contact support@borrowbox.app", Snackbar.LENGTH_LONG).show();
        });

        view.findViewById(R.id.btnProfileLogout).setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });

        return view;
    }

    private void showAvatarSourceDialog() {
        if (getContext() == null) return;
        String[] options = {getString(R.string.camera), getString(R.string.gallery)};
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.update_avatar_title)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            launchCamera();
                        } else {
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                        }
                    } else {
                        galleryPickerLauncher.launch("image/*");
                    }
                })
                .show();
    }

    private void launchCamera() {
        Intent intent = new Intent(requireContext(), CameraCaptureActivity.class);
        cameraActivityLauncher.launch(intent);
    }

    private void showCameraRationale() {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.camera_permission_title)
                .setMessage(R.string.camera_permission_rationale)
                .setPositiveButton(R.string.grant_permission, (d, w) -> requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showCameraSettings() {
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

    private void updateAvatarPath(String path) {
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        currentUser.setAvatarUrl(path);
        if (getContext() != null) {
            SessionManager.getInstance(requireContext()).updateLoggedInUser(currentUser);
        }
        bindUserData();

        userRepository.updateProfile(currentUser, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User updated) {
                if (!isAdded()) return;
                if (getContext() != null) {
                    SessionManager.getInstance(requireContext()).updateLoggedInUser(updated);
                }
                bindUserData();
                if (getView() != null) {
                    Snackbar.make(getView(), R.string.avatar_updated_success, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!isAdded()) return;
                if (getView() != null) {
                    Snackbar.make(getView(), "Failed to update profile: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showEditProfileDialog() {
        User user = DataManager.getInstance().getCurrentUser();
        if (user == null || getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText etName = dialogView.findViewById(R.id.etEditName);
        EditText etCollege = dialogView.findViewById(R.id.etEditCollege);
        EditText etPhone = dialogView.findViewById(R.id.etEditPhone);

        etName.setText(user.getName());
        etCollege.setText(user.getCollege());
        etPhone.setText(user.getPhone() != null ? user.getPhone() : "");

        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.edit_profile)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    String newCollege = etCollege.getText().toString().trim();
                    String newPhone = etPhone.getText().toString().trim();

                    if (newName.length() < 2) {
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.invalid_name, Snackbar.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    user.setName(newName);
                    user.setCollege(newCollege);
                    user.setPhone(newPhone);

                    userRepository.updateProfile(user, new RepositoryCallback<User>() {
                        @Override
                        public void onSuccess(User updated) {
                            if (!isAdded()) return;
                            bindUserData();
                            if (getView() != null) {
                                Snackbar.make(getView(), R.string.profile_updated_success, Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            if (!isAdded()) return;
                            if (getView() != null) {
                                Snackbar.make(getView(), "Failed: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showLogoutConfirmationDialog() {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.log_out)
                .setMessage(R.string.logout_confirm_msg)
                .setPositiveButton(R.string.log_out, (dialog, which) -> {
                    DataManager.getInstance().logout();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void bindUserData() {
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        if (tvName != null) tvName.setText(currentUser.getName());
        if (tvCollege != null) {
            String col = currentUser.getCollege();
            tvCollege.setText((col != null && !col.isEmpty()) ? col : "Campus Student");
        }

        if (ivAvatar != null) {
            String avatar = currentUser.getAvatarUrl();
            if (avatar != null && !avatar.trim().isEmpty()) {
                String cleanPath = avatar.trim();
                Object model = (cleanPath.startsWith("/") && !cleanPath.startsWith("content:"))
                        ? new File(cleanPath)
                        : cleanPath;
                Glide.with(this)
                        .load(model)
                        .circleCrop()
                        .placeholder(new AvatarDrawable(currentUser.getName()))
                        .error(new AvatarDrawable(currentUser.getName()))
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageDrawable(new AvatarDrawable(currentUser.getName()));
            }
        }

        // Live computed stats from BorrowRepository
        borrowRepository.getUserDashboardStats(currentUser.getId(), new RepositoryCallback<Map<String, Number>>() {
            @Override
            public void onSuccess(Map<String, Number> stats) {
                if (!isAdded()) return;
                int lent = stats.get("lentOut") != null ? stats.get("lentOut").intValue() : 0;
                int borrowed = stats.get("borrowing") != null ? stats.get("borrowing").intValue() : 0;
                int points = stats.get("points") != null ? stats.get("points").intValue() : 50;
                float rating = stats.get("rating") != null ? stats.get("rating").floatValue() : 5.0f;

                if (tvStatLent != null) tvStatLent.setText(String.valueOf(lent));
                if (tvStatBorrowed != null) tvStatBorrowed.setText(String.valueOf(borrowed));
                if (tvStatPoints != null) tvStatPoints.setText(String.valueOf(points));
                if (tvRating != null) tvRating.setText(String.format(Locale.getDefault(), "%.1f", rating));
            }

            @Override
            public void onError(Throwable t) {
                // Keep default placeholders
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        bindUserData();
    }
}
