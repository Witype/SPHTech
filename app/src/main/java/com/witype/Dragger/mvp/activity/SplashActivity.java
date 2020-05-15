package com.witype.Dragger.mvp.activity;

import android.content.Intent;

import com.witype.Dragger.R;
import com.witype.Dragger.mvp.BaseActivity;
import com.witype.mvp.di.component.AppComponent;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class SplashActivity extends BaseActivity {

    private Disposable subscribe;

    @Override
    public int getResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void setupComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public void initView() {
        super.initView();
        subscribe = Observable.timer(900, TimeUnit.MICROSECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        toMain();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void toMain() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
