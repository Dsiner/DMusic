package com.d.lib.aster;

import android.content.Context;
import android.support.annotation.NonNull;

import com.d.lib.aster.base.AsterModule;
import com.d.lib.aster.base.AsterModule.Singleton;
import com.d.lib.aster.base.Config;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.integration.retrofit.RetrofitModule;
import com.d.lib.aster.request.IDownloadRequest;
import com.d.lib.aster.request.IHttpRequest;
import com.d.lib.aster.request.IPostRequest;
import com.d.lib.aster.request.IUploadRequest;

import retrofit2.Retrofit;

/**
 * Created by D on 2017/10/24.
 */
public class Aster {
    private volatile static AsterModule mAster;

    private static AsterModule getAster() {
        if (mAster == null) {
            synchronized (Aster.class) {
                if (mAster == null) {
                    mAster = RetrofitModule.factory();
                }
            }
        }
        return mAster;
    }

    public static void init(Context context, AsterModule module) {
        context = context.getApplicationContext();
        module.applyOptions(context, new Config.Builder(context));
        module.registerComponents(context, new AsterModule.Registry(module));
        mAster = module;
    }

    public static Config.Builder init() {
        return new Config.Builder();
    }

    public static Config.Builder init(@NonNull Context context) {
        return new Config.Builder(context);
    }

    public static Singleton getDefault() {
        return getAster().getDefault();
    }

    public static Retrofit getRetrofit() {
        return null;
    }

    private Aster() {
    }

    public static IHttpRequest get(String url) {
        return getAster().get(url);
    }

    public static IHttpRequest get(String url, Params params) {
        return getAster().get(url, params);
    }

    public static IPostRequest post(String url) {
        return getAster().post(url);
    }

    public static IPostRequest post(String url, Params params) {
        return getAster().post(url, params);
    }

    public static IHttpRequest head(String url, Params params) {
        return getAster().head(url, params);
    }

    public static IHttpRequest options(String url, Params params) {
        return getAster().options(url, params);
    }

    public static IHttpRequest put(String url, Params params) {
        return getAster().put(url, params);
    }

    public static IHttpRequest patch(String url, Params params) {
        return getAster().patch(url, params);
    }

    public static IHttpRequest delete(String url, Params params) {
        return getAster().delete(url, params);
    }

    public static IDownloadRequest download(String url) {
        return getAster().download(url);
    }

    public static IDownloadRequest download(String url, Params params) {
        return getAster().download(url, params);
    }

    public static IUploadRequest upload(String url) {
        return getAster().upload(url);
    }
}
