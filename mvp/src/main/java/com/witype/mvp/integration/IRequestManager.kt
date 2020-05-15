package com.witype.mvp.integration

interface IRequestManager {

    fun <T : Any> create(clazz: Class<T>) : T;

    fun <T : Any> createCache(clazz: Class<T>) : T;
}