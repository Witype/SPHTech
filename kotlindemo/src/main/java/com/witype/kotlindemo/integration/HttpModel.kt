package com.witype.kotlindemo.integration

import com.witype.kotlindemo.entity.MobileDateUsageEntity
import com.witype.kotlindemo.mvp.contract.ICacheRequestModel
import com.witype.kotlindemo.mvp.model.ApiModel
import com.witype.mvp.integration.RetrofitRequestManager
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

open class HttpModel @Inject constructor() {

    @set:Inject
    lateinit var iRequestManager : RetrofitRequestManager

    fun getMobileDataUsage(resourceId: String, limit: Int): Observable<MobileDateUsageEntity> {
        return getRequestManager().createCache(ICacheRequestModel::class.java)
                .getMobileDataUsage(getRequestManager().create(ApiModel::class.java)
                        .getMobileDataUsage(resourceId, limit)
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                )
    }

    fun getRequestManager() : RetrofitRequestManager {
        return iRequestManager;
    }
}