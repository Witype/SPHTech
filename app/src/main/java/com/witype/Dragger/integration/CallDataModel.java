package com.witype.Dragger.integration;

import com.witype.Dragger.entity.MobileDateUsageEntity;
import com.witype.Dragger.mvp.contract.ICacheRequestModel;
import com.witype.Dragger.mvp.contract.IRequestModel;
import com.witype.Dragger.mvp.model.ApiModel;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class CallDataModel implements IRequestModel {

    IRequestManager requestManager;

    @Inject
    public CallDataModel(IRequestManager requestManager) {
        this.requestManager = requestManager;
    }

    @Override
    public Observable<MobileDateUsageEntity> getMobileDataUsage(String resourceId, int limit) {
        return getRequestManager().createCache(ICacheRequestModel.class)
                .getMobileDataUsage(getRequestManager().create(ApiModel.class)
                        .getMobileDataUsage(resourceId, limit)
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                );
    }

    public IRequestManager getRequestManager() {
        return requestManager;
    }
}
