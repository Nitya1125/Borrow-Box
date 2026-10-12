package com.borrowbox.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.borrowbox.app.R;
import com.borrowbox.app.models.Category;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.NotificationItem;
import com.borrowbox.app.models.Review;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.models.User;
import com.borrowbox.app.utils.DateTimeUtils;
import com.borrowbox.app.utils.PasswordHasher;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "borrowbox.db";
    private static final int DATABASE_VERSION = 2;

    // Table Users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD_HASH = "password_hash";
    public static final String COL_USER_SALT = "salt";
    public static final String COL_USER_COLLEGE = "college";
    public static final String COL_USER_AVATAR = "avatar_url";
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_CREATED_AT = "created_at";

    // Table Categories (Reference lookup data)
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COL_CAT_ID = "id";
    public static final String COL_CAT_NAME = "name";
    public static final String COL_CAT_ICON = "icon_res";

    // Table Items
    public static final String TABLE_ITEMS = "items";
    public static final String COL_ITEM_ID = "id";
    public static final String COL_ITEM_OWNER_ID = "owner_id";
    public static final String COL_ITEM_CATEGORY_ID = "category_id";
    public static final String COL_ITEM_NAME = "name";
    public static final String COL_ITEM_DESC = "description";
    public static final String COL_ITEM_PHOTO_PATH = "photo_path";
    public static final String COL_ITEM_STATUS = "status"; // "available", "borrowed", "inactive"
    public static final String COL_ITEM_MAX_DURATION = "max_duration_days";
    public static final String COL_ITEM_PICKUP = "pickup_location";
    public static final String COL_ITEM_CREATED_AT = "created_at";

    // Table Transactions
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COL_TX_ID = "id";
    public static final String COL_TX_ITEM_ID = "item_id";
    public static final String COL_TX_BORROWER_ID = "borrower_id";
    public static final String COL_TX_OWNER_ID = "owner_id";
    public static final String COL_TX_STATUS = "status"; // "pending", "approved", "borrowed", "returned", "rejected", "cancelled", "overdue"
    public static final String COL_TX_REQUESTED_AT = "requested_at";
    public static final String COL_TX_DUE_AT = "due_at";
    public static final String COL_TX_RETURNED_AT = "returned_at";
    public static final String COL_TX_MESSAGE = "message";
    public static final String COL_TX_QR_TOKEN = "qr_token";

    // Table Reviews
    public static final String TABLE_REVIEWS = "reviews";
    public static final String COL_REV_ID = "id";
    public static final String COL_REV_TX_ID = "transaction_id";
    public static final String COL_REV_REVIEWER_ID = "reviewer_id";
    public static final String COL_REV_REVIEWEE_ID = "reviewee_id";
    public static final String COL_REV_RATING = "rating";
    public static final String COL_REV_COMMENT = "comment";
    public static final String COL_REV_CREATED_AT = "created_at";

    // Table Notifications
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COL_NOTIF_ID = "id";
    public static final String COL_NOTIF_USER_ID = "user_id";
    public static final String COL_NOTIF_TYPE = "type";
    public static final String COL_NOTIF_REF_ID = "ref_id";
    public static final String COL_NOTIF_TITLE = "title";
    public static final String COL_NOTIF_MESSAGE = "message";
    public static final String COL_NOTIF_CREATED_AT = "created_at";
    public static final String COL_NOTIF_READ = "is_read";

    // Table Saved Items
    public static final String TABLE_SAVED = "saved_items";
    public static final String COL_SAVED_USER_ID = "user_id";
    public static final String COL_SAVED_ITEM_ID = "item_id";

    // Table Recent Searches
    public static final String TABLE_SEARCHES = "recent_searches";
    public static final String COL_SEARCH_ID = "id";
    public static final String COL_SEARCH_USER_ID = "user_id";
    public static final String COL_SEARCH_QUERY = "query_text";
    public static final String COL_SEARCH_TIME = "timestamp";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable Foreign Key support
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " TEXT PRIMARY KEY, " +
                COL_USER_NAME + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_USER_PASSWORD_HASH + " TEXT NOT NULL, " +
                COL_USER_SALT + " TEXT NOT NULL, " +
                COL_USER_COLLEGE + " TEXT, " +
                COL_USER_AVATAR + " TEXT, " +
                COL_USER_PHONE + " TEXT, " +
                COL_USER_CREATED_AT + " INTEGER NOT NULL)");

        // Categories Table (Lookup/reference)
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                COL_CAT_ID + " TEXT PRIMARY KEY, " +
                COL_CAT_NAME + " TEXT NOT NULL, " +
                COL_CAT_ICON + " INTEGER NOT NULL)");

        // Items Table
        db.execSQL("CREATE TABLE " + TABLE_ITEMS + " (" +
                COL_ITEM_ID + " TEXT PRIMARY KEY, " +
                COL_ITEM_OWNER_ID + " TEXT NOT NULL, " +
                COL_ITEM_CATEGORY_ID + " TEXT NOT NULL, " +
                COL_ITEM_NAME + " TEXT NOT NULL, " +
                COL_ITEM_DESC + " TEXT, " +
                COL_ITEM_PHOTO_PATH + " TEXT, " +
                COL_ITEM_STATUS + " TEXT NOT NULL, " +
                COL_ITEM_MAX_DURATION + " INTEGER NOT NULL DEFAULT 3, " +
                COL_ITEM_PICKUP + " TEXT, " +
                COL_ITEM_CREATED_AT + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COL_ITEM_OWNER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + COL_ITEM_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COL_CAT_ID + "))");

        // Transactions Table
        db.execSQL("CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_TX_ID + " TEXT PRIMARY KEY, " +
                COL_TX_ITEM_ID + " TEXT NOT NULL, " +
                COL_TX_BORROWER_ID + " TEXT NOT NULL, " +
                COL_TX_OWNER_ID + " TEXT NOT NULL, " +
                COL_TX_STATUS + " TEXT NOT NULL, " +
                COL_TX_REQUESTED_AT + " INTEGER NOT NULL, " +
                COL_TX_DUE_AT + " INTEGER NOT NULL, " +
                COL_TX_RETURNED_AT + " INTEGER DEFAULT 0, " +
                COL_TX_MESSAGE + " TEXT, " +
                COL_TX_QR_TOKEN + " TEXT, " +
                "FOREIGN KEY (" + COL_TX_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + COL_ITEM_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + COL_TX_BORROWER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + COL_TX_OWNER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE)");

        // Reviews Table
        db.execSQL("CREATE TABLE " + TABLE_REVIEWS + " (" +
                COL_REV_ID + " TEXT PRIMARY KEY, " +
                COL_REV_TX_ID + " TEXT NOT NULL, " +
                COL_REV_REVIEWER_ID + " TEXT NOT NULL, " +
                COL_REV_REVIEWEE_ID + " TEXT NOT NULL, " +
                COL_REV_RATING + " REAL NOT NULL, " +
                COL_REV_COMMENT + " TEXT, " +
                COL_REV_CREATED_AT + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COL_REV_TX_ID + ") REFERENCES " + TABLE_TRANSACTIONS + "(" + COL_TX_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + COL_REV_REVIEWER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY (" + COL_REV_REVIEWEE_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))");

        // Notifications Table
        db.execSQL("CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                COL_NOTIF_ID + " TEXT PRIMARY KEY, " +
                COL_NOTIF_USER_ID + " TEXT NOT NULL, " +
                COL_NOTIF_TYPE + " TEXT NOT NULL, " +
                COL_NOTIF_REF_ID + " TEXT, " +
                COL_NOTIF_TITLE + " TEXT NOT NULL, " +
                COL_NOTIF_MESSAGE + " TEXT NOT NULL, " +
                COL_NOTIF_CREATED_AT + " INTEGER NOT NULL, " +
                COL_NOTIF_READ + " INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY (" + COL_NOTIF_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE)");

        // Saved Items Table
        db.execSQL("CREATE TABLE " + TABLE_SAVED + " (" +
                COL_SAVED_USER_ID + " TEXT NOT NULL, " +
                COL_SAVED_ITEM_ID + " TEXT NOT NULL, " +
                "PRIMARY KEY (" + COL_SAVED_USER_ID + ", " + COL_SAVED_ITEM_ID + "), " +
                "FOREIGN KEY (" + COL_SAVED_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + COL_SAVED_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + COL_ITEM_ID + ") ON DELETE CASCADE)");

        // Recent Searches Table
        db.execSQL("CREATE TABLE " + TABLE_SEARCHES + " (" +
                COL_SEARCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SEARCH_USER_ID + " TEXT NOT NULL, " +
                COL_SEARCH_QUERY + " TEXT NOT NULL, " +
                COL_SEARCH_TIME + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COL_SEARCH_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE)");

        // Create Performance Indexes on Foreign Keys and Search columns
        createIndexes(db);

        // Seed Reference Categories ONLY (Not fake user/item/tx demo data)
        seedReferenceCategories(db);
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_email ON " + TABLE_USERS + "(" + COL_USER_EMAIL + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_items_owner ON " + TABLE_ITEMS + "(" + COL_ITEM_OWNER_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_items_cat ON " + TABLE_ITEMS + "(" + COL_ITEM_CATEGORY_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_items_status ON " + TABLE_ITEMS + "(" + COL_ITEM_STATUS + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_items_name ON " + TABLE_ITEMS + "(" + COL_ITEM_NAME + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_borrower ON " + TABLE_TRANSACTIONS + "(" + COL_TX_BORROWER_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_owner ON " + TABLE_TRANSACTIONS + "(" + COL_TX_OWNER_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_item ON " + TABLE_TRANSACTIONS + "(" + COL_TX_ITEM_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_status ON " + TABLE_TRANSACTIONS + "(" + COL_TX_STATUS + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_rev_reviewee ON " + TABLE_REVIEWS + "(" + COL_REV_REVIEWEE_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_notif_user ON " + TABLE_NOTIFICATIONS + "(" + COL_NOTIF_USER_ID + ", " + COL_NOTIF_READ + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_searches_user ON " + TABLE_SEARCHES + "(" + COL_SEARCH_USER_ID + ")");
    }

    private void seedReferenceCategories(SQLiteDatabase db) {
        insertCategoryRow(db, "all", "All", R.drawable.ic_cat_all);
        insertCategoryRow(db, "books", "Books", R.drawable.ic_cat_books);
        insertCategoryRow(db, "electronics", "Electronics", R.drawable.ic_cat_electronics);
        insertCategoryRow(db, "calculators", "Calculators", R.drawable.ic_cat_calculators);
        insertCategoryRow(db, "stationery", "Stationery", R.drawable.ic_cat_stationery);
        insertCategoryRow(db, "sports", "Sports", R.drawable.ic_cat_sports);
        insertCategoryRow(db, "lab", "Lab", R.drawable.ic_cat_lab);
        insertCategoryRow(db, "others", "Others", R.drawable.ic_cat_others);
    }

    private void insertCategoryRow(SQLiteDatabase db, String id, String name, int iconRes) {
        ContentValues v = new ContentValues();
        v.put(COL_CAT_ID, id);
        v.put(COL_CAT_NAME, name);
        v.put(COL_CAT_ICON, iconRes);
        db.insertWithOnConflict(TABLE_CATEGORIES, null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Incremental migration preserving real user data
            // 1. Temporarily backup real users (excluding hardcoded demo user 'u_nitya' or 'nitya@bits.edu')
            List<ContentValues> realUsers = new ArrayList<>();
            try (Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email != 'nitya@bits.edu'", null)) {
                if (c != null && c.moveToFirst()) {
                    do {
                        String email = c.getString(c.getColumnIndexOrThrow("email"));
                        String name = c.getString(c.getColumnIndexOrThrow("name"));
                        String id = c.getString(c.getColumnIndexOrThrow("id"));
                        String rawPass = c.getString(c.getColumnIndexOrThrow("password"));
                        String college = c.getString(c.getColumnIndexOrThrow("college"));
                        String avatar = c.getString(c.getColumnIndexOrThrow("avatar_url"));
                        String phone = c.getString(c.getColumnIndexOrThrow("phone"));

                        String salt = PasswordHasher.generateSalt();
                        String hash = PasswordHasher.hashPassword(rawPass != null ? rawPass : "BorrowBox#2026", salt);

                        ContentValues cv = new ContentValues();
                        cv.put(COL_USER_ID, id);
                        cv.put(COL_USER_NAME, name);
                        cv.put(COL_USER_EMAIL, email.toLowerCase().trim());
                        cv.put(COL_USER_PASSWORD_HASH, hash);
                        cv.put(COL_USER_SALT, salt);
                        cv.put(COL_USER_COLLEGE, college);
                        cv.put(COL_USER_AVATAR, avatar);
                        cv.put(COL_USER_PHONE, phone);
                        cv.put(COL_USER_CREATED_AT, System.currentTimeMillis());
                        realUsers.add(cv);
                    } while (c.moveToNext());
                }
            } catch (Exception ignored) {}

            // 2. Drop legacy tables
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCHES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

            // 3. Re-create new schema
            onCreate(db);

            // 4. Restore real users
            for (ContentValues cv : realUsers) {
                db.insert(TABLE_USERS, null, cv);
            }
        }
    }

    // ==========================================
    // REPOSITORY QUERY HELPERS (CALLED BY REPOSITORIES)
    // ==========================================

    public User queryUserByEmail(String email) {
        if (email == null) return null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_USERS + " WHERE email = ?";
        try (Cursor c = db.rawQuery(sql, new String[]{email.toLowerCase().trim()})) {
            if (c != null && c.moveToFirst()) {
                User user = cursorToUser(c);
                enrichUserComputedStats(db, user);
                return user;
            }
        }
        return null;
    }

    public User queryUserById(String userId) {
        if (userId == null) return null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_USERS + " WHERE id = ?";
        try (Cursor c = db.rawQuery(sql, new String[]{userId})) {
            if (c != null && c.moveToFirst()) {
                User user = cursorToUser(c);
                enrichUserComputedStats(db, user);
                return user;
            }
        }
        return null;
    }

    private User cursorToUser(Cursor c) {
        return new User(
                c.getString(c.getColumnIndexOrThrow(COL_USER_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_PASSWORD_HASH)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_SALT)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_COLLEGE)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_AVATAR)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_PHONE)),
                c.getLong(c.getColumnIndexOrThrow(COL_USER_CREATED_AT))
        );
    }

    public void enrichUserComputedStats(SQLiteDatabase db, User user) {
        if (user == null || user.getId() == null) return;
        String userId = user.getId();

        // 1. Rating & Review Count
        try (Cursor c = db.rawQuery("SELECT AVG(rating), COUNT(*) FROM " + TABLE_REVIEWS + " WHERE reviewee_id = ?", new String[]{userId})) {
            if (c != null && c.moveToFirst()) {
                double avg = c.getDouble(0);
                int count = c.getInt(1);
                user.setRating(count > 0 ? Math.round(avg * 10.0) / 10.0 : 5.0);
                user.setReviewCount(count);
            }
        }

        // 2. Lent Count (Completed or active)
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS + " WHERE owner_id = ? AND status IN ('borrowed', 'returned')", new String[]{userId})) {
            if (c != null && c.moveToFirst()) {
                user.setLentCount(c.getInt(0));
            }
        }

        // 3. Borrows Count (Completed or active)
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS + " WHERE borrower_id = ? AND status IN ('borrowed', 'returned')", new String[]{userId})) {
            if (c != null && c.moveToFirst()) {
                user.setBorrowsCount(c.getInt(0));
            }
        }

        // 4. Karma Points (Computed: 15 per lent, 5 per borrow, 5 per review)
        int points = (user.getLentCount() * 15) + (user.getBorrowsCount() * 5) + (user.getReviewCount() * 5) + 50;
        user.setPoints(points);

        // 5. Member Since Date
        if (user.getCreatedAt() > 0) {
            user.setJoinedDate(DateTimeUtils.formatMonthYear(user.getCreatedAt()));
        } else {
            user.setJoinedDate("Campus Student");
        }
    }
}
