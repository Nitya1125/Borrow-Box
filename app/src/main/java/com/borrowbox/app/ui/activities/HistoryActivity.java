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
import com.borrowbox.app.data.repository.BorrowRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.models.User;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvCount, chipAll, chipBorrowed, chipLent;
    private RecyclerView rvHistory;
    private View layoutEmpty;
    private HistoryAdapter adapter;

    private String currentFilter = "All";
    private BorrowRepository borrowRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        borrowRepository = BorrowRepository.getInstance(this);

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
        loadHistory(currentFilter);
    }

    private void setupFilters() {
        View.OnClickListener listener = v -> {
            resetChips();
            TextView tv = (TextView) v;
            tv.setBackgroundResource(R.drawable.bg_chip_selected);
            tv.setTextColor(Color.WHITE);
            currentFilter = tv.getText().toString();
            loadHistory(currentFilter);
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

    private void loadHistory(String filter) {
        User user = DataManager.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        String typeArg = "All".equalsIgnoreCase(filter) ? null : ("Borrowed".equalsIgnoreCase(filter) ? "borrowed" : "lent");

        borrowRepository.getTransactionsForUser(user.getId(), typeArg, new RepositoryCallback<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> transactions) {
                List<Transaction> completed = new ArrayList<>();
                if (transactions != null) {
                    for (Transaction t : transactions) {
                        if ("returned".equalsIgnoreCase(t.getStatus())) {
                            completed.add(t);
                        }
                    }
                }

                int size = completed.size();
                String countText = getResources().getQuantityString(R.plurals.completed_transactions_count, size, size);
                tvCount.setText(countText);
                adapter.updateList(completed);

                if (completed.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvHistory.setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    rvHistory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable t) {
                tvCount.setText("0 completed transactions");
                layoutEmpty.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
            }
        });
    }
}
