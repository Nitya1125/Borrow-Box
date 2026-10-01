package com.borrowbox.app.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borrowbox.app.R;
import com.borrowbox.app.adapters.NotificationAdapter;
import com.borrowbox.app.data.DataManager;
import com.borrowbox.app.models.NotificationItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvUnreadCount;
    private MaterialButton btnMarkAllRead;
    private RecyclerView rvNotifications;
    private View layoutEmpty;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        btnBack = findViewById(R.id.btnNotifBack);
        tvUnreadCount = findViewById(R.id.tvNotifUnreadCount);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        rvNotifications = findViewById(R.id.rvNotifications);
        layoutEmpty = findViewById(R.id.layoutNotifEmpty);

        btnBack.setOnClickListener(v -> finish());

        btnMarkAllRead.setOnClickListener(v -> {
            DataManager.getInstance().markAllNotificationsRead();
            updateNotificationsUI();
            Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show();
        });

        setupRecyclerView();
        updateNotificationsUI();
    }

    private void setupRecyclerView() {
        List<NotificationItem> list = DataManager.getInstance().getNotifications();
        adapter = new NotificationAdapter(this, list, (item, position) -> {
            updateUnreadCount();
        });

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    private void updateNotificationsUI() {
        List<NotificationItem> list = DataManager.getInstance().getNotifications();
        adapter.updateList(list);

        if (list.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
            btnMarkAllRead.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
            updateUnreadCount();
        }
    }

    private void updateUnreadCount() {
        int unread = DataManager.getInstance().getUnreadNotificationCount();
        if (unread > 0) {
            tvUnreadCount.setText(unread + " unread");
            btnMarkAllRead.setVisibility(View.VISIBLE);
        } else {
            tvUnreadCount.setText("All caught up");
            btnMarkAllRead.setVisibility(View.GONE);
        }
    }
}
