package com.d.music.component.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.d.lib.common.utils.log.ULog;
import com.d.music.App;
import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.component.media.controler.MediaControler;
import com.d.music.data.Constants;
import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.db.AppDB;
import com.d.music.data.database.greendao.util.AppDBUtil;
import com.d.music.data.preferences.Preferences;
import com.d.music.event.eventbus.MusicInfoEvent;
import com.d.music.play.activity.PlayActivity;
import com.d.music.setting.activity.ModeActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * NotificationService
 * Created by D on 2017/4/29.
 */
public class NotificationService extends Service {
    private static final String NOTIFICATION_ID = "com.d.music";
    private static final String NOTIFICATION_NAME = "NotificationService";
    private static final int NOTIFICATION_UNIQUE_ID = 6671;

    private static boolean mIsRunning;

    private MediaControler mControl;
    private MusicBinder mBinder;
    private NotificationManager mManager;

    private ControlBroadcast mBroadcast;

    public static boolean isRunning() {
        return mIsRunning;
    }

    public static void startService(Context context) {
        if (context == null || mIsRunning) {
            return;
        }
        // 开启service服务
        Intent intent = new Intent(context, NotificationService.class);
        context.startService(intent);
    }

    /**
     * 睡眠定时
     *
     * @param start: true: 开始定时睡眠 false: 取消睡眠设置
     * @param time:  定时时间
     */
    public static void timing(Context context, boolean start, long time) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 4681,
                new Intent(Constants.PlayFlag.PLAYER_CONTROL_TIMING), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (start) {
            if (am != null) {
                am.set(AlarmManager.RTC, System.currentTimeMillis() + time, pendingIntent);
            }
        } else {
            if (am != null) {
                am.cancel(pendingIntent);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mIsRunning = true;
        EventBus.getDefault().register(this);
        mControl = MediaControler.getIns(getApplicationContext());
        mBinder = new MusicBinder();
        mBroadcast = new ControlBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.PlayFlag.PLAYER_CONTROL_PLAY_PAUSE);
        filter.addAction(Constants.PlayFlag.PLAYER_CONTROL_PREV);
        filter.addAction(Constants.PlayFlag.PLAYER_CONTROL_NEXT);
        filter.addAction(Constants.PlayFlag.PLAYER_CONTROL_EXIT);
        filter.addAction(Constants.PlayFlag.PLAYER_CONTROL_TIMING);
        filter.addAction(Constants.PlayFlag.PLAYER_RELOAD);
        registerReceiver(mBroadcast, filter);

        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        loadMusic();
    }

    private void loadMusic() {
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = AppDBUtil.getIns(getApplicationContext()).optMusic().queryAll(AppDB.MUSIC);
                if (list == null) {
                    list = new ArrayList<>();
                }
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MusicModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<MusicModel> list) {
                        Preferences p = Preferences.getIns(getApplicationContext());
                        boolean play = Constants.PlayerMode.mode == Constants.PlayerMode.PLAYER_MODE_NOTIFICATION
                                || (p.getIsAutoPlay() && list.size() > 0);
                        mControl.init(list, p.getLastPlayPosition(), play);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 刷新通知栏
     *
     * @param bitmap:     图标
     * @param songName:   歌曲名
     * @param artistName: 歌手
     * @param status:     无/播放/暂停
     */
    private void updateNotification(Bitmap bitmap, String songName, String artistName, int status) {
        Intent intent;
        if (Constants.PlayerMode.mode == Constants.PlayerMode.PLAYER_MODE_NOTIFICATION) {
            intent = new Intent(this, ModeActivity.class);
        } else if (Constants.PlayerMode.mode == Constants.PlayerMode.PLAYER_MODE_MINIMALIST) {
            intent = new Intent(this, PlayActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        NotificationChannel notificationChannel = getNotificationChannel(this,
                NOTIFICATION_ID, NOTIFICATION_NAME);
        NotificationCompat.Builder builder = getNotification(this, notificationChannel);
        builder.setSmallIcon(R.mipmap.ic_launcher) // 设置图标
                .setTicker("") // 手机状态栏的提示
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0)); // 点击后的意图

        RemoteViews rv = getRemoteViews(bitmap, songName, artistName, status);
        builder.setContent(rv);
        Notification notification = builder.build();
        mManager.notify(NOTIFICATION_UNIQUE_ID, notification);
        startForeground(NOTIFICATION_UNIQUE_ID, notification); // 自定义的notification_ID不能为0
    }

    @Nullable
    private NotificationChannel getNotificationChannel(Context context, String id, String name) {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        return channel;
    }

    private NotificationCompat.Builder getNotification(Context context,
                                                       @Nullable NotificationChannel channel) {
        NotificationCompat.Builder builder;
        if (channel != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, channel.getId());
        } else {
            builder = new NotificationCompat.Builder(context, "");
        }
        return builder;
    }

    @android.support.annotation.NonNull
    private RemoteViews getRemoteViews(Bitmap bitmap, String songName, String artistName, int status) {
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.module_play_notification);
        if (bitmap != null) {
            rv.setImageViewBitmap(R.id.image, bitmap);
        } else {
            rv.setImageViewResource(R.id.image, R.drawable.module_play_ic_notification);
        }
        if (status == Constants.PlayStatus.PLAY_STATUS_PLAYING) {
            rv.setImageViewResource(R.id.tv_play_pause, R.drawable.module_play_ic_notification_pause);
        } else if (status == Constants.PlayStatus.PLAY_STATUS_PAUSE) {
            rv.setImageViewResource(R.id.tv_play_pause, R.drawable.module_play_ic_notification_play);
        }
        rv.setTextViewText(R.id.title, songName);
        rv.setTextViewText(R.id.text, artistName);

        // 此处action不能是一样的 如果一样的 接受的flag参数只是第一个设置的值
        Intent pauseIntent = new Intent(Constants.PlayFlag.PLAYER_CONTROL_PLAY_PAUSE);
        pauseIntent.putExtra("flag", Constants.PlayFlag.PLAY_FLAG_PLAY_PAUSE);
        PendingIntent pausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        rv.setOnClickPendingIntent(R.id.play_pause, pausePIntent);

        Intent nextIntent = new Intent(Constants.PlayFlag.PLAYER_CONTROL_NEXT);
        nextIntent.putExtra("flag", Constants.PlayFlag.PLAY_FLAG_NEXT);
        PendingIntent nextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        rv.setOnClickPendingIntent(R.id.next, nextPIntent);

        Intent preIntent = new Intent(Constants.PlayFlag.PLAYER_CONTROL_PREV);
        preIntent.putExtra("flag", Constants.PlayFlag.PLAY_FLAG_PRE);
        PendingIntent prePIntent = PendingIntent.getBroadcast(this, 0, preIntent, 0);
        rv.setOnClickPendingIntent(R.id.prev, prePIntent);

        Intent exitIntent = new Intent(Constants.PlayFlag.PLAYER_CONTROL_EXIT);
        exitIntent.putExtra("flag", Constants.PlayFlag.PLAY_FLAG_EXIT);
        PendingIntent exitPIntent = PendingIntent.getBroadcast(this, 0, exitIntent, 0);
        rv.setOnClickPendingIntent(R.id.exit, exitPIntent);
        return rv;
    }

    /**
     * 取消通知栏
     */
    public void cancelNotification() {
        // Can not work because of "startForeground"!
        mManager.cancel(NOTIFICATION_UNIQUE_ID);
    }

    public class MusicBinder extends Binder {
        public void updateNotificationTask(Bitmap bitmap, String title, String name, int playStatus) {
            updateNotification(bitmap, title, name, playStatus);
        }

        public MediaControler getInstanceMusicControlUtils() {
            return mControl;
        }
    }

    private class ControlBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            switch (action) {
                case Constants.PlayFlag.PLAYER_CONTROL_PLAY_PAUSE:
                case Constants.PlayFlag.PLAYER_CONTROL_NEXT:
                case Constants.PlayFlag.PLAYER_CONTROL_PREV:
                case Constants.PlayFlag.PLAYER_CONTROL_EXIT:
                    final int flag = intent.getIntExtra("flag", -1);
                    ULog.d("flags" + flag + "");
                    switch (flag) {
                        case Constants.PlayFlag.PLAY_FLAG_PLAY_PAUSE:
                            if (mControl.getStatus() == Constants.PlayStatus.PLAY_STATUS_PLAYING) {
                                mControl.pause();
                            } else if (mControl.getStatus() == Constants.PlayStatus.PLAY_STATUS_PAUSE) {
                                mControl.start();
                            }
                            break;
                        case Constants.PlayFlag.PLAY_FLAG_NEXT:
                            mControl.next();
                            break;
                        case Constants.PlayFlag.PLAY_FLAG_PRE:
                            mControl.prev();
                            break;
                        case Constants.PlayFlag.PLAY_FLAG_EXIT:
                            App.exit(); // 退出应用
                            break;
                    }
                    break;
                case Constants.PlayFlag.PLAYER_CONTROL_TIMING:
                    App.exit(); // 退出应用
                    break;
                case Constants.PlayFlag.PLAYER_RELOAD:
                    updateNotification(Constants.PlayStatus.PLAY_STATUS_PLAYING); // 正在播放
                    break;
            }
        }
    }

    private void updateNotification(int status) {
        switch (status) {
            case Constants.PlayStatus.PLAY_STATUS_STOP:
                updateNotification(null, mControl.getSongName(),
                        mControl.getArtistName(), status);
                // 取消通知栏
                cancelNotification();
                break;
            case Constants.PlayStatus.PLAY_STATUS_PLAYING:
            case Constants.PlayStatus.PLAY_STATUS_PAUSE:
                // 正在播放/暂停
                // 更新Notification的显示
                updateNotification(null, mControl.getSongName(),
                        mControl.getArtistName(), status);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEventMainThread(MusicInfoEvent event) {
        if (event != null && mIsRunning && event.isUpdateNotif) {
            updateNotification(event.status); // 正在播放
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcast);
        mIsRunning = false;
        stopForeground(true);
        super.onDestroy();
    }
}