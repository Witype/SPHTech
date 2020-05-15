package com.witype.kotlindemo.integration

import com.witype.kotlindemo.integration.exception.ResponseException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import timber.log.Timber

/**
 * 统一处理Http请求结果，
 * 当OnError [HttpObserver.onError]时处理统一处理各类异常
 * 通常情况下会出现如：网络错误 [,] 等错误
 * 另外，接口定义的统一错误码也是常常出现的错误 [StructureFunction]
 *
 * data automatic logic,you can focus you job with main process,
 */
abstract class HttpObserver<T> : Observer<T> {

    val TAG = "HttpObserver"

    override fun onSubscribe(d: Disposable) {
        Timber.tag(TAG).i("onSubscribe")
    }

    override fun onNext(t: T) {
        Timber.tag(TAG).i("onNext : %s", t.toString())
        try {
            onSuccess(t)
        } catch (e: Exception) {
            onError(e)
        }
    }

    @Throws(Exception::class)
    abstract fun onSuccess(t: T)

    /**
     * 接口请求定义的错误类型[ResponseException]，可以统一处理，也可以在请求接口处独立处理
     * handle[ResponseException],just form your owm logic
     * @param message
     * @param code
     */
    open fun onResponseError(message: String?, code: Int) {}

    override fun onError(e: Throwable) {
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
     * 这里只处理和接口有关的错误，如api正常请求情况返回的数据
     */
    private fun doThrowable(e: Throwable) {
        if (e is ResponseException) {
            onResponseError(e.message, e.code)
        }
    }


    override fun onComplete() {
        Timber.tag(TAG).i("onComplete")
    }

}