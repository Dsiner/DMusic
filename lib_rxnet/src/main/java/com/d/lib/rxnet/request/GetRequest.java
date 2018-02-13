package com.d.lib.rxnet.request;

import com.d.lib.rxnet.api.RetrofitAPI;
import com.d.lib.rxnet.base.RetrofitClient;

import java.util.Map;

/**
 * Instance
 * Created by D on 2017/10/24.
 */
public class GetRequest extends HttpRequest<GetRequest> {

    public GetRequest(String url) {
        super(url);
    }

    public GetRequest(String url, Map<String, String> params) {
        super(url, params);
    }

    @Override
    protected void init() {
        if (params == null) {
            observable = RetrofitClient.getInstance().create(RetrofitAPI.class).get(url);
        } else {
            observable = RetrofitClient.getInstance().create(RetrofitAPI.class).get(url, params);
        }
    }

    /**
     * New
     */
    public static class GetRequestF extends HttpRequestF<GetRequestF> {

        public GetRequestF(String url) {
            super(url);
        }

        public GetRequestF(String url, Map<String, String> params) {
            super(url, params);
        }

        @Override
        protected void init() {
            if (params == null) {
                observable = RetrofitClient.getRetrofit(config).create(RetrofitAPI.class).get(url);
            } else {
                observable = RetrofitClient.getRetrofit(config).create(RetrofitAPI.class).get(url, params);
            }
        }
    }
}
