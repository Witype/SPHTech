package com.witype.Dragger.di.module;

import android.app.Application;

import com.witype.Dragger.integration.IRequestManager;
import com.witype.Dragger.integration.Retrofit2RequestManger;

import java.io.File;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public abstract class ClientModule {

    @Binds
    abstract IRequestManager provideRepositoryManager(Retrofit2RequestManger retrofit2RequestManger);

    @Singleton
    @Provides
    static RxCache provideRxCache(Application application) {
        return new RxCache.Builder().persistence(application.getCacheDir(), new GsonSpeaker());
    }

    @Singleton
    @Provides
    static Retrofit provideRetrofit(HttpUrl url, OkHttpClient okHttpClient, ConfigModule.RetrofitConfig retrofitConfig) {
        Retrofit.Builder retrofitBuild = new Retrofit.Builder().baseUrl(url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        if (retrofitConfig != null) {
            retrofitConfig.config(retrofitBuild);
        }
        return retrofitBuild.build();
    }

    @Singleton
    @Provides
    static OkHttpClient provideOkHttpClient(ConfigModule.OkHttpConfig okHttpConfig,ThreadPoolExecutor httpExecutor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .dispatcher(new Dispatcher(httpExecutor));
        if (okHttpConfig != null) {
            okHttpConfig.config(builder);
        }
        return builder.build();
    }

    @Singleton
    @Provides
    static ThreadPoolExecutor provideThreadPoolExecutor() {
        ThreadFactory threadFactory = runnable -> {
            Thread result = new Thread(runnable, "http");
            result.setDaemon(false);
            return result;
        };
        //ensure execute http request sync,
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactory);
    }

}
