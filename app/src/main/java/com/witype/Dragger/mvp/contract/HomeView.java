package com.witype.Dragger.mvp.contract;

public interface HomeView extends IBaseView {

    void onGetMobileDataUsage();

    void onGetDataError(String message);
}
