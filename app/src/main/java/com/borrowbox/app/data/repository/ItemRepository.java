package com.borrowbox.app.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.borrowbox.app.data.DatabaseHelper;
import com.borrowbox.app.models.Category;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.utils.AppExecutors;
import com.borrowbox.app.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemRepository {

    private static ItemRepository instance;
    private final DatabaseHelper dbHelper;
    private final AppExecutors executors;

    public static synchronized ItemRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ItemRepository(context.getApplicationContext());
        }
        return instance;
    }

    private ItemRepository(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.executors = AppExecutors.getInstance();
    }

    public void insertItem(Item item, RepositoryCallback<Item> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                if (item.getId() == null || item.getId().isEmpty()) {
                    item.setId("item_" + UUID.randomUUID().toString().substring(0, 8));
                }
                long now = System.currentTimeMillis();
                item.setCreatedAt(now);

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Ensure valid category_id matching foreign key
                String rawCat = item.getCategoryId();
                String targetCatId = "others";
                if (rawCat != null && !rawCat.trim().isEmpty()) {
                    String candidate = rawCat.trim();
                    try (Cursor cr = db.rawQuery("SELECT " + DatabaseHelper.COL_CAT_ID + " FROM " + DatabaseHelper.TABLE_CATEGORIES +
                            " WHERE LOWER(" + DatabaseHelper.COL_CAT_ID + ") = LOWER(?) OR LOWER(" + DatabaseHelper.COL_CAT_NAME + ") = LOWER(?) LIMIT 1",
                            new String[]{candidate, candidate})) {
                        if (cr != null && cr.moveToFirst()) {
                            targetCatId = cr.getString(0);
                        }
                    }
                }
                item.setCategoryId(targetCatId);

                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COL_ITEM_ID, item.getId());
                v.put(DatabaseHelper.COL_ITEM_OWNER_ID, item.getOwnerId());
                v.put(DatabaseHelper.COL_ITEM_CATEGORY_ID, targetCatId);
                v.put(DatabaseHelper.COL_ITEM_NAME, item.getName());
                v.put(DatabaseHelper.COL_ITEM_DESC, item.getDescription());
                v.put(DatabaseHelper.COL_ITEM_PHOTO_PATH, item.getPhotoPath());
                v.put(DatabaseHelper.COL_ITEM_STATUS, item.getStatus());
                v.put(DatabaseHelper.COL_ITEM_MAX_DURATION, item.getMaxDurationDays());
                v.put(DatabaseHelper.COL_ITEM_PICKUP, item.getPickupLocation());
                v.put(DatabaseHelper.COL_ITEM_CREATED_AT, now);

                long res = db.insertOrThrow(DatabaseHelper.TABLE_ITEMS, null, v);
                if (res == -1) {
                    executors.postToMainThread(() -> callback.onError(new Exception("Database insert failed.")));
                    return;
                }

                Item refreshed = getItemByIdDirect(db, item.getId());
                executors.postToMainThread(() -> callback.onSuccess(refreshed));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void updateItem(Item item, RepositoryCallback<Item> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COL_ITEM_NAME, item.getName());
                v.put(DatabaseHelper.COL_ITEM_CATEGORY_ID, item.getCategoryId());
                v.put(DatabaseHelper.COL_ITEM_DESC, item.getDescription());
                v.put(DatabaseHelper.COL_ITEM_PHOTO_PATH, item.getPhotoPath());
                v.put(DatabaseHelper.COL_ITEM_STATUS, item.getStatus());
                v.put(DatabaseHelper.COL_ITEM_MAX_DURATION, item.getMaxDurationDays());
                v.put(DatabaseHelper.COL_ITEM_PICKUP, item.getPickupLocation());

                db.update(DatabaseHelper.TABLE_ITEMS, v, DatabaseHelper.COL_ITEM_ID + " = ?", new String[]{item.getId()});
                Item refreshed = getItemByIdDirect(db, item.getId());
                executors.postToMainThread(() -> callback.onSuccess(refreshed));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void deleteItem(String itemId, RepositoryCallback<Boolean> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Item item = getItemByIdDirect(db, itemId);

                int count = db.delete(DatabaseHelper.TABLE_ITEMS, DatabaseHelper.COL_ITEM_ID + " = ?", new String[]{itemId});
                if (count > 0 && item != null) {
                    // Clean up photo files
                    for (String path : item.getAllPhotoPaths()) {
                        ImageUtils.deleteFileQuietly(path);
                    }
                }
                executors.postToMainThread(() -> callback.onSuccess(count > 0));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void getItemById(String itemId, RepositoryCallback<Item> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Item item = getItemByIdDirect(db, itemId);
                executors.postToMainThread(() -> callback.onSuccess(item));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void getRecommendedItems(String excludeUserId, RepositoryCallback<List<Item>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String query = "SELECT i.*, u.name AS owner_name, u.avatar_url AS owner_avatar, " +
                        "u.college AS owner_college, u.email AS owner_email " +
                        "FROM " + DatabaseHelper.TABLE_ITEMS + " i " +
                        "INNER JOIN " + DatabaseHelper.TABLE_USERS + " u ON i.owner_id = u.id " +
                        "WHERE i.status = 'available' " +
                        (excludeUserId != null && !excludeUserId.isEmpty() ? "AND i.owner_id != ? " : "") +
                        "ORDER BY i.created_at DESC";

                String[] args = (excludeUserId != null && !excludeUserId.isEmpty()) ? new String[]{excludeUserId} : null;
                List<Item> items = queryItemsList(db, query, args);
                executors.postToMainThread(() -> callback.onSuccess(items));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void getItemsByCategory(String categoryId, String excludeUserId, RepositoryCallback<List<Item>> callback) {
        if ("all".equalsIgnoreCase(categoryId)) {
            getRecommendedItems(excludeUserId, callback);
            return;
        }
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String query = "SELECT i.*, u.name AS owner_name, u.avatar_url AS owner_avatar, " +
                        "u.college AS owner_college, u.email AS owner_email " +
                        "FROM " + DatabaseHelper.TABLE_ITEMS + " i " +
                        "INNER JOIN " + DatabaseHelper.TABLE_USERS + " u ON i.owner_id = u.id " +
                        "WHERE i.status = 'available' AND (i.category_id = ? OR i.category_id LIKE ?) " +
                        (excludeUserId != null && !excludeUserId.isEmpty() ? "AND i.owner_id != ? " : "") +
                        "ORDER BY i.created_at DESC";

                List<String> argsList = new ArrayList<>();
                argsList.add(categoryId);
                argsList.add("%" + categoryId + "%");
                if (excludeUserId != null && !excludeUserId.isEmpty()) {
                    argsList.add(excludeUserId);
                }

                List<Item> items = queryItemsList(db, query, argsList.toArray(new String[0]));
                executors.postToMainThread(() -> callback.onSuccess(items));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void searchItems(String keyword, String excludeUserId, RepositoryCallback<List<Item>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String pattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
                String query = "SELECT i.*, u.name AS owner_name, u.avatar_url AS owner_avatar, " +
                        "u.college AS owner_college, u.email AS owner_email " +
                        "FROM " + DatabaseHelper.TABLE_ITEMS + " i " +
                        "INNER JOIN " + DatabaseHelper.TABLE_USERS + " u ON i.owner_id = u.id " +
                        "WHERE i.status = 'available' AND (i.name LIKE ? OR i.description LIKE ? OR i.category_id LIKE ? OR u.name LIKE ?) " +
                        (excludeUserId != null && !excludeUserId.isEmpty() ? "AND i.owner_id != ? " : "") +
                        "ORDER BY i.created_at DESC";

                List<String> argsList = new ArrayList<>();
                argsList.add(pattern);
                argsList.add(pattern);
                argsList.add(pattern);
                argsList.add(pattern);
                if (excludeUserId != null && !excludeUserId.isEmpty()) {
                    argsList.add(excludeUserId);
                }

                List<Item> items = queryItemsList(db, query, argsList.toArray(new String[0]));
                executors.postToMainThread(() -> callback.onSuccess(items));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void getMyItems(String ownerId, RepositoryCallback<List<Item>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String query = "SELECT i.*, u.name AS owner_name, u.avatar_url AS owner_avatar, " +
                        "u.college AS owner_college, u.email AS owner_email " +
                        "FROM " + DatabaseHelper.TABLE_ITEMS + " i " +
                        "INNER JOIN " + DatabaseHelper.TABLE_USERS + " u ON i.owner_id = u.id " +
                        "WHERE i.owner_id = ? " +
                        "ORDER BY i.created_at DESC";

                List<Item> items = queryItemsList(db, query, new String[]{ownerId});

                // Find active borrower name if currently borrowed
                for (Item it : items) {
                    if ("borrowed".equalsIgnoreCase(it.getStatus())) {
                        try (Cursor c = db.rawQuery("SELECT u.name FROM " + DatabaseHelper.TABLE_TRANSACTIONS + " t " +
                                "INNER JOIN " + DatabaseHelper.TABLE_USERS + " u ON t.borrower_id = u.id " +
                                "WHERE t.item_id = ? AND t.status = 'borrowed'", new String[]{it.getId()})) {
                            if (c != null && c.moveToFirst()) {
                                it.setCurrentBorrower(c.getString(0));
                            }
                        }
                    }
                }

                executors.postToMainThread(() -> callback.onSuccess(items));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void getCategories(RepositoryCallback<List<Category>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                List<Category> list = new ArrayList<>();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                try (Cursor c = db.query(DatabaseHelper.TABLE_CATEGORIES, null, null, null, null, null, null)) {
                    if (c != null && c.moveToFirst()) {
                        do {
                            String id = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_ID));
                            String name = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_NAME));
                            int iconRes = c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_CAT_ICON));
                            list.add(new Category(id, name, iconRes));
                        } while (c.moveToNext());
                    }
                }
                executors.postToMainThread(() -> callback.onSuccess(list));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void isItemSaved(String userId, String itemId, RepositoryCallback<Boolean> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                try (Cursor c = db.query(DatabaseHelper.TABLE_SAVED, null,
                        DatabaseHelper.COL_SAVED_USER_ID + " = ? AND " + DatabaseHelper.COL_SAVED_ITEM_ID + " = ?",
                        new String[]{userId, itemId}, null, null, null)) {
                    boolean saved = (c != null && c.getCount() > 0);
                    if (callback != null) {
                        executors.postToMainThread(() -> callback.onSuccess(saved));
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    executors.postToMainThread(() -> callback.onError(e));
                }
            }
        });
    }

    public void toggleSaved(String userId, String itemId, RepositoryCallback<Boolean> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                boolean exists = false;
                try (Cursor c = db.query(DatabaseHelper.TABLE_SAVED, null,
                        DatabaseHelper.COL_SAVED_USER_ID + " = ? AND " + DatabaseHelper.COL_SAVED_ITEM_ID + " = ?",
                        new String[]{userId, itemId}, null, null, null)) {
                    exists = (c != null && c.getCount() > 0);
                }

                if (exists) {
                    db.delete(DatabaseHelper.TABLE_SAVED,
                            DatabaseHelper.COL_SAVED_USER_ID + " = ? AND " + DatabaseHelper.COL_SAVED_ITEM_ID + " = ?",
                            new String[]{userId, itemId});
                    if (callback != null) {
                        executors.postToMainThread(() -> callback.onSuccess(false));
                    }
                } else {
                    ContentValues v = new ContentValues();
                    v.put(DatabaseHelper.COL_SAVED_USER_ID, userId);
                    v.put(DatabaseHelper.COL_SAVED_ITEM_ID, itemId);
                    db.insert(DatabaseHelper.TABLE_SAVED, null, v);
                    if (callback != null) {
                        executors.postToMainThread(() -> callback.onSuccess(true));
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    executors.postToMainThread(() -> callback.onError(e));
                }
            }
        });
    }

    public void getSavedItems(String userId, RepositoryCallback<List<Item>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String query = "SELECT i.*, u.name AS owner_name, u.avatar_url AS owner_avatar, " +
                        "u.college AS owner_college, u.email AS owner_email " +
                        "FROM " + DatabaseHelper.TABLE_SAVED + " s " +
                        "INNER JOIN " + DatabaseHelper.TABLE_ITEMS + " i ON s.item_id = i.id " +
                        "INNER JOIN " + DatabaseHelper.TABLE_USERS + " u ON i.owner_id = u.id " +
                        "WHERE s.user_id = ? " +
                        "ORDER BY i.created_at DESC";

                List<Item> items = queryItemsList(db, query, new String[]{userId});
                executors.postToMainThread(() -> callback.onSuccess(items));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void getRecentSearches(String userId, RepositoryCallback<List<String>> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                List<String> searches = new ArrayList<>();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                try (Cursor c = db.query(DatabaseHelper.TABLE_SEARCHES, new String[]{DatabaseHelper.COL_SEARCH_QUERY},
                        DatabaseHelper.COL_SEARCH_USER_ID + " = ?", new String[]{userId},
                        null, null, DatabaseHelper.COL_SEARCH_TIME + " DESC", "10")) {
                    if (c != null && c.moveToFirst()) {
                        do {
                            searches.add(c.getString(0));
                        } while (c.moveToNext());
                    }
                }
                executors.postToMainThread(() -> callback.onSuccess(searches));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void addRecentSearch(String userId, String query) {
        addRecentSearch(userId, query, null);
    }

    public void addRecentSearch(String userId, String query, RepositoryCallback<Boolean> callback) {
        if (query == null || query.trim().isEmpty()) {
            if (callback != null) callback.onSuccess(false);
            return;
        }
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COL_SEARCH_USER_ID, userId);
                v.put(DatabaseHelper.COL_SEARCH_QUERY, query.trim());
                v.put(DatabaseHelper.COL_SEARCH_TIME, System.currentTimeMillis());
                db.insert(DatabaseHelper.TABLE_SEARCHES, null, v);
                if (callback != null) {
                    executors.postToMainThread(() -> callback.onSuccess(true));
                }
            } catch (Exception e) {
                if (callback != null) {
                    executors.postToMainThread(() -> callback.onError(e));
                }
            }
        });
    }

    public void clearRecentSearches(String userId, RepositoryCallback<Boolean> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int rows = db.delete(DatabaseHelper.TABLE_SEARCHES, DatabaseHelper.COL_SEARCH_USER_ID + " = ?", new String[]{userId});
                executors.postToMainThread(() -> callback.onSuccess(rows > 0));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    private Item getItemByIdDirect(SQLiteDatabase db, String itemId) {
        String query = "SELECT i.*, u.name AS owner_name, u.avatar_url AS owner_avatar, " +
                "u.college AS owner_college, u.email AS owner_email " +
                "FROM " + DatabaseHelper.TABLE_ITEMS + " i " +
                "INNER JOIN " + DatabaseHelper.TABLE_USERS + " u ON i.owner_id = u.id " +
                "WHERE i.id = ?";
        List<Item> list = queryItemsList(db, query, new String[]{itemId});
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Item> queryItemsList(SQLiteDatabase db, String query, String[] args) {
        List<Item> items = new ArrayList<>();
        try (Cursor c = db.rawQuery(query, args)) {
            if (c != null && c.moveToFirst()) {
                do {
                    Item it = new Item();
                    it.setId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_ID)));
                    it.setOwnerId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_OWNER_ID)));
                    it.setCategoryId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_CATEGORY_ID)));
                    it.setName(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_NAME)));
                    it.setDescription(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_DESC)));
                    it.setPhotoPath(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_PHOTO_PATH)));
                    it.setStatus(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_STATUS)));
                    it.setMaxDurationDays(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_MAX_DURATION)));
                    it.setPickupLocation(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_PICKUP)));
                    it.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_CREATED_AT)));

                    // Joined owner info
                    it.setOwner(c.getString(c.getColumnIndexOrThrow("owner_name")));
                    it.setOwnerAvatar(c.getString(c.getColumnIndexOrThrow("owner_avatar")));
                    it.setOwnerCollege(c.getString(c.getColumnIndexOrThrow("owner_college")));
                    it.setOwnerEmail(c.getString(c.getColumnIndexOrThrow("owner_email")));

                    // Fetch owner rating
                    try (Cursor cr = db.rawQuery("SELECT AVG(rating) FROM " + DatabaseHelper.TABLE_REVIEWS + " WHERE reviewee_id = ?",
                            new String[]{it.getOwnerId()})) {
                        if (cr != null && cr.moveToFirst() && !cr.isNull(0)) {
                            it.setOwnerRating(Math.round(cr.getDouble(0) * 10.0) / 10.0);
                        } else {
                            it.setOwnerRating(5.0);
                        }
                    }

                    items.add(it);
                } while (c.moveToNext());
            }
        }
        return items;
    }
}
