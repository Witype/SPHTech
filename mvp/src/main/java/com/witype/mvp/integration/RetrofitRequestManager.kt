package com.witype.mvp.integration

import io.rx_cache2.internal.RxCache
import retrofit2.Retrofit
import javax.inject.Inject

class RetrofitRequestManager @Inject constructor(val retrofit2 : Retrofit, val rxCache: RxCache): IRequestManager {

    override fun <T : Any> create(clazz: Class<T>) : T {
        return retrofit2.create(clazz);
    }

    override fun <T : Any> createCache(clazz: Class<T>) : T {
        return rxCache.using(clazz);
    }
}