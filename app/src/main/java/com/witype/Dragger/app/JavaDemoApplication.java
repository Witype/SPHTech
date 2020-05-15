package com.witype.Dragger.app;

import com.witype.Dragger.BuildConfig;
import com.witype.mvp.app.MApplication;
import com.witype.mvp.di.module.ConfigModule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class JavaDemoApplication extends MApplication {

    private static final int CONNECT_TIME_OUT = 30;
    private static final int READ_TIME_OUT = 30;
    private static final int WRITE_TIME_OUT = 30;
    private static final int THREAD_ALIVE = 60;
    private static final String BASE_URL = BuildConfig.HOST;

    @NotNull
    @Override
    public ConfigModule getConfigModule() {
        return new ConfigModule.Builder()
                .httpUrl(HttpUrl.parse(BuildConfig.HOST))
                .okHttpConfig(new ConfigModule.OkHttpConfig() {
                    @Override
                    public void config(@Nullable OkHttpClient.Builder builder) {
                        builder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
                    }
                })
                .retrofitConfig(new ConfigModule.RetrofitConfig() {
                    @Override
                    public void config(@Nullable Retrofit.Builder retrofitBuilder) {
                        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
                    }
                })
                .build();
    }

    @Override
    public void init() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
