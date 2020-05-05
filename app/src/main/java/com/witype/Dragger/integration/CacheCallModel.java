package com.witype.Dragger.integration;

import com.witype.Dragger.entity.MobileDateUsageEntity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;

/**
 * Created by WiType on 2020/5/5.
 * Email witype716@gmail.com
 * Desc:
 */
public interface CacheCallModel {

    @LifeCache(duration = 30, timeUnit = TimeUnit.MINUTES)
    Observable<MobileDateUsageEntity> getMobileDataUsage(Observable<MobileDateUsageEntity> response);
}
