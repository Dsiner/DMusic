package com.d.music.module.greendao.music.base;

import com.d.lib.common.module.mvp.model.BaseModel;
import com.d.music.module.media.MusicFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by D on 2017/4/28.
 */

public class MusicModel extends BaseModel{

    public String url;
    public String songName;
    public String singer;
    public String album;
    public Long duration;
    public Long size;
    public String filePostfix;
    public String folder;
    public String lrcName;
    public String lrcUrl;
    public Boolean isCollected;
    public Integer seq;
    public Long timeStamp;

    /**
     * extra
     */
    public boolean isChecked = false;//附加属性：是否选中
    public boolean isSortChecked = false;//附加属性：是否选中

    public String letter;//section首字母
    public String pinyin;//内容全拼音
    public boolean isLetter;//是否是section第一条

    public MusicModel() {
    }

    public MusicModel(String url) {
        this.url = url;
    }

    public MusicModel(String url, String songName, String singer, String album, Long duration, Long size, String filePostfix, String folder, String lrcName, String lrcUrl, Boolean isCollected, Integer seq, Long timeStamp) {
        this.url = url;
        this.songName = songName;
        this.singer = singer;
        this.album = album;
        this.duration = duration;
        this.size = size;
        this.filePostfix = filePostfix;
        this.folder = folder;
        this.lrcName = lrcName;
        this.lrcUrl = lrcUrl;
        this.isCollected = isCollected;
        this.seq = seq;
        this.timeStamp = timeStamp;
    }

    /**
     * 类型转换-数据浅拷贝
     *
     * @param list:原始拷贝数据
     * @param type:目标数据类型
     * @return ret:目标类型数据
     */
    public static List<? extends MusicModel> clone(List<? extends MusicModel> list, int type) {
        if (list == null) {
            return null;
        }
        int size = list.size();
        List<MusicModel> ret = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MusicModel newIns = MusicFactory.getNewInstance(type);
            if (newIns == null) {
                return null;
            }
            ret.add(newIns);
            MusicModel model = ret.get(i);
            list.get(i).clone(model);
        }
        return ret;
    }

    /**
     * 类型转换-数据浅拷贝
     *
     * @param model:目标类型数据
     * @param <T>:目标类型
     * @return model
     */
    public <T extends MusicModel> T clone(T model) {
        model.url = url;
        model.songName = songName;
        model.singer = singer;
        model.album = album;
        model.duration = duration;
        model.size = size;
        model.filePostfix = filePostfix;
        model.folder = folder;
        model.lrcName = lrcName;
        model.lrcUrl = lrcUrl;
        model.isCollected = isCollected;
        model.seq = seq;
        model.timeStamp = timeStamp;
        return model;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getFilePostfix() {
        return filePostfix;
    }

    public void setFilePostfix(String filePostfix) {
        this.filePostfix = filePostfix;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getLrcName() {
        return lrcName;
    }

    public void setLrcName(String lrcName) {
        this.lrcName = lrcName;
    }

    public String getLrcUrl() {
        return lrcUrl;
    }

    public void setLrcUrl(String lrcUrl) {
        this.lrcUrl = lrcUrl;
    }

    public Boolean getIsCollected() {
        return isCollected;
    }

    public void setIsCollected(Boolean isCollected) {
        this.isCollected = isCollected;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
