package com.witype.kotlindemo.mvp.presenter

import android.app.Activity
import com.trello.rxlifecycle3.LifecycleTransformer
import com.trello.rxlifecycle3.RxLifecycle
import com.trello.rxlifecycle3.android.ActivityEvent
import com.witype.kotlindemo.integration.HttpModel
import com.witype.kotlindemo.mvp.contract.IBaseView
import com.witype.mvp.integration.IRequestManager
import com.witype.mvp.integration.RetrofitRequestManager
import com.witype.mvp.integration.UIObservableTransformer
import com.witype.mvp.integration.scope.ActivityScope
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

/**
 * V 不是必须继承IBaseView，以适应Presenter在Service等其他模块中使用
 */
open class BasePresenter<V>(var view: V) : IBasePresenter, IBaseView {
    
    private var lifecycleSubject: BehaviorSubject<ActivityEvent> = BehaviorSubject.create()

    @set:Inject
    lateinit var model: HttpModel

    fun <T> bindUntilEvent(event: ActivityEvent): LifecycleTransformer<T> {
        Timber.tag(TAG).i("bindUntilEvent : %s", event.toString())
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

    fun <T> bindUIEvent(): UIObservableTransformer<T> {
        return object : UIObservableTransformer<T>() {
            override fun hasSubscribe() {
                showLoading()
            }

            override fun willComplete() {
                dismissLoading()
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