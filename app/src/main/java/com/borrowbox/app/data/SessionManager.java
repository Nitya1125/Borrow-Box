package com.borrowbox.app.data;

import android.content.Context;
import android.content.SharedPreferences;

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
    private static final String KEY_USER_RATING = "userRating";
    private static final String KEY_ONBOARDING_DONE = "onboardingDone";

    private static SessionManager instance;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private SessionManager(Context context) {
        this.context = context.getApplicationContext();
        this.pref = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = pref.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_COLLEGE, user.getCollege());
        editor.putString(KEY_USER_AVATAR, user.getAvatarUrl());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putFloat(KEY_USER_RATING, (float) user.getRating());
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getLoggedInUser() {
        if (!isLoggedIn()) {
            return new User(
                    "u1",
                    "Nitya Kapoor",
                    "nitya@bits.edu",
                    "student123",
                    "BITS Pilani, 3rd Year",
                    "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=120&h=120&fit=crop&auto=format"
            );
        }
        User user = new User();
        user.setId(pref.getString(KEY_USER_ID, "u1"));
        user.setName(pref.getString(KEY_USER_NAME, "Nitya Kapoor"));
        user.setEmail(pref.getString(KEY_USER_EMAIL, "nitya@bits.edu"));
        user.setCollege(pref.getString(KEY_USER_COLLEGE, "BITS Pilani, 3rd Year"));
        user.setAvatarUrl(pref.getString(KEY_USER_AVATAR, "https://images.unsplash.com/photo-1489424731084-a5d8b219a5bb?w=120&h=120&fit=crop&auto=format"));
        user.setPhone(pref.getString(KEY_USER_PHONE, ""));
        user.setRating(pref.getFloat(KEY_USER_RATING, 4.9f));
        return user;
    }

    public void updateLoggedInUser(User user) {
        if (user != null) {
            editor.putString(KEY_USER_NAME, user.getName());
            editor.putString(KEY_USER_COLLEGE, user.getCollege());
            editor.putString(KEY_USER_AVATAR, user.getAvatarUrl());
            editor.putString(KEY_USER_PHONE, user.getPhone());
            editor.putFloat(KEY_USER_RATING, (float) user.getRating());
            editor.apply();
        }
    }

    public void logout() {
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_COLLEGE);
        editor.remove(KEY_USER_AVATAR);
        editor.remove(KEY_USER_PHONE);
        editor.remove(KEY_USER_RATING);
        editor.apply();
    }

    public boolean isOnboardingCompleted() {
        return pref.getBoolean(KEY_ONBOARDING_DONE, false);
    }

    public void setOnboardingCompleted(boolean completed) {
        editor.putBoolean(KEY_ONBOARDING_DONE, completed);
        editor.apply();
    }
}
