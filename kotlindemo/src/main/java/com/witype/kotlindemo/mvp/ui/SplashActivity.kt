package com.witype.kotlindemo.mvp.ui

import android.content.Intent
import com.witype.kotlindemo.R
import com.witype.kotlindemo.mvp.presenter.IBasePresenter
import com.witype.mvp.di.component.AppComponent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity<IBasePresenter>() {

    private var subscribe: Disposable? = null

    override val resId: Int
        get() = R.layout.activity_splash

    override fun setupComponent(appComponent: AppComponent) {}

    override fun initView() {
        super.initView()
        subscribe = Observable.timer(900, TimeUnit.MICROSECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ toMain() }) { throwable -> throwable.printStackTrace() }
    }

    private fun toMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (subscribe != null) {
            subscribe!!.dispose()
        }
    }
}