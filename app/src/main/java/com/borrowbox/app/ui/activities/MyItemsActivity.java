package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.MyItemAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.repository.ItemRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MyItemsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private MaterialButton btnAdd, btnEmptyAdd;
    private TextView tabAvailable, tabBorrowed, tabInactive;
    private RecyclerView rvMyItems;
    private View layoutEmpty;
    private TextView tvEmptyTitle, tvEmptySub;
    private MyItemAdapter adapter;

    private String currentTab = "available";
    private ItemRepository itemRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);

        itemRepository = ItemRepository.getInstance(this);

        btnBack = findViewById(R.id.btnMyItemsBack);
        btnAdd = findViewById(R.id.btnMyItemsAdd);
        btnEmptyAdd = findViewById(R.id.btnEmptyAddItem);

        tabAvailable = findViewById(R.id.tabMyAvailable);
        tabBorrowed = findViewById(R.id.tabMyBorrowed);
        tabInactive = findViewById(R.id.tabMyInactive);

        rvMyItems = findViewById(R.id.rvMyItemsList);
        layoutEmpty = findViewById(R.id.layoutMyItemsEmpty);
        tvEmptyTitle = findViewById(R.id.tvMyItemsEmptyTitle);
        tvEmptySub = findViewById(R.id.tvMyItemsEmptySub);

        btnBack.setOnClickListener(v -> finish());

        View.OnClickListener addAction = v -> {
            Intent intent = new Intent(MyItemsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        };

        btnAdd.setOnClickListener(addAction);
        if (btnEmptyAdd != null) {
            btnEmptyAdd.setOnClickListener(addAction);
        }

        setupTabs();
        setupRecyclerView();
        loadItems(currentTab);
    }

    private void setupTabs() {
        tabAvailable.setOnClickListener(v -> switchTab("available"));
        tabBorrowed.setOnClickListener(v -> switchTab("borrowed"));
        tabInactive.setOnClickListener(v -> switchTab("inactive"));
    }

    private void switchTab(String tab) {
        currentTab = tab;
        int unselected = ContextCompat.getColor(this, R.color.white_overlay_70);

        tabAvailable.setBackgroundResource(R.color.transparent);
        tabAvailable.setTextColor(unselected);
        tabBorrowed.setBackgroundResource(R.color.transparent);
        tabBorrowed.setTextColor(unselected);
        tabInactive.setBackgroundResource(R.color.transparent);
        tabInactive.setTextColor(unselected);

        if ("available".equalsIgnoreCase(tab)) {
            tabAvailable.setBackgroundResource(R.drawable.bg_card);
            tabAvailable.setTextColor(ContextCompat.getColor(this, R.color.primary));
        } else if ("borrowed".equalsIgnoreCase(tab)) {
            tabBorrowed.setBackgroundResource(R.drawable.bg_card);
            tabBorrowed.setTextColor(ContextCompat.getColor(this, R.color.primary));
        } else if ("inactive".equalsIgnoreCase(tab)) {
            tabInactive.setBackgroundResource(R.drawable.bg_card);
            tabInactive.setTextColor(ContextCompat.getColor(this, R.color.primary));
        }

        loadItems(tab);
    }

    private void setupRecyclerView() {
        adapter = new MyItemAdapter(this, new ArrayList<>(), new MyItemAdapter.OnMyItemActionListener() {
            @Override
            public void onEdit(Item item) {
                Intent intent = new Intent(MyItemsActivity.this, ItemDetailsActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }

            @Override
            public void onToggleActive(Item item, int position) {
                if ("inactive".equalsIgnoreCase(item.getStatus())) {
                    confirmDeleteItem(item);
                } else {
                    item.setStatus("inactive");
                    itemRepository.updateItem(item, new RepositoryCallback<Item>() {
                        @Override
                        public void onSuccess(Item result) {
                            Snackbar.make(findViewById(android.R.id.content), "Item deactivated.", Snackbar.LENGTH_SHORT).show();
                            loadItems(currentTab);
                        }

                        @Override
                        public void onError(Throwable t) {
                            Snackbar.make(findViewById(android.R.id.content), "Failed to deactivate: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        rvMyItems.setLayoutManager(new LinearLayoutManager(this));
        rvMyItems.setAdapter(adapter);
    }

    private void confirmDeleteItem(Item item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to permanently delete '" + item.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    itemRepository.deleteItem(item.getId(), new RepositoryCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Snackbar.make(findViewById(android.R.id.content), "Item permanently deleted.", Snackbar.LENGTH_SHORT).show();
                            loadItems(currentTab);
                        }

                        @Override
                        public void onError(Throwable t) {
                            Snackbar.make(findViewById(android.R.id.content), "Delete failed: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void loadItems(String tab) {
        User user = DataManager.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        itemRepository.getMyItems(user.getId(), new RepositoryCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> items) {
                List<Item> filtered = new ArrayList<>();
                if (items != null) {
                    for (Item it : items) {
                        String st = it.getStatus() != null ? it.getStatus() : (it.isAvailable() ? "available" : "borrowed");
                        if (st.equalsIgnoreCase(tab)) {
                            filtered.add(it);
                        }
                    }
                }

                adapter.updateList(filtered);

                if (filtered.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvMyItems.setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    rvMyItems.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable t) {
                layoutEmpty.setVisibility(View.VISIBLE);
                rvMyItems.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems(currentTab);
    }
}
