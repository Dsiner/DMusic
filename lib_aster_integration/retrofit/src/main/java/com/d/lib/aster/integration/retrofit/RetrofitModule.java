package com.d.lib.aster.integration.retrofit;

import android.content.Context;
import android.support.annotation.NonNull;

import com.d.lib.aster.base.AsterModule;
import com.d.lib.aster.base.Config;
import com.d.lib.aster.base.IClient;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.integration.retrofit.request.DeleteRequest;
import com.d.lib.aster.integration.retrofit.request.DownloadRequest;
import com.d.lib.aster.integration.retrofit.request.GetRequest;
import com.d.lib.aster.integration.retrofit.request.HeadRequest;
import com.d.lib.aster.integration.retrofit.request.OptionRequest;
import com.d.lib.aster.integration.retrofit.request.PatchRequest;
import com.d.lib.aster.integration.retrofit.request.PostRequest;
import com.d.lib.aster.integration.retrofit.request.PutRequest;
import com.d.lib.aster.integration.retrofit.request.UploadRequest;

import retrofit2.Retrofit;

/**
 * Created by D on 2017/10/24.
 */
public class RetrofitModule extends AsterModule {

    public static RetrofitModule factory() {
        return new RetrofitModule();
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull Config.Builder builder) {
        builder.build();
    }

    @Override
    public Singleton getDefault() {
        return Default.INSTANCE;
    }

    public Retrofit getRetrofit() {
        return RetrofitClient.getDefault(IClient.TYPE_NORMAL).getClient();
    }

    public GetRequest get(String url) {
        return new GetRequest(url);
    }

    public GetRequest get(String url, Params params) {
        return new GetRequest(url, params);
    }

    public PostRequest post(String url) {
        return new PostRequest(url);
    }

    public PostRequest post(String url, Params params) {
        return new PostRequest(url, params);
    }

    public HeadRequest head(String url, Params params) {
        return new HeadRequest(url, params);
    }

    public OptionRequest options(String url, Params params) {
        return new OptionRequest(url, params);
    }

    public PutRequest put(String url, Params params) {
        return new PutRequest(url, params);
    }

    public PatchRequest patch(String url, Params params) {
        return new PatchRequest(url, params);
    }

    public DeleteRequest delete(String url, Params params) {
        return new DeleteRequest(url, params);
    }

    public DownloadRequest download(String url) {
        return new DownloadRequest(url);
    }

    public DownloadRequest download(String url, Params params) {
        return new DownloadRequest(url, params);
    }

    public UploadRequest upload(String url) {
        return new UploadRequest(url);
    }

    private static class Default {
        private final static Singleton INSTANCE = new Singleton() {

            @Override
            public GetRequest.Singleton get(String url) {
                return new GetRequest.Singleton(url);
            }

            @Override
            public GetRequest.Singleton get(String url, Params params) {
                return new GetRequest.Singleton(url, params);
            }

            @Override
            public PostRequest.Singleton post(String url) {
                return new PostRequest.Singleton(url);
            }

            @Override
            public PostRequest.Singleton post(String url, Params params) {
                return new PostRequest.Singleton(url, params);
            }

            @Override
            public HeadRequest.Singleton head(String url, Params params) {
                return new HeadRequest.Singleton(url, params);
            }

            @Override
            public OptionRequest.Singleton options(String url, Params params) {
                return new OptionRequest.Singleton(url, params);
            }

            @Override
            public PutRequest.Singleton put(String url, Params params) {
                return new PutRequest.Singleton(url, params);
            }

            @Override
            public PatchRequest.Singleton patch(String url, Params params) {
                return new PatchRequest.Singleton(url, params);
            }

            @Override
            public DeleteRequest.Singleton delete(String url, Params params) {
                return new DeleteRequest.Singleton(url, params);
            }

            @Override
            public DownloadRequest.Singleton download(String url) {
                return new DownloadRequest.Singleton(url);
            }

            @Override
            public DownloadRequest.Singleton download(String url, Params params) {
                return new DownloadRequest.Singleton(url, params);
            }

            @Override
            public UploadRequest.Singleton upload(String url) {
                return new UploadRequest.Singleton(url);
            }
        };
    }
}
