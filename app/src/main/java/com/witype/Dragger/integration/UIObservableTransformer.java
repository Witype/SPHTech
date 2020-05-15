package com.witype.Dragger.integration;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 用户监听Observable的处理状态
 * {@link Observable#doOnSubscribe(Consumer)} 在被订阅之后处理对应逻辑
 * {@link Observable#doOnComplete(Action)} 在被订阅即将完成时处理相应逻辑
 * @param <T>
 */
public abstract class UIObservableTransformer<T> implements ObservableTransformer<T, T> {

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
                .doOnSubscribe(disposable -> hasSubscribe())
                .doOnComplete(this::willComplete)
                .doOnError(throwable -> willComplete());
    }

    /**
     * 用于实现
     */
    public abstract void hasSubscribe();

    /**
     * 用于实现
     */
    public abstract void willComplete();
}
