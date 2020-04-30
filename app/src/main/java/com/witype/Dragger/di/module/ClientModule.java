package com.witype.Dragger.di.module;

import com.witype.Dragger.integration.CallDataModel;
import com.witype.Dragger.integration.IRequestManager;
import com.witype.Dragger.integration.Retrofit2RequestManger;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class ClientModule {

    @Binds
    abstract IRequestManager bindRepositoryManager(Retrofit2RequestManger repositoryManager);

    @Singleton
    @Provides
    static CallDataModel provideModel() {
        return new CallDataModel();
    }

}
