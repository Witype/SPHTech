package com.witype.Dragger.integration;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import io.rx_cache2.internal.RxCache;
import retrofit2.Retrofit;

public class Retrofit2RequestManger implements IRequestManager {

    Retrofit retrofit;

    RxCache rxCache;

    @Inject
    public Retrofit2RequestManger(Retrofit retrofit,RxCache rxCache) {
        this.retrofit = retrofit;
        this.rxCache = rxCache;
    }

    @Override
    @NonNull
    public synchronized <T> T create(Class<T> tClass) {
        return getRetrofit().create(tClass);
    }

    @Override
    @NonNull
    public synchronized <T> T createCache(Class<T> tClass) {
        return getRxCache().using(tClass);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public RxCache getRxCache() {
        return rxCache;
    }
}
