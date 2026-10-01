package com.borrowbox.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.borrowbox.app.R;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.User;
import com.borrowbox.app.ui.activities.HistoryActivity;
import com.borrowbox.app.ui.activities.LoginActivity;
import com.borrowbox.app.ui.activities.MyItemsActivity;
import com.borrowbox.app.ui.activities.RatingActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileFragment extends Fragment {

    private ShapeableImageView ivAvatar;
    private TextView tvName, tvCollege, tvRating, tvStatLent, tvStatBorrowed, tvStatPoints;

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

        view.findViewById(R.id.menuMyItems).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MyItemsActivity.class));
        });

        view.findViewById(R.id.menuHistory).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), HistoryActivity.class));
        });

        view.findViewById(R.id.menuWishlist).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Wishlist contains all your bookmarked items", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.menuReviews).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), RatingActivity.class));
        });

        view.findViewById(R.id.menuSettings).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Settings & Preferences: All campus notifications active", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.menuHelp).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Campus Support: help@borrowbox.edu", Toast.LENGTH_LONG).show();
        });

        view.findViewById(R.id.btnProfileLogout).setOnClickListener(v -> {
            DataManager.getInstance().logout();
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void bindUserData() {
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (tvName != null) tvName.setText(currentUser.getName());
            if (tvCollege != null) tvCollege.setText(currentUser.getCollege());
            if (tvRating != null) tvRating.setText(String.format("%.1f", currentUser.getRating()));
            if (tvStatLent != null) tvStatLent.setText(String.valueOf(currentUser.getLentCount()));
            if (tvStatBorrowed != null) tvStatBorrowed.setText(String.valueOf(currentUser.getBorrowsCount()));
            if (tvStatPoints != null) tvStatPoints.setText(String.valueOf(currentUser.getPoints()));

            if (ivAvatar != null) {
                Glide.with(this)
                        .load(currentUser.getAvatarUrl())
                        .placeholder(R.drawable.ic_profile)
                        .into(ivAvatar);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bindUserData();
    }
}
