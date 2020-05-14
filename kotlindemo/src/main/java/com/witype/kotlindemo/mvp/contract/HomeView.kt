package com.witype.kotlindemo.mvp.contract

interface HomeView : IBaseView {

    fun onGetMobileDataUsage()

    fun onGetDataError(message: String?)

}