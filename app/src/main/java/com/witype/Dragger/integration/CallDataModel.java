package com.witype.Dragger.integration;

import com.witype.Dragger.entity.MobileDateUsageEntity;
import com.witype.Dragger.mvp.contract.ICacheRequestModel;
import com.witype.Dragger.mvp.contract.IRequestModel;
import com.witype.Dragger.mvp.model.ApiModel;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class CallDataModel implements IRequestModel {

    @Inject
    IRequestManager requestManager;

    @Inject
    public CallDataModel() {
    }

    @Override
    public Observable<MobileDateUsageEntity> getMobileDataUsage(String resourceId, int limit) {
        return requestManager.createCache(ICacheRequestModel.class)
                .getMobileDataUsage(requestManager.create(ApiModel.class)
                        .getMobileDataUsage(resourceId, limit)
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io()));
    }
}
