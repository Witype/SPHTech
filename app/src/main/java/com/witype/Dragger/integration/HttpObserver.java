package com.witype.Dragger.integration;

import androidx.annotation.Nullable;

import com.witype.Dragger.integration.exception.ResponseException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.observers.TestObserver;
import retrofit2.HttpException;
import timber.log.Timber;

/**
 * 统一处理Http请求结果，
 * 当OnError {@link HttpObserver#onError(Throwable)}时处理统一处理各类异常
 * 通常情况下会出现如：网络错误 {@link SocketTimeoutException,HttpException,UnknownHostException} 等错误
 * 另外，接口定义的统一错误码也是常常出现的错误 {@link StructureFunction}
 * @param <T>
 */
public abstract class HttpObserver<T> implements Observer<T> {

    public static final String TAG = "HttpObserver";

    @Override
    public void onSubscribe(Disposable d) {
        Timber.tag(TAG).i("onSubscribe");
    }

    @Override
    public void onNext(@Nullable T t) {
        Timber.tag(TAG).i("onNext : %s" , t == null ? "null" : t.toString());
        if (t == null) {
            throw new IllegalArgumentException("response empty");
        }
        try {
            onSuccess(t);
        } catch (Exception e) {
            onError(e);
        }
    }

    public abstract void onSuccess(T t) throws Exception;

    public void onNetworkError(Throwable t) {
        Timber.tag(TAG).i("onNetworkError : %s" , t == null ? "null" : t.toString());
    }

    public void onHttpError(String message,int httpCode) {
        Timber.tag(TAG).i("onHttpError message : %s , code : %s" , message ,httpCode);
    }

    /**
     * 接口请求定义的错误类型，可以统一处理，也可以在请求接口处独立处理
     * @param message
     * @param code
     */
    public void onResponseError(String message,int code) {

    }

    @Override
    public void onError(Throwable e) {
        Timber.tag(TAG).i("onError : %s" , e.getMessage());
        if (e instanceof CompositeException) {
            CompositeException exception = (CompositeException) e;
            List<Throwable> exceptions = exception.getExceptions();
            if (exceptions != null && exceptions.size() > 0) {
                doThrowable(exceptions.get(0));
            } else {
                doThrowable(exception.getCause());
            }
        } else {
            doThrowable(e);
        }
    }

    private void doThrowable(Throwable e) {
        if (e instanceof SocketTimeoutException ||
                e instanceof ConnectException ||
                e instanceof UnknownHostException) {
            onNetworkError(e);
        } else if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            onHttpError(convertStatusCode(exception),exception.code());
        } else if (e instanceof ResponseException) {
            ResponseException exception = (ResponseException) e;
            onResponseError(exception.getMessage(),exception.getCode());
        } else {
            onResponseError(e.getMessage(),-1);
        }
    }

    /**
     * 根据Http Status Code判断错误类型
     * @param httpException
     * @return String
     * //todo string res
     */
    private String convertStatusCode(HttpException httpException) {
        String msg;
        if (httpException.code() == 500) {
            msg = "服务器发生错误";
        } else if (httpException.code() == 404) {
            msg = "请求地址不存在";
        } else if (httpException.code() == 403) {
            msg = "请求被服务器拒绝";
        } else if (httpException.code() == 307) {
            msg = "请求被重定向到其他页面";
        } else if (httpException.code() == 401) {
            msg = "权限不足（401）";
        } else {
            msg = httpException.message();
        }
        //more check
        return msg;
    }

    @Override
    public void onComplete() {
        Timber.tag(TAG).i("onComplete");
    }
}
