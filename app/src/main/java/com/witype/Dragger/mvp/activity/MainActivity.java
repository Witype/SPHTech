package com.witype.Dragger.mvp.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.witype.Dragger.R;
import com.witype.Dragger.di.component.AppComponent;
import com.witype.Dragger.di.component.DaggerHomeComponent;
import com.witype.Dragger.mvp.BaseActivity;
import com.witype.Dragger.mvp.contract.HomeView;
import com.witype.Dragger.mvp.present.HomePresenter;

//import com.witype.Dragger.di.component.DaggerHomeComponent;

//import com.witype.Dragger.mvp.present.IHomePresenter;

public class MainActivity extends BaseActivity<HomePresenter> implements HomeView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPresenter().getMobileDataUsage(10);
    }

    @Override
    protected void setupComponent(@NonNull AppComponent appComponent) {
        DaggerHomeComponent.builder().view(this).appComponent(appComponent).build().inject(this);
    }

    @Override
    protected void initView() {
        super.initView();
        setupRecycler();
    }

    @Override
    public void onGetMobileDataUsage() {

    }

    private void setupRecycler() {

    }

}
