package com.d.music.module.media;

import android.content.Context;
import android.text.TextUtils;

import com.d.lib.common.utils.log.ULog;
import com.d.music.module.greendao.db.MusicDB;
import com.d.music.module.greendao.music.CollectionMusic;
import com.d.music.module.greendao.music.CustomMusic0;
import com.d.music.module.greendao.music.CustomMusic1;
import com.d.music.module.greendao.music.CustomMusic10;
import com.d.music.module.greendao.music.CustomMusic11;
import com.d.music.module.greendao.music.CustomMusic12;
import com.d.music.module.greendao.music.CustomMusic13;
import com.d.music.module.greendao.music.CustomMusic14;
import com.d.music.module.greendao.music.CustomMusic15;
import com.d.music.module.greendao.music.CustomMusic16;
import com.d.music.module.greendao.music.CustomMusic17;
import com.d.music.module.greendao.music.CustomMusic18;
import com.d.music.module.greendao.music.CustomMusic19;
import com.d.music.module.greendao.music.CustomMusic2;
import com.d.music.module.greendao.music.CustomMusic3;
import com.d.music.module.greendao.music.CustomMusic4;
import com.d.music.module.greendao.music.CustomMusic5;
import com.d.music.module.greendao.music.CustomMusic6;
import com.d.music.module.greendao.music.CustomMusic7;
import com.d.music.module.greendao.music.CustomMusic8;
import com.d.music.module.greendao.music.CustomMusic9;
import com.d.music.module.greendao.music.LocalAllMusic;
import com.d.music.module.greendao.music.Music;
import com.d.music.module.greendao.music.base.MusicModel;
import com.d.music.module.greendao.util.MusicDBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * MusicFactory
 * Created by D on 2017/5/2.
 */
public class MusicFactory {
    private Context context;
    private List<MusicInfo> infos;
    private List<Music> musics;
    private List<LocalAllMusic> localAllMusics;
    private List<CollectionMusic> collectionMusics;
    private List<CusMusic> cuss;
    private int type;

    private MusicFactory(Context context, int type) {
        this.context = context;
        this.type = type;
        switch (type) {
            case MusicDBUtil.MUSIC:
                musics = new ArrayList<>();
                break;
            case MusicDBUtil.LOCAL_ALL_MUSIC:
                localAllMusics = new ArrayList<>();
                break;
            case MusicDBUtil.COLLECTION_MUSIC:
                collectionMusics = new ArrayList<>();
                break;
            default:
                if (type >= MusicDB.CUSTOM_MUSIC_INDEX && type < MusicDB.CUSTOM_MUSIC_INDEX + MusicDB.CUSTOM_MUSIC_COUNT) {
                    cuss = new ArrayList<>();
                    initCustom(cuss, type);
                }
                break;
        }
    }

    private void initCustom(List<CusMusic> cuss, int type) {
        for (int i = MusicDB.CUSTOM_MUSIC_INDEX; i < MusicDB.CUSTOM_MUSIC_INDEX + MusicDB.CUSTOM_MUSIC_COUNT; i++) {
            if (i == type) {
                cuss.add(getCusMusic(type));
            } else {
                cuss.add(null);
            }
        }
    }

    public static MusicFactory createFactory(Context context, int type) {
        return new MusicFactory(context, type);
    }

    public List<? extends MusicModel> getMusic(List<String> paths) {
        if (paths == null) {
            return null;
        }
        infos = MediaUtil.getMusicInfos(context);
        for (String p : paths) {
            getMusic(p);
        }
        return ret(type);
    }

    private List<? extends MusicModel> getMusic(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        if (infos == null || infos.size() == 0) {
            return null;
        }
        int size = infos.size();
        for (int i = 0; i < size; i++) {
            MusicInfo info = infos.get(i);
            String url = info.url;
            ULog.d("url" + url);
            ULog.d("path" + path);
            if (url.length() > path.length()) {
                //加"/"表明为路径
                if (url.substring(0, path.length() + 1).equals(path + "/")) {
                    String displayName = info.displayName;
                    //文件格式过滤，MediaPlayer对其他格式支持不好（例如.wma）
                    for (String format : MediaUtil.imageFormatSet) {
                        if (displayName.endsWith(format)) {
                            boxUp(info, displayName);
                        }
                    }
                }
            }
        }
        return ret(type);
    }

    private void boxUp(MusicInfo info, String displayName) {
        MusicModel model = getNewInstance(type);
        if (model == null) {
            return;
        }
        model.songName = displayName.substring(0, displayName.lastIndexOf("."));
        model.singer = info.artist;
        model.album = info.album;
        model.duration = info.duration;
        model.size = info.size;
        model.filePostfix = displayName.substring(displayName.lastIndexOf("."));
        model.url = info.url;
        model.folder = info.url.substring(0, info.url.lastIndexOf("/"));
        //// TODO: @D new Thread 2017/5/7
        model.isCollected = false;
        model.timeStamp = System.currentTimeMillis();
        add(model);
    }

    private void add(MusicModel model) {
        switch (type) {
            case MusicDBUtil.MUSIC:
                musics.add((Music) model);
                break;
            case MusicDBUtil.LOCAL_ALL_MUSIC:
                localAllMusics.add((LocalAllMusic) model);
                break;
            case MusicDBUtil.COLLECTION_MUSIC:
                collectionMusics.add((CollectionMusic) model);
                break;
            default:
                if (type >= MusicDB.CUSTOM_MUSIC_INDEX && type < MusicDB.CUSTOM_MUSIC_INDEX + MusicDB.CUSTOM_MUSIC_COUNT) {
                    //自定义歌曲
                    CusMusic cus = cuss.get(type - MusicDB.CUSTOM_MUSIC_INDEX);
                    if (cus != null) {
                        cus.models.add(model);
                    }
                }
                break;
        }
    }

    public static MusicModel getNewInstance(int type) {
        switch (type) {
            case MusicDBUtil.MUSIC:
                return new Music();
            case MusicDBUtil.LOCAL_ALL_MUSIC:
                return new LocalAllMusic();
            case MusicDBUtil.COLLECTION_MUSIC:
                return new CollectionMusic();
            default:
                if (type >= MusicDB.CUSTOM_MUSIC_INDEX && type < MusicDB.CUSTOM_MUSIC_INDEX + MusicDB.CUSTOM_MUSIC_COUNT) {
                    return getCustomMusicIns(type);
                }
        }
        return null;
    }

    private CusMusic getCusMusic(int type) {
        if (type / 10 != 1) {
            return null;
        }
        switch (type % 10) {
            case 0:
                List<CustomMusic0> cm0 = new ArrayList<>();
                return new CusMusic(cm0);
            case 1:
                List<CustomMusic1> cm1 = new ArrayList<>();
                return new CusMusic(cm1);
            case 2:
                List<CustomMusic2> cm2 = new ArrayList<>();
                return new CusMusic(cm2);
            case 3:
                List<CustomMusic3> cm3 = new ArrayList<>();
                return new CusMusic(cm3);
            case 4:
                List<CustomMusic4> cm4 = new ArrayList<>();
                return new CusMusic(cm4);
            case 5:
                List<CustomMusic5> cm5 = new ArrayList<>();
                return new CusMusic(cm5);
            case 6:
                List<CustomMusic6> cm6 = new ArrayList<>();
                return new CusMusic(cm6);
            case 7:
                List<CustomMusic7> cm7 = new ArrayList<>();
                return new CusMusic(cm7);
            case 8:
                List<CustomMusic8> cm8 = new ArrayList<>();
                return new CusMusic(cm8);
            case 9:
                List<CustomMusic9> cm9 = new ArrayList<>();
                return new CusMusic(cm9);
            case 10:
                List<CustomMusic10> cm10 = new ArrayList<>();
                return new CusMusic(cm10);
            case 11:
                List<CustomMusic11> cm11 = new ArrayList<>();
                return new CusMusic(cm11);
            case 12:
                List<CustomMusic12> cm12 = new ArrayList<>();
                return new CusMusic(cm12);
            case 13:
                List<CustomMusic13> cm13 = new ArrayList<>();
                return new CusMusic(cm13);
            case 14:
                List<CustomMusic14> cm14 = new ArrayList<>();
                return new CusMusic(cm14);
            case 15:
                List<CustomMusic15> cm15 = new ArrayList<>();
                return new CusMusic(cm15);
            case 16:
                List<CustomMusic16> cm16 = new ArrayList<>();
                return new CusMusic(cm16);
            case 17:
                List<CustomMusic17> cm17 = new ArrayList<>();
                return new CusMusic(cm17);
            case 18:
                List<CustomMusic18> cm18 = new ArrayList<>();
                return new CusMusic(cm18);
            case 19:
                List<CustomMusic19> cm19 = new ArrayList<>();
                return new CusMusic(cm19);
        }
        return null;
    }

    private static <T extends MusicModel> T getCustomMusicIns(int type) {
        if (type / 10 != 1) {
            return null;
        }
        switch (type % 10) {
            case 0:
                return (T) new CustomMusic0();
            case 1:
                return (T) new CustomMusic1();
            case 2:
                return (T) new CustomMusic2();
            case 3:
                return (T) new CustomMusic3();
            case 4:
                return (T) new CustomMusic4();
            case 5:
                return (T) new CustomMusic5();
            case 6:
                return (T) new CustomMusic6();
            case 7:
                return (T) new CustomMusic7();
            case 8:
                return (T) new CustomMusic8();
            case 9:
                return (T) new CustomMusic9();
            case 10:
                return (T) new CustomMusic10();
            case 11:
                return (T) new CustomMusic11();
            case 12:
                return (T) new CustomMusic12();
            case 13:
                return (T) new CustomMusic13();
            case 14:
                return (T) new CustomMusic14();
            case 15:
                return (T) new CustomMusic15();
            case 16:
                return (T) new CustomMusic16();
            case 17:
                return (T) new CustomMusic17();
            case 18:
                return (T) new CustomMusic18();
            case 19:
                return (T) new CustomMusic19();
        }
        return null;
    }

    private List<? extends MusicModel> ret(int type) {
        switch (type) {
            case MusicDBUtil.MUSIC:
                return musics;
            case MusicDBUtil.LOCAL_ALL_MUSIC:
                return localAllMusics;
            case MusicDBUtil.COLLECTION_MUSIC:
                return collectionMusics;
            default:
                if (type >= MusicDB.CUSTOM_MUSIC_INDEX && type < MusicDB.CUSTOM_MUSIC_INDEX + MusicDB.CUSTOM_MUSIC_COUNT) {
                    return cuss.get(type - MusicDB.CUSTOM_MUSIC_INDEX).models;
                }
        }
        return null;
    }
}
