package com.witype.Dragger.integration;

import com.witype.Dragger.entity.BaseHttpResponseEntity;
import com.witype.Dragger.integration.exception.ResponseException;

import io.reactivex.functions.Function;

/**
 * 结构化请求结果转换，通常情况下http请求的返回数据都会基于一定的格式，如：{"code":20005,"message":"请重新登录."}
 * 为了简化不同情况下（code值）的处理逻辑，使用此方法{@link BaseHttpResponseEntity}可以直接进行过滤
 *
 * @param <T>
 */
public class StructureFunction<T> implements Function<BaseHttpResponseEntity<T>,T> {

    public static final int SUCCESS = 0;

    @Override
    public T apply(BaseHttpResponseEntity<T> entity) throws Exception {
        if (entity == null) {
            throw new NullPointerException("response can not be null");
        } else if (entity.getCode() != SUCCESS) {
            throw new ResponseException(entity.getMessage(),entity.getCode());
        }
        return entity.getData();
    }

}
