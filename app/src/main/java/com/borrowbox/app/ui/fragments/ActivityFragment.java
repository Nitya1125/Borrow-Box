package com.borrowbox.app.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.TransactionAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.repository.BorrowRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.models.User;
import com.borrowbox.app.ui.activities.QRVerificationActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {

    private TextView tabBorrowed, tabLent, tabRequests;
    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private View layoutEmpty;
    private TextView tvEmptySub;
    private ProgressBar progressBar;

    private String currentTab = "Borrowed";
    private BorrowRepository borrowRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        borrowRepository = BorrowRepository.getInstance(requireContext());

        tabBorrowed = view.findViewById(R.id.tabBorrowed);
        tabLent = view.findViewById(R.id.tabLent);
        tabRequests = view.findViewById(R.id.tabRequests);
        rvTransactions = view.findViewById(R.id.rvActivityTransactions);
        layoutEmpty = view.findViewById(R.id.layoutActivityEmpty);
        tvEmptySub = view.findViewById(R.id.tvActivityEmptySub);
        progressBar = view.findViewById(R.id.progressActivityLoading);

        setupTabs();
        setupRecyclerView();
        filterTab(currentTab);

        return view;
    }

    private void setupTabs() {
        tabBorrowed.setOnClickListener(v -> switchTab("Borrowed"));
        tabLent.setOnClickListener(v -> switchTab("Shared"));
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
        } else if ("Shared".equals(tabName) || "Lent".equals(tabName)) {
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
                intent.putExtra("transaction", transaction);
                startActivity(intent);
            }

            @Override
            public void onAcceptRequest(Transaction transaction, int position) {
                borrowRepository.approveRequest(transaction.getId(), new RepositoryCallback<Transaction>() {
                    @Override
                    public void onSuccess(Transaction result) {
                        if (getView() != null) {
                            Snackbar.make(getView(), R.string.request_accepted_notif, Snackbar.LENGTH_SHORT).show();
                        }
                        filterTab(currentTab);
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (getView() != null) {
                            Snackbar.make(getView(), "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onRejectRequest(Transaction transaction, int position) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.decline_request_title)
                        .setMessage(R.string.decline_request_confirm)
                        .setPositiveButton(R.string.decline, (dialog, which) -> {
                            borrowRepository.rejectRequest(transaction.getId(), new RepositoryCallback<Transaction>() {
                                @Override
                                public void onSuccess(Transaction result) {
                                    if (getView() != null) {
                                        Snackbar.make(getView(), R.string.request_rejected_notif, Snackbar.LENGTH_SHORT).show();
                                    }
                                    filterTab(currentTab);
                                }

                                @Override
                                public void onError(Throwable t) {
                                    if (getView() != null) {
                                        Snackbar.make(getView(), "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });

        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransactions.setAdapter(adapter);
    }

    private void filterTab(String tab) {
        User user = DataManager.getInstance().getCurrentUser();
        if (user == null) return;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        boolean isRequests = "Requests".equals(tab);
        String filter = "Borrowed".equals(tab) ? "borrowed" : (("Shared".equals(tab) || "Lent".equals(tab)) ? "lent" : "requests");

        if (isRequests) {
            borrowRepository.getPendingRequestsForOwner(user.getId(), new RepositoryCallback<List<Transaction>>() {
                @Override
                public void onSuccess(List<Transaction> list) {
                    if (!isAdded()) return;
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (tvEmptySub != null) tvEmptySub.setText("No incoming requests yet.");
                    displayTransactions(list, true);
                }

                @Override
                public void onError(Throwable t) {
                    if (!isAdded()) return;
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    displayTransactions(new ArrayList<>(), true);
                }
            });
        } else {
            borrowRepository.getTransactionsForUser(user.getId(), filter, new RepositoryCallback<List<Transaction>>() {
                @Override
                public void onSuccess(List<Transaction> list) {
                    if (!isAdded()) return;
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (tvEmptySub != null) {
                        if ("Borrowed".equals(tab)) {
                            tvEmptySub.setText("Start borrowing items from students near you!");
                        } else {
                            tvEmptySub.setText("List your items and help others!");
                        }
                    }
                    displayTransactions(list, false);
                }

                @Override
                public void onError(Throwable t) {
                    if (!isAdded()) return;
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    displayTransactions(new ArrayList<>(), false);
                }
            });
        }
    }

    private void displayTransactions(List<Transaction> list, boolean isRequests) {
        adapter.updateData(list != null ? list : new ArrayList<>(), isRequests);
        if (list == null || list.isEmpty()) {
            if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
            rvTransactions.setVisibility(View.GONE);
        } else {
            if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);
            rvTransactions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        filterTab(currentTab);
    }
}
