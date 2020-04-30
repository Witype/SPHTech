package com.witype.Dragger.mvp.present;

import com.witype.Dragger.integration.CallDataModel;
import com.witype.Dragger.mvp.contract.IBaseView;

import javax.inject.Inject;

public class BasePresenter<V extends IBaseView> implements IBasePresenter {

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
}
