package com.borrowbox.app.data;

import android.content.Context;

import androidx.annotation.Nullable;

import com.borrowbox.app.data.repository.BorrowRepository;
import com.borrowbox.app.data.repository.ItemRepository;
import com.borrowbox.app.data.repository.NotificationRepository;
import com.borrowbox.app.data.repository.ReviewRepository;
import com.borrowbox.app.data.repository.UserRepository;
import com.borrowbox.app.models.Category;
import com.borrowbox.app.models.User;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;

    private Context appContext;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BorrowRepository borrowRepository;
    private NotificationRepository notificationRepository;
    private ReviewRepository reviewRepository;

    private DataManager() {}

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void init(Context context) {
        if (appContext == null) {
            this.appContext = context.getApplicationContext();
            this.dbHelper = DatabaseHelper.getInstance(appContext);
            this.sessionManager = SessionManager.getInstance(appContext);
            this.userRepository = UserRepository.getInstance(appContext);
            this.itemRepository = ItemRepository.getInstance(appContext);
            this.borrowRepository = BorrowRepository.getInstance(appContext);
            this.notificationRepository = NotificationRepository.getInstance(appContext);
            this.reviewRepository = ReviewRepository.getInstance(appContext);
        }
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }

    public BorrowRepository getBorrowRepository() {
        return borrowRepository;
    }

    public NotificationRepository getNotificationRepository() {
        return notificationRepository;
    }

    public ReviewRepository getReviewRepository() {
        return reviewRepository;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    // ==========================================
    // AUTH & SESSION HELPERS
    // ==========================================

    @Nullable
    public User getCurrentUser() {
        if (sessionManager == null) return null;
        return sessionManager.getLoggedInUser();
    }

    public String getCurrentUserId() {
        User u = getCurrentUser();
        return u != null ? u.getId() : "";
    }

    public boolean isLoggedIn() {
        return sessionManager != null && sessionManager.isLoggedIn();
    }

    public void logout() {
        if (sessionManager != null) {
            sessionManager.logout();
        }
    }

    public boolean isOnboardingCompleted() {
        return sessionManager != null && sessionManager.isOnboardingCompleted();
    }

    public void setOnboardingCompleted(boolean completed) {
        if (sessionManager != null) {
            sessionManager.setOnboardingCompleted(completed);
        }
    }

    public boolean isSaved(String itemId) {
        User u = getCurrentUser();
        if (u == null || dbHelper == null || itemId == null) return false;
        try (android.database.Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_SAVED + " WHERE user_id = ? AND item_id = ?",
                new String[]{u.getId(), itemId})) {
            return c != null && c.moveToFirst() && c.getInt(0) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void toggleSaved(String itemId) {
        User u = getCurrentUser();
        if (u != null && itemRepository != null && itemId != null) {
            itemRepository.toggleSaved(u.getId(), itemId, null);
        }
    }
}
