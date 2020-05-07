package com.witype.Dragger.app;

import android.app.Application;

import com.witype.Dragger.BuildConfig;
import com.witype.Dragger.di.component.AppComponent;
import com.witype.Dragger.di.component.DaggerAppComponent;
import com.witype.Dragger.di.module.ConfigModule;

import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class MApplication extends Application {

    private static final int CONNECT_TIME_OUT = 30;
    private static final int READ_TIME_OUT = 30;
    private static final int WRITE_TIME_OUT = 30;
    private static final int THREAD_ALIVE = 60;
    private static final String BASE_URL = BuildConfig.HOST;

    protected AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .application(this)
                .config(getConfigModule())
                .build();
        appComponent.inject(this);
        init();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    /**
     * 获取配置器
     * @return {@link ConfigModule}
     */
    private ConfigModule getConfigModule() {
        return ConfigModule.builder()
                .httpUrl(HttpUrl.parse(BuildConfig.HOST))
                .okHttpConfig(new ConfigModule.OkHttpConfig() {
                    @Override
                    public void config(OkHttpClient.Builder builder) {
                        builder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
                    }
                })
                .retrofitConfig(new ConfigModule.RetrofitConfig() {
                    @Override
                    public void config(Retrofit.Builder retrofitBuilder) {
                        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
                    }
                })
                .build();
    }

    private void init() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
