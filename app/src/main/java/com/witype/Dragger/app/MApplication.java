package com.witype.Dragger.app;

import android.app.Application;

import com.witype.Dragger.BuildConfig;
import com.witype.Dragger.di.component.AppComponent;
import com.witype.Dragger.di.component.DaggerAppComponent;

import timber.log.Timber;

public class MApplication extends Application {

    protected AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);
        init();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private void init() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
