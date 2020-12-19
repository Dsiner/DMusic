package com.d.music.online.model;

import android.text.TextUtils;

import com.d.music.data.database.greendao.bean.MusicModel;
import com.d.music.data.database.greendao.bean.TransferModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * MVInfoModel
 * Created by D on 2018/8/13.
 */
public class MVInfoModel extends MVDetailModel {

    /**
     * id : 5965036
     * name : 卡路里
     * artistId : 27693474
     * artistName : 火箭少女101
     * briefDesc : 火箭少女101献唱电影《西虹市首富》插曲
     * desc : MV采用了画中画的剪辑手法，表现一位边吃着爆米花、炸鸡边看电视的“胖妹”，因为一档“西虹人瘦”电视节目洗心革面励志减肥的故事。复古的色调、超写实的歌词搭配魔性搞怪的曲风，让沉迷美食的胖妹都毅然决然地丢掉了手中的汉堡、三明治，加入减肥大军当中
     * cover : http://p1.music.126.net/FuznAWoLFsX7TTBoQl4CLQ==/109951163425874499.jpg
     * coverId : 109951163425874499
     * playCount : 7538896
     * subCount : 46462
     * shareCount : 18661
     * likeCount : 20497
     * commentCount : 6818
     * duration : 170000
     * nType : 0
     * publishTime : 2018-07-26
     * brs : brs
     * artists : [{"id":27693474,"name":"火箭少女101"}]
     * isReward : false
     * commentThreadId : R_MV_5_5965036
     */
    public int id;
    public String name;
    public int artistId;
    public String artistName;
    public String briefDesc;
    @SerializedName("desc")
    public String descX;
    public String cover;
    public long coverId;
    public int playCount;
    public int subCount;
    public int shareCount;
    public int likeCount;
    public int commentCount;
    public int duration;
    public int nType;
    public String publishTime;
    public BrsBean brs;
    public boolean isReward;
    public String commentThreadId;
    public List<ArtistsBean> artists;

    public MVInfoModel() {
        this.view_type = TYPE_INFO;
    }

    public static TransferModel convertToTransfer(MVInfoModel model) {
        TransferModel transferModel = new TransferModel();
        transferModel.transferId = transferModel.id
                = TransferModel.generateId(MusicModel.TYPE_NETEASE,
                MusicModel.Channel.CHANNEL_TYPE_NONE, "" + model.id);
        transferModel.transferType = TransferModel.TRANSFER_TYPE_MV;
        transferModel.type = MusicModel.TYPE_NETEASE;
        transferModel.songId = "" + model.id;
        transferModel.songName = model.name;
        transferModel.songUrl = getUrl(model);
        transferModel.artistName = model.artistName;
        transferModel.albumId = "" + model.coverId;
        transferModel.albumUrl = model.cover;
        return transferModel;
    }

    public static String getUrl(MVInfoModel model) {
        String url = model.brs._$480;
        if (TextUtils.isEmpty(url)) {
            url = model.brs._$720;
        }
        if (TextUtils.isEmpty(url)) {
            url = model.brs._$1080;
        }
        if (TextUtils.isEmpty(url)) {
            url = model.brs._$240;
        }
        return !TextUtils.isEmpty(url) ? url : "";
    }

    public static class BrsBean {

        /**
         * 240 : http://vodkgeyttp8.vod.126.net/cloudmusic/2428/mv/f9fc/930e8886608bedac4b728fe5610f95db.mp4?wsSecret=26e9919e709af00787e8497e6de27e6d&wsTime=1534146094
         * 480 : http://vodkgeyttp8.vod.126.net/cloudmusic/2428/mv/f9fc/48982d8ea830f8f20d6e7a1a925d1147.mp4?wsSecret=681764d25d17c529f4ab014a1909b1fc&wsTime=1534146094
         * 720 : http://v4.music.126.net/20180813164134/2e4a4fa4728f1a1c9a0d94c1c31c055c/web/cloudmusic/2428/mv/f9fc/72f051eec3b1afbdc8efb7e08c942752.mp4
         * 1080 : http://v4.music.126.net/20180813164134/26aabde93f2ed69fa9a3d245ebd1a14c/web/cloudmusic/2428/mv/f9fc/19797ae5a5ec9849a0e48a019ec985cc.mp4
         */
        @SerializedName("240")
        public String _$240;
        @SerializedName("480")
        public String _$480;
        @SerializedName("720")
        public String _$720;
        @SerializedName("1080")
        public String _$1080;
    }

    public static class ArtistsBean {

        /**
         * id : 27693474
         * name : 火箭少女101
         */
        public int id;
        public String name;
    }
}
