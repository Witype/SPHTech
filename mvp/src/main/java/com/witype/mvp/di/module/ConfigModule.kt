package com.witype.mvp.di.module

import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ConfigModule constructor(builder: Builder) {

    private var httpUrl: HttpUrl? = null

    private var okHttpConfig: OkHttpConfig? = null

    private var retrofitConfig: RetrofitConfig? = null

    init {
        httpUrl = builder.httpUrl;
        okHttpConfig = builder.okHttpConfig;
        retrofitConfig = builder.retrofitConfig;
    }

    fun builder(): Builder? {
        return Builder()
    }

    @Singleton
    @Provides
    fun provideBaseUrl(): HttpUrl? {
        return httpUrl
    }

    @Singleton
    @Provides
    fun provideOkHttpConfig(): OkHttpConfig? {
        return okHttpConfig
    }

    @Singleton
    @Provides
    fun provideRetrofitConfig(): RetrofitConfig? {
        return retrofitConfig
    }

    /**
     * 配置Okhttp
     */
    interface OkHttpConfig {
        fun config(builder: OkHttpClient.Builder)
    }

    /**
     * 配置Retrofit
     */
    interface RetrofitConfig {
        fun config(retrofitBuilder: Retrofit.Builder)
    }

    class Builder {
        var httpUrl: HttpUrl? = null

        var okHttpConfig: OkHttpConfig? = null

        var retrofitConfig: RetrofitConfig? = null

        fun httpUrl(httpUrl: HttpUrl?): Builder {
            this.httpUrl = httpUrl
            return this
        }

        fun okHttpConfig(okHttpConfig: OkHttpConfig?): Builder {
            this.okHttpConfig = okHttpConfig
            return this
        }

        fun retrofitConfig(retrofitConfig: RetrofitConfig?): Builder {
            this.retrofitConfig = retrofitConfig
            return this
        }

        fun build(): ConfigModule {
            return ConfigModule(this)
        }
    }
}