package com.witype.mvp.integration

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable

/**
 * 用户监听Observable的处理状态
 * [Observable.doOnSubscribe] 在被订阅之后处理对应逻辑
 * [Observable.doOnComplete] 在被订阅即将完成时处理相应逻辑
 * @param <T>
</T> */
abstract class UIObservableTransformer<T> : ObservableTransformer<T, T> {
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream
                .doOnSubscribe { hasSubscribe() }
                .doFinally { willComplete() }
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