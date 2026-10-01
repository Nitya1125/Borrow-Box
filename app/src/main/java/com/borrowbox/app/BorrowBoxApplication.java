package com.borrowbox.app;

import android.app.Application;

import com.borrowbox.app.data.DataManager;

public class BorrowBoxApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataManager.getInstance().init(this);
    }
}
