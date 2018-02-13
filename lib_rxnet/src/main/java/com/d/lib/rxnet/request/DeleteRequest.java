package com.d.lib.rxnet.request;

import com.d.lib.rxnet.api.RetrofitAPI;
import com.d.lib.rxnet.base.RetrofitClient;

import java.util.Map;

/**
 * Instance
 * Created by D on 2017/10/24.
 */
public class DeleteRequest extends HttpRequest<DeleteRequest> {

    public DeleteRequest(String url) {
        super(url);
    }

    public DeleteRequest(String url, Map<String, String> params) {
        super(url, params);
    }

    @Override
    protected void init() {
        observable = RetrofitClient.getInstance().create(RetrofitAPI.class).delete(url, params);
    }

    /**
     * New
     */
    public static class DeleteRequestF extends HttpRequestF<DeleteRequestF> {

        public DeleteRequestF(String url) {
            super(url);
        }

        public DeleteRequestF(String url, Map<String, String> params) {
            super(url, params);
        }

        @Override
        protected void init() {
            observable = RetrofitClient.getRetrofit(config).create(RetrofitAPI.class).delete(url, params);
        }
    }
}
