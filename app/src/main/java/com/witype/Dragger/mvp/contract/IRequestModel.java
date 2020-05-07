package com.witype.Dragger.mvp.contract;

import com.witype.Dragger.entity.MobileDateUsageEntity;

import io.reactivex.Observable;

public interface IRequestModel {

    Observable<MobileDateUsageEntity> getMobileDataUsage(String resourceId, int limit);
}
