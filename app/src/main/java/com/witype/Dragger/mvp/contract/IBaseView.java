package com.witype.Dragger.mvp.contract;

import android.app.Activity;

public interface IBaseView {

    void showToast(String message);

    void showToast(int resId);

    void showLoading(String message);

    void showLoading();

    void dismissLoading();

    Activity getActivity();
}
