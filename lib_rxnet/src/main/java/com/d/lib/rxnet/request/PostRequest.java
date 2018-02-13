package com.d.lib.rxnet.request;

import com.d.lib.rxnet.api.RetrofitAPI;
import com.d.lib.rxnet.base.RetrofitClient;
import com.d.lib.rxnet.mode.MediaTypes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Instance
 * Created by D on 2017/10/24.
 */
public class PostRequest extends HttpRequest<PostRequest> {
    private Map<String, Object> forms = new LinkedHashMap<>();
    private RequestBody requestBody;
    private MediaType mediaType;
    private String content;

    public PostRequest(String url) {
        super(url);
    }

    public PostRequest(String url, Map<String, String> params) {
        super(url, params);
    }

    @Override
    protected void init() {
        if (forms != null && forms.size() > 0) {
            if (params != null && params.size() > 0) {
                Iterator<Map.Entry<String, String>> entryIterator = params.entrySet().iterator();
                Map.Entry<String, String> entry;
                while (entryIterator.hasNext()) {
                    entry = entryIterator.next();
                    if (entry != null) {
                        forms.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            observable = RetrofitClient.getInstance().create(RetrofitAPI.class).postForm(url, forms);
        }
        if (requestBody != null) {
            observable = RetrofitClient.getInstance().create(RetrofitAPI.class).postBody(url, requestBody);
            return;
        }
        if (content != null && mediaType != null) {
            requestBody = RequestBody.create(mediaType, content);
            observable = RetrofitClient.getInstance().create(RetrofitAPI.class).postBody(url, requestBody);
            return;
        }
        if (params != null && params.size() > 0) {
            observable = RetrofitClient.getInstance().create(RetrofitAPI.class).post(url, params);
            return;
        }
        observable = RetrofitClient.getInstance().create(RetrofitAPI.class).post(url);
    }

    public PostRequest addForm(String formKey, Object formValue) {
        if (formKey != null && formValue != null) {
            forms.put(formKey, formValue);
        }
        return this;
    }

    public PostRequest setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public PostRequest setString(String string) {
        this.content = string;
        this.mediaType = MediaTypes.TEXT_PLAIN_TYPE;
        return this;
    }

    public PostRequest setString(String string, MediaType mediaType) {
        this.content = string;
        this.mediaType = mediaType;
        return this;
    }

    public PostRequest setJson(String json) {
        this.content = json;
        this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    public PostRequest setJson(JSONObject jsonObject) {
        this.content = jsonObject.toString();
        this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    public PostRequest setJson(JSONArray jsonArray) {
        this.content = jsonArray.toString();
        this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
        return this;
    }

    /**
     * New
     */
    public static class PostRequestF extends HttpRequestF<PostRequestF> {
        private Map<String, Object> forms = new LinkedHashMap<>();
        private RequestBody requestBody;
        private MediaType mediaType;
        private String content;

        public PostRequestF(String url) {
            super(url);
        }

        public PostRequestF(String url, Map<String, String> params) {
            super(url, params);
        }

        @Override
        protected void init() {
            if (forms != null && forms.size() > 0) {
                if (params != null && params.size() > 0) {
                    Iterator<Map.Entry<String, String>> entryIterator = params.entrySet().iterator();
                    Map.Entry<String, String> entry;
                    while (entryIterator.hasNext()) {
                        entry = entryIterator.next();
                        if (entry != null) {
                            forms.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                observable = RetrofitClient.getRetrofit(config).create(RetrofitAPI.class).postForm(url, forms);
            }
            if (requestBody != null) {
                observable = RetrofitClient.getRetrofit(config).create(RetrofitAPI.class).postBody(url, requestBody);
                return;
            }
            if (content != null && mediaType != null) {
                requestBody = RequestBody.create(mediaType, content);
                observable = RetrofitClient.getRetrofit(config).create(RetrofitAPI.class).postBody(url, requestBody);
                return;
            }
            if (params != null && params.size() > 0) {
                observable = RetrofitClient.getRetrofit(config).create(RetrofitAPI.class).post(url, params);
                return;
            }
            observable = RetrofitClient.getRetrofit(config).create(RetrofitAPI.class).post(url);
        }

        public PostRequestF addForm(String formKey, Object formValue) {
            if (formKey != null && formValue != null) {
                forms.put(formKey, formValue);
            }
            return this;
        }

        public PostRequestF setRequestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public PostRequestF setString(String string) {
            this.content = string;
            this.mediaType = MediaTypes.TEXT_PLAIN_TYPE;
            return this;
        }

        public PostRequestF setString(String string, MediaType mediaType) {
            this.content = string;
            this.mediaType = mediaType;
            return this;
        }

        public PostRequestF setJson(String json) {
            this.content = json;
            this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
            return this;
        }

        public PostRequestF setJson(JSONObject jsonObject) {
            this.content = jsonObject.toString();
            this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
            return this;
        }

        public PostRequestF setJson(JSONArray jsonArray) {
            this.content = jsonArray.toString();
            this.mediaType = MediaTypes.APPLICATION_JSON_TYPE;
            return this;
        }
    }
}
