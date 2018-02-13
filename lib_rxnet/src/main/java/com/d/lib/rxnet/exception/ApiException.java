package com.d.lib.rxnet.exception;

import android.net.ParseException;

import com.d.lib.rxnet.mode.ApiCode;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

/**
 * API异常
 */
public class ApiException extends Exception {
    private final int code;
    private String message;

    public ApiException(String message) {
        this(-1, message);
    }

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ApiException(int code, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = cause.getMessage();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ApiException setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getDisplayMessage() {
        return "code: " + code + " message: " + message;
    }

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ApiException(ApiCode.Request.HTTP_ERROR, e);
            switch (httpException.code()) {
                case ApiCode.Http.UNAUTHORIZED:
                case ApiCode.Http.FORBIDDEN:
                case ApiCode.Http.NOT_FOUND:
                case ApiCode.Http.REQUEST_TIMEOUT:
                case ApiCode.Http.GATEWAY_TIMEOUT:
                case ApiCode.Http.INTERNAL_SERVER_ERROR:
                case ApiCode.Http.BAD_GATEWAY:
                case ApiCode.Http.SERVICE_UNAVAILABLE:
                default:
                    ex.message = "NETWORK_ERROR";
                    break;
            }
            return ex;
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            ex = new ApiException(ApiCode.Request.PARSE_ERROR, e);
            ex.message = "PARSE_ERROR";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(ApiCode.Request.NETWORK_ERROR, e);
            ex.message = "NETWORK_ERROR";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(ApiCode.Request.SSL_ERROR, e);
            ex.message = "SSL_ERROR";
            return ex;
        } else if (e instanceof SocketTimeoutException) {
            ex = new ApiException(ApiCode.Request.TIMEOUT_ERROR, e);
            ex.message = "TIMEOUT_ERROR";
            return ex;
        } else {
            ex = new ApiException(ApiCode.Request.UNKNOWN, e);
            ex.message = "UNKNOWN";
            return ex;
        }
    }
}
