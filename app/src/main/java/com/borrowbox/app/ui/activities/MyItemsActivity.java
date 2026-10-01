package com.borrowbox.app.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.MyItemAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.Item;
import com.google.android.material.button.MaterialButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);

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
            // Return to main activity and select Add tab
            Intent intent = new Intent(MyItemsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        };

        btnAdd.setOnClickListener(addAction);
        btnEmptyAdd.setOnClickListener(addAction);

        setupTabs();
        setupRecyclerView();
        filterTab("available");
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

        filterTab(tab);
    }

    private void setupRecyclerView() {
        adapter = new MyItemAdapter(this, new ArrayList<>(), new MyItemAdapter.OnMyItemActionListener() {
            @Override
            public void onEdit(Item item) {
                Toast.makeText(MyItemsActivity.this, "Editing " + item.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onToggleActive(Item item, int position) {
                if ("inactive".equalsIgnoreCase(item.getStatus())) {
                    DataManager.getInstance().getMyItems().remove(item);
                    Toast.makeText(MyItemsActivity.this, "Item deleted.", Toast.LENGTH_SHORT).show();
                } else {
                    item.setStatus("inactive");
                    Toast.makeText(MyItemsActivity.this, "Item deactivated.", Toast.LENGTH_SHORT).show();
                }
                filterTab(currentTab);
            }
        });

        rvMyItems.setLayoutManager(new LinearLayoutManager(this));
        rvMyItems.setAdapter(adapter);
    }

    private void filterTab(String tab) {
        List<Item> all = DataManager.getInstance().getMyItems();
        List<Item> filtered = new ArrayList<>();

        for (Item it : all) {
            String st = it.getStatus() != null ? it.getStatus() : (it.isAvailable() ? "available" : "borrowed");
            if (st.equalsIgnoreCase(tab)) {
                filtered.add(it);
            }
        }

        adapter.updateList(filtered);

        if (filtered.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvMyItems.setVisibility(View.GONE);
            tvEmptyTitle.setText("No " + tab + " items");
            if ("available".equalsIgnoreCase(tab)) {
                tvEmptySub.setText("List an item and help students near you!");
                btnEmptyAdd.setVisibility(View.VISIBLE);
            } else if ("borrowed".equalsIgnoreCase(tab)) {
                tvEmptySub.setText("None of your items are currently borrowed.");
                btnEmptyAdd.setVisibility(View.GONE);
            } else {
                tvEmptySub.setText("No inactive items.");
                btnEmptyAdd.setVisibility(View.GONE);
            }
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvMyItems.setVisibility(View.VISIBLE);
        }
    }
}
