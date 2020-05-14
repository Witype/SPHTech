package com.witype.kotlindemo.integration

import com.witype.kotlindemo.integration.exception.ResponseException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 统一处理Http请求结果，
 * 当OnError [HttpObserver.onError]时处理统一处理各类异常
 * 通常情况下会出现如：网络错误 [,] 等错误
 * 另外，接口定义的统一错误码也是常常出现的错误 [StructureFunction]
 * @param <T>
</T> */
abstract class HttpObserver<T : Any> : Observer<T> {

    override fun onSubscribe(d: Disposable) {
        Timber.tag(TAG).i("onSubscribe")
    }

    override fun onNext(t: T) {
        requireNotNull(t) { "response empty" }
        Timber.tag(TAG).i("onNext : %s", t.toString())
        try {
            onSuccess(t)
        } catch (e: Exception) {
            onError(e)
        }
    }

    @Throws(Exception::class)
    abstract fun onSuccess(t: T)

    fun onNetworkError(t: Throwable?) {
        Timber.tag(TAG).i("onNetworkError : %s", t?.toString() ?: "null")
    }

    open fun onHttpError(message: String?, httpCode: Int) {
        Timber.tag(TAG).i("onHttpError message : %s , code : %s", message, httpCode)
    }

    /**
     * 接口请求定义的错误类型，可以统一处理，也可以在请求接口处独立处理
     * @param message
     * @param code
     */
    fun onResponseError(message: String?, code: Int) {}

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

    private fun doThrowable(e: Throwable) {
        if (e is SocketTimeoutException ||
                e is ConnectException ||
                e is UnknownHostException) {
            onNetworkError(e)
        } else if (e is HttpException) {
            onHttpError(convertStatusCode(e), e.code())
        } else if (e is ResponseException) {
            onResponseError(e.message, e.code)
        } else {
            onResponseError(e.message, -1)
        }
    }

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

    override fun onComplete() {
        Timber.tag(TAG).i("onComplete")
    }

    companion object {
        const val TAG = "HttpObserver"
    }
}