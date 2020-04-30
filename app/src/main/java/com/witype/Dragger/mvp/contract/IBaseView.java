package com.witype.Dragger.mvp.contract;

import android.app.Activity;

public interface IBaseView {

    void showToast(String message);

    void showLoading(String message);

    void showLoading();

    void dismissLoading();

    Activity getActivity();
}
