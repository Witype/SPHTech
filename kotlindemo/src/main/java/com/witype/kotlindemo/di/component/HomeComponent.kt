package com.witype.kotlindemo.di.component

import com.witype.kotlindemo.di.module.HomeModule
import com.witype.kotlindemo.mvp.contract.HomeView
import com.witype.kotlindemo.mvp.ui.MainActivity
import com.witype.mvp.di.component.AppComponent
import com.witype.mvp.integration.scope.ActivityScope
import dagger.BindsInstance
import dagger.Component

@ActivityScope
@Component(modules = [HomeModule::class], dependencies = [AppComponent::class])
interface HomeComponent {
    fun inject(mainActivity: MainActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun view(view: HomeView): Builder
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): HomeComponent
    }
}