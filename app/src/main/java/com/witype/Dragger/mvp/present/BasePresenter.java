package com.witype.Dragger.mvp.present;

import android.app.Activity;

import com.witype.Dragger.integration.CallDataModel;
import com.witype.Dragger.mvp.contract.IBaseView;

import javax.inject.Inject;

public class BasePresenter<V extends IBaseView> implements IBasePresenter , IBaseView {

    protected V view;

    @Inject
    CallDataModel model;

    public BasePresenter(V view) {
        this.view = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void showToast(String message) {
        view.showToast(message);
    }

    @Override
    public void showLoading(String message) {
        view.showLoading(message);
    }

    @Override
    public void showLoading() {
        view.showLoading();
    }

    @Override
    public void dismissLoading() {
        view.dismissLoading();
    }

    @Override
    public void showToast(int resId) {
        view.showToast(resId);
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}
