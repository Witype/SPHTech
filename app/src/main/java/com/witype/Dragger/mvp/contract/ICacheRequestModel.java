package com.witype.Dragger.mvp.contract;

import com.witype.Dragger.entity.MobileDateUsageEntity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;

/**
 * 如果接口需要被缓存，则需要在这里定义接口，
 * Created by WiType on 2020/5/5.
 * Email witype716@gmail.com
 * Desc:
 */
public interface ICacheRequestModel {

    @LifeCache(duration = 30, timeUnit = TimeUnit.MINUTES)
    Observable<MobileDateUsageEntity> getMobileDataUsage(Observable<MobileDateUsageEntity> response);
}
