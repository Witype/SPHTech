package com.witype.kotlindemo.app

import com.witype.kotlindemo.BuildConfig
import com.witype.mvp.app.MApplication
import com.witype.mvp.di.module.ConfigModule
import com.witype.mvp.di.module.ConfigModule.RetrofitConfig
import okhttp3.HttpUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class KotlinDemoApplication : MApplication() {

    override fun getConfigModule(): ConfigModule {
        return ConfigModule.Builder()
                .httpUrl(HttpUrl.parse(BuildConfig.HOST))
                .retrofitConfig(object : RetrofitConfig {
                    override fun config(retrofitBuilder: Retrofit.Builder) {
                        retrofitBuilder.addConverterFactory(GsonConverterFactory.create())
                    }
                })
                .build()
    }

    override fun init() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}