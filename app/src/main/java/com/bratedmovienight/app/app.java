package com.bratedmovienight.app;

import android.app.Application;
import android.content.res.Configuration;

import com.bratedmovienight.data.Settings;
import com.squareup.leakcanary.LeakCanary;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        LeakCanary.install(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Settings.getInstance().reloadDataWhenConfigurationChanged();
    }
}