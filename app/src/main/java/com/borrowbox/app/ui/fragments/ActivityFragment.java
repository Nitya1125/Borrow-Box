package com.borrowbox.app.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.TransactionAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.ui.activities.QRVerificationActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {

    private TextView tabBorrowed, tabLent, tabRequests;
    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private View layoutEmpty;
    private TextView tvEmptySub;

    private String currentTab = "Borrowed";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        tabBorrowed = view.findViewById(R.id.tabBorrowed);
        tabLent = view.findViewById(R.id.tabLent);
        tabRequests = view.findViewById(R.id.tabRequests);
        rvTransactions = view.findViewById(R.id.rvActivityTransactions);
        layoutEmpty = view.findViewById(R.id.layoutActivityEmpty);
        tvEmptySub = view.findViewById(R.id.tvActivityEmptySub);

        setupTabs();
        setupRecyclerView();
        filterTab(currentTab);

        return view;
    }

    private void setupTabs() {
        tabBorrowed.setOnClickListener(v -> switchTab("Borrowed"));
        tabLent.setOnClickListener(v -> switchTab("Lent"));
        tabRequests.setOnClickListener(v -> switchTab("Requests"));
    }

    private void switchTab(String tabName) {
        currentTab = tabName;
        int unselectedColor = ContextCompat.getColor(requireContext(), R.color.text_tertiary);

        tabBorrowed.setBackgroundResource(R.color.transparent);
        tabBorrowed.setTextColor(unselectedColor);
        tabLent.setBackgroundResource(R.color.transparent);
        tabLent.setTextColor(unselectedColor);
        tabRequests.setBackgroundResource(R.color.transparent);
        tabRequests.setTextColor(unselectedColor);

        if ("Borrowed".equals(tabName)) {
            tabBorrowed.setBackgroundResource(R.drawable.bg_chip_selected);
            tabBorrowed.setTextColor(Color.WHITE);
        } else if ("Lent".equals(tabName)) {
            tabLent.setBackgroundResource(R.drawable.bg_chip_selected);
            tabLent.setTextColor(Color.WHITE);
        } else if ("Requests".equals(tabName)) {
            tabRequests.setBackgroundResource(R.drawable.bg_chip_selected);
            tabRequests.setTextColor(Color.WHITE);
        }

        filterTab(tabName);
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter(getContext(), new ArrayList<>(), false, new TransactionAdapter.OnTransactionActionListener() {
            @Override
            public void onShowQr(Transaction transaction) {
                Intent intent = new Intent(getActivity(), QRVerificationActivity.class);
                intent.putExtra("item", transaction.getItem());
                startActivity(intent);
            }

            @Override
            public void onAcceptRequest(Transaction transaction, int position) {
                DataManager.getInstance().acceptBorrowRequest(transaction);
                Toast.makeText(getContext(), "Request accepted! QR code ready for handover.", Toast.LENGTH_SHORT).show();
                filterTab(currentTab);
            }

            @Override
            public void onRejectRequest(Transaction transaction, int position) {
                DataManager.getInstance().rejectBorrowRequest(transaction);
                Toast.makeText(getContext(), "Request rejected.", Toast.LENGTH_SHORT).show();
                filterTab(currentTab);
            }
        });

        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransactions.setAdapter(adapter);
    }

    private void filterTab(String tab) {
        List<Transaction> all = DataManager.getInstance().getTransactions();
        List<Transaction> filtered = new ArrayList<>();
        boolean isReq = "Requests".equals(tab);

        if ("Borrowed".equals(tab)) {
            for (Transaction t : all) {
                if ("borrowed".equalsIgnoreCase(t.getType())) filtered.add(t);
            }
            tvEmptySub.setText("Start borrowing items from students near you!");
        } else if ("Lent".equals(tab)) {
            for (Transaction t : all) {
                if ("lent".equalsIgnoreCase(t.getType())) filtered.add(t);
            }
            tvEmptySub.setText("List your items and help others!");
        } else if ("Requests".equals(tab)) {
            for (Transaction t : all) {
                if ("lent".equalsIgnoreCase(t.getType()) && ("pending".equalsIgnoreCase(t.getStatus()) || "approved".equalsIgnoreCase(t.getStatus()))) {
                    filtered.add(t);
                }
            }
            tvEmptySub.setText("No incoming requests yet.");
        }

        adapter.updateData(filtered, isReq);

        if (filtered.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvTransactions.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvTransactions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        filterTab(currentTab);
    }
}
