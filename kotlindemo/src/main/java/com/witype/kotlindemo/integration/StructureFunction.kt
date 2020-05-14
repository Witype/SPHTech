package com.witype.kotlindemo.integration

import com.witype.kotlindemo.entity.BaseHttpResponseEntity
import com.witype.kotlindemo.integration.exception.ResponseException
import io.reactivex.functions.Function

/**
 * 结构化请求结果转换，通常情况下http请求的返回数据都会基于一定的格式，如：{"code":20005,"message":"请重新登录."}
 * 为了简化不同情况下（code值）的处理逻辑，使用此方法[com.witype.kotlindemo.entity.BaseHttpResponseEntity]可以直接进行过滤
 *
 * @param <T>
</T> */
class StructureFunction<T : Any> : Function<BaseHttpResponseEntity<T>?, T?> {
    @Throws(Exception::class)
    override fun apply(entity: BaseHttpResponseEntity<T>): T? {
        if (entity.code != SUCCESS) {
            throw ResponseException(entity.message, entity.code)
        }
        return entity.data
    }

    companion object {
        const val SUCCESS = 0
    }
}