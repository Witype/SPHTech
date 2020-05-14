package com.witype.mvp.app

import android.app.Application
import com.witype.mvp.di.component.AppComponent
import com.witype.mvp.di.component.DaggerAppComponent
import com.witype.mvp.di.module.ConfigModule

abstract class MApplication : Application() {

    private lateinit var appComponent : AppComponent;

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .application(this)
                .config(getConfigModule())
                .build()
        appComponent.inject(this)
    }

    fun getAppComponent() :AppComponent {
        return appComponent
    }

    abstract fun getConfigModule() : ConfigModule

    abstract fun init()
}