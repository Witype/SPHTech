package com.witype.kotlindemo.entity

class BaseHttpResponseEntity<T : Any> {
    var message: String? = null

    var code = 0

    var data: T? = null

}