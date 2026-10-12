package com.borrowbox.app.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.borrowbox.app.data.DatabaseHelper;
import com.borrowbox.app.models.Review;
import com.borrowbox.app.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReviewRepository {

    private static ReviewRepository instance;
    private final DatabaseHelper dbHelper;
    private final AppExecutors executors;

    public static synchronized ReviewRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ReviewRepository(context.getApplicationContext());
        }
        return instance;
    }

    private ReviewRepository(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.executors = AppExecutors.getInstance();
    }

    public void submitReview(Review review, RepositoryCallback<Review> callback) {
        if (review == null) {
            if (callback != null) callback.onError(new IllegalArgumentException("Review cannot be null"));
            return;
        }
        submitReview(review.getTransactionId(), review.getReviewerId(), review.getRevieweeId(), review.getRating(), review.getComment(), callback);
    }

    public void submitReview(String transactionId, String reviewerId, String revieweeId, float rating, String comment, RepositoryCallback<Review> callback) {
        executors.executeOnDiskIO(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                String reviewId = "rev_" + UUID.randomUUID().toString().substring(0, 8);
                long now = System.currentTimeMillis();

                // 1. Insert into reviews table
                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COL_REV_ID, reviewId);
                v.put(DatabaseHelper.COL_REV_TX_ID, transactionId);
                v.put(DatabaseHelper.COL_REV_REVIEWER_ID, reviewerId);
                v.put(DatabaseHelper.COL_REV_REVIEWEE_ID, revieweeId);
                v.put(DatabaseHelper.COL_REV_RATING, rating);
                v.put(DatabaseHelper.COL_REV_COMMENT, comment != null ? comment.trim() : "");
                v.put(DatabaseHelper.COL_REV_CREATED_AT, now);
                db.insertOrThrow(DatabaseHelper.TABLE_REVIEWS, null, v);

                // 2. Notification for reviewee
                ContentValues notif = new ContentValues();
                notif.put(DatabaseHelper.COL_NOTIF_ID, "notif_" + UUID.randomUUID().toString().substring(0, 8));
                notif.put(DatabaseHelper.COL_NOTIF_USER_ID, revieweeId);
                notif.put(DatabaseHelper.COL_NOTIF_TYPE, "reminder");
                notif.put(DatabaseHelper.COL_NOTIF_REF_ID, transactionId);
                notif.put(DatabaseHelper.COL_NOTIF_TITLE, "New Rating Received");
                notif.put(DatabaseHelper.COL_NOTIF_MESSAGE, "You received a " + String.format("%.1f", rating) + " star review for your recent handover.");
                notif.put(DatabaseHelper.COL_NOTIF_CREATED_AT, now);
                notif.put(DatabaseHelper.COL_NOTIF_READ, 0);
                db.insert(DatabaseHelper.TABLE_NOTIFICATIONS, null, notif);

                db.setTransactionSuccessful();

                Review rev = new Review(reviewId, transactionId, reviewerId, revieweeId, rating, comment, now);
                executors.postToMainThread(() -> callback.onSuccess(rev));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            } finally {
                db.endTransaction();
            }
        });
    }

    public void getReviewsForUser(String userId, RepositoryCallback<List<Review>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                List<Review> list = new ArrayList<>();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String sql = "SELECT r.*, u.name AS reviewer_name, u.avatar_url AS reviewer_avatar " +
                        "FROM " + DatabaseHelper.TABLE_REVIEWS + " r " +
                        "INNER JOIN " + DatabaseHelper.TABLE_USERS + " u ON r.reviewer_id = u.id " +
                        "WHERE r.reviewee_id = ? " +
                        "ORDER BY r.created_at DESC";

                try (Cursor c = db.rawQuery(sql, new String[]{userId})) {
                    if (c != null && c.moveToFirst()) {
                        do {
                            Review r = new Review();
                            r.setId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REV_ID)));
                            r.setTransactionId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REV_TX_ID)));
                            r.setReviewerId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REV_REVIEWER_ID)));
                            r.setRevieweeId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REV_REVIEWEE_ID)));
                            r.setRating(c.getFloat(c.getColumnIndexOrThrow(DatabaseHelper.COL_REV_RATING)));
                            r.setComment(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_REV_COMMENT)));
                            r.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_REV_CREATED_AT)));
                            r.setReviewerName(c.getString(c.getColumnIndexOrThrow("reviewer_name")));
                            r.setReviewerAvatar(c.getString(c.getColumnIndexOrThrow("reviewer_avatar")));
                            list.add(r);
                        } while (c.moveToNext());
                    }
                }
                executors.postToMainThread(() -> callback.onSuccess(list));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }
}
