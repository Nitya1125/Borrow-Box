package com.borrowbox.app.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.borrowbox.app.data.DatabaseHelper;
import com.borrowbox.app.models.NotificationItem;
import com.borrowbox.app.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    private static NotificationRepository instance;
    private final DatabaseHelper dbHelper;
    private final AppExecutors executors;

    public static synchronized NotificationRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationRepository(context.getApplicationContext());
        }
        return instance;
    }

    private NotificationRepository(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.executors = AppExecutors.getInstance();
    }

    public void getNotifications(String userId, RepositoryCallback<List<NotificationItem>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                List<NotificationItem> list = new ArrayList<>();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                try (Cursor c = db.query(DatabaseHelper.TABLE_NOTIFICATIONS, null,
                        DatabaseHelper.COL_NOTIF_USER_ID + " = ?", new String[]{userId},
                        null, null, DatabaseHelper.COL_NOTIF_CREATED_AT + " DESC")) {
                    if (c != null && c.moveToFirst()) {
                        do {
                            NotificationItem item = new NotificationItem(
                                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_ID)),
                                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_USER_ID)),
                                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_TYPE)),
                                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_REF_ID)),
                                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_TITLE)),
                                    c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_MESSAGE)),
                                    c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_CREATED_AT)),
                                    c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_READ)) == 1
                            );
                            list.add(item);
                        } while (c.moveToNext());
                    }
                }
                executors.postToMainThread(() -> callback.onSuccess(list));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void getUserNotifications(String userId, RepositoryCallback<List<NotificationItem>> callback) {
        getNotifications(userId, callback);
    }

    public void getUnreadCount(String userId, RepositoryCallback<Integer> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                int unread = 0;
                try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_NOTIFICATIONS + " WHERE user_id = ? AND is_read = 0",
                        new String[]{userId})) {
                    if (c != null && c.moveToFirst()) {
                        unread = c.getInt(0);
                    }
                }
                final int count = unread;
                executors.postToMainThread(() -> callback.onSuccess(count));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void markAsRead(String notifId, RepositoryCallback<Boolean> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COL_NOTIF_READ, 1);
                int rows = db.update(DatabaseHelper.TABLE_NOTIFICATIONS, v, DatabaseHelper.COL_NOTIF_ID + " = ?", new String[]{notifId});
                executors.postToMainThread(() -> callback.onSuccess(rows > 0));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void markAllAsRead(String userId, RepositoryCallback<Boolean> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COL_NOTIF_READ, 1);
                int rows = db.update(DatabaseHelper.TABLE_NOTIFICATIONS, v, DatabaseHelper.COL_NOTIF_USER_ID + " = ?", new String[]{userId});
                executors.postToMainThread(() -> callback.onSuccess(rows > 0));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }
}
