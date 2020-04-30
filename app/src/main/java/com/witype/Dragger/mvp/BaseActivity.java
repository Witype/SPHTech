package com.witype.Dragger.mvp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.witype.Dragger.app.MApplication;
import com.witype.Dragger.di.component.AppComponent;
import com.witype.Dragger.mvp.contract.IBaseView;
import com.witype.Dragger.mvp.present.IBasePresenter;

import javax.inject.Inject;

public abstract class BaseActivity<P extends IBasePresenter> extends AppCompatActivity implements IBaseView {

    @Inject
    P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(((MApplication) getApplication()).getAppComponent());
        initView();
        if (presenter != null) {
            presenter.onStart();
        }
        initData();
    }

    protected void initView() {

    }

    protected void initData() {

    }

    @Nullable
    public P getPresenter() {
        return presenter;
    }

    protected abstract void setupComponent(@NonNull AppComponent appComponent);

    @Override
    public void showToast(String message) {

    }

    @Override
    public void showLoading(String message) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}
