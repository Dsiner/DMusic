package com.d.lib.aster.integration.okhttp3;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;

import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.aster.scheduler.Observable;
import com.d.lib.aster.scheduler.callback.Task;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * OkHttpApi
 * Created by D on 2018/12/7.
 **/
public class OkHttpApi {
    private Imp mImp;

    public OkHttpApi(okhttp3.OkHttpClient mClient) {
        this.mImp = new Imp(mClient);
    }

    public Imp getImp() {
        return mImp;
    }

    public Observable<ResponseBody> get(String url, Params params) {
        return get(url + "?" + params.getRequestParamsString());
    }

    public Observable<ResponseBody> get(final String url) {
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().getImp(url).execute();
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }

    public Observable<ResponseBody> post(final String url) {
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().post(url);
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }

    public Observable<ResponseBody> post(final String url, final Params params) {
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().post(url, params);
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }

    public Observable<ResponseBody> postForm(final String url, final Map<String, Object> forms) {
        final FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : forms.entrySet()) {
            builder.add(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().postBody(url, builder.build());
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }

    public Observable<ResponseBody> postBody(final String url, final RequestBody requestBody) {
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().postBodyImp(url, requestBody).execute();
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }

    public Observable<ResponseBody> put(final String url, final Params params) {
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().putImp(url, params).execute();
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }

    public Observable<ResponseBody> patch(final String url, final Params params) {
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().putImp(url, params).execute();
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }

    public Observable<ResponseBody> options(final String url, final Params params) {
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().options(url, params);
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }

    public Observable<ResponseBody> head(final String url, final Params params) {
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().head(url, params);
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }

    public Observable<ResponseBody> delete(final String url, final Params params) {
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().delete(url, params);
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }

    public Observable<ResponseBody> download(final String url) {
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().downloadImp(url).execute();
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }

    public Observable<ResponseBody> download(final String url, final Params params) {
        return download(url + "?" + params.getRequestParamsString());
    }

    public Observable<ResponseBody> upload(final String url, final List<MultipartBody.Part> multipartBodyParts) {
        return Observable.create(new Task<ResponseBody>() {
            @Override
            public ResponseBody run() throws Exception {
                try {
                    Response response = getImp().uploadImp(url, multipartBodyParts).execute();
                    int code = response.code();
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException("Request error.");
                }
            }
        });
    }


    static class Imp {
        private okhttp3.OkHttpClient mClient;

        public Imp(OkHttpClient client) {
            this.mClient = client;
        }

        private Call patchImp(String url, Params params) {
            final FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            final Request request = new Request.Builder()
                    .url(url)
                    .patch(builder.build())
                    .build();
            return mClient.newCall(request);
        }

        private Call putImp(String url, Params params) {
            final FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            final Request request = new Request.Builder()
                    .url(url)
                    .put(builder.build())
                    .build();
            return mClient.newCall(request);
        }

        public void get(String url, Params params, SimpleCallback<Response> callback) {
            get(url + "?" + params.getRequestParamsString(), callback);
        }

        public void get(String url, final SimpleCallback<Response> callback) {
            enqueue(getImp(url), callback);
        }

        private Call getImp(String url) {
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            return mClient.newCall(request);
        }

        public Response post(String url) throws IOException {
            return postImp(url, null).execute();
        }

        public void post(String url, final SimpleCallback<Response> callback) {
            enqueue(postImp(url, null), callback);
        }

        public Response post(String url, Params params) throws IOException {
            return postImp(url, params).execute();
        }

        public void post(String url, Params params, final SimpleCallback<Response> callback) {
            enqueue(postImp(url, params), callback);
        }

        private Call postImp(String url, Params params) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.add(key, params.get(key));
                }
            }
            final Request request = new Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .build();
            return mClient.newCall(request);
        }

        public Response postBody(String url, RequestBody requestBody) throws IOException {
            return postBodyImp(url, requestBody).execute();
        }

        public void postBody(String url, RequestBody requestBody,
                             final SimpleCallback<Response> callback) {
            enqueue(postBodyImp(url, requestBody), callback);
        }

        private Call postBodyImp(String url, RequestBody requestBody) {
            final Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            return mClient.newCall(request);
        }

        public Response options(String url, Params params) throws IOException {
            return optionsImp(url, params).execute();
        }

        private Call optionsImp(String url, Params params) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.add(key, params.get(key));
                }
            }
            final Request request = new Request.Builder()
                    .url(url)
                    .method("OPTIONS", builder.build())
                    .build();
            return mClient.newCall(request);
        }

        public Response head(String url, Params params) throws IOException {
            return headImp(url, params).execute();
        }

        private Call headImp(String url, Params params) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.add(key, params.get(key));
                }
            }
            final Request request = new Request.Builder()
                    .url(url)
                    .method("HEAD", builder.build())
                    .build();
            return mClient.newCall(request);
        }

        public Response delete(String url, Params params) throws IOException {
            return deleteImp(url, params).execute();
        }

        private Call deleteImp(String url, Params params) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.add(key, params.get(key));
                }
            }
            final Request request = new Request.Builder()
                    .url(url)
                    .delete(builder.build())
                    .build();
            return mClient.newCall(request);
        }

        private Call downloadImp(String url) {
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            return mClient.newCall(request);
        }

        private Call uploadImp(String url, List<MultipartBody.Part> multipartBodyParts) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            for (MultipartBody.Part part : multipartBodyParts) {
                builder.addPart(part);
            }
            final Request request = new Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .build();
            return mClient.newCall(request);
        }

        private void enqueue(@NonNull Call call, final SimpleCallback<Response> callback) {
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (callback != null) {
                        callback.onSuccess(response);
                    }
                }
            });
        }


    }
}
