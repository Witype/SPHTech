package com.witype.mvp.di.component

import android.app.Application
import com.witype.mvp.di.module.ClientModule
import com.witype.mvp.di.module.ConfigModule
import com.witype.mvp.integration.IRequestManager
import com.witype.mvp.integration.RetrofitRequestManager
import dagger.BindsInstance
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = [ClientModule::class, ConfigModule::class])
interface AppComponent {

    fun inject(application: Application)

    fun IRequestManager() : RetrofitRequestManager

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application) : Builder

        fun config(configModule: ConfigModule) : Builder

        fun build() : AppComponent

    }
}