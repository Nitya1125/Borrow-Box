package com.borrowbox.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.CategoryAdapter;
import com.borrowbox.app.adapters.ItemGridAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.Category;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.User;
import com.borrowbox.app.ui.activities.ItemDetailsActivity;
import com.borrowbox.app.ui.activities.NotificationsActivity;
import com.borrowbox.app.ui.activities.SearchActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories;
    private RecyclerView rvRecommendedItems;
    private CategoryAdapter categoryAdapter;
    private ItemGridAdapter itemGridAdapter;
    private View layoutEmpty;
    private TextView tvSectionTitle, tvUserName;
    private View notificationDot;
    private ShapeableImageView ivAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvCategories = view.findViewById(R.id.rvCategories);
        rvRecommendedItems = view.findViewById(R.id.rvRecommendedItems);
        layoutEmpty = view.findViewById(R.id.layoutHomeEmpty);
        tvSectionTitle = view.findViewById(R.id.tvSectionTitle);
        tvUserName = view.findViewById(R.id.tvHomeUserName);
        notificationDot = view.findViewById(R.id.homeNotificationDot);
        ivAvatar = view.findViewById(R.id.ivHomeAvatar);

        // Bind user info
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (tvUserName != null && currentUser != null) {
            tvUserName.setText(currentUser.getName().split(" ")[0]);
        }

        if (ivAvatar != null && currentUser != null) {
            Glide.with(this)
                    .load(currentUser.getAvatarUrl())
                    .placeholder(R.drawable.ic_profile)
                    .into(ivAvatar);
        }

        // Setup notification button
        view.findViewById(R.id.btnHomeNotifications).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), NotificationsActivity.class));
        });

        // Setup search trigger
        view.findViewById(R.id.cardHomeSearchBar).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SearchActivity.class));
        });

        setupCategories();
        setupItemsGrid();

        return view;
    }

    private void setupCategories() {
        List<Category> categories = DataManager.getInstance().getCategories();
        categoryAdapter = new CategoryAdapter(getContext(), categories, (category, position) -> {
            if ("all".equalsIgnoreCase(category.getId())) {
                tvSectionTitle.setText(R.string.recommended_for_you);
            } else {
                tvSectionTitle.setText(category.getLabel());
            }
            filterItemsByCategory(category.getId());
        });

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupItemsGrid() {
        List<Item> items = DataManager.getInstance().getItems();
        itemGridAdapter = new ItemGridAdapter(getContext(), items, item -> {
            Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
        });

        rvRecommendedItems.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvRecommendedItems.setAdapter(itemGridAdapter);
    }

    private void filterItemsByCategory(String categoryId) {
        List<Item> filtered = DataManager.getInstance().getItemsByCategory(categoryId);
        itemGridAdapter.updateList(filtered);

        if (filtered.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvRecommendedItems.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvRecommendedItems.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (tvUserName != null && currentUser != null) {
            String firstName = currentUser.getName() != null ? currentUser.getName().split(" ")[0] : "Student";
            tvUserName.setText(firstName);
        }
        if (ivAvatar != null && currentUser != null) {
            Glide.with(this)
                    .load(currentUser.getAvatarUrl())
                    .placeholder(R.drawable.ic_profile)
                    .into(ivAvatar);
        }
        if (itemGridAdapter != null) {
            itemGridAdapter.updateList(DataManager.getInstance().getItems());
        }
        int unread = DataManager.getInstance().getUnreadNotificationCount();
        if (notificationDot != null) {
            notificationDot.setVisibility(unread > 0 ? View.VISIBLE : View.GONE);
        }
    }
}
