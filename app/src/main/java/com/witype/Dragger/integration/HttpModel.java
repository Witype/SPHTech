package com.witype.Dragger.integration;

import com.witype.Dragger.entity.MobileDateUsageEntity;
import com.witype.Dragger.mvp.contract.ICacheRequestModel;
import com.witype.Dragger.mvp.contract.IRequestModel;
import com.witype.Dragger.mvp.model.ApiModel;
import com.witype.mvp.integration.CallDataModel;
import com.witype.mvp.integration.IRequestManager;
import com.witype.mvp.integration.RetrofitRequestManager;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class HttpModel {

    @Inject
    RetrofitRequestManager retrofitRequestManager;

    @Inject
    public HttpModel() {
    }

    public Observable<MobileDateUsageEntity> getMobileDataUsage(String resourceId, int limit) {
        return retrofitRequestManager.createCache(ICacheRequestModel.class)
                .getMobileDataUsage(retrofitRequestManager.create(ApiModel.class)
                        .getMobileDataUsage(resourceId, limit)
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                );
    }

}
