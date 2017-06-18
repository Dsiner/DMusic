package com.d.music.module.skin;

import android.content.Context;

import com.d.music.commen.Preferences;
import com.d.music.module.global.MusicCst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.feng.skin.manager.listener.ILoaderListener;
import cn.feng.skin.manager.loader.SkinManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * SkinUtil
 * Created by D on 2017/6/17.
 */
public class SkinUtil {
    public static void restoreDefaultTheme() {
        SkinManager.getInstance().restoreDefaultTheme();
    }

    /**
     * skin init
     */
    public static void initSkin(final Context context) {
        SkinManager.getInstance().init(context.getApplicationContext());
        final Preferences p = Preferences.getInstance(context.getApplicationContext());
        if (p.getSkinLoaded()) {
            SkinManager.getInstance().load();
            return;
        }
        final String root = getRoot(context);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                InputStream in = null;
                FileOutputStream out = null;
                int success = 0;
                for (int i = 0; i < MusicCst.SKIN_COUNT; i++) {
                    String name = MusicCst.SKIN_NAME + i + MusicCst.SKIN_NAME_POSTFIX;
                    File file = new File(root + "/" + name);
                    if (!file.exists()) {
                        try {
                            in = context.getApplicationContext().getAssets().open(name);//从assets目录下复制
                            out = new FileOutputStream(file);
                            int length = -1;
                            byte[] buf = new byte[1024];
                            while ((length = in.read(buf)) != -1) {
                                out.write(buf, 0, length);
                            }
                            out.flush();
                            success++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (out != null) out.close();
                                if (in != null) in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        success++;
                    }
                }
                emitter.onNext(success == MusicCst.SKIN_COUNT);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean success) throws Exception {
                        if (success) {
                            p.putSkinLoaded(true);
                        }
                        load(context, p.getSkin());
                    }
                });
    }

    public static void load(final Context context, final int type) {
        load(context, type, null);
    }

    public static void load(final Context context, final int type, final ILoaderListener callback) {
        if (type >= 0 && type < MusicCst.SKIN_COUNT) {
            String root = getRoot(context);
            String skin = root + "/" + MusicCst.SKIN_NAME + type + MusicCst.SKIN_NAME_POSTFIX;
            File file = new File(skin);
            if (!file.exists()) {
                SkinManager.getInstance().load();
                if (callback != null) {
                    callback.onSuccess();
                }
                return;
            }
            SkinManager.getInstance().load(skin, new ILoaderListener() {
                @Override
                public void onStart() {
                    if (callback != null) {
                        callback.onStart();
                    }
                }

                @Override
                public void onSuccess() {
                    Preferences.getInstance(context).putSkin(type);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailed() {
                    if (callback != null) {
                        callback.onFailed();
                    }
                }
            });
        } else {
            SkinManager.getInstance().restoreDefaultTheme();
            if (callback != null) {
                callback.onSuccess();
            }
        }
    }

    private static String getRoot(Context context) {
        return context.getApplicationContext().getFilesDir().getAbsolutePath();
    }
}
