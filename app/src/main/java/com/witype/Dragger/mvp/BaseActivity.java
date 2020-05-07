package com.witype.Dragger.mvp;

import android.app.Activity;
import android.os.Bundle;

import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import com.witype.Dragger.app.MApplication;
import com.witype.Dragger.di.component.AppComponent;
import com.witype.Dragger.mvp.contract.IBaseView;
import com.witype.Dragger.mvp.present.IBasePresenter;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity<P extends IBasePresenter> extends RxAppCompatActivity implements IBaseView {

    @Inject
    P presenter;

    private Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(((MApplication) getApplication()).getAppComponent());
        setContentView(getResId());
        initView();
        if (presenter != null) {
            presenter.onStart();
        }
        initData();
    }

    public abstract int getResId();

    public void initView() {
        bind = ButterKnife.bind(this);
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
    public void showToast(int resId) {
        showToast(getString(resId));
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
        if (bind != null) {
            bind.unbind();
        }
    }
}
