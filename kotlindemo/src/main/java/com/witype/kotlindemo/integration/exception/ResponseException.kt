package com.witype.kotlindemo.integration.exception

class ResponseException(override var message: String?, var code: Int) : Exception(message, null)