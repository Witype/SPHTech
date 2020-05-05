package com.witype.Dragger.integration;

import com.witype.Dragger.BuildConfig;

import java.io.File;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit2RequestManger implements IRequestManager {

    private static final int CONNECT_TIME_OUT = 30;
    private static final int READ_TIME_OUT = 30;
    private static final int WRITE_TIME_OUT = 30;
    private static final int THREAD_ALIVE = 60;
    private static final String BASE_URL = BuildConfig.HOST;

    private Retrofit mRetrofit;

    RxCache rxCache;

    @Inject
    public Retrofit2RequestManger() {
        mRetrofit = provideRetrofit(getRetrofitBuilder(), provideClient(getOkHttpClientBuilder()));
        rxCache = new RxCache.Builder().persistence(new File(FileManager.getInstance().getFileCacheDir()), new GsonSpeaker());
    }

    @Override
    @NonNull
    public synchronized <T> T create(Class<T> tClass) {
        return mRetrofit.create(tClass);
    }

    @NonNull
    public synchronized <T> T createCache(Class<T> tClass) {
        return rxCache.using(tClass);
    }

    private Retrofit provideRetrofit(Retrofit.Builder retrofitBuild, OkHttpClient okHttpClient) {
        return retrofitBuild.baseUrl(BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private OkHttpClient provideClient(OkHttpClient.Builder builder) {
        ThreadFactory threadFactory = runnable -> {
            Thread result = new Thread(runnable, "http");
            result.setDaemon(false);
            return result;
        };
        //ensure execute http request sync,
        ThreadPoolExecutor httpExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, THREAD_ALIVE, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactory);
        builder
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .dispatcher(new Dispatcher(httpExecutor));
        return builder.build();
    }

    private Retrofit.Builder getRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    private OkHttpClient.Builder getOkHttpClientBuilder() {
        return new OkHttpClient.Builder();
    }
}
