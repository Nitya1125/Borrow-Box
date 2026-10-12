package com.borrowbox.app.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.borrowbox.app.data.DatabaseHelper;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.NotificationItem;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.utils.AppExecutors;
import com.borrowbox.app.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BorrowRepository {

    private static BorrowRepository instance;
    private final DatabaseHelper dbHelper;
    private final AppExecutors executors;

    public static synchronized BorrowRepository getInstance(Context context) {
        if (instance == null) {
            instance = new BorrowRepository(context.getApplicationContext());
        }
        return instance;
    }

    private BorrowRepository(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.executors = AppExecutors.getInstance();
    }

    public void requestBorrow(Item item, String borrowerId, String borrowerName, int durationDays, String message, RepositoryCallback<Transaction> callback) {
        executors.executeOnDiskIO(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                // Business Rule 1: Cannot borrow your own item
                if (item.getOwnerId() != null && item.getOwnerId().equals(borrowerId)) {
                    throw new IllegalStateException("You cannot borrow your own listed item.");
                }

                // Business Rule 2: Cannot borrow an unavailable item
                try (Cursor c = db.query(DatabaseHelper.TABLE_ITEMS, new String[]{DatabaseHelper.COL_ITEM_STATUS},
                        DatabaseHelper.COL_ITEM_ID + " = ?", new String[]{item.getId()}, null, null, null)) {
                    if (c != null && c.moveToFirst()) {
                        String st = c.getString(0);
                        if (!"available".equalsIgnoreCase(st)) {
                            throw new IllegalStateException("This item is currently unavailable.");
                        }
                    }
                }

                String txId = "tx_" + UUID.randomUUID().toString().substring(0, 8);
                String qrToken = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
                long now = System.currentTimeMillis();
                long dueAt = DateTimeUtils.calculateDueTimestamp(now, durationDays);

                ContentValues txValues = new ContentValues();
                txValues.put(DatabaseHelper.COL_TX_ID, txId);
                txValues.put(DatabaseHelper.COL_TX_ITEM_ID, item.getId());
                txValues.put(DatabaseHelper.COL_TX_BORROWER_ID, borrowerId);
                txValues.put(DatabaseHelper.COL_TX_OWNER_ID, item.getOwnerId());
                txValues.put(DatabaseHelper.COL_TX_STATUS, "pending");
                txValues.put(DatabaseHelper.COL_TX_REQUESTED_AT, now);
                txValues.put(DatabaseHelper.COL_TX_DUE_AT, dueAt);
                txValues.put(DatabaseHelper.COL_TX_RETURNED_AT, 0);
                txValues.put(DatabaseHelper.COL_TX_MESSAGE, message != null ? message : "");
                txValues.put(DatabaseHelper.COL_TX_QR_TOKEN, qrToken);

                db.insertOrThrow(DatabaseHelper.TABLE_TRANSACTIONS, null, txValues);

                // Atomic notification for item owner
                ContentValues notifValues = new ContentValues();
                notifValues.put(DatabaseHelper.COL_NOTIF_ID, "notif_" + UUID.randomUUID().toString().substring(0, 8));
                notifValues.put(DatabaseHelper.COL_NOTIF_USER_ID, item.getOwnerId());
                notifValues.put(DatabaseHelper.COL_NOTIF_TYPE, "request");
                notifValues.put(DatabaseHelper.COL_NOTIF_REF_ID, txId);
                notifValues.put(DatabaseHelper.COL_NOTIF_TITLE, "New Borrow Request");
                notifValues.put(DatabaseHelper.COL_NOTIF_MESSAGE, borrowerName + " wants to borrow your " + item.getName());
                notifValues.put(DatabaseHelper.COL_NOTIF_CREATED_AT, now);
                notifValues.put(DatabaseHelper.COL_NOTIF_READ, 0);

                db.insertOrThrow(DatabaseHelper.TABLE_NOTIFICATIONS, null, notifValues);

                db.setTransactionSuccessful();

                Transaction tx = getTransactionByIdDirect(db, txId, borrowerId);
                executors.postToMainThread(() -> callback.onSuccess(tx));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            } finally {
                db.endTransaction();
            }
        });
    }

    public void approveRequest(String txId, RepositoryCallback<Transaction> callback) {
        executors.executeOnDiskIO(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COL_TX_STATUS, "approved");
                db.update(DatabaseHelper.TABLE_TRANSACTIONS, cv, DatabaseHelper.COL_TX_ID + " = ?", new String[]{txId});

                // Fetch details for notification
                String borrowerId = "";
                String itemName = "";
                try (Cursor c = db.rawQuery("SELECT t.borrower_id, i.name FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " t " +
                        "INNER JOIN " + DatabaseHelper.TABLE_ITEMS + " i ON t.item_id = i.id " +
                        "WHERE t.id = ?", new String[]{txId})) {
                    if (c != null && c.moveToFirst()) {
                        borrowerId = c.getString(0);
                        itemName = c.getString(1);
                    }
                }

                // Notify borrower
                if (!borrowerId.isEmpty()) {
                    ContentValues notifValues = new ContentValues();
                    notifValues.put(DatabaseHelper.COL_NOTIF_ID, "notif_" + UUID.randomUUID().toString().substring(0, 8));
                    notifValues.put(DatabaseHelper.COL_NOTIF_USER_ID, borrowerId);
                    notifValues.put(DatabaseHelper.COL_NOTIF_TYPE, "accepted");
                    notifValues.put(DatabaseHelper.COL_NOTIF_REF_ID, txId);
                    notifValues.put(DatabaseHelper.COL_NOTIF_TITLE, "Request Approved!");
                    notifValues.put(DatabaseHelper.COL_NOTIF_MESSAGE, "Your borrow request for " + itemName + " was approved. Meet up for QR handover.");
                    notifValues.put(DatabaseHelper.COL_NOTIF_CREATED_AT, System.currentTimeMillis());
                    notifValues.put(DatabaseHelper.COL_NOTIF_READ, 0);
                    db.insert(DatabaseHelper.TABLE_NOTIFICATIONS, null, notifValues);
                }

                db.setTransactionSuccessful();
                Transaction refreshed = getTransactionByIdDirect(db, txId, null);
                executors.postToMainThread(() -> callback.onSuccess(refreshed));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            } finally {
                db.endTransaction();
            }
        });
    }

    public void rejectRequest(String txId, RepositoryCallback<Transaction> callback) {
        executors.executeOnDiskIO(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COL_TX_STATUS, "rejected");
                db.update(DatabaseHelper.TABLE_TRANSACTIONS, cv, DatabaseHelper.COL_TX_ID + " = ?", new String[]{txId});

                String borrowerId = "";
                String itemName = "";
                try (Cursor c = db.rawQuery("SELECT t.borrower_id, i.name FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " t " +
                        "INNER JOIN " + DatabaseHelper.TABLE_ITEMS + " i ON t.item_id = i.id " +
                        "WHERE t.id = ?", new String[]{txId})) {
                    if (c != null && c.moveToFirst()) {
                        borrowerId = c.getString(0);
                        itemName = c.getString(1);
                    }
                }

                if (!borrowerId.isEmpty()) {
                    ContentValues notifValues = new ContentValues();
                    notifValues.put(DatabaseHelper.COL_NOTIF_ID, "notif_" + UUID.randomUUID().toString().substring(0, 8));
                    notifValues.put(DatabaseHelper.COL_NOTIF_USER_ID, borrowerId);
                    notifValues.put(DatabaseHelper.COL_NOTIF_TYPE, "reminder");
                    notifValues.put(DatabaseHelper.COL_NOTIF_REF_ID, txId);
                    notifValues.put(DatabaseHelper.COL_NOTIF_TITLE, "Request Declined");
                    notifValues.put(DatabaseHelper.COL_NOTIF_MESSAGE, "Your borrow request for " + itemName + " was declined.");
                    notifValues.put(DatabaseHelper.COL_NOTIF_CREATED_AT, System.currentTimeMillis());
                    notifValues.put(DatabaseHelper.COL_NOTIF_READ, 0);
                    db.insert(DatabaseHelper.TABLE_NOTIFICATIONS, null, notifValues);
                }

                db.setTransactionSuccessful();
                Transaction refreshed = getTransactionByIdDirect(db, txId, null);
                executors.postToMainThread(() -> callback.onSuccess(refreshed));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            } finally {
                db.endTransaction();
            }
        });
    }

    public void verifyHandoverQr(String txId, String scannedToken, String scannedByUserId, RepositoryCallback<Transaction> callback) {
        executors.executeOnDiskIO(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                String expectedToken = null;
                String itemId = null;
                String ownerId = null;
                String borrowerId = null;

                try (Cursor c = db.query(DatabaseHelper.TABLE_TRANSACTIONS,
                        new String[]{DatabaseHelper.COL_TX_QR_TOKEN, DatabaseHelper.COL_TX_ITEM_ID, DatabaseHelper.COL_TX_OWNER_ID, DatabaseHelper.COL_TX_BORROWER_ID, DatabaseHelper.COL_TX_STATUS},
                        DatabaseHelper.COL_TX_ID + " = ?", new String[]{txId}, null, null, null)) {
                    if (c != null && c.moveToFirst()) {
                        expectedToken = c.getString(0);
                        itemId = c.getString(1);
                        ownerId = c.getString(2);
                        borrowerId = c.getString(3);
                    }
                }

                if (expectedToken == null || !expectedToken.equals(scannedToken)) {
                    throw new IllegalArgumentException("Invalid QR verification token.");
                }

                // Update Transaction to 'borrowed'
                ContentValues txV = new ContentValues();
                txV.put(DatabaseHelper.COL_TX_STATUS, "borrowed");
                db.update(DatabaseHelper.TABLE_TRANSACTIONS, txV, DatabaseHelper.COL_TX_ID + " = ?", new String[]{txId});

                // Update Item to 'borrowed'
                ContentValues itemV = new ContentValues();
                itemV.put(DatabaseHelper.COL_ITEM_STATUS, "borrowed");
                db.update(DatabaseHelper.TABLE_ITEMS, itemV, DatabaseHelper.COL_ITEM_ID + " = ?", new String[]{itemId});

                // Notification for borrower confirming handover
                ContentValues notif = new ContentValues();
                notif.put(DatabaseHelper.COL_NOTIF_ID, "notif_" + UUID.randomUUID().toString().substring(0, 8));
                notif.put(DatabaseHelper.COL_NOTIF_USER_ID, borrowerId);
                notif.put(DatabaseHelper.COL_NOTIF_TYPE, "accepted");
                notif.put(DatabaseHelper.COL_NOTIF_REF_ID, txId);
                notif.put(DatabaseHelper.COL_NOTIF_TITLE, "Handover Complete!");
                notif.put(DatabaseHelper.COL_NOTIF_MESSAGE, "Handover verified via QR code. Enjoy using the item!");
                notif.put(DatabaseHelper.COL_NOTIF_CREATED_AT, System.currentTimeMillis());
                notif.put(DatabaseHelper.COL_NOTIF_READ, 0);
                db.insert(DatabaseHelper.TABLE_NOTIFICATIONS, null, notif);

                db.setTransactionSuccessful();
                Transaction tx = getTransactionByIdDirect(db, txId, scannedByUserId);
                executors.postToMainThread(() -> callback.onSuccess(tx));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            } finally {
                db.endTransaction();
            }
        });
    }

    public void verifyReturn(String txId, RepositoryCallback<Transaction> callback) {
        executors.executeOnDiskIO(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                String itemId = null;
                String ownerId = null;
                String borrowerId = null;

                try (Cursor c = db.query(DatabaseHelper.TABLE_TRANSACTIONS,
                        new String[]{DatabaseHelper.COL_TX_ITEM_ID, DatabaseHelper.COL_TX_OWNER_ID, DatabaseHelper.COL_TX_BORROWER_ID},
                        DatabaseHelper.COL_TX_ID + " = ?", new String[]{txId}, null, null, null)) {
                    if (c != null && c.moveToFirst()) {
                        itemId = c.getString(0);
                        ownerId = c.getString(1);
                        borrowerId = c.getString(2);
                    }
                }

                long now = System.currentTimeMillis();

                // 1. Update Transaction to 'returned'
                ContentValues txV = new ContentValues();
                txV.put(DatabaseHelper.COL_TX_STATUS, "returned");
                txV.put(DatabaseHelper.COL_TX_RETURNED_AT, now);
                db.update(DatabaseHelper.TABLE_TRANSACTIONS, txV, DatabaseHelper.COL_TX_ID + " = ?", new String[]{txId});

                // 2. Make Item available again
                ContentValues itemV = new ContentValues();
                itemV.put(DatabaseHelper.COL_ITEM_STATUS, "available");
                db.update(DatabaseHelper.TABLE_ITEMS, itemV, DatabaseHelper.COL_ITEM_ID + " = ?", new String[]{itemId});

                // 3. Prompt review notification for borrower
                if (borrowerId != null) {
                    ContentValues notif = new ContentValues();
                    notif.put(DatabaseHelper.COL_NOTIF_ID, "notif_" + UUID.randomUUID().toString().substring(0, 8));
                    notif.put(DatabaseHelper.COL_NOTIF_USER_ID, borrowerId);
                    notif.put(DatabaseHelper.COL_NOTIF_TYPE, "returned");
                    notif.put(DatabaseHelper.COL_NOTIF_REF_ID, txId);
                    notif.put(DatabaseHelper.COL_NOTIF_TITLE, "Item Returned!");
                    notif.put(DatabaseHelper.COL_NOTIF_MESSAGE, "The return has been confirmed. Tap to rate your experience!");
                    notif.put(DatabaseHelper.COL_NOTIF_CREATED_AT, now);
                    notif.put(DatabaseHelper.COL_NOTIF_READ, 0);
                    db.insert(DatabaseHelper.TABLE_NOTIFICATIONS, null, notif);
                }

                db.setTransactionSuccessful();
                Transaction tx = getTransactionByIdDirect(db, txId, null);
                executors.postToMainThread(() -> callback.onSuccess(tx));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            } finally {
                db.endTransaction();
            }
        });
    }

    public void getTransactionsForUser(String userId, String typeFilter, RepositoryCallback<List<Transaction>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                StringBuilder query = new StringBuilder();
                query.append("SELECT t.*, i.name AS item_name, i.photo_path AS item_photo, i.category_id AS item_category, ")
                        .append("bo.name AS borrower_name, bo.avatar_url AS borrower_avatar, bo.email AS borrower_email, ")
                        .append("ow.name AS owner_name, ow.avatar_url AS owner_avatar, ow.email AS owner_email ")
                        .append("FROM ").append(DatabaseHelper.TABLE_TRANSACTIONS).append(" t ")
                        .append("INNER JOIN ").append(DatabaseHelper.TABLE_ITEMS).append(" i ON t.item_id = i.id ")
                        .append("INNER JOIN ").append(DatabaseHelper.TABLE_USERS).append(" bo ON t.borrower_id = bo.id ")
                        .append("INNER JOIN ").append(DatabaseHelper.TABLE_USERS).append(" ow ON t.owner_id = ow.id ");

                List<String> args = new ArrayList<>();
                if ("borrowed".equalsIgnoreCase(typeFilter)) {
                    query.append("WHERE t.borrower_id = ? ");
                    args.add(userId);
                } else if ("lent".equalsIgnoreCase(typeFilter)) {
                    query.append("WHERE t.owner_id = ? ");
                    args.add(userId);
                } else {
                    query.append("WHERE (t.borrower_id = ? OR t.owner_id = ?) ");
                    args.add(userId);
                    args.add(userId);
                }

                query.append("ORDER BY t.requested_at DESC");

                List<Transaction> list = queryTransactionsList(db, query.toString(), args.toArray(new String[0]), userId);
                executors.postToMainThread(() -> callback.onSuccess(list));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void getPendingRequestsForOwner(String ownerId, RepositoryCallback<List<Transaction>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String query = "SELECT t.*, i.name AS item_name, i.photo_path AS item_photo, i.category_id AS item_category, " +
                        "bo.name AS borrower_name, bo.avatar_url AS borrower_avatar, bo.email AS borrower_email, " +
                        "ow.name AS owner_name, ow.avatar_url AS owner_avatar, ow.email AS owner_email " +
                        "FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " t " +
                        "INNER JOIN " + DatabaseHelper.TABLE_ITEMS + " i ON t.item_id = i.id " +
                        "INNER JOIN " + DatabaseHelper.TABLE_USERS + " bo ON t.borrower_id = bo.id " +
                        "INNER JOIN " + DatabaseHelper.TABLE_USERS + " ow ON t.owner_id = ow.id " +
                        "WHERE t.owner_id = ? AND t.status = 'pending' " +
                        "ORDER BY t.requested_at DESC";

                List<Transaction> list = queryTransactionsList(db, query, new String[]{ownerId}, ownerId);
                executors.postToMainThread(() -> callback.onSuccess(list));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void getDueSoonAndOverdueBorrows(String userId, RepositoryCallback<List<Transaction>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String query = "SELECT t.*, i.name AS item_name, i.photo_path AS item_photo, i.category_id AS item_category, " +
                        "bo.name AS borrower_name, bo.avatar_url AS borrower_avatar, bo.email AS borrower_email, " +
                        "ow.name AS owner_name, ow.avatar_url AS owner_avatar, ow.email AS owner_email " +
                        "FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " t " +
                        "INNER JOIN " + DatabaseHelper.TABLE_ITEMS + " i ON t.item_id = i.id " +
                        "INNER JOIN " + DatabaseHelper.TABLE_USERS + " bo ON t.borrower_id = bo.id " +
                        "INNER JOIN " + DatabaseHelper.TABLE_USERS + " ow ON t.owner_id = ow.id " +
                        "WHERE t.borrower_id = ? AND t.status IN ('approved', 'borrowed') " +
                        "ORDER BY t.due_at ASC";

                List<Transaction> list = queryTransactionsList(db, query, new String[]{userId}, userId);
                executors.postToMainThread(() -> callback.onSuccess(list));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void getUserDashboardStats(String userId, RepositoryCallback<Map<String, Number>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Map<String, Number> stats = new HashMap<>();

                // 1. My Listings
                int myListings = 0;
                try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ITEMS + " WHERE owner_id = ? AND status != 'inactive'", new String[]{userId})) {
                    if (c != null && c.moveToFirst()) myListings = c.getInt(0);
                }

                // 2. Currently Borrowing
                int borrowing = 0;
                try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " WHERE borrower_id = ? AND status = 'borrowed'", new String[]{userId})) {
                    if (c != null && c.moveToFirst()) borrowing = c.getInt(0);
                }

                // 3. Currently Lent Out
                int lentOut = 0;
                try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " WHERE owner_id = ? AND status = 'borrowed'", new String[]{userId})) {
                    if (c != null && c.moveToFirst()) lentOut = c.getInt(0);
                }

                // 4. Overdue
                int overdue = 0;
                long now = System.currentTimeMillis();
                try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " WHERE borrower_id = ? AND status = 'borrowed' AND due_at < ?", new String[]{userId, String.valueOf(now)})) {
                    if (c != null && c.moveToFirst()) overdue = c.getInt(0);
                }

                // 5. Rating & Points
                double rating = 5.0;
                int reviewsCount = 0;
                try (Cursor c = db.rawQuery("SELECT AVG(rating), COUNT(*) FROM " + DatabaseHelper.TABLE_REVIEWS + " WHERE reviewee_id = ?", new String[]{userId})) {
                    if (c != null && c.moveToFirst() && !c.isNull(0)) {
                        rating = Math.round(c.getDouble(0) * 10.0) / 10.0;
                        reviewsCount = c.getInt(1);
                    }
                }

                int points = (lentOut * 15) + (borrowing * 5) + (reviewsCount * 5) + (myListings * 10);

                stats.put("myListings", myListings);
                stats.put("borrowing", borrowing);
                stats.put("lentOut", lentOut);
                stats.put("overdue", overdue);
                stats.put("rating", rating);
                stats.put("points", points);

                executors.postToMainThread(() -> callback.onSuccess(stats));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    private Transaction getTransactionByIdDirect(SQLiteDatabase db, String txId, String currentUserId) {
        String query = "SELECT t.*, i.name AS item_name, i.photo_path AS item_photo, i.category_id AS item_category, " +
                "bo.name AS borrower_name, bo.avatar_url AS borrower_avatar, bo.email AS borrower_email, " +
                "ow.name AS owner_name, ow.avatar_url AS owner_avatar, ow.email AS owner_email " +
                "FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " t " +
                "INNER JOIN " + DatabaseHelper.TABLE_ITEMS + " i ON t.item_id = i.id " +
                "INNER JOIN " + DatabaseHelper.TABLE_USERS + " bo ON t.borrower_id = bo.id " +
                "INNER JOIN " + DatabaseHelper.TABLE_USERS + " ow ON t.owner_id = ow.id " +
                "WHERE t.id = ?";

        List<Transaction> list = queryTransactionsList(db, query, new String[]{txId}, currentUserId);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Transaction> queryTransactionsList(SQLiteDatabase db, String query, String[] args, String currentUserId) {
        List<Transaction> list = new ArrayList<>();
        try (Cursor c = db.rawQuery(query, args)) {
            if (c != null && c.moveToFirst()) {
                do {
                    Transaction tx = new Transaction();
                    tx.setId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_ID)));
                    tx.setItemId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_ITEM_ID)));
                    tx.setBorrowerId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_BORROWER_ID)));
                    tx.setOwnerId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_OWNER_ID)));
                    tx.setStatus(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_STATUS)));
                    tx.setRequestedAt(c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_REQUESTED_AT)));
                    tx.setDueAt(c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_DUE_AT)));
                    tx.setReturnedAt(c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_RETURNED_AT)));
                    tx.setMessage(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_MESSAGE)));
                    tx.setQrToken(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_TX_QR_TOKEN)));

                    // Joined Item
                    Item item = new Item();
                    item.setId(tx.getItemId());
                    item.setName(c.getString(c.getColumnIndexOrThrow("item_name")));
                    item.setPhotoPath(c.getString(c.getColumnIndexOrThrow("item_photo")));
                    item.setCategoryId(c.getString(c.getColumnIndexOrThrow("item_category")));
                    item.setOwner(c.getString(c.getColumnIndexOrThrow("owner_name")));
                    item.setOwnerAvatar(c.getString(c.getColumnIndexOrThrow("owner_avatar")));
                    item.setOwnerEmail(c.getString(c.getColumnIndexOrThrow("owner_email")));
                    tx.setItem(item);

                    String borrowerName = c.getString(c.getColumnIndexOrThrow("borrower_name"));
                    String borrowerAvatar = c.getString(c.getColumnIndexOrThrow("borrower_avatar"));
                    String ownerName = c.getString(c.getColumnIndexOrThrow("owner_name"));
                    String ownerAvatar = c.getString(c.getColumnIndexOrThrow("owner_avatar"));

                    tx.setBorrowerEmail(c.getString(c.getColumnIndexOrThrow("borrower_email")));
                    tx.setOwnerEmail(c.getString(c.getColumnIndexOrThrow("owner_email")));

                    // Determine relative party info
                    boolean isBorrower = currentUserId != null && currentUserId.equals(tx.getBorrowerId());
                    if (isBorrower) {
                        tx.setType("borrowed");
                        tx.setPerson(ownerName);
                        tx.setPersonAvatar(ownerAvatar);
                    } else {
                        tx.setType("lent");
                        tx.setPerson(borrowerName);
                        tx.setPersonAvatar(borrowerAvatar);
                    }

                    list.add(tx);
                } while (c.moveToNext());
            }
        }
        return list;
    }
}
