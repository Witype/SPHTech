package com.witype.kotlindemo.mvp.model

import com.witype.kotlindemo.entity.MobileDateUsageEntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiModel {

    @GET(Api.datastoreSearch)
    fun getMobileDataUsage(@Query("resource_id") resourceId: String, @Query("limit") limit: Int): Observable<MobileDateUsageEntity>

}