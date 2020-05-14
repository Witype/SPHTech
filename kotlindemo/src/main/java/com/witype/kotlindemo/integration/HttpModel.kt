package com.witype.kotlindemo.integration

import com.witype.kotlindemo.entity.MobileDateUsageEntity
import com.witype.kotlindemo.mvp.contract.ICacheRequestModel
import com.witype.kotlindemo.mvp.contract.IRequestModel
import com.witype.kotlindemo.mvp.model.ApiModel
import com.witype.mvp.integration.CallDataModel
import com.witype.mvp.integration.IRequestManager
import com.witype.mvp.integration.RetrofitRequestManager
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HttpModel @Inject constructor() : IRequestModel {

    @set:Inject
    lateinit var iRequestManager : RetrofitRequestManager

    override fun getMobileDataUsage(resourceId: String, limit: Int): Observable<MobileDateUsageEntity> {
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