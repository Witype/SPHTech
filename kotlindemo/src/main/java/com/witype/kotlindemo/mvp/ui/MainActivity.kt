package com.witype.kotlindemo.mvp.ui

import android.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import butterknife.BindView
import com.witype.kotlindemo.R
import com.witype.kotlindemo.di.component.DaggerHomeComponent
import com.witype.kotlindemo.mvp.contract.HomeView
import com.witype.kotlindemo.mvp.presenter.HomePresenter
import com.witype.mvp.di.component.AppComponent
import javax.inject.Inject

//import com.witype.Dragger.di.component.DaggerHomeComponent;
//import com.witype.Dragger.mvp.present.IHomePresenter;
class MainActivity : BaseActivity<HomePresenter>(), HomeView, OnRefreshListener {

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.refresh)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @set:Inject
    lateinit var quarterAdapter: QuarterAdapter

    var alertDialog: AlertDialog? = null

    override fun setupComponent(appComponent: AppComponent) {
        DaggerHomeComponent.builder().view(this).appComponent(appComponent).build().inject(this);
    }

    override val resId: Int get() = R.layout.activity_main

    override fun initView() {
        super.initView()
        setupRecycler()
        onRefresh()
    }

    override fun onGetMobileDataUsage() {}

    override fun onGetDataError(message: String?) {
        alertDialog?.dismiss()
        var msg = getString(R.string.tip_occurred)
        msg = String.format("%s : %s", msg, message)
        alertDialog = AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(R.string.dialog_case_retry) { dialogInterface, _ ->
                    onRefresh()
                    dialogInterface.dismiss()
                }
                .create()
        alertDialog?.show()
    }

    private fun setupRecycler() {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        swipeRefreshLayout.setOnRefreshListener(this)
        recyclerView.adapter = quarterAdapter
    }

    override fun onRefresh() {
        presenter!!.getMobileDataUsage(100)
    }

    override fun showLoading() {
        //Activity 自己处理loading因此不需要调用父类的处理逻辑
//        super.showLoading();
        swipeRefreshLayout.isRefreshing = true
    }

    override fun dismissLoading() {
//        super.dismissLoading();
        swipeRefreshLayout.isRefreshing = false
    }
}