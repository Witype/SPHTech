package com.witype.Dragger.mvp.model;

import com.witype.Dragger.entity.MobileDateUsageEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiModel {

    @GET(Api.datastoreSearch)
    Observable<MobileDateUsageEntity> getMobileDataUsage(@Query("resource_id") String resourceId, @Query("limit") int limit);
}
