package com.witype.kotlindemo.mvp.contract

import com.witype.kotlindemo.entity.MobileDateUsageEntity
import io.reactivex.Observable
import io.rx_cache2.LifeCache
import java.util.concurrent.TimeUnit

/**
 * 如果接口需要被缓存，则需要在这里定义接口，
 * Created by WiType on 2020/5/5.
 * Email witype716@gmail.com
 * Desc:
 */
interface ICacheRequestModel {

    @LifeCache(duration = 5, timeUnit = TimeUnit.MINUTES)
    fun getMobileDataUsage(response: Observable<MobileDateUsageEntity>): Observable<MobileDateUsageEntity>
}