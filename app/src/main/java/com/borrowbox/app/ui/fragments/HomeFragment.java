package com.borrowbox.app.ui.fragments;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.CategoryAdapter;
import com.borrowbox.app.adapters.DueItemAdapter;
import com.borrowbox.app.adapters.ItemGridAdapter;
import com.borrowbox.app.adapters.PendingRequestAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.repository.BorrowRepository;
import com.borrowbox.app.data.repository.ItemRepository;
import com.borrowbox.app.data.repository.NotificationRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.Category;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.models.User;
import com.borrowbox.app.ui.activities.ItemDetailsActivity;
import com.borrowbox.app.ui.activities.MainActivity;
import com.borrowbox.app.ui.activities.NotificationsActivity;
import com.borrowbox.app.ui.activities.QRVerificationActivity;
import com.borrowbox.app.ui.activities.SearchActivity;
import com.borrowbox.app.utils.AvatarDrawable;
import com.borrowbox.app.utils.DateTimeUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private TextView tvGreeting, tvUserName;
    private ImageView ivAvatar;
    private View cardAvatar;
    private View btnNotifications, notificationDot;
    private View cardSearchBar;
    private ProgressBar progressLoading;

    // Stat views
    private TextView tvStatListings, tvStatBorrowing, tvStatLentOut;
    private TextView tvStatOverdue, tvStatRating, tvStatPoints;

    // Due Soon
    private View layoutDueSection;
    private RecyclerView rvDueItems;
    private DueItemAdapter dueItemAdapter;
    private final List<Transaction> dueItemsList = new ArrayList<>();

    // Pending Requests
    private View layoutPendingSection;
    private RecyclerView rvPendingRequests;
    private PendingRequestAdapter pendingRequestAdapter;
    private final List<Transaction> pendingRequestsList = new ArrayList<>();

    // Categories
    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private final List<Category> categoriesList = new ArrayList<>();
    private String selectedCategoryId = "all";

    // Recommended Items
    private TextView tvSectionTitle, tvSeeAll;
    private RecyclerView rvRecommendedItems;
    private ItemGridAdapter itemGridAdapter;
    private final List<Item> recommendedItemsList = new ArrayList<>();
    private View layoutEmpty;

    private BorrowRepository borrowRepository;
    private ItemRepository itemRepository;
    private NotificationRepository notificationRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        borrowRepository = BorrowRepository.getInstance(requireContext());
        itemRepository = ItemRepository.getInstance(requireContext());
        notificationRepository = NotificationRepository.getInstance(requireContext());

        initViews(view);
        setupAdapters();
        setupListeners();

        loadAllData(false);

        return view;
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipeRefreshHome);
        tvGreeting = view.findViewById(R.id.tvHomeGreeting);
        tvUserName = view.findViewById(R.id.tvHomeUserName);
        ivAvatar = view.findViewById(R.id.ivHomeAvatar);
        cardAvatar = view.findViewById(R.id.cardHomeAvatar);
        btnNotifications = view.findViewById(R.id.btnHomeNotifications);
        notificationDot = view.findViewById(R.id.homeNotificationDot);
        cardSearchBar = view.findViewById(R.id.cardHomeSearchBar);
        progressLoading = view.findViewById(R.id.progressHomeLoading);

        tvStatListings = view.findViewById(R.id.tvStatListingsCount);
        tvStatBorrowing = view.findViewById(R.id.tvStatBorrowingCount);
        tvStatLentOut = view.findViewById(R.id.tvStatLentOutCount);
        tvStatOverdue = view.findViewById(R.id.tvStatOverdueCount);
        tvStatRating = view.findViewById(R.id.tvStatRatingCount);
        tvStatPoints = view.findViewById(R.id.tvStatPointsCount);

        layoutDueSection = view.findViewById(R.id.layoutDueSection);
        rvDueItems = view.findViewById(R.id.rvDueItems);

        layoutPendingSection = view.findViewById(R.id.layoutPendingRequestsSection);
        rvPendingRequests = view.findViewById(R.id.rvPendingRequests);

        rvCategories = view.findViewById(R.id.rvCategories);
        tvSectionTitle = view.findViewById(R.id.tvSectionTitle);
        tvSeeAll = view.findViewById(R.id.tvSeeAll);
        rvRecommendedItems = view.findViewById(R.id.rvRecommendedItems);
        layoutEmpty = view.findViewById(R.id.layoutHomeEmpty);
    }

    private void setupAdapters() {
        // Due items horizontal
        dueItemAdapter = new DueItemAdapter(getContext(), dueItemsList, tx -> {
            Intent intent = new Intent(getActivity(), QRVerificationActivity.class);
            intent.putExtra("transaction", tx);
            startActivity(intent);
        });
        rvDueItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDueItems.setAdapter(dueItemAdapter);

        // Pending requests vertical
        pendingRequestAdapter = new PendingRequestAdapter(getContext(), pendingRequestsList, new PendingRequestAdapter.OnRequestActionListener() {
            @Override
            public void onAccept(Transaction tx, int position) {
                handleAcceptRequest(tx);
            }

            @Override
            public void onReject(Transaction tx, int position) {
                confirmRejectRequest(tx);
            }
        });
        rvPendingRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPendingRequests.setAdapter(pendingRequestAdapter);

        // Categories horizontal
        categoryAdapter = new CategoryAdapter(getContext(), categoriesList, (cat, pos) -> {
            selectedCategoryId = cat.getId();
            if ("all".equalsIgnoreCase(selectedCategoryId)) {
                tvSectionTitle.setText(R.string.recommended_for_you);
            } else {
                tvSectionTitle.setText(cat.getLabel());
            }
            loadItemsForCategory();
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        // Recommended grid
        itemGridAdapter = new ItemGridAdapter(getContext(), recommendedItemsList, item -> {
            Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
        });
        rvRecommendedItems.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvRecommendedItems.setAdapter(itemGridAdapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> loadAllData(true));

        btnNotifications.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationsActivity.class)));

        cardSearchBar.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchActivity.class)));

        if (tvSeeAll != null) {
            tvSeeAll.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchActivity.class)));
        }

        if (cardAvatar != null) {
            cardAvatar.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToProfile();
                }
            });
        }
        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToProfile();
                }
            });
        }
    }

    private void loadAllData(boolean isRefresh) {
        User currentUser = DataManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            return;
        }

        bindUserInfo(currentUser);

        if (!isRefresh && progressLoading != null) {
            progressLoading.setVisibility(View.VISIBLE);
        }

        loadDashboardStats(currentUser.getId());
        loadDueItems(currentUser.getId());
        loadPendingRequests(currentUser.getId());
        loadCategories();
        loadItemsForCategory();
        checkUnreadNotifications(currentUser.getId());
    }

    private void bindUserInfo(User user) {
        if (tvGreeting != null) {
            tvGreeting.setText(DateTimeUtils.getTimeOfDayGreeting());
        }
        if (tvUserName != null) {
            String name = user.getName() != null ? user.getName() : "Student";
            String firstName = name.contains(" ") ? name.split(" ")[0] : name;
            tvUserName.setText(firstName);
        }
        if (ivAvatar != null) {
            String avatar = user.getAvatarUrl();
            if (avatar != null && !avatar.trim().isEmpty()) {
                String cleanPath = avatar.trim();
                Object model = (cleanPath.startsWith("/") && !cleanPath.startsWith("content:"))
                        ? new File(cleanPath)
                        : cleanPath;
                Glide.with(this)
                        .load(model)
                        .circleCrop()
                        .placeholder(new AvatarDrawable(user.getName()))
                        .error(new AvatarDrawable(user.getName()))
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageDrawable(new AvatarDrawable(user.getName()));
            }
        }
    }

    private void checkUnreadNotifications(String userId) {
        notificationRepository.getUnreadCount(userId, new RepositoryCallback<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                if (notificationDot != null && isAdded()) {
                    notificationDot.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onError(Throwable t) {
                // Ignore silently
            }
        });
    }

    private void loadDashboardStats(String userId) {
        borrowRepository.getUserDashboardStats(userId, new RepositoryCallback<Map<String, Number>>() {
            @Override
            public void onSuccess(Map<String, Number> stats) {
                if (!isAdded() || stats == null) return;
                Number myListingsVal = stats.get("myListings");
                int listings = myListingsVal != null ? myListingsVal.intValue() : 0;

                Number borrowingVal = stats.get("borrowing");
                int borrowing = borrowingVal != null ? borrowingVal.intValue() : 0;

                Number lentOutVal = stats.get("lentOut");
                int lentOut = lentOutVal != null ? lentOutVal.intValue() : 0;

                Number overdueVal = stats.get("overdue");
                int overdue = overdueVal != null ? overdueVal.intValue() : 0;

                Number ratingVal = stats.get("rating");
                float rating = ratingVal != null ? ratingVal.floatValue() : 5.0f;

                Number pointsVal = stats.get("points");
                int points = pointsVal != null ? pointsVal.intValue() : 50;

                animateCountUp(tvStatListings, listings);
                animateCountUp(tvStatBorrowing, borrowing);
                animateCountUp(tvStatLentOut, lentOut);
                animateCountUp(tvStatOverdue, overdue);
                animateRating(tvStatRating, rating);
                animateCountUp(tvStatPoints, points);
            }

            @Override
            public void onError(Throwable t) {
                // Ignore
            }
        });
    }

    private void animateCountUp(TextView textView, int target) {
        if (textView == null) return;
        ValueAnimator animator = ValueAnimator.ofInt(0, target);
        animator.setDuration(700);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> textView.setText(String.valueOf(animation.getAnimatedValue())));
        animator.start();
    }

    private void animateRating(TextView textView, float target) {
        if (textView == null) return;
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, target);
        animator.setDuration(700);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float val = (float) animation.getAnimatedValue();
            textView.setText(String.format(Locale.getDefault(), "%.1f", val));
        });
        animator.start();
    }

    private void loadDueItems(String userId) {
        borrowRepository.getDueSoonAndOverdueBorrows(userId, new RepositoryCallback<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> list) {
                if (!isAdded()) return;
                dueItemsList.clear();
                if (list != null && !list.isEmpty()) {
                    dueItemsList.addAll(list);
                    layoutDueSection.setVisibility(View.VISIBLE);
                    dueItemAdapter.notifyDataSetChanged();
                } else {
                    layoutDueSection.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable t) {
                if (isAdded()) layoutDueSection.setVisibility(View.GONE);
            }
        });
    }

    private void loadPendingRequests(String userId) {
        borrowRepository.getPendingRequestsForOwner(userId, new RepositoryCallback<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> list) {
                if (!isAdded()) return;
                pendingRequestsList.clear();
                if (list != null && !list.isEmpty()) {
                    pendingRequestsList.addAll(list);
                    layoutPendingSection.setVisibility(View.VISIBLE);
                    pendingRequestAdapter.notifyDataSetChanged();
                } else {
                    layoutPendingSection.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable t) {
                if (isAdded()) layoutPendingSection.setVisibility(View.GONE);
            }
        });
    }

    private void handleAcceptRequest(Transaction tx) {
        borrowRepository.approveRequest(tx.getId(), new RepositoryCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                if (!isAdded()) return;
                if (getView() != null) {
                    Snackbar.make(getView(), R.string.request_accepted_notif, Snackbar.LENGTH_SHORT).show();
                }
                User user = DataManager.getInstance().getCurrentUser();
                if (user != null) {
                    loadDashboardStats(user.getId());
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!isAdded()) return;
                if (getView() != null) {
                    Snackbar.make(getView(), "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void confirmRejectRequest(Transaction tx) {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.decline_request_title)
                .setMessage(R.string.decline_request_confirm)
                .setPositiveButton(R.string.decline, (dialog, which) -> {
                    borrowRepository.rejectRequest(tx.getId(), new RepositoryCallback<Transaction>() {
                        @Override
                        public void onSuccess(Transaction result) {
                            if (!isAdded()) return;
                            if (getView() != null) {
                                Snackbar.make(getView(), R.string.request_rejected_notif, Snackbar.LENGTH_SHORT).show();
                            }
                            User user = DataManager.getInstance().getCurrentUser();
                            if (user != null) {
                                loadDashboardStats(user.getId());
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            if (!isAdded()) return;
                            if (getView() != null) {
                                Snackbar.make(getView(), "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void loadCategories() {
        itemRepository.getCategories(new RepositoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> list) {
                if (!isAdded()) return;
                categoriesList.clear();
                categoriesList.add(new Category("all", getString(R.string.all), R.drawable.ic_cat_all));
                if (list != null) {
                    for (Category cat : list) {
                        if (!"all".equalsIgnoreCase(cat.getId())) {
                            categoriesList.add(cat);
                        }
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable t) {
                // Keep default if error
            }
        });
    }

    private void loadItemsForCategory() {
        User currentUser = DataManager.getInstance().getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getId() : "";

        RepositoryCallback<List<Item>> callback = new RepositoryCallback<List<Item>>() {
            @Override
            public void onSuccess(List<Item> items) {
                if (!isAdded()) return;
                if (progressLoading != null) progressLoading.setVisibility(View.GONE);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);

                recommendedItemsList.clear();
                if (items != null && !items.isEmpty()) {
                    recommendedItemsList.addAll(items);
                    rvRecommendedItems.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                } else {
                    rvRecommendedItems.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
                itemGridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable t) {
                if (!isAdded()) return;
                if (progressLoading != null) progressLoading.setVisibility(View.GONE);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                rvRecommendedItems.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }
        };

        if ("all".equalsIgnoreCase(selectedCategoryId)) {
            itemRepository.getRecommendedItems(currentUserId, callback);
        } else {
            itemRepository.getItemsByCategory(selectedCategoryId, currentUserId, callback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllData(false);
    }
}
