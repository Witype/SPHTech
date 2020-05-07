package com.witype.Dragger.di.module;

import com.witype.Dragger.integration.CallDataModel;
import com.witype.Dragger.integration.scope.ActivityScope;
import com.witype.Dragger.mvp.activity.QuarterAdapter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by WiType on 2020/5/5.
 * Email witype716@gmail.com
 * Desc:
 */
@Module
public class HomeModule {

    @Provides
    @ActivityScope
    QuarterAdapter provideAdapter() {
        return new QuarterAdapter();
    }
}
