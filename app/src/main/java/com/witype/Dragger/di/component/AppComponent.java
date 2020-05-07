package com.witype.Dragger.di.component;

import android.app.Application;

import com.witype.Dragger.di.module.ClientModule;
import com.witype.Dragger.di.module.ConfigModule;
import com.witype.Dragger.integration.CallDataModel;
import com.witype.Dragger.integration.IRequestManager;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {ClientModule.class, ConfigModule.class})
public interface AppComponent {

    void inject(Application application);

    IRequestManager getRequestManager();

    CallDataModel getCallDataModel();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        Builder config(ConfigModule configModule);

        AppComponent build();
    }
}
