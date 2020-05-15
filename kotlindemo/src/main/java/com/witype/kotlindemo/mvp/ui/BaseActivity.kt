package com.witype.kotlindemo.mvp.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.Unbinder
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import com.witype.kotlindemo.app.KotlinDemoApplication
import com.witype.kotlindemo.mvp.contract.IBaseView
import com.witype.kotlindemo.mvp.presenter.IBasePresenter
import com.witype.mvp.di.component.AppComponent
import javax.inject.Inject

abstract class BaseActivity<P : IBasePresenter> : RxAppCompatActivity(), IBaseView {

    @set:Inject
    internal var presenter: P? = null

    var bind: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupComponent((application as KotlinDemoApplication).getAppComponent())
        setContentView(resId)
        initView()
        if (presenter != null) {
            presenter?.onStart()
        }
        initData()
    }

    abstract val resId: Int

    open fun initView() {
        bind = ButterKnife.bind(this)
    }

    protected fun initData() {}

    protected abstract fun setupComponent(appComponent: AppComponent)

    override fun showToast(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    override fun showLoading(message: String) {}

    override fun showLoading() {}

    override fun dismissLoading() {}

    override fun showToast(resId: Int) {
        showToast(getString(resId))
    }

    override val activity: Activity
        get() = this

    override fun onStop() {
        super.onStop()
        presenter?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
        bind?.unbind()
    }
}