package com.witype.mvp.di.module

import android.app.Application
import com.witype.mvp.integration.IRequestManager
import com.witype.mvp.integration.RetrofitRequestManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.rx_cache2.internal.RxCache
import io.victoralbertos.jolyglot.GsonSpeaker
import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ClientModule {

    @Singleton
    @Provides
    fun getRequestManager(retrofit: Retrofit, rxCache: RxCache): RetrofitRequestManager {
        return RetrofitRequestManager(retrofit, rxCache)
    }

    @Singleton
    @Provides
    @Named("cacheFile")
    fun provideCacheFile(application: Application): File {
        return application.cacheDir;
    }

    @Singleton
    @Provides
    fun provideRxCache(@Named("cacheFile") file: File): RxCache {
        return RxCache.Builder().persistence(file, GsonSpeaker())
    }

    @Singleton
    @Provides
    fun provideThreadPoolExecutor(): ThreadPoolExecutor {
        val threadFactory = ThreadFactory { runnable: Runnable ->
            val result = Thread(runnable, "http")
            result.isDaemon = false
            result
        }
        return ThreadPoolExecutor(0, Integer.MAX_VALUE, 60,TimeUnit.SECONDS, SynchronousQueue(), threadFactory)
    }

    @Singleton
    @Provides
    fun provideRetrofit(url: HttpUrl?, okHttpClient: OkHttpClient, retrofitConfig: ConfigModule.RetrofitConfig?): Retrofit {
        val retrofitBuild = Retrofit.Builder().baseUrl(url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        retrofitConfig?.config(retrofitBuild)
        return retrofitBuild.build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(okHttpConfig: ConfigModule.OkHttpConfig?, httpExecutor: ThreadPoolExecutor): OkHttpClient {
        val builder = OkHttpClient.Builder().dispatcher(Dispatcher(httpExecutor))
        okHttpConfig?.config(builder)
        return builder.build()
    }
}