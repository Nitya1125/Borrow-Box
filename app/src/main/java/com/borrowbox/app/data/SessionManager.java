package com.borrowbox.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.borrowbox.app.models.User;

public class SessionManager {
    private static final String PREF_NAME = "BorrowBoxSessionPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_COLLEGE = "userCollege";
    private static final String KEY_USER_AVATAR = "userAvatar";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_ONBOARDING_DONE = "onboardingDone";

    private static SessionManager instance;
    private final SharedPreferences pref;

    private SessionManager(Context context) {
        this.pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void createLoginSession(User user) {
        if (user == null) return;
        pref.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_ID, user.getId())
                .putString(KEY_USER_NAME, user.getName())
                .putString(KEY_USER_EMAIL, user.getEmail())
                .putString(KEY_USER_COLLEGE, user.getCollege())
                .putString(KEY_USER_AVATAR, user.getAvatarUrl())
                .putString(KEY_USER_PHONE, user.getPhone())
                .apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false) && pref.getString(KEY_USER_ID, null) != null;
    }

    @Nullable
    public User getLoggedInUser() {
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setId(pref.getString(KEY_USER_ID, ""));
        user.setName(pref.getString(KEY_USER_NAME, ""));
        user.setEmail(pref.getString(KEY_USER_EMAIL, ""));
        user.setCollege(pref.getString(KEY_USER_COLLEGE, ""));
        user.setAvatarUrl(pref.getString(KEY_USER_AVATAR, ""));
        user.setPhone(pref.getString(KEY_USER_PHONE, ""));
        return user;
    }

    public void updateLoggedInUser(User user) {
        if (user != null) {
            pref.edit()
                    .putString(KEY_USER_NAME, user.getName())
                    .putString(KEY_USER_COLLEGE, user.getCollege())
                    .putString(KEY_USER_AVATAR, user.getAvatarUrl())
                    .putString(KEY_USER_PHONE, user.getPhone())
                    .apply();
        }
    }

    public void logout() {
        pref.edit()
                .remove(KEY_IS_LOGGED_IN)
                .remove(KEY_USER_ID)
                .remove(KEY_USER_NAME)
                .remove(KEY_USER_EMAIL)
                .remove(KEY_USER_COLLEGE)
                .remove(KEY_USER_AVATAR)
                .remove(KEY_USER_PHONE)
                .apply();
    }

    public boolean isOnboardingCompleted() {
        return pref.getBoolean(KEY_ONBOARDING_DONE, false);
    }

    public void setOnboardingCompleted(boolean completed) {
        pref.edit().putBoolean(KEY_ONBOARDING_DONE, completed).apply();
    }
}
