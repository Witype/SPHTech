package com.witype.kotlindemo.mvp.presenter

import android.app.Activity
import com.trello.rxlifecycle3.LifecycleTransformer
import com.trello.rxlifecycle3.android.ActivityEvent
import com.trello.rxlifecycle3.android.RxLifecycleAndroid
import com.witype.kotlindemo.integration.HttpModel
import com.witype.kotlindemo.integration.exception.ResponseException
import com.witype.kotlindemo.mvp.contract.IBaseView
import com.witype.mvp.integration.UIObservableTransformer
import io.reactivex.subjects.BehaviorSubject
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * V 不是必须继承IBaseView，以适应Presenter在Service等其他模块中使用
 * V unnecessary extension to [IBaseView],so presenter can compact other android component,such as service
 */
open class BasePresenter<V>(var view: V) : IBasePresenter, IBaseView {

    private val lifecycleSubject: BehaviorSubject<ActivityEvent> = BehaviorSubject.create()

    @set:Inject
    lateinit var model: HttpModel

    /**
     * 将请求和Activity生命周期绑定，当[onDestroy]调用时可以自动取消订阅[io.reactivex.disposables.Disposable.dispose]
     *
     * bind [io.reactivex.Observable] with Activity lifecycle, automatic disposse[io.reactivex.disposables.Disposable.dispose]
     * when called [onDestroy] or [onStop]
     */
    fun <T> bindUntilEvent(event: ActivityEvent): LifecycleTransformer<T>? {
        Timber.tag(TAG).i("bindUntilEvent : %s", event.toString())
        return RxLifecycleAndroid.bindActivity(lifecycleSubject)
    }

    fun <T> bindUIEvent(): UIObservableTransformer<T> {
        return object : UIObservableTransformer<T>() {
            override fun hasSubscribe() {
                showLoading()
            }

            override fun willComplete() {
                dismissLoading()
            }

            override fun onHttpError(message: String?, httpCode: Int) {
                super.onHttpError(message, httpCode)
                showToast("reponse error : $message($httpCode)")
            }

            override fun onNetworkError(t: Throwable) {
                super.onNetworkError(t)
                showToast("network error")
            }

            override fun onUnCatchError(t: Throwable) {
                super.onUnCatchError(t)
                if (t is HttpException) {
                    this@BasePresenter.onHttpError(convertStatusCode(t), t.code())
                }
            }
        }
    }

    /**
     * 只处理和http请求状态有关的错误
     * only call when httpException[HttpException],such as http response code 404,405,500
     */
    open fun onHttpError(message: String?, httpCode: Int) {
        Timber.tag(TAG).i("onHttpError message : %s , code : %s", message, httpCode)
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

    override fun onStart() {
        Timber.tag(TAG).i("onStart")
        lifecycleSubject.onNext(ActivityEvent.START)
    }

    override fun onStop() {
        Timber.tag(TAG).i("onStop")
        lifecycleSubject.onNext(ActivityEvent.STOP)
    }

    override fun onDestroy() {
        Timber.tag(TAG).i("onDestroy")
        lifecycleSubject.onNext(ActivityEvent.DESTROY)
    }

    override fun showToast(message: String) {
        if (view is IBaseView) {
            (view as IBaseView).showToast(message)
        }
    }

    override fun showLoading(message: String) {
        if (view is IBaseView) {
            (view as IBaseView).showLoading(message)
        }
    }

    override fun showLoading() {
        if (view is IBaseView) {
            (view as IBaseView).showLoading()
        }
    }

    override fun dismissLoading() {
        if (view is IBaseView) {
            (view as IBaseView).dismissLoading()
        }
    }

    override fun showToast(resId: Int) {
        if (view is IBaseView) {
            (view as IBaseView).showToast(resId)
        }
    }

    override val activity: Activity?
        get() = if (view is IBaseView) {
            val vi = view as IBaseView;
            vi.activity
        } else null

    companion object {
        const val TAG = "Presenter"
    }

}