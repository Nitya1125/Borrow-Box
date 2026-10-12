package com.borrowbox.app.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.borrowbox.app.data.DatabaseHelper;
import com.borrowbox.app.models.User;
import com.borrowbox.app.utils.AppExecutors;
import com.borrowbox.app.utils.PasswordHasher;

import java.util.UUID;

public class UserRepository {

    private static UserRepository instance;
    private final DatabaseHelper dbHelper;
    private final AppExecutors executors;
    private final com.borrowbox.app.data.SessionManager sessionManager;

    public static synchronized UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context.getApplicationContext());
        }
        return instance;
    }

    private UserRepository(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        this.executors = AppExecutors.getInstance();
        this.sessionManager = com.borrowbox.app.data.SessionManager.getInstance(context);
    }

    public void registerUser(String name, String email, String plainPassword, String college, RepositoryCallback<User> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                String cleanEmail = email.toLowerCase().trim();
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Check duplicate email
                try (Cursor c = db.query(DatabaseHelper.TABLE_USERS, new String[]{DatabaseHelper.COL_USER_ID},
                        DatabaseHelper.COL_USER_EMAIL + " = ?", new String[]{cleanEmail}, null, null, null)) {
                    if (c != null && c.getCount() > 0) {
                        executors.postToMainThread(() -> callback.onError(new IllegalArgumentException("An account with this email already exists.")));
                        return;
                    }
                }

                String userId = "u_" + UUID.randomUUID().toString().substring(0, 8);
                String salt = PasswordHasher.generateSalt();
                String passwordHash = PasswordHasher.hashPassword(plainPassword, salt);
                long now = System.currentTimeMillis();

                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COL_USER_ID, userId);
                v.put(DatabaseHelper.COL_USER_NAME, name.trim());
                v.put(DatabaseHelper.COL_USER_EMAIL, cleanEmail);
                v.put(DatabaseHelper.COL_USER_PASSWORD_HASH, passwordHash);
                v.put(DatabaseHelper.COL_USER_SALT, salt);
                v.put(DatabaseHelper.COL_USER_COLLEGE, college.trim());
                v.put(DatabaseHelper.COL_USER_AVATAR, "");
                v.put(DatabaseHelper.COL_USER_PHONE, "");
                v.put(DatabaseHelper.COL_USER_CREATED_AT, now);

                long rowId = db.insert(DatabaseHelper.TABLE_USERS, null, v);
                if (rowId == -1) {
                    executors.postToMainThread(() -> callback.onError(new Exception("Database insert failed.")));
                    return;
                }

                User user = dbHelper.queryUserById(userId);
                executors.postToMainThread(() -> callback.onSuccess(user));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void authenticateUser(String email, String plainPassword, RepositoryCallback<User> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                String cleanEmail = email.toLowerCase().trim();
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String sql = "SELECT * FROM " + DatabaseHelper.TABLE_USERS + " WHERE email = ?";
                try (Cursor c = db.rawQuery(sql, new String[]{cleanEmail})) {
                    if (c != null && c.moveToFirst()) {
                        String storedHash = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD_HASH));
                        String salt = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_SALT));

                        boolean verified = PasswordHasher.verifyPassword(plainPassword, salt, storedHash);
                        if (verified) {
                            String userId = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
                            User user = dbHelper.queryUserById(userId);
                            executors.postToMainThread(() -> callback.onSuccess(user));
                            return;
                        }
                    }
                }

                executors.postToMainThread(() -> callback.onError(new IllegalArgumentException("Invalid email or password.")));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void resetPassword(String email, String newPassword, RepositoryCallback<Boolean> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                String cleanEmail = email.toLowerCase().trim();
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String sql = "SELECT id FROM " + DatabaseHelper.TABLE_USERS + " WHERE email = ?";
                String userId = null;
                try (Cursor c = db.rawQuery(sql, new String[]{cleanEmail})) {
                    if (c != null && c.moveToFirst()) {
                        userId = c.getString(0);
                    }
                }

                if (userId == null) {
                    executors.postToMainThread(() -> callback.onError(new IllegalArgumentException("Email address not found.")));
                    return;
                }

                String newSalt = PasswordHasher.generateSalt();
                String newHash = PasswordHasher.hashPassword(newPassword, newSalt);

                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COL_USER_PASSWORD_HASH, newHash);
                v.put(DatabaseHelper.COL_USER_SALT, newSalt);

                int rows = db.update(DatabaseHelper.TABLE_USERS, v, DatabaseHelper.COL_USER_ID + " = ?", new String[]{userId});
                executors.postToMainThread(() -> callback.onSuccess(rows > 0));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void updateProfile(User user, RepositoryCallback<User> callback) {
        updateUserProfile(user, callback);
    }

    public void updateUserProfile(User user, RepositoryCallback<User> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues v = new ContentValues();
                v.put(DatabaseHelper.COL_USER_NAME, user.getName());
                v.put(DatabaseHelper.COL_USER_COLLEGE, user.getCollege());
                v.put(DatabaseHelper.COL_USER_AVATAR, user.getAvatarUrl());
                v.put(DatabaseHelper.COL_USER_PHONE, user.getPhone());

                db.update(DatabaseHelper.TABLE_USERS, v, DatabaseHelper.COL_USER_ID + " = ?", new String[]{user.getId()});
                User refreshed = dbHelper.queryUserById(user.getId());
                if (refreshed != null && sessionManager != null) {
                    sessionManager.updateLoggedInUser(refreshed);
                }
                executors.postToMainThread(() -> callback.onSuccess(refreshed != null ? refreshed : user));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }

    public void getUserById(String userId, RepositoryCallback<User> callback) {
        executors.executeOnDiskIO(() -> {
            try {
                User user = dbHelper.queryUserById(userId);
                executors.postToMainThread(() -> callback.onSuccess(user));
            } catch (Exception e) {
                executors.postToMainThread(() -> callback.onError(e));
            }
        });
    }
}
