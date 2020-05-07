package com.witype.Dragger.integration.exception;

import androidx.annotation.Nullable;

public class ResponseException extends Exception {

    private String message;

    private int code;

    public ResponseException(@Nullable String message, int code) {
        super(message, null);
        this.message = message;
        this.code = code;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
