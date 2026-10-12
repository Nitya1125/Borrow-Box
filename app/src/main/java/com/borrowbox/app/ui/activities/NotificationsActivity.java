package com.borrowbox.app.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.NotificationAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.data.repository.NotificationRepository;
import com.borrowbox.app.data.repository.RepositoryCallback;
import com.borrowbox.app.models.NotificationItem;
import com.borrowbox.app.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvUnreadCount;
    private MaterialButton btnMarkAllRead;
    private RecyclerView rvNotifications;
    private View layoutEmpty;
    private NotificationAdapter adapter;

    private NotificationRepository notificationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationRepository = NotificationRepository.getInstance(this);

        btnBack = findViewById(R.id.btnNotifBack);
        tvUnreadCount = findViewById(R.id.tvNotifUnreadCount);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        rvNotifications = findViewById(R.id.rvNotifications);
        layoutEmpty = findViewById(R.id.layoutNotifEmpty);

        btnBack.setOnClickListener(v -> finish());

        btnMarkAllRead.setOnClickListener(v -> {
            User user = DataManager.getInstance().getCurrentUser();
            if (user == null) return;

            notificationRepository.markAllAsRead(user.getId(), new RepositoryCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    loadNotifications();
                    Snackbar.make(findViewById(android.R.id.content), "All notifications marked as read", Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable t) {
                    Snackbar.make(findViewById(android.R.id.content), "Error: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            });
        });

        setupRecyclerView();
        loadNotifications();
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(this, new ArrayList<>(), (item, position) -> {
            notificationRepository.markAsRead(item.getId(), new RepositoryCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    updateUnreadCountOnly();
                }

                @Override
                public void onError(Throwable t) {
                    // Ignore
                }
            });
        });

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        User user = DataManager.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        notificationRepository.getUserNotifications(user.getId(), new RepositoryCallback<List<NotificationItem>>() {
            @Override
            public void onSuccess(List<NotificationItem> list) {
                adapter.updateList(list != null ? list : new ArrayList<>());

                if (list == null || list.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvNotifications.setVisibility(View.GONE);
                    btnMarkAllRead.setVisibility(View.GONE);
                    tvUnreadCount.setText("All caught up");
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    rvNotifications.setVisibility(View.VISIBLE);
                    updateUnreadCountOnly();
                }
            }

            @Override
            public void onError(Throwable t) {
                layoutEmpty.setVisibility(View.VISIBLE);
                rvNotifications.setVisibility(View.GONE);
            }
        });
    }

    private void updateUnreadCountOnly() {
        User user = DataManager.getInstance().getCurrentUser();
        if (user == null) return;

        notificationRepository.getUnreadCount(user.getId(), new RepositoryCallback<Integer>() {
            @Override
            public void onSuccess(Integer unread) {
                if (unread != null && unread > 0) {
                    String countText = getResources().getQuantityString(R.plurals.unread_notifications_count, unread, unread);
                    tvUnreadCount.setText(countText);
                    btnMarkAllRead.setVisibility(View.VISIBLE);
                } else {
                    tvUnreadCount.setText("All caught up");
                    btnMarkAllRead.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable t) {
                // Ignore
            }
        });
    }
}
