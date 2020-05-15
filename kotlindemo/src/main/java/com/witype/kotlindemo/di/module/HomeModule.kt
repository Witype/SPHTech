package com.witype.kotlindemo.di.module

import com.witype.kotlindemo.mvp.ui.QuarterAdapter
import com.witype.mvp.integration.scope.ActivityScope
import dagger.Module
import dagger.Provides

/**
 * Created by WiType on 2020/5/5.
 * Email witype716@gmail.com
 * Desc:
 */
@Module
class HomeModule {

    @Provides
    @ActivityScope
    fun provideAdapter(): QuarterAdapter {
        return QuarterAdapter()
    }
}