package com.d.dmusic.module.service;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import com.d.dmusic.module.events.PlayOrPauseEvent;
import com.d.dmusic.module.global.MusciCst;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.greendao.util.MusicDBUtil;
import com.d.dmusic.utils.TaskManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * MusicControl
 * Created by D on 2017/4/29.
 */
public class MusicControl {
    private static MusicControl instance;
    public static int playMode;// 当前列表播放模式

    private Context context;
    private int status;//0:无 1:播放 2:暂停
    private MediaPlayer mediaPlayer;
    private List<MusicModel> models;// 播放列表
    private int count;
    private int curPos;//当前播放的位置
    private String curSongName = "";// 当前播放音乐名
    private String curSinger = "";// 当前播放音乐歌手

    public boolean isLoaded() {
        return false;
    }

    public int getCurPos() {
        return curPos;
    }

    public String getCurSongName() {
        return curSongName;
    }

    public String getCurSinger() {
        return curSinger;
    }

    public List<MusicModel> getModels() {
        return models;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public int getStatus() {
        return status;
    }

    /**
     * 获取当前MusicModel
     */
    public MusicModel getCurModel() {
        if (models != null && models.size() > 0 && curPos >= 0 && curPos < models.size()) {
            return models.get(curPos);
        }
        return null;
    }

    private MusicControl(Context context) {
        this.context = context.getApplicationContext();
        reset();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                autoNext();// 自动下一首
            }
        });
    }

    /**
     * 为了保证context的统一和持久化，通过MyService.getInstanceMusicControlUtils()那取单例，
     * context为MyService.getBaseContext()
     */
    public static MusicControl getInstance(Context context) {
        if (instance == null) {
            synchronized (MusicControl.class) {
                if (instance == null) {
                    instance = new MusicControl(context);
                }
            }
        }
        return instance;
    }

    public void init(final List<MusicModel> datas, final int position) {
        Observable.create(new ObservableOnSubscribe<List<MusicModel>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MusicModel>> e) throws Exception {
                List<MusicModel> list = (List<MusicModel>) MusicModel.clone(datas, MusicDB.MUSIC);
                MusicDBUtil.getInstance(context).insertOrReplaceMusicInTx(list, MusicDB.MUSIC);
                if (list == null) {
                    list = new ArrayList<MusicModel>();
                }
                e.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MusicModel>>() {
                    @Override
                    public void accept(@NonNull List<MusicModel> list) throws Exception {
                        if (models != null && list != null) {
                            models.clear();
                            models.addAll(list);
                            count = models.size();
                            curPos = (position >= 0 && position < models.size()) ? position : 0;
                            play();
                        }
                    }
                });
    }

    public void reLoad() {
        List<MusicModel> list = (List<MusicModel>) MusicDBUtil.getInstance(context).queryAllMusic(MusicDB.MUSIC);
        if (models != null && list != null) {
            models.clear();
            models.addAll(list);
            count = models.size();
            curPos = (curPos >= 0 && curPos < models.size()) ? curPos : 0;
        }
    }

    public void seekTo(int msec) {
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            return;
        }
        int duration = mediaPlayer.getDuration();// 毫秒
        if (msec >= 0 && msec <= duration) {
            mediaPlayer.seekTo(msec);
        }
    }

    public void playPosition(int position) {
        if (position >= 0 && position < count) {
            curPos = position;
            play();
        }
    }

    /**
     * 清空当前播放列表
     */
    public void delelteAll() {
        stop();
        reset();
        TaskManager.getIns().executeTask(new Runnable() {
            @Override
            public void run() {
                MusicDBUtil.getInstance(context).deleteAll(MusicDB.MUSIC);
            }
        });
    }

    public void delelteByPosition(final int position) {
        if (position < 0 || position >= count) {
            return;
        }
        if (position > curPos) {
            reCul(position);
        } else if (position < curPos) {
            reCul(position);
            curPos--;
        } else if (position == curPos) {
            stop();
            if (position == count - 1) {
                reCul(position);
                curPos = 0;
                if (count > 0) {
                    play();
                }
            } else {
                reCul(position);
                play();
            }
        }
    }

    private void reCul(int position) {
        MusicDBUtil.getInstance(context).delete(MusicDB.MUSIC, getCurModel());
        models.remove(position);
        count = models.size();
    }

    private void play() {
        stop();
        if (models.size() <= 0) {
            reset();
            sendBroadcast();
            return;
        }
        try {
            mediaPlayer.reset();// 重置
            mediaPlayer.setDataSource(models.get(curPos).url);// 指定要播放的音频文件
            mediaPlayer.prepare();// 预加载音频文件
            mediaPlayer.start();
            status = MusciCst.PLAY_STATUS_PLAYING;//正在播放

            //广播通知
            curSongName = models.get(curPos).songName;
            curSinger = models.get(curPos).singer;
            sendBroadcast();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendBroadcast() {
        Intent intent = new Intent(MusciCst.MUSIC_CURRENT_INFO);
        intent.putExtra("songName", curSongName);
        intent.putExtra("singer", curSinger);
        context.sendBroadcast(intent);
    }

    /**
     * 播放/暂停
     *
     * @return ret :执行结果1:播放 2:暂停
     */
    public int playOrPause() {
        PlayOrPauseEvent event = new PlayOrPauseEvent();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            status = MusciCst.PLAY_STATUS_PAUSE;//暂停
            event.isPlay = false;
        } else {
            mediaPlayer.start();
            status = MusciCst.PLAY_STATUS_PLAYING;//正在播放
            event.isPlay = true;
        }
        EventBus.getDefault().post(event);
        return status;
    }

    public void next() {
        switch (playMode) {
            case MusciCst.PLAY_MODE_ALL_REPEAT:
            case MusciCst.PLAY_MODE_ORDER:
            case MusciCst.PLAY_MODE_SINGLE_CYCLE:
                //列表循环、顺序播放、单曲循环
                if (++curPos >= count) {
                    curPos = 0;
                }
                break;
            case MusciCst.PLAY_MODE_SHUFFLE:
                //随机播放
                if (count > 0) {
                    Random random = new Random(System.currentTimeMillis());
                    curPos = Math.abs(random.nextInt()) % count;
                }
                break;
            default:
                break;
        }
        play();
    }

    public void prev() {
        switch (playMode) {
            case MusciCst.PLAY_MODE_ALL_REPEAT:
            case MusciCst.PLAY_MODE_ORDER:
            case MusciCst.PLAY_MODE_SINGLE_CYCLE:
                //列表循环、顺序播放、单曲循环
                if (--curPos < 0) {
                    curPos = count - 1;
                }
                break;
            case MusciCst.PLAY_MODE_SHUFFLE:
                //随机播放
                if (count > 0) {
                    Random random = new Random(System.currentTimeMillis());
                    curPos = Math.abs(random.nextInt()) % count;
                }
                break;
        }
        play();
    }

    /**
     * 自动播放下一首
     */
    public void autoNext() {
        switch (playMode) {
            case MusciCst.PLAY_MODE_ALL_REPEAT:
                if (++curPos >= count) {
                    curPos = 0;
                }
                play();
                break;
            case MusciCst.PLAY_MODE_ORDER:
                if (++curPos >= count) {
                    curPos--;
                    stop();
                } else {
                    play();
                }
                break;
            case MusciCst.PLAY_MODE_SHUFFLE:
                if (count > 0) {
                    Random random = new Random(System.currentTimeMillis());
                    curPos = Math.abs(random.nextInt()) % count;
                }
                play();
                break;
            case MusciCst.PLAY_MODE_SINGLE_CYCLE:
                play();
                break;
        }
    }

    /**
     * 重置
     */
    private void reset() {
        curPos = 0;
        curSongName = "";
        curSinger = "";
        count = 0;
        if (models == null) {
            models = new ArrayList<MusicModel>();
        } else {
            models.clear();
        }
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        status = MusciCst.PLAY_STATUS_STOP;//无
    }

    public void onDestroy() {
        stop();
        reset();
    }
}
