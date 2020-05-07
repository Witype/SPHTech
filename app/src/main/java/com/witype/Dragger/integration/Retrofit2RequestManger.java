package com.witype.Dragger.integration;

import com.witype.Dragger.BuildConfig;

import java.io.File;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Lazy;
import io.reactivex.annotations.NonNull;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit2RequestManger implements IRequestManager {

    @Inject
    Lazy<Retrofit> retrofitLazy;

    @Inject
    Lazy<RxCache> rxCacheLazy;

    @Inject
    public Retrofit2RequestManger() {
    }

    @Override
    @NonNull
    public synchronized <T> T create(Class<T> tClass) {
        return retrofitLazy.get().create(tClass);
    }

    @Override
    @NonNull
    public synchronized <T> T createCache(Class<T> tClass) {
        return rxCacheLazy.get().using(tClass);
    }

}
