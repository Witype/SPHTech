package com.witype.Dragger.integration.fastjson;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.witype.Dragger.BuildConfig;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Converter;

/**
 * Created by WiType on 2016/3/22 0022.
 */
public class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    public static final String TAG = "FastJsonResponse";

    private final Type type;

    public FastJsonResponseBodyConverter(Type type) {
        this.type = type;
    }

    /*
     * 转换方法
     */
    @Override
    public T convert(ResponseBody value) {
        BufferedSource bufferedSource = Okio.buffer(value.source());
        String tempStr = "";
        try {
            tempStr = bufferedSource.readUtf8();
            bufferedSource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Json:" + tempStr);
        }
        return JSON.parseObject(tempStr, type);
    }
}
