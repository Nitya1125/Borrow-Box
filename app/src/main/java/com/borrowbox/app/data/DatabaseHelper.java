package com.borrowbox.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.NotificationItem;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "borrowbox.db";
    private static final int DATABASE_VERSION = 1;

    // Table Users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_COLLEGE = "college";
    public static final String COL_USER_AVATAR = "avatar_url";
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_RATING = "rating";
    public static final String COL_USER_REVIEWS = "review_count";
    public static final String COL_USER_POINTS = "points";
    public static final String COL_USER_BORROWS = "borrows_count";
    public static final String COL_USER_LENT = "lent_count";
    public static final String COL_USER_JOINED = "joined_date";

    // Table Items
    public static final String TABLE_ITEMS = "items";
    public static final String COL_ITEM_ID = "id";
    public static final String COL_ITEM_NAME = "name";
    public static final String COL_ITEM_CATEGORY = "category";
    public static final String COL_ITEM_IMAGE = "image_url";
    public static final String COL_ITEM_OWNER = "owner_name";
    public static final String COL_ITEM_OWNER_AVATAR = "owner_avatar";
    public static final String COL_ITEM_OWNER_COLLEGE = "owner_college";
    public static final String COL_ITEM_OWNER_RATING = "owner_rating";
    public static final String COL_ITEM_AVAILABLE = "is_available";
    public static final String COL_ITEM_DESC = "description";
    public static final String COL_ITEM_DISTANCE = "distance";
    public static final String COL_ITEM_DURATION = "max_duration";
    public static final String COL_ITEM_PICKUP = "pickup_method";
    public static final String COL_ITEM_STATUS = "status";
    public static final String COL_ITEM_BORROWER = "borrower_name";
    public static final String COL_ITEM_OWNER_EMAIL = "owner_email";
    public static final String COL_ITEM_CREATED = "created_at";

    // Table Transactions
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COL_TX_ID = "id";
    public static final String COL_TX_ITEM_ID = "item_id";
    public static final String COL_TX_ITEM_NAME = "item_name";
    public static final String COL_TX_ITEM_IMAGE = "item_image";
    public static final String COL_TX_ITEM_CATEGORY = "item_category";
    public static final String COL_TX_OWNER_NAME = "owner_name";
    public static final String COL_TX_OWNER_AVATAR = "owner_avatar";
    public static final String COL_TX_OWNER_COLLEGE = "owner_college";
    public static final String COL_TX_OWNER_RATING = "owner_rating";
    public static final String COL_TX_OTHER_NAME = "other_party_name";
    public static final String COL_TX_OTHER_AVATAR = "other_party_avatar";
    public static final String COL_TX_STATUS = "status";
    public static final String COL_TX_BORROW_DATE = "borrow_date";
    public static final String COL_TX_RETURN_DATE = "return_date";
    public static final String COL_TX_TYPE = "type";
    public static final String COL_TX_BORROWER_EMAIL = "borrower_email";
    public static final String COL_TX_OWNER_EMAIL = "owner_email";
    public static final String COL_TX_REQUEST_MSG = "request_message";
    public static final String COL_TX_RATING = "rating_score";
    public static final String COL_TX_REVIEW = "review_comment";
    public static final String COL_TX_CREATED = "created_at";

    // Table Notifications
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COL_NOTIF_ID = "id";
    public static final String COL_NOTIF_USER_EMAIL = "user_email";
    public static final String COL_NOTIF_TYPE = "type";
    public static final String COL_NOTIF_TITLE = "title";
    public static final String COL_NOTIF_MESSAGE = "message";
    public static final String COL_NOTIF_TIME = "time_ago";
    public static final String COL_NOTIF_READ = "is_read";
    public static final String COL_NOTIF_CREATED = "created_at";

    // Table Saved Items
    public static final String TABLE_SAVED = "saved_items";
    public static final String COL_SAVED_USER = "user_email";
    public static final String COL_SAVED_ITEM = "item_id";

    // Table Recent Searches
    public static final String TABLE_SEARCHES = "recent_searches";
    public static final String COL_SEARCH_ID = "id";
    public static final String COL_SEARCH_USER = "user_email";
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
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " TEXT PRIMARY KEY, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_EMAIL + " TEXT UNIQUE, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_COLLEGE + " TEXT, " +
                COL_USER_AVATAR + " TEXT, " +
                COL_USER_PHONE + " TEXT, " +
                COL_USER_RATING + " REAL, " +
                COL_USER_REVIEWS + " INTEGER, " +
                COL_USER_POINTS + " INTEGER, " +
                COL_USER_BORROWS + " INTEGER, " +
                COL_USER_LENT + " INTEGER, " +
                COL_USER_JOINED + " TEXT)";
        db.execSQL(createUsersTable);

        // Create Items table
        String createItemsTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COL_ITEM_ID + " TEXT PRIMARY KEY, " +
                COL_ITEM_NAME + " TEXT, " +
                COL_ITEM_CATEGORY + " TEXT, " +
                COL_ITEM_IMAGE + " TEXT, " +
                COL_ITEM_OWNER + " TEXT, " +
                COL_ITEM_OWNER_AVATAR + " TEXT, " +
                COL_ITEM_OWNER_COLLEGE + " TEXT, " +
                COL_ITEM_OWNER_RATING + " REAL, " +
                COL_ITEM_AVAILABLE + " INTEGER, " +
                COL_ITEM_DESC + " TEXT, " +
                COL_ITEM_DISTANCE + " TEXT, " +
                COL_ITEM_DURATION + " TEXT, " +
                COL_ITEM_PICKUP + " TEXT, " +
                COL_ITEM_STATUS + " TEXT, " +
                COL_ITEM_BORROWER + " TEXT, " +
                COL_ITEM_OWNER_EMAIL + " TEXT, " +
                COL_ITEM_CREATED + " INTEGER)";
        db.execSQL(createItemsTable);

        // Create Transactions table
        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_TX_ID + " TEXT PRIMARY KEY, " +
                COL_TX_ITEM_ID + " TEXT, " +
                COL_TX_ITEM_NAME + " TEXT, " +
                COL_TX_ITEM_IMAGE + " TEXT, " +
                COL_TX_ITEM_CATEGORY + " TEXT, " +
                COL_TX_OWNER_NAME + " TEXT, " +
                COL_TX_OWNER_AVATAR + " TEXT, " +
                COL_TX_OWNER_COLLEGE + " TEXT, " +
                COL_TX_OWNER_RATING + " REAL, " +
                COL_TX_OTHER_NAME + " TEXT, " +
                COL_TX_OTHER_AVATAR + " TEXT, " +
                COL_TX_STATUS + " TEXT, " +
                COL_TX_BORROW_DATE + " TEXT, " +
                COL_TX_RETURN_DATE + " TEXT, " +
                COL_TX_TYPE + " TEXT, " +
                COL_TX_BORROWER_EMAIL + " TEXT, " +
                COL_TX_OWNER_EMAIL + " TEXT, " +
                COL_TX_REQUEST_MSG + " TEXT, " +
                COL_TX_RATING + " REAL, " +
                COL_TX_REVIEW + " TEXT, " +
                COL_TX_CREATED + " INTEGER)";
        db.execSQL(createTransactionsTable);

        // Create Notifications table
        String createNotifTable = "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                COL_NOTIF_ID + " TEXT PRIMARY KEY, " +
                COL_NOTIF_USER_EMAIL + " TEXT, " +
                COL_NOTIF_TYPE + " TEXT, " +
                COL_NOTIF_TITLE + " TEXT, " +
                COL_NOTIF_MESSAGE + " TEXT, " +
                COL_NOTIF_TIME + " TEXT, " +
                COL_NOTIF_READ + " INTEGER, " +
                COL_NOTIF_CREATED + " INTEGER)";
        db.execSQL(createNotifTable);

        // Create Saved Items table
        String createSavedTable = "CREATE TABLE " + TABLE_SAVED + " (" +
                COL_SAVED_USER + " TEXT, " +
                COL_SAVED_ITEM + " TEXT, " +
                "PRIMARY KEY (" + COL_SAVED_USER + ", " + COL_SAVED_ITEM + "))";
        db.execSQL(createSavedTable);

        // Create Recent Searches table
        String createSearchTable = "CREATE TABLE " + TABLE_SEARCHES + " (" +
                COL_SEARCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SEARCH_USER + " TEXT, " +
                COL_SEARCH_QUERY + " TEXT, " +
                COL_SEARCH_TIME + " INTEGER)";
        db.execSQL(createSearchTable);

        // Seed initial data
        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCHES);
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Pre-populate Default User
        ContentValues userVal = new ContentValues();
        userVal.put(COL_USER_ID, "u_nitya");
        userVal.put(COL_USER_NAME, "Nitya Kapoor");
        userVal.put(COL_USER_EMAIL, "nitya@bits.edu");
        userVal.put(COL_USER_PASSWORD, "student123");
        userVal.put(COL_USER_COLLEGE, "BITS Pilani, 3rd Year");
        userVal.put(COL_USER_AVATAR, "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=120&h=120&fit=crop&auto=format");
        userVal.put(COL_USER_PHONE, "+91 98765 43210");
        userVal.put(COL_USER_RATING, 4.9);
        userVal.put(COL_USER_REVIEWS, 14);
        userVal.put(COL_USER_POINTS, 280);
        userVal.put(COL_USER_BORROWS, 8);
        userVal.put(COL_USER_LENT, 12);
        userVal.put(COL_USER_JOINED, "Aug 2026");
        db.insert(TABLE_USERS, null, userVal);

        // Seed Items
        insertItemDirect(db, "1", "Casio FX-991EX Calculator", "Calculators",
                "https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400&h=300&fit=crop&auto=format",
                "Arjun Mehta", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=80&h=80&fit=crop&auto=format",
                "BITS Pilani", 4.8, 1, "Scientific calculator, perfect for engineering and physics exams. Battery is new. Handle with care.",
                "0.3 km", "3 days", "Library gate", "available", null, "arjun@bits.edu");

        insertItemDirect(db, "2", "Data Structures — Cormen", "Books",
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=300&fit=crop&auto=format",
                "Priya Sharma", "https://images.unsplash.com/photo-1494790108755-2616b612b604?w=80&h=80&fit=crop&auto=format",
                "IIT Delhi", 4.9, 1, "Introduction to Algorithms, 3rd edition. Good condition, some margin notes. Great for interviews.",
                "0.7 km", "7 days", "Hostel C, Room 204", "available", null, "priya@iitd.ac.in");

        insertItemDirect(db, "3", "Sony WH-1000XM4 Headphones", "Electronics",
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop&auto=format",
                "Rahul Gupta", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=80&h=80&fit=crop&auto=format",
                "NIT Trichy", 4.7, 0, "Premium noise-cancelling headphones. Great for study sessions. Comes with carry case.",
                "1.2 km", "2 days", "Academic block A", "borrowed", "Tanmay Sen", "rahul@nitt.edu");

        insertItemDirect(db, "4", "USB-C Fast Charging Cable (1m)", "Electronics",
                "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=300&fit=crop&auto=format",
                "Sneha Patel", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=80&h=80&fit=crop&auto=format",
                "VIT Vellore", 4.6, 1, "Fast charging USB-C braided cable. Compatible with laptops and phones.",
                "0.1 km", "1 day", "Canteen area", "available", null, "sneha@vit.ac.in");

        insertItemDirect(db, "5", "Lab Coat (Size M)", "Lab",
                "https://images.unsplash.com/photo-1576671081837-49000212a370?w=400&h=300&fit=crop&auto=format",
                "Vikram Singh", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=80&h=80&fit=crop&auto=format",
                "SRM University", 4.5, 1, "White lab coat, size medium. Clean and pressed. Perfect for chemistry and bio practicals.",
                "0.4 km", "5 days", "Lab 3 entrance", "available", null, "vikram@srm.edu");

        insertItemDirect(db, "6", "Badminton Racket Set", "Sports",
                "https://images.unsplash.com/photo-1592656094267-764a45160876?w=400&h=300&fit=crop&auto=format",
                "Meera Krishnan", "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=80&h=80&fit=crop&auto=format",
                "Manipal University", 4.9, 1, "Set of 2 rackets with 3 shuttlecocks. Good quality Yonex rackets for casual play.",
                "0.5 km", "3 days", "Sports complex reception", "available", null, "meera@manipal.edu");

        insertItemDirect(db, "7", "Drawing Kit (Professional)", "Stationery",
                "https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=400&h=300&fit=crop&auto=format",
                "Ananya Roy", "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=80&h=80&fit=crop&auto=format",
                "NIFT Delhi", 4.8, 1, "Complete drawing kit with pencils, charcoals, erasers and blending tools.",
                "0.9 km", "7 days", "Design studio, Block D", "available", null, "ananya@nift.ac.in");

        insertItemDirect(db, "8", "Raspberry Pi 4B (4GB)", "Electronics",
                "https://images.unsplash.com/photo-1518770660439-4636190af475?w=400&h=300&fit=crop&auto=format",
                "Deepak Nair", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=80&h=80&fit=crop&auto=format",
                "IIIT Hyderabad", 4.7, 1, "Raspberry Pi 4B 4GB RAM with power adapter and 32GB SD card. Ideal for IoT and ML projects.",
                "1.5 km", "5 days", "CS dept common room", "available", null, "deepak@iiit.ac.in");

        // Seed user's own items
        insertItemDirect(db, "my_1", "Texas Instruments TI-84 Plus", "Calculators",
                "https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400&h=300&fit=crop&auto=format",
                "Nitya Kapoor", "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=80&h=80&fit=crop&auto=format",
                "BITS Pilani", 4.9, 1, "Graphing calculator, ideal for advanced mathematics and engineering.",
                "0.1 km", "5 days", "Library gate", "available", null, "nitya@bits.edu");

        insertItemDirect(db, "my_2", "Microbiology Laboratory Manual", "Books",
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=300&fit=crop&auto=format",
                "Nitya Kapoor", "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=80&h=80&fit=crop&auto=format",
                "BITS Pilani", 4.9, 0, "Complete laboratory manual with experiment guidelines.",
                "0.1 km", "7 days", "Hostel D, Block 2", "borrowed", "Rohan Das", "nitya@bits.edu");

        // Seed Transactions
        insertTransactionDirect(db, "t1", "2", "Data Structures — Cormen",
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=300&fit=crop&auto=format", "Books",
                "Priya Sharma", "https://images.unsplash.com/photo-1494790108755-2616b612b604?w=80&h=80&fit=crop&auto=format",
                "IIT Delhi", 4.9, "Priya Sharma", "https://images.unsplash.com/photo-1494790108755-2616b612b604?w=80&h=80&fit=crop&auto=format",
                "borrowed", "24 Sep 2026", "01 Oct 2026", "borrowed", "nitya@bits.edu", "priya@iitd.ac.in", "Need for interview prep");

        insertTransactionDirect(db, "t2", "my_1", "Texas Instruments TI-84 Plus",
                "https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400&h=300&fit=crop&auto=format", "Calculators",
                "Nitya Kapoor", "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=80&h=80&fit=crop&auto=format",
                "BITS Pilani", 4.9, "Karan Verma", "https://images.unsplash.com/photo-1463453091185-61582044d556?w=80&h=80&fit=crop&auto=format",
                "pending", "29 Sep 2026", "02 Oct 2026", "lent", "karan@bits.edu", "nitya@bits.edu", "Need for mid-sem exam tomorrow");

        insertTransactionDirect(db, "t3", "4", "USB-C Fast Charging Cable (1m)",
                "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=300&fit=crop&auto=format", "Electronics",
                "Sneha Patel", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=80&h=80&fit=crop&auto=format",
                "VIT Vellore", 4.6, "Sneha Patel", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=80&h=80&fit=crop&auto=format",
                "returned", "20 Sep 2026", "21 Sep 2026", "borrowed", "nitya@bits.edu", "sneha@vit.ac.in", "Quick charge required");

        insertTransactionDirect(db, "t4", "my_2", "Microbiology Laboratory Manual",
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=300&fit=crop&auto=format", "Books",
                "Nitya Kapoor", "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=80&h=80&fit=crop&auto=format",
                "BITS Pilani", 4.9, "Rohan Das", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=80&h=80&fit=crop&auto=format",
                "approved", "30 Sep 2026", "03 Oct 2026", "lent", "rohan@bits.edu", "nitya@bits.edu", "Practical test next week");

        // Seed Notifications
        insertNotificationDirect(db, "n1", "nitya@bits.edu", "accepted", "Request Accepted!", "Priya accepted your request for Data Structures book.", "2 min ago", 0);
        insertNotificationDirect(db, "n2", "nitya@bits.edu", "request", "New Borrow Request", "Karan wants to borrow your Texas Instruments Calculator.", "1 hr ago", 0);
        insertNotificationDirect(db, "n3", "nitya@bits.edu", "due", "Return Due Tomorrow", "Your borrowed Data Structures book is due tomorrow.", "3 hr ago", 0);
        insertNotificationDirect(db, "n4", "nitya@bits.edu", "returned", "Item Returned", "Sneha confirmed receipt and return of USB-C Cable.", "1 day ago", 1);
        insertNotificationDirect(db, "n5", "nitya@bits.edu", "reminder", "Wishlist Update", "Sony WH-1000XM4 Headphones will be available soon.", "2 days ago", 1);

        // Seed Recent Searches
        addSearchDirect(db, "nitya@bits.edu", "Scientific Calculator");
        addSearchDirect(db, "nitya@bits.edu", "Physics Textbook");
        addSearchDirect(db, "nitya@bits.edu", "USB-C Charger");
        addSearchDirect(db, "nitya@bits.edu", "Lab Coat");
    }

    private void insertItemDirect(SQLiteDatabase db, String id, String name, String category,
                                  String imageUrl, String ownerName, String ownerAvatar,
                                  String ownerCollege, double ownerRating, int isAvailable,
                                  String description, String distance, String maxDuration,
                                  String pickupMethod, String status, String borrowerName, String ownerEmail) {
        ContentValues v = new ContentValues();
        v.put(COL_ITEM_ID, id);
        v.put(COL_ITEM_NAME, name);
        v.put(COL_ITEM_CATEGORY, category);
        v.put(COL_ITEM_IMAGE, imageUrl);
        v.put(COL_ITEM_OWNER, ownerName);
        v.put(COL_ITEM_OWNER_AVATAR, ownerAvatar);
        v.put(COL_ITEM_OWNER_COLLEGE, ownerCollege);
        v.put(COL_ITEM_OWNER_RATING, ownerRating);
        v.put(COL_ITEM_AVAILABLE, isAvailable);
        v.put(COL_ITEM_DESC, description);
        v.put(COL_ITEM_DISTANCE, distance);
        v.put(COL_ITEM_DURATION, maxDuration);
        v.put(COL_ITEM_PICKUP, pickupMethod);
        v.put(COL_ITEM_STATUS, status);
        v.put(COL_ITEM_BORROWER, borrowerName);
        v.put(COL_ITEM_OWNER_EMAIL, ownerEmail);
        v.put(COL_ITEM_CREATED, System.currentTimeMillis());
        db.insert(TABLE_ITEMS, null, v);
    }

    private void insertTransactionDirect(SQLiteDatabase db, String id, String itemId, String itemName,
                                         String itemImage, String itemCategory, String ownerName,
                                         String ownerAvatar, String ownerCollege, double ownerRating,
                                         String otherName, String otherAvatar, String status,
                                         String borrowDate, String returnDate, String type,
                                         String borrowerEmail, String ownerEmail, String reqMsg) {
        ContentValues v = new ContentValues();
        v.put(COL_TX_ID, id);
        v.put(COL_TX_ITEM_ID, itemId);
        v.put(COL_TX_ITEM_NAME, itemName);
        v.put(COL_TX_ITEM_IMAGE, itemImage);
        v.put(COL_TX_ITEM_CATEGORY, itemCategory);
        v.put(COL_TX_OWNER_NAME, ownerName);
        v.put(COL_TX_OWNER_AVATAR, ownerAvatar);
        v.put(COL_TX_OWNER_COLLEGE, ownerCollege);
        v.put(COL_TX_OWNER_RATING, ownerRating);
        v.put(COL_TX_OTHER_NAME, otherName);
        v.put(COL_TX_OTHER_AVATAR, otherAvatar);
        v.put(COL_TX_STATUS, status);
        v.put(COL_TX_BORROW_DATE, borrowDate);
        v.put(COL_TX_RETURN_DATE, returnDate);
        v.put(COL_TX_TYPE, type);
        v.put(COL_TX_BORROWER_EMAIL, borrowerEmail);
        v.put(COL_TX_OWNER_EMAIL, ownerEmail);
        v.put(COL_TX_REQUEST_MSG, reqMsg);
        v.put(COL_TX_RATING, 5.0f);
        v.put(COL_TX_REVIEW, "");
        v.put(COL_TX_CREATED, System.currentTimeMillis());
        db.insert(TABLE_TRANSACTIONS, null, v);
    }

    private void insertNotificationDirect(SQLiteDatabase db, String id, String userEmail, String type,
                                         String title, String msg, String timeAgo, int isRead) {
        ContentValues v = new ContentValues();
        v.put(COL_NOTIF_ID, id);
        v.put(COL_NOTIF_USER_EMAIL, userEmail);
        v.put(COL_NOTIF_TYPE, type);
        v.put(COL_NOTIF_TITLE, title);
        v.put(COL_NOTIF_MESSAGE, msg);
        v.put(COL_NOTIF_TIME, timeAgo);
        v.put(COL_NOTIF_READ, isRead);
        v.put(COL_NOTIF_CREATED, System.currentTimeMillis());
        db.insert(TABLE_NOTIFICATIONS, null, v);
    }

    private void addSearchDirect(SQLiteDatabase db, String userEmail, String query) {
        ContentValues v = new ContentValues();
        v.put(COL_SEARCH_USER, userEmail);
        v.put(COL_SEARCH_QUERY, query);
        v.put(COL_SEARCH_TIME, System.currentTimeMillis());
        db.insert(TABLE_SEARCHES, null, v);
    }

    // ==========================================
    // USER AUTH & PROFILE OPERATIONS
    // ==========================================

    public boolean registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_USER_ID, user.getId());
        v.put(COL_USER_NAME, user.getName());
        v.put(COL_USER_EMAIL, user.getEmail().toLowerCase().trim());
        v.put(COL_USER_PASSWORD, user.getPassword());
        v.put(COL_USER_COLLEGE, user.getCollege());
        v.put(COL_USER_AVATAR, user.getAvatarUrl());
        v.put(COL_USER_PHONE, user.getPhone());
        v.put(COL_USER_RATING, user.getRating());
        v.put(COL_USER_REVIEWS, user.getReviewCount());
        v.put(COL_USER_POINTS, user.getPoints());
        v.put(COL_USER_BORROWS, user.getBorrowsCount());
        v.put(COL_USER_LENT, user.getLentCount());
        v.put(COL_USER_JOINED, user.getJoinedDate());

        long result = db.insert(TABLE_USERS, null, v);
        return result != -1;
    }

    public User authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String cleanEmail = email.toLowerCase().trim();
        Cursor c = db.query(TABLE_USERS, null,
                COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD + " = ?",
                new String[]{cleanEmail, password}, null, null, null);

        User user = null;
        if (c != null && c.moveToFirst()) {
            user = cursorToUser(c);
            c.close();
        }
        return user;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, new String[]{COL_USER_ID},
                COL_USER_EMAIL + " = ?",
                new String[]{email.toLowerCase().trim()}, null, null, null);
        boolean exists = (c != null && c.getCount() > 0);
        if (c != null) c.close();
        return exists;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, null,
                COL_USER_EMAIL + " = ?",
                new String[]{email.toLowerCase().trim()}, null, null, null);
        User user = null;
        if (c != null && c.moveToFirst()) {
            user = cursorToUser(c);
            c.close();
        }
        return user;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_USER_NAME, user.getName());
        v.put(COL_USER_COLLEGE, user.getCollege());
        v.put(COL_USER_AVATAR, user.getAvatarUrl());
        v.put(COL_USER_PHONE, user.getPhone());
        v.put(COL_USER_RATING, user.getRating());
        v.put(COL_USER_REVIEWS, user.getReviewCount());
        v.put(COL_USER_POINTS, user.getPoints());
        v.put(COL_USER_BORROWS, user.getBorrowsCount());
        v.put(COL_USER_LENT, user.getLentCount());

        int count = db.update(TABLE_USERS, v, COL_USER_EMAIL + " = ?",
                new String[]{user.getEmail().toLowerCase().trim()});
        return count > 0;
    }

    private User cursorToUser(Cursor c) {
        return new User(
                c.getString(c.getColumnIndexOrThrow(COL_USER_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_PASSWORD)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_COLLEGE)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_AVATAR)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_PHONE)),
                c.getDouble(c.getColumnIndexOrThrow(COL_USER_RATING)),
                c.getInt(c.getColumnIndexOrThrow(COL_USER_REVIEWS)),
                c.getInt(c.getColumnIndexOrThrow(COL_USER_POINTS)),
                c.getInt(c.getColumnIndexOrThrow(COL_USER_BORROWS)),
                c.getInt(c.getColumnIndexOrThrow(COL_USER_LENT)),
                c.getString(c.getColumnIndexOrThrow(COL_USER_JOINED))
        );
    }

    // ==========================================
    // ITEM OPERATIONS
    // ==========================================

    public boolean insertItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_ITEM_ID, item.getId());
        v.put(COL_ITEM_NAME, item.getName());
        v.put(COL_ITEM_CATEGORY, item.getCategory());
        v.put(COL_ITEM_IMAGE, item.getImageUrl());
        v.put(COL_ITEM_OWNER, item.getOwner());
        v.put(COL_ITEM_OWNER_AVATAR, item.getOwnerAvatar());
        v.put(COL_ITEM_OWNER_COLLEGE, item.getOwnerCollege());
        v.put(COL_ITEM_OWNER_RATING, item.getOwnerRating());
        v.put(COL_ITEM_AVAILABLE, item.isAvailable() ? 1 : 0);
        v.put(COL_ITEM_DESC, item.getDescription());
        v.put(COL_ITEM_DISTANCE, item.getDistance());
        v.put(COL_ITEM_DURATION, item.getMaxDuration());
        v.put(COL_ITEM_PICKUP, item.getPickupMethod());
        v.put(COL_ITEM_STATUS, item.getStatus());
        v.put(COL_ITEM_BORROWER, item.getCurrentBorrower());
        v.put(COL_ITEM_OWNER_EMAIL, item.getOwnerEmail());
        v.put(COL_ITEM_CREATED, System.currentTimeMillis());

        long res = db.insert(TABLE_ITEMS, null, v);
        return res != -1;
    }

    public List<Item> getAllItems() {
        List<Item> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_ITEMS, null, null, null, null, null, COL_ITEM_CREATED + " DESC");
        if (c != null && c.moveToFirst()) {
            do {
                list.add(cursorToItem(c));
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public List<Item> getItemsByCategory(String categoryId) {
        if ("all".equalsIgnoreCase(categoryId)) {
            return getAllItems();
        }
        List<Item> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_ITEMS, null,
                COL_ITEM_CATEGORY + " LIKE ?",
                new String[]{"%" + categoryId + "%"}, null, null, COL_ITEM_CREATED + " DESC");
        if (c != null && c.moveToFirst()) {
            do {
                list.add(cursorToItem(c));
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public List<Item> searchItems(String query) {
        List<Item> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String pattern = "%" + query.trim() + "%";
        Cursor c = db.query(TABLE_ITEMS, null,
                COL_ITEM_NAME + " LIKE ? OR " + COL_ITEM_CATEGORY + " LIKE ? OR " + COL_ITEM_OWNER + " LIKE ? OR " + COL_ITEM_DESC + " LIKE ?",
                new String[]{pattern, pattern, pattern, pattern}, null, null, COL_ITEM_CREATED + " DESC");
        if (c != null && c.moveToFirst()) {
            do {
                list.add(cursorToItem(c));
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public List<Item> getItemsByOwnerEmail(String email) {
        List<Item> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_ITEMS, null,
                COL_ITEM_OWNER_EMAIL + " = ?",
                new String[]{email.toLowerCase().trim()}, null, null, COL_ITEM_CREATED + " DESC");
        if (c != null && c.moveToFirst()) {
            do {
                list.add(cursorToItem(c));
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public Item getItemById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_ITEMS, null,
                COL_ITEM_ID + " = ?",
                new String[]{id}, null, null, null);
        Item item = null;
        if (c != null && c.moveToFirst()) {
            item = cursorToItem(c);
            c.close();
        }
        return item;
    }

    public boolean updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_ITEM_NAME, item.getName());
        v.put(COL_ITEM_CATEGORY, item.getCategory());
        v.put(COL_ITEM_IMAGE, item.getImageUrl());
        v.put(COL_ITEM_AVAILABLE, item.isAvailable() ? 1 : 0);
        v.put(COL_ITEM_DESC, item.getDescription());
        v.put(COL_ITEM_DURATION, item.getMaxDuration());
        v.put(COL_ITEM_PICKUP, item.getPickupMethod());
        v.put(COL_ITEM_STATUS, item.getStatus());
        v.put(COL_ITEM_BORROWER, item.getCurrentBorrower());

        int count = db.update(TABLE_ITEMS, v, COL_ITEM_ID + " = ?", new String[]{item.getId()});
        return count > 0;
    }

    public boolean deleteItem(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_ITEMS, COL_ITEM_ID + " = ?", new String[]{itemId});
        return count > 0;
    }

    private Item cursorToItem(Cursor c) {
        return new Item(
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_CATEGORY)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_IMAGE)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_OWNER)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_OWNER_AVATAR)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_OWNER_COLLEGE)),
                c.getDouble(c.getColumnIndexOrThrow(COL_ITEM_OWNER_RATING)),
                c.getInt(c.getColumnIndexOrThrow(COL_ITEM_AVAILABLE)) == 1,
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_DESC)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_DISTANCE)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_DURATION)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_PICKUP)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_STATUS)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_BORROWER)),
                c.getString(c.getColumnIndexOrThrow(COL_ITEM_OWNER_EMAIL))
        );
    }

    // ==========================================
    // TRANSACTION OPERATIONS
    // ==========================================

    public boolean insertTransaction(Transaction tx, String requestMsg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_TX_ID, tx.getId());
        v.put(COL_TX_ITEM_ID, tx.getItem() != null ? tx.getItem().getId() : "");
        v.put(COL_TX_ITEM_NAME, tx.getItem() != null ? tx.getItem().getName() : "");
        v.put(COL_TX_ITEM_IMAGE, tx.getItem() != null ? tx.getItem().getImageUrl() : "");
        v.put(COL_TX_ITEM_CATEGORY, tx.getItem() != null ? tx.getItem().getCategory() : "");
        v.put(COL_TX_OWNER_NAME, tx.getItem() != null ? tx.getItem().getOwner() : "");
        v.put(COL_TX_OWNER_AVATAR, tx.getItem() != null ? tx.getItem().getOwnerAvatar() : "");
        v.put(COL_TX_OWNER_COLLEGE, tx.getItem() != null ? tx.getItem().getOwnerCollege() : "");
        v.put(COL_TX_OWNER_RATING, tx.getItem() != null ? tx.getItem().getOwnerRating() : 5.0);
        v.put(COL_TX_OTHER_NAME, tx.getPerson());
        v.put(COL_TX_OTHER_AVATAR, tx.getPersonAvatar());
        v.put(COL_TX_STATUS, tx.getStatus());
        v.put(COL_TX_BORROW_DATE, tx.getBorrowDate());
        v.put(COL_TX_RETURN_DATE, tx.getReturnDate());
        v.put(COL_TX_TYPE, tx.getType());
        v.put(COL_TX_BORROWER_EMAIL, tx.getBorrowerEmail());
        v.put(COL_TX_OWNER_EMAIL, tx.getOwnerEmail());
        v.put(COL_TX_REQUEST_MSG, requestMsg);
        v.put(COL_TX_RATING, tx.getRatingScore());
        v.put(COL_TX_REVIEW, tx.getReviewComment());
        v.put(COL_TX_CREATED, System.currentTimeMillis());

        long res = db.insert(TABLE_TRANSACTIONS, null, v);
        return res != -1;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_TRANSACTIONS, null, null, null, null, null, COL_TX_CREATED + " DESC");
        if (c != null && c.moveToFirst()) {
            do {
                list.add(cursorToTransaction(c));
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public List<Transaction> getTransactionsForUser(String email, String type) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection;
        String[] args;
        if (type == null || type.isEmpty()) {
            selection = COL_TX_BORROWER_EMAIL + " = ? OR " + COL_TX_OWNER_EMAIL + " = ?";
            args = new String[]{email, email};
        } else {
            selection = "(" + COL_TX_BORROWER_EMAIL + " = ? OR " + COL_TX_OWNER_EMAIL + " = ?) AND " + COL_TX_TYPE + " = ?";
            args = new String[]{email, email, type};
        }

        Cursor c = db.query(TABLE_TRANSACTIONS, null, selection, args, null, null, COL_TX_CREATED + " DESC");
        if (c != null && c.moveToFirst()) {
            do {
                list.add(cursorToTransaction(c));
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public List<Transaction> getPendingRequestsForOwner(String ownerEmail) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_TRANSACTIONS, null,
                COL_TX_OWNER_EMAIL + " = ? AND " + COL_TX_STATUS + " = ?",
                new String[]{ownerEmail, "pending"}, null, null, COL_TX_CREATED + " DESC");
        if (c != null && c.moveToFirst()) {
            do {
                list.add(cursorToTransaction(c));
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public boolean updateTransactionStatus(String txId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_TX_STATUS, status);
        int count = db.update(TABLE_TRANSACTIONS, v, COL_TX_ID + " = ?", new String[]{txId});
        return count > 0;
    }

    public boolean submitTransactionRating(String txId, float rating, String reviewComment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_TX_RATING, rating);
        v.put(COL_TX_REVIEW, reviewComment);
        int count = db.update(TABLE_TRANSACTIONS, v, COL_TX_ID + " = ?", new String[]{txId});
        return count > 0;
    }

    private Transaction cursorToTransaction(Cursor c) {
        Item item = new Item(
                c.getString(c.getColumnIndexOrThrow(COL_TX_ITEM_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_ITEM_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_ITEM_CATEGORY)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_ITEM_IMAGE)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_OWNER_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_OWNER_AVATAR)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_OWNER_COLLEGE)),
                c.getDouble(c.getColumnIndexOrThrow(COL_TX_OWNER_RATING)),
                true,
                "",
                "",
                "",
                ""
        );

        return new Transaction(
                c.getString(c.getColumnIndexOrThrow(COL_TX_ID)),
                item,
                c.getString(c.getColumnIndexOrThrow(COL_TX_OTHER_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_OTHER_AVATAR)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_STATUS)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_BORROW_DATE)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_RETURN_DATE)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_TYPE)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_BORROWER_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_OWNER_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_REQUEST_MSG)),
                c.getFloat(c.getColumnIndexOrThrow(COL_TX_RATING)),
                c.getString(c.getColumnIndexOrThrow(COL_TX_REVIEW))
        );
    }

    // ==========================================
    // NOTIFICATION OPERATIONS
    // ==========================================

    public boolean insertNotification(NotificationItem notif) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_NOTIF_ID, notif.getId());
        v.put(COL_NOTIF_USER_EMAIL, notif.getUserEmail());
        v.put(COL_NOTIF_TYPE, notif.getType());
        v.put(COL_NOTIF_TITLE, notif.getTitle());
        v.put(COL_NOTIF_MESSAGE, notif.getMessage());
        v.put(COL_NOTIF_TIME, notif.getTime());
        v.put(COL_NOTIF_READ, notif.isRead() ? 1 : 0);
        v.put(COL_NOTIF_CREATED, System.currentTimeMillis());

        long res = db.insert(TABLE_NOTIFICATIONS, null, v);
        return res != -1;
    }

    public List<NotificationItem> getNotifications(String userEmail) {
        List<NotificationItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_NOTIFICATIONS, null,
                COL_NOTIF_USER_EMAIL + " = ?",
                new String[]{userEmail}, null, null, COL_NOTIF_CREATED + " DESC");
        if (c != null && c.moveToFirst()) {
            do {
                list.add(new NotificationItem(
                        c.getString(c.getColumnIndexOrThrow(COL_NOTIF_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_NOTIF_TYPE)),
                        c.getString(c.getColumnIndexOrThrow(COL_NOTIF_TITLE)),
                        c.getString(c.getColumnIndexOrThrow(COL_NOTIF_MESSAGE)),
                        c.getString(c.getColumnIndexOrThrow(COL_NOTIF_TIME)),
                        c.getInt(c.getColumnIndexOrThrow(COL_NOTIF_READ)) == 1,
                        c.getString(c.getColumnIndexOrThrow(COL_NOTIF_USER_EMAIL))
                ));
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public int getUnreadNotificationCount(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_NOTIFICATIONS, new String[]{COL_NOTIF_ID},
                COL_NOTIF_USER_EMAIL + " = ? AND " + COL_NOTIF_READ + " = 0",
                new String[]{userEmail}, null, null, null);
        int count = 0;
        if (c != null) {
            count = c.getCount();
            c.close();
        }
        return count;
    }

    public boolean markNotificationRead(String notifId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_NOTIF_READ, 1);
        int count = db.update(TABLE_NOTIFICATIONS, v, COL_NOTIF_ID + " = ?", new String[]{notifId});
        return count > 0;
    }

    public boolean markAllNotificationsRead(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_NOTIF_READ, 1);
        int count = db.update(TABLE_NOTIFICATIONS, v, COL_NOTIF_USER_EMAIL + " = ?", new String[]{userEmail});
        return count > 0;
    }

    // ==========================================
    // SAVED ITEMS / WISHLIST OPERATIONS
    // ==========================================

    public boolean toggleSaved(String userEmail, String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (isSaved(userEmail, itemId)) {
            db.delete(TABLE_SAVED, COL_SAVED_USER + " = ? AND " + COL_SAVED_ITEM + " = ?",
                    new String[]{userEmail, itemId});
            return false;
        } else {
            ContentValues v = new ContentValues();
            v.put(COL_SAVED_USER, userEmail);
            v.put(COL_SAVED_ITEM, itemId);
            db.insert(TABLE_SAVED, null, v);
            return true;
        }
    }

    public boolean isSaved(String userEmail, String itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_SAVED, null,
                COL_SAVED_USER + " = ? AND " + COL_SAVED_ITEM + " = ?",
                new String[]{userEmail, itemId}, null, null, null);
        boolean saved = (c != null && c.getCount() > 0);
        if (c != null) c.close();
        return saved;
    }

    public Set<String> getSavedItemIds(String userEmail) {
        Set<String> set = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_SAVED, new String[]{COL_SAVED_ITEM},
                COL_SAVED_USER + " = ?",
                new String[]{userEmail}, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                set.add(c.getString(c.getColumnIndexOrThrow(COL_SAVED_ITEM)));
            } while (c.moveToNext());
            c.close();
        }
        return set;
    }

    // ==========================================
    // RECENT SEARCHES OPERATIONS
    // ==========================================

    public void addRecentSearch(String userEmail, String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Remove existing duplicates
        db.delete(TABLE_SEARCHES, COL_SEARCH_USER + " = ? AND " + COL_SEARCH_QUERY + " = ?",
                new String[]{userEmail, query.trim()});
        ContentValues v = new ContentValues();
        v.put(COL_SEARCH_USER, userEmail);
        v.put(COL_SEARCH_QUERY, query.trim());
        v.put(COL_SEARCH_TIME, System.currentTimeMillis());
        db.insert(TABLE_SEARCHES, null, v);
    }

    public List<String> getRecentSearches(String userEmail) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_SEARCHES, new String[]{COL_SEARCH_QUERY},
                COL_SEARCH_USER + " = ?",
                new String[]{userEmail}, null, null, COL_SEARCH_TIME + " DESC", "10");
        if (c != null && c.moveToFirst()) {
            do {
                list.add(c.getString(c.getColumnIndexOrThrow(COL_SEARCH_QUERY)));
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public void clearRecentSearches(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SEARCHES, COL_SEARCH_USER + " = ?", new String[]{userEmail});
    }
}
