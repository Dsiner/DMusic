package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseModel;

import java.util.List;

/**
 * SongInfoRespModel
 * Created by D on 2018/8/12.
 */
public class SongInfoRespModel extends BaseModel {

    /**
     * errorCode : 22000
     * data : {"xcode":"7f8e25cf1e3916e9f28109fff31ff97f","songList":[{"queryId":"1123781","songId":1123781,"songName":"花香","artistId":"1203","artistName":"许绍洋","albumId":108466,"albumName":"熏衣草 电视原声带","songPicSmall":"http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_1,w_90,h_90","songPicBig":"http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_1,w_150,h_150","songPicRadio":"http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_1,w_300,h_300","lrcLink":"http://qukufile2.qianqian.com/data2/lrc/13919037/13919037.lrc","version":"","copyType":0,"time":268,"linkCode":22000,"songLink":"http://zhangmenshiting.qianqian.com/data2/music/33d4b96857c9725b039c99f198a57eda/599495422/112378118000128.mp3?xcode=7f8e25cf1e3916e9e977ca24e3730051","showLink":"http://zhangmenshiting.qianqian.com/data2/music/33d4b96857c9725b039c99f198a57eda/599495422/112378118000128.mp3?xcode=7f8e25cf1e3916e9e977ca24e3730051","format":"mp3","rate":128,"size":4293988,"relateStatus":"0","resourceType":"0","source":"web"}]}
     */
    public int errorCode;
    public DataBean data;

    public static class DataBean {

        /**
         * xcode : 7f8e25cf1e3916e9f28109fff31ff97f
         * songList : [{"queryId":"1123781","songId":1123781,"songName":"花香","artistId":"1203","artistName":"许绍洋","albumId":108466,"albumName":"熏衣草 电视原声带","songPicSmall":"http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_1,w_90,h_90","songPicBig":"http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_1,w_150,h_150","songPicRadio":"http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_1,w_300,h_300","lrcLink":"http://qukufile2.qianqian.com/data2/lrc/13919037/13919037.lrc","version":"","copyType":0,"time":268,"linkCode":22000,"songLink":"http://zhangmenshiting.qianqian.com/data2/music/33d4b96857c9725b039c99f198a57eda/599495422/112378118000128.mp3?xcode=7f8e25cf1e3916e9e977ca24e3730051","showLink":"http://zhangmenshiting.qianqian.com/data2/music/33d4b96857c9725b039c99f198a57eda/599495422/112378118000128.mp3?xcode=7f8e25cf1e3916e9e977ca24e3730051","format":"mp3","rate":128,"size":4293988,"relateStatus":"0","resourceType":"0","source":"web"}]
         */
        public String xcode;
        public List<SongListBean> songList;

        public static class SongListBean {

            /**
             * queryId : 1123781
             * songId : 1123781
             * songName : 花香
             * artistId : 1203
             * artistName : 许绍洋
             * albumId : 108466
             * albumName : 熏衣草 电视原声带
             * songPicSmall : http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_1,w_90,h_90
             * songPicBig : http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_1,w_150,h_150
             * songPicRadio : http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_1,w_300,h_300
             * lrcLink : http://qukufile2.qianqian.com/data2/lrc/13919037/13919037.lrc
             * version :
             * copyType : 0
             * time : 268
             * linkCode : 22000
             * songLink : http://zhangmenshiting.qianqian.com/data2/music/33d4b96857c9725b039c99f198a57eda/599495422/112378118000128.mp3?xcode=7f8e25cf1e3916e9e977ca24e3730051
             * showLink : http://zhangmenshiting.qianqian.com/data2/music/33d4b96857c9725b039c99f198a57eda/599495422/112378118000128.mp3?xcode=7f8e25cf1e3916e9e977ca24e3730051
             * format : mp3
             * rate : 128
             * size : 4293988
             * relateStatus : 0
             * resourceType : 0
             * source : web
             */
            public String queryId;
            public int songId;
            public String songName;
            public String artistId;
            public String artistName;
            public int albumId;
            public String albumName;
            public String songPicSmall;
            public String songPicBig;
            public String songPicRadio;
            public String lrcLink;
            public String version;
            public int copyType;
            public int time;
            public int linkCode;
            public String songLink;
            public String showLink;
            public String format;
            public int rate;
            public int size;
            public String relateStatus;
            public String resourceType;
            public String source;
        }
    }
}
