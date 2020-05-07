package com.witype.Dragger.di.module;

import com.witype.Dragger.BuildConfig;

import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@Module
public class ConfigModule {

    private HttpUrl httpUrl;

    private OkHttpConfig okHttpConfig;

    private RetrofitConfig retrofitConfig;

    public ConfigModule(Builder builder) {
        this.httpUrl = builder.httpUrl;
        this.okHttpConfig = builder.okHttpConfig;
        this.retrofitConfig = builder.retrofitConfig;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Singleton
    @Provides
    HttpUrl provideBaseUrl() {
        return httpUrl == null ? HttpUrl.parse(BuildConfig.HOST) : httpUrl;
    }

    @Singleton
    @Provides
    OkHttpConfig provideOkHttpConfig() {
        return okHttpConfig;
    }

    @Singleton
    @Provides
    RetrofitConfig provideRetrofitConfig() {
        return retrofitConfig;
    }

    /**
     * 配置Okhttp
     */
    public interface OkHttpConfig {
        void config(OkHttpClient.Builder builder);
    }

    /**
     * 配置Retrofit
     */
    public interface RetrofitConfig {
        void config(Retrofit.Builder retrofitBuilder);
    }

    public static class Builder {

        private HttpUrl httpUrl;

        private OkHttpConfig okHttpConfig;

        private RetrofitConfig retrofitConfig;

        public Builder() {
        }

        public Builder httpUrl(HttpUrl httpUrl) {
            this.httpUrl = httpUrl;
            return this;
        }

        public Builder okHttpConfig(OkHttpConfig okHttpConfig) {
            this.okHttpConfig = okHttpConfig;
            return this;
        }

        public Builder retrofitConfig(RetrofitConfig retrofitConfig) {
            this.retrofitConfig = retrofitConfig;
            return this;
        }

        public ConfigModule build() {
            return new ConfigModule(this);
        }
    }
}
