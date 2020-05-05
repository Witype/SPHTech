package com.witype.Dragger.mvp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.witype.Dragger.R;
import com.witype.Dragger.di.component.AppComponent;
import com.witype.Dragger.di.component.DaggerHomeComponent;
import com.witype.Dragger.mvp.BaseActivity;
import com.witype.Dragger.mvp.contract.HomeView;
import com.witype.Dragger.mvp.present.HomePresenter;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

//import com.witype.Dragger.di.component.DaggerHomeComponent;

//import com.witype.Dragger.mvp.present.IHomePresenter;

public class MainActivity extends BaseActivity<HomePresenter> implements HomeView , SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    QuarterAdapter quarterAdapter;

    AlertDialog alertDialog;

    @Override
    protected void setupComponent(@NonNull AppComponent appComponent) {
        DaggerHomeComponent.builder().view(this).appComponent(appComponent).build().inject(this);
    }

    @Override
    public int getResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        super.initView();
        setupRecycler();
        onRefresh();
    }

    @Override
    public void onGetMobileDataUsage() {

    }

    @Override
    public void onGetDataError(String message) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        String msg = getString(R.string.tip_occurred);
        msg = String.format("%s : %s",msg,message);
        alertDialog = new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(R.string.dialog_case_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onRefresh();
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void setupRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setAdapter(quarterAdapter);
    }

    @Override
    public void onRefresh() {
        getPresenter().getMobileDataUsage(100);
    }

    @Override
    public void showLoading() {
        //Activity 自己处理loading因此不需要调用父类的处理逻辑
//        super.showLoading();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void dismissLoading() {
//        super.dismissLoading();
        swipeRefreshLayout.setRefreshing(false);
    }

}
