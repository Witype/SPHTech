package com.witype.kotlindemo.mvp.contract

import android.app.Activity

interface IBaseView {
    fun showToast(message: String)

    fun showToast(resId: Int)

    fun showLoading(message: String)

    fun showLoading()

    fun dismissLoading()

    val activity: Activity?
}