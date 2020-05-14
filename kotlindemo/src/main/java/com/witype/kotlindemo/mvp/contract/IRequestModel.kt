package com.witype.kotlindemo.mvp.contract

import com.witype.kotlindemo.entity.MobileDateUsageEntity
import io.reactivex.Observable

interface IRequestModel {

    fun getMobileDataUsage(resourceId: String, limit: Int): Observable<MobileDateUsageEntity>
}