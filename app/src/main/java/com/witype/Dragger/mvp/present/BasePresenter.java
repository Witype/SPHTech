package com.witype.Dragger.mvp.present;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.RxLifecycle;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.witype.Dragger.integration.CallDataModel;
import com.witype.Dragger.integration.UIObservableTransformer;
import com.witype.Dragger.mvp.contract.IBaseView;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

public class BasePresenter<V> implements IBasePresenter, IBaseView {

    public static final String TAG = "Presenter";

    /**
     * V 不是必须继承IBaseView，以适应Presenter在Service等其他模块中使用
     */
    protected V view;

    private BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    private UIObservableTransformer observableTransformer;

    @Inject
    CallDataModel model;

    public BasePresenter(V view) {
        this.view = view;
    }

    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        Timber.tag(TAG).i("bindUntilEvent : %s", event.toString());
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    public final <T> ObservableTransformer<T, T> bindUIEvent() {
        if (observableTransformer == null) {
            observableTransformer = new UIObservableTransformer() {
                @Override
                public void hasSubscribe() {
                    showLoading();
                }

                @Override
                public void willComplete() {
                    dismissLoading();
                }
            };
        }
        return observableTransformer;
    }

    @Override
    public void onStart() {
        Timber.tag(TAG).i("onStart");
    }

    @Override
    public void onStop() {
        Timber.tag(TAG).i("onStop");
        lifecycleSubject.onNext(ActivityEvent.STOP);
    }

    @Override
    public void onDestroy() {
        Timber.tag(TAG).i("onDestroy");
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        lifecycleSubject = null;
    }

    @Override
    public void showToast(String message) {
        if (view instanceof IBaseView) {
            ((IBaseView) view).showToast(message);
        }
    }

    @Override
    public void showLoading(String message) {
        if (view instanceof IBaseView) {
            ((IBaseView) view).showLoading(message);
        }
    }

    @Override
    public void showLoading() {
        if (view instanceof IBaseView) {
            ((IBaseView) view).showLoading();
        }
    }

    @Override
    public void dismissLoading() {
        if (view instanceof IBaseView) {
            ((IBaseView) view).dismissLoading();
        }
    }

    @Override
    public void showToast(int resId) {
        if (view instanceof IBaseView) {
            ((IBaseView) view).showToast(resId);
        }
    }

    @Override
    @Nullable
    public Activity getActivity() {
        if (view instanceof IBaseView) {
            return ((IBaseView) view).getActivity();
        }
        return null;
    }
}
