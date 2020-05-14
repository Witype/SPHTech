package com.witype.Dragger.di.component;

import com.witype.Dragger.di.module.HomeModule;
import com.witype.Dragger.mvp.activity.MainActivity;
import com.witype.Dragger.mvp.contract.HomeView;
import com.witype.mvp.di.component.AppComponent;
import com.witype.mvp.integration.scope.ActivityScope;

import dagger.BindsInstance;
import dagger.Component;

@ActivityScope
@Component(modules = {HomeModule.class} ,dependencies = AppComponent.class)
public interface HomeComponent {

    void inject(MainActivity mainActivity);

    @Component.Builder
    interface Builder {
        @BindsInstance
        HomeComponent.Builder view(HomeView view);

        HomeComponent.Builder appComponent(AppComponent appComponent);

        HomeComponent build();
    }

}
