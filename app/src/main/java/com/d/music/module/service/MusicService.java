package com.d.music.module.service;

import android.app.AlarmManager;
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

import com.d.music.MainActivity;
import com.d.music.R;
import com.d.music.App;
import com.d.music.commen.Preferences;
import com.d.music.module.events.MusicInfoEvent;
import com.d.music.module.global.MusicCst;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.greendao.util.MusicDBUtil;
import com.d.music.mvp.activity.PlayActivity;
import com.d.music.mvp.activity.PlayerModeActivity;
import com.d.commen.utils.log.ULog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    public static MusicControl getControl(Context context) {
        if (control == null) {
            control = MusicControl.getInstance(context.getApplicationContext());
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

    /**
     * 睡眠定时
     *
     * @param start: true:开始定时睡眠 false:取消睡眠设置
     * @param time:  定时时间
     */
    public static void timing(Context context, boolean start, long time) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 4681,
                new Intent(MusicCst.PLAYER_CONTROL_TIMING), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (start) {
            am.set(AlarmManager.RTC, System.currentTimeMillis() + time, pendingIntent);
        } else {
            am.cancel(pendingIntent);
        }
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
        EventBus.getDefault().register(this);
        isRunning = true;
        if (control == null) {
            control = MusicControl.getInstance(getApplicationContext());
        }
        binder = new MyBinder();
        broadcast = new ControlBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicCst.PLAYER_CONTROL_PLAY_PAUSE);
        filter.addAction(MusicCst.PLAYER_CONTROL_PREV);
        filter.addAction(MusicCst.PLAYER_CONTROL_NEXT);
        filter.addAction(MusicCst.PLAYER_CONTROL_EXIT);
        filter.addAction(MusicCst.PLAYER_CONTROL_TIMING);
        filter.addAction(MusicCst.PLAYER_RELOAD);
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
                        Preferences p = Preferences.getInstance(getApplicationContext());
                        boolean play = MusicCst.playerMode == MusicCst.PLAYER_MODE_NOTIFICATION
                                || (p.getIsAutoPlay() && list.size() > 0);
                        control.init(getApplicationContext(), list, p.getLastPlayPosition(), play);
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
        Intent intent;
        if (MusicCst.playerMode == MusicCst.PLAYER_MODE_NOTIFICATION) {
            intent = new Intent(this, PlayerModeActivity.class);
        } else if (MusicCst.playerMode == MusicCst.PLAYER_MODE_MINIMALIST) {
            intent = new Intent(this, PlayActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
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

        Intent exitIntent = new Intent(MusicCst.PLAYER_CONTROL_EXIT);
        exitIntent.putExtra("flag", MusicCst.PLAY_FLAG_EXIT);
        PendingIntent exitPIntent = PendingIntent.getBroadcast(this, 0, exitIntent, 0);
        rv.setOnClickPendingIntent(R.id.exit, exitPIntent);

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
//        manager.cancel(notification_ID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeMessages(1);
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
                case MusicCst.PLAYER_CONTROL_PLAY_PAUSE:
                case MusicCst.PLAYER_CONTROL_NEXT:
                case MusicCst.PLAYER_CONTROL_PREV:
                case MusicCst.PLAYER_CONTROL_EXIT:
                    int flag = intent.getIntExtra("flag", -1);
                    ULog.d("flags" + flag + "");
                    switch (flag) {
                        case MusicCst.PLAY_FLAG_PLAY_PAUSE:
                            control.playOrPause();
                            break;
                        case MusicCst.PLAY_FLAG_NEXT:
                            control.next();
                            break;
                        case MusicCst.PLAY_FLAG_PRE:
                            control.prev();
                            break;
                        case MusicCst.PLAY_FLAG_EXIT:
                            App.exit(getApplicationContext());//退出应用
                            break;
                    }
                    break;
                case MusicCst.PLAYER_CONTROL_TIMING:
                    App.exit(getApplicationContext());//退出应用
                    break;
                case MusicCst.PLAYER_RELOAD:
                    updateNotif(MusicCst.PLAY_STATUS_PLAYING);//正在播放
                    break;
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
                    progressLock = false;//解锁
                    break;
            }
        }
    }

    private void updateNotif(int status) {
        switch (status) {
            case MusicCst.PLAY_STATUS_STOP:
                updateNotification(null, control.getCurSongName(), control.getCurSinger(), status);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MusicInfoEvent event) {
        if (event != null && isRunning && event.isUpdateNotif) {
            updateNotif(event.status);//正在播放
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        isRunning = false;
        control = null;//release
        stopForeground(true);
        super.onDestroy();
    }
}