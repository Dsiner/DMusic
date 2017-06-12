package com.d.dmusic.module.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.d.dmusic.R;
import com.d.dmusic.application.SysApplication;
import com.d.dmusic.module.global.MusicCst;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.mvp.activity.PlayActivity;
import com.d.dmusic.utils.log.ULog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * MusicService
 * Created by D on 2017/4/29.
 */
public class MusicService extends Service {
    public static boolean progressLock = false;
    private static boolean isRunning;
    private static MusicControl control;

    private MyBinder binder;
    private NotificationManager manager;
    private RemoteViews rv;
    private int notification_ID;
    private WeakHandler handler = new WeakHandler(this);
    private ControlBroadcast broadcast;

    public static boolean isRunning() {
        return isRunning;
    }

    public static MusicControl getControl() {
        if (control == null) {
            control = MusicControl.getInstance(SysApplication.getInstance().getApplicationContext());
        }
        return control;
    }

    public static void startService(Context context) {
        if (context == null || isRunning) {
            return;
        }
        // 开启service服务
        Intent intent = new Intent(context, MusicService.class);
        context.startService(intent);
    }

    private static class WeakHandler extends Handler {
        WeakReference<MusicService> weakReference;

        WeakHandler(MusicService service) {
            weakReference = new WeakReference<MusicService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicService theService = weakReference.get();
            if (theService != null && theService.isRunning()) {
                switch (msg.what) {
                    case 1:
                        MediaPlayer mediaPlayer = control.getMediaPlayer();
                        if (mediaPlayer != null && control.getStatus() == MusicCst.PLAY_STATUS_PLAYING) {
                            int currentPosition = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
                            int duration = mediaPlayer.getDuration(); // 获取当前音乐播放总时间
                            Intent intent = new Intent();
                            intent.setAction(MusicCst.MUSIC_CURRENT_POSITION);
                            intent.putExtra("currentPosition", currentPosition);
                            intent.putExtra("duration", duration);
                            if (!progressLock) {
                                theService.sendBroadcast(intent);//给PlayerActivity发送广播
                            }
                        }
                        theService.handler.sendEmptyMessageDelayed(1, 1000);
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        if (control == null) {
            control = MusicControl.getInstance(SysApplication.getInstance().getApplicationContext());
        }
        binder = new MyBinder();
        broadcast = new ControlBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicCst.PLAYER_RELOAD);
        filter.addAction(MusicCst.MUSIC_CURRENT_INFO);
        filter.addAction(MusicCst.PLAYER_CONTROL_PLAY_PAUSE);
        filter.addAction(MusicCst.PLAYER_CONTROL_PREV);
        filter.addAction(MusicCst.PLAYER_CONTROL_NEXT);
        filter.addAction(MusicCst.MUSIC_SEEK_TO_TIME);
        registerReceiver(broadcast, filter);

        notification_ID = 6671;
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = (List<MusicModel>) MusicDBUtil.getInstance(getApplicationContext()).queryAllMusic(MusicDB.MUSIC);
                if (list == null) {
                    list = new ArrayList<MusicModel>();
                }
                e.onNext(list);//非空
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MusicModel>>() {
                    @Override
                    public void accept(@NonNull List<MusicModel> list) throws Exception {
                        if (list.size() > 0) {
                            control.init(list, 0);
                        }
                    }
                });
    }

    /**
     * 刷新通知栏
     *
     * @param bitmap:图标
     * @param songName:歌曲名
     * @param singer:歌手
     * @param status:无/播放/暂停
     */
    private void updateNotification(Bitmap bitmap, String songName, String singer, int status) {
        Intent intent = new Intent(this, PlayActivity.class);
        PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }
        builder.setSmallIcon(R.drawable.ic_launcher);// 设置图标
        builder.setTicker("");// 手机状态栏的提示；
        builder.setContentIntent(pintent);// 点击后的意图
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notification);
        if (bitmap != null) {
            rv.setImageViewBitmap(R.id.image, bitmap);
        } else {
            rv.setImageViewResource(R.id.image, R.drawable.ic_notification_icon);
        }
        if (status == MusicCst.PLAY_STATUS_PLAYING) {
            rv.setImageViewResource(R.id.tv_play_pause, R.drawable.ic_notification_pause);
        } else if (status == MusicCst.PLAY_STATUS_PAUSE) {
            rv.setImageViewResource(R.id.tv_play_pause, R.drawable.ic_notification_play);
        }
        rv.setTextViewText(R.id.title, songName);
        rv.setTextViewText(R.id.text, singer);

        //此处action不能是一样的 如果一样的 接受的flag参数只是第一个设置的值
        Intent pauseIntent = new Intent(MusicCst.PLAYER_CONTROL_PLAY_PAUSE);
        pauseIntent.putExtra("flag", MusicCst.PLAY_FLAG_PLAY_PAUSE);
        PendingIntent pausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        rv.setOnClickPendingIntent(R.id.play_pause, pausePIntent);

        Intent nextIntent = new Intent(MusicCst.PLAYER_CONTROL_NEXT);
        nextIntent.putExtra("flag", MusicCst.PLAY_FLAG_NEXT);
        PendingIntent nextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        rv.setOnClickPendingIntent(R.id.next, nextPIntent);

        Intent preIntent = new Intent(MusicCst.PLAYER_CONTROL_PREV);
        preIntent.putExtra("flag", MusicCst.PLAY_FLAG_PRE);
        PendingIntent prePIntent = PendingIntent.getBroadcast(this, 0, preIntent, 0);
        rv.setOnClickPendingIntent(R.id.prev, prePIntent);

        builder.setContent(rv);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();// 4.1以上
        } else {
            notification = builder.getNotification();
        }
        manager.notify(notification_ID, notification);
        startForeground(notification_ID, notification);// 自定义的notification_ID不能为0
    }

    /**
     * 取消通知栏
     */
    public void cancleNotification() {
        //can not work beacauseof "startForeground"!
        manager.cancel(notification_ID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.sendEmptyMessageDelayed(1, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyBinder extends Binder {
        // 执行任务
        public void updateNotificationTask(Bitmap bitmap, String title, String name, int playStatus) {
            updateNotification(bitmap, title, name, playStatus);
        }

        public MusicControl getinstanceMusicControlUtils() {
            return control;
        }
    }

    private class ControlBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case MusicCst.MUSIC_SEEK_TO_TIME:
                    MediaPlayer mediaPlayer = control.getMediaPlayer();
                    if (mediaPlayer != null) {
                        int currentPosttion = intent.getIntExtra("progress", 0);
                        if (currentPosttion / 1000 >= mediaPlayer.getDuration() / 1000) {
                            control.next();
                        } else {
                            control.seekTo(currentPosttion);
                        }
                    }
                    progressLock = false;
                    break;
                case MusicCst.PLAYER_CONTROL_PLAY_PAUSE:
                case MusicCst.PLAYER_CONTROL_NEXT:
                case MusicCst.PLAYER_CONTROL_PREV:
                    int flag = intent.getIntExtra("flag", -1);
                    ULog.v("flags" + flag + "");
                    switch (flag) {
                        case MusicCst.PLAY_FLAG_PLAY_PAUSE:
                            int playStatus = control.playOrPause();
                            updateNotif(playStatus);//正在播放
                            break;
                        case MusicCst.PLAY_FLAG_NEXT:
                            control.next();
                            break;
                        case MusicCst.PLAY_FLAG_PRE:
                            control.prev();
                            break;
                    }
                    break;
                case MusicCst.PLAYER_RELOAD:
                case MusicCst.MUSIC_CURRENT_INFO:
                    updateNotif(MusicCst.PLAY_STATUS_PLAYING);//正在播放
                    break;
            }
        }
    }

    private void updateNotif(int status) {
        switch (status) {
            case MusicCst.PLAY_STATUS_STOP:
                //取消通知栏
                cancleNotification();
                break;
            case MusicCst.PLAY_STATUS_PLAYING:
            case MusicCst.PLAY_STATUS_PAUSE:
                //正在播放/暂停
                //更新notification的view显示
                updateNotification(null, control.getCurSongName(), control.getCurSinger(), status);
                break;
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }
}