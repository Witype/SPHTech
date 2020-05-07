package com.witype.Dragger.integration;

public interface IRequestManager {

    <T> T create(Class<T> tClass);

    <T> T createCache(Class<T> tClass);
}
