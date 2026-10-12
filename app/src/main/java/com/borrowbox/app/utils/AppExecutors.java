package com.borrowbox.app.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static AppExecutors instance;

    private final ExecutorService diskIO;
    private final Handler mainThreadHandler;

    private AppExecutors() {
        this.diskIO = Executors.newFixedThreadPool(4);
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized AppExecutors getInstance() {
        if (instance == null) {
            instance = new AppExecutors();
        }
        return instance;
    }

    public ExecutorService diskIO() {
        return diskIO;
    }

    public void executeOnDiskIO(@NonNull Runnable runnable) {
        diskIO.execute(runnable);
    }

    public void postToMainThread(@NonNull Runnable runnable) {
        mainThreadHandler.post(runnable);
    }
}
