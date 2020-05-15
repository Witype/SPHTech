package com.witype.mvp.integration

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.exceptions.CompositeException
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 用户监听Observable的处理状态
 * [Observable.doOnSubscribe] 在被订阅之后处理对应逻辑 (do something when observable Subscribe,such as loading)
 * [Observable.doOnComplete] 在被订阅即将完成时处理相应逻辑 (do something when observable Complete,such as dismissLoading)
 * [Observable.doOnError] 处理请求中的一些异常情况,如网络错我。(do something when observable Error,such as network error,)
 * @param <T>
</T> */
abstract class UIObservableTransformer<T> : ObservableTransformer<T, T> {

    val TAG = "UIObservableTransformer";

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream
                .doOnSubscribe { hasSubscribe() }
                .doOnComplete() { willComplete() }
                .doOnError { t: Throwable ->
                    willComplete()
                    onError(t)
                 }
    }

    fun onError(e: Throwable) {
        Timber.tag(TAG).i("onError : %s", e.message)
        if (e is CompositeException) {
            val exceptions = e.exceptions
            if (exceptions.size > 0) {
                doThrowable(exceptions[0])
            } else {
                doThrowable(e.cause)
            }
        } else {
            doThrowable(e)
        }
    }

    /**
     * 只处理和网络有关的错误
     * only call when network error
     */
    open fun onNetworkError(t: Throwable) {
        Timber.tag(TAG).i("onNetworkError : %s", t.message)
    }

    /**
     * 只处理和http请求状态有关的错误
     * only call when httpException[HttpException],such as http response code 404,405,500
     */
    open fun onHttpError(message: String?, httpCode: Int) {
        Timber.tag(TAG).i("onHttpError message : %s , code : %s", message, httpCode)
    }

    private fun doThrowable(e: Throwable) {
        if (e is SocketTimeoutException ||
                e is ConnectException ||
                e is UnknownHostException) {
            onNetworkError(e)
        } else if (e is HttpException) {
            onHttpError(convertStatusCode(e), e.code())
        } else {
            onUnCatchError(e)
        }
    }

    open fun onUnCatchError(t:Throwable) {}

    /**
     * 根据Http Status Code判断错误类型
     * @param httpException
     * @return String
     * //todo string res
     */
    private fun convertStatusCode(httpException: HttpException): String {
        return when {
            httpException.code() == 500 -> {
                "服务器发生错误"
            }
            httpException.code() == 404 -> {
                "请求地址不存在"
            }
            httpException.code() == 403 -> {
                "请求被服务器拒绝"
            }
            httpException.code() == 307 -> {
                "请求被重定向到其他页面"
            }
            httpException.code() == 401 -> {
                "权限不足（401）"
            }
            else -> {
                httpException.message()
            }
        }
    }

    /**
     * 用于实现
     */
    abstract fun hasSubscribe()

    /**
     * 用于实现
     */
    abstract fun willComplete()
}