package com.borrowbox.app.ui.activities;

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
import com.borrowbox.app.adapters.HistoryAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvCount, chipAll, chipBorrowed, chipLent;
    private RecyclerView rvHistory;
    private View layoutEmpty;
    private HistoryAdapter adapter;

    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btnBack = findViewById(R.id.btnHistoryBack);
        tvCount = findViewById(R.id.tvHistoryCompletedCount);
        chipAll = findViewById(R.id.chipHistAll);
        chipBorrowed = findViewById(R.id.chipHistBorrowed);
        chipLent = findViewById(R.id.chipHistLent);
        rvHistory = findViewById(R.id.rvHistoryList);
        layoutEmpty = findViewById(R.id.layoutHistoryEmpty);

        btnBack.setOnClickListener(v -> finish());

        setupFilters();
        setupRecyclerView();
        applyFilter("All");
    }

    private void setupFilters() {
        View.OnClickListener listener = v -> {
            resetChips();
            TextView tv = (TextView) v;
            tv.setBackgroundResource(R.drawable.bg_chip_selected);
            tv.setTextColor(Color.WHITE);
            currentFilter = tv.getText().toString();
            applyFilter(currentFilter);
        };

        chipAll.setOnClickListener(listener);
        chipBorrowed.setOnClickListener(listener);
        chipLent.setOnClickListener(listener);
    }

    private void resetChips() {
        int unselected = ContextCompat.getColor(this, R.color.text_secondary);
        chipAll.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipAll.setTextColor(unselected);
        chipBorrowed.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipBorrowed.setTextColor(unselected);
        chipLent.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipLent.setTextColor(unselected);
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(this, new ArrayList<>());
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }

    private void applyFilter(String filter) {
        List<Transaction> all = DataManager.getInstance().getTransactions();
        List<Transaction> completed = new ArrayList<>();

        for (Transaction t : all) {
            if ("returned".equalsIgnoreCase(t.getStatus())) {
                if ("All".equalsIgnoreCase(filter) || filter.equalsIgnoreCase(t.getType())) {
                    completed.add(t);
                }
            }
        }

        tvCount.setText(completed.size() + " completed transactions");
        adapter.updateList(completed);

        if (completed.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
        }
    }
}
