package com.witype.Dragger.integration;

import com.witype.Dragger.entity.MobileDateUsageEntity;
import com.witype.Dragger.mvp.model.ApiModel;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class CallDataModel {

    @Inject
    Retrofit2RequestManger requestManager;

    @Inject
    public CallDataModel() {

    }

    public Observable<MobileDateUsageEntity> getMobileDataUsage(String resourceId, int limit) {
        return requestManager.create(ApiModel.class)
                .getMobileDataUsage(resourceId, limit)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());
    }
}
