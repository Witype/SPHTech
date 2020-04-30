package com.witype.Dragger.di.component;

import com.witype.Dragger.integration.scope.ActivityScope;
import com.witype.Dragger.mvp.activity.MainActivity;
import com.witype.Dragger.mvp.contract.HomeView;

import dagger.BindsInstance;
import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class)
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
