package com.borrowbox.app.data;

import android.content.Context;

import com.borrowbox.app.R;
import com.borrowbox.app.models.Category;
import com.borrowbox.app.models.Item;
import com.borrowbox.app.models.NotificationItem;
import com.borrowbox.app.models.Transaction;
import com.borrowbox.app.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataManager {
    private static DataManager instance;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private List<Category> categories = new ArrayList<>();

    private DataManager() {
        initCategories();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void init(Context context) {
        if (dbHelper == null) {
            dbHelper = DatabaseHelper.getInstance(context);
        }
        if (sessionManager == null) {
            sessionManager = SessionManager.getInstance(context);
        }
    }

    private void ensureInitialized() {
        // Safe fallback if called before activity attaches
    }

    private void initCategories() {
        categories.clear();
        categories.add(new Category("all", "All", R.drawable.ic_cat_all));
        categories.add(new Category("books", "Books", R.drawable.ic_cat_books));
        categories.add(new Category("electronics", "Electronics", R.drawable.ic_cat_electronics));
        categories.add(new Category("calculators", "Calculators", R.drawable.ic_cat_calculators));
        categories.add(new Category("stationery", "Stationery", R.drawable.ic_cat_stationery));
        categories.add(new Category("sports", "Sports", R.drawable.ic_cat_sports));
        categories.add(new Category("lab", "Lab", R.drawable.ic_cat_lab));
        categories.add(new Category("others", "Others", R.drawable.ic_cat_others));
    }

    // ==========================================
    // USER / AUTH
    // ==========================================

    public boolean registerUser(User user) {
        if (dbHelper == null) return false;
        boolean success = dbHelper.registerUser(user);
        if (success && sessionManager != null) {
            sessionManager.createLoginSession(user);
        }
        return success;
    }

    public User loginUser(String email, String password) {
        if (dbHelper == null) return null;
        User user = dbHelper.authenticateUser(email, password);
        if (user != null && sessionManager != null) {
            sessionManager.createLoginSession(user);
        }
        return user;
    }

    public boolean isEmailRegistered(String email) {
        if (dbHelper == null) return false;
        return dbHelper.isEmailExists(email);
    }

    public User getCurrentUser() {
        if (sessionManager != null) {
            return sessionManager.getLoggedInUser();
        }
        return new User("u_nitya", "Nitya Kapoor", "nitya@bits.edu", "student123", "BITS Pilani, 3rd Year", "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=120&h=120&fit=crop&auto=format");
    }

    public void updateCurrentUser(User user) {
        if (dbHelper != null) {
            dbHelper.updateUser(user);
        }
        if (sessionManager != null) {
            sessionManager.updateLoggedInUser(user);
        }
    }

    public void logout() {
        if (sessionManager != null) {
            sessionManager.logout();
        }
    }

    public boolean isLoggedIn() {
        return sessionManager != null && sessionManager.isLoggedIn();
    }

    public boolean isOnboardingCompleted() {
        return sessionManager != null && sessionManager.isOnboardingCompleted();
    }

    public void setOnboardingCompleted(boolean completed) {
        if (sessionManager != null) {
            sessionManager.setOnboardingCompleted(completed);
        }
    }

    // ==========================================
    // CATEGORIES & ITEMS
    // ==========================================

    public List<Category> getCategories() {
        return categories;
    }

    public List<Item> getItems() {
        if (dbHelper == null) return new ArrayList<>();
        return dbHelper.getAllItems();
    }

    public List<Item> getItemsByCategory(String categoryId) {
        if (dbHelper == null) return new ArrayList<>();
        return dbHelper.getItemsByCategory(categoryId);
    }

    public List<Item> searchItems(String query) {
        if (dbHelper == null) return new ArrayList<>();
        return dbHelper.searchItems(query);
    }

    public List<Item> getMyItems() {
        if (dbHelper == null) return new ArrayList<>();
        String userEmail = getCurrentUser().getEmail();
        return dbHelper.getItemsByOwnerEmail(userEmail);
    }

    public void addItem(Item item) {
        if (dbHelper != null) {
            dbHelper.insertItem(item);
        }
    }

    public void updateItem(Item item) {
        if (dbHelper != null) {
            dbHelper.updateItem(item);
        }
    }

    public void deleteItem(String itemId) {
        if (dbHelper != null) {
            dbHelper.deleteItem(itemId);
        }
    }

    // ==========================================
    // TRANSACTIONS & REQUESTS
    // ==========================================

    public List<Transaction> getTransactions() {
        if (dbHelper == null) return new ArrayList<>();
        String userEmail = getCurrentUser().getEmail();
        return dbHelper.getTransactionsForUser(userEmail, null);
    }

    public List<Transaction> getTransactionsByType(String type) {
        if (dbHelper == null) return new ArrayList<>();
        String userEmail = getCurrentUser().getEmail();
        return dbHelper.getTransactionsForUser(userEmail, type);
    }

    public List<Transaction> getPendingRequests() {
        if (dbHelper == null) return new ArrayList<>();
        String userEmail = getCurrentUser().getEmail();
        return dbHelper.getPendingRequestsForOwner(userEmail);
    }

    public void addBorrowRequest(Item item, String duration, String message) {
        String txId = "tx_" + System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        String today = sdf.format(new Date());

        User currentUser = getCurrentUser();
        Transaction tx = new Transaction(
                txId,
                item,
                currentUser.getName(),
                currentUser.getAvatarUrl(),
                "pending",
                today,
                duration,
                "borrowed",
                currentUser.getEmail(),
                item.getOwnerEmail(),
                message,
                5.0f,
                ""
        );

        if (dbHelper != null) {
            dbHelper.insertTransaction(tx, message);

            // Add notification for the item owner
            NotificationItem notif = new NotificationItem(
                    "notif_" + System.currentTimeMillis(),
                    "request",
                    "New Borrow Request",
                    currentUser.getName() + " wants to borrow your " + item.getName(),
                    "Just now",
                    false,
                    item.getOwnerEmail()
            );
            dbHelper.insertNotification(notif);
        }
    }

    public void acceptBorrowRequest(Transaction tx) {
        if (dbHelper != null) {
            dbHelper.updateTransactionStatus(tx.getId(), "approved");

            // Mark item as borrowed
            if (tx.getItem() != null) {
                Item item = tx.getItem();
                item.setAvailable(false);
                item.setStatus("borrowed");
                item.setCurrentBorrower(tx.getPerson());
                dbHelper.updateItem(item);
            }

            // Notify borrower
            NotificationItem notif = new NotificationItem(
                    "notif_" + System.currentTimeMillis(),
                    "accepted",
                    "Request Accepted!",
                    "Your request for " + tx.getItem().getName() + " was approved!",
                    "Just now",
                    false,
                    tx.getBorrowerEmail()
            );
            dbHelper.insertNotification(notif);
        }
    }

    public void rejectBorrowRequest(Transaction tx) {
        if (dbHelper != null) {
            dbHelper.updateTransactionStatus(tx.getId(), "rejected");
        }
    }

    public void markTransactionReturned(String txId) {
        if (dbHelper != null) {
            dbHelper.updateTransactionStatus(txId, "returned");
        }
    }

    public void submitRating(String txId, float rating, String comment) {
        if (dbHelper != null) {
            dbHelper.submitTransactionRating(txId, rating, comment);
        }
    }

    // ==========================================
    // NOTIFICATIONS
    // ==========================================

    public List<NotificationItem> getNotifications() {
        if (dbHelper == null) return new ArrayList<>();
        return dbHelper.getNotifications(getCurrentUser().getEmail());
    }

    public int getUnreadNotificationCount() {
        if (dbHelper == null) return 0;
        return dbHelper.getUnreadNotificationCount(getCurrentUser().getEmail());
    }

    public void markNotificationRead(String notifId) {
        if (dbHelper != null) {
            dbHelper.markNotificationRead(notifId);
        }
    }

    public void markAllNotificationsRead() {
        if (dbHelper != null) {
            dbHelper.markAllNotificationsRead(getCurrentUser().getEmail());
        }
    }

    // ==========================================
    // WISHLIST / SAVED ITEMS
    // ==========================================

    public boolean isSaved(String itemId) {
        if (dbHelper == null) return false;
        return dbHelper.isSaved(getCurrentUser().getEmail(), itemId);
    }

    public boolean toggleSaved(String itemId) {
        if (dbHelper == null) return false;
        return dbHelper.toggleSaved(getCurrentUser().getEmail(), itemId);
    }

    // ==========================================
    // RECENT SEARCHES
    // ==========================================

    public List<String> getRecentSearches() {
        if (dbHelper == null) return new ArrayList<>();
        return dbHelper.getRecentSearches(getCurrentUser().getEmail());
    }

    public void addRecentSearch(String query) {
        if (dbHelper != null && query != null && !query.trim().isEmpty()) {
            dbHelper.addRecentSearch(getCurrentUser().getEmail(), query.trim());
        }
    }

    public void clearRecentSearches() {
        if (dbHelper != null) {
            dbHelper.clearRecentSearches(getCurrentUser().getEmail());
        }
    }
}
