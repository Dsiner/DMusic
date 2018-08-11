package com.d.music.online.model;

import com.d.lib.common.module.mvp.model.BaseRespModel;
import com.d.music.module.greendao.music.base.MusicModel;

import java.util.List;

/**
 * RadioSongsRespModel
 * Created by D on 2018/8/11.
 */
public class RadioSongsRespModel extends BaseRespModel {

    /**
     * error_code : 22000
     * result : {"channel":"漫步春天","channelid":null,"ch_name":"public_tuijian_spring","artistid":null,"avatar":null,"count":null,"songlist":[{"songid":"1123781","title":"花香","artist":"许绍洋","thumb":"http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"333","all_artist_id":"333","resource_type":"0","havehigh":2,"charge":0,"all_rate":"96,128,224,320,flac"},{"songid":"283493","title":"让我想一想","artist":"陈绮贞","thumb":"http://qukufile2.qianqian.com/data2/pic/9f63c6b3c2795395d422f9818d83287d/68624/68624.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"429","all_artist_id":"429","resource_type":"0","havehigh":2,"charge":0,"all_rate":"64,96,128,224,320,flac"},{"songid":"1000922","title":"春风","artist":"王筝","thumb":"http://qukufile2.qianqian.com/data2/pic/540d21363e5317e431070ac4016a5b98/601786768/601786768.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"383","all_artist_id":"383","resource_type":"0","havehigh":2,"charge":0,"all_rate":"96,128,224,320,flac"},{"songid":"1159960","title":"深呼吸","artist":"羽泉","thumb":"http://qukufile2.qianqian.com/data2/music/5C123C4E11A70AB61A3EB2CC1E203942/252256504/252256504.jpg@s_0,w_90","method":0,"flow":0,"artist_id":"786","all_artist_id":"786","resource_type":"0","havehigh":2,"charge":0,"all_rate":"96,128,224,320,flac"},{"songid":"537345","title":"暖心","artist":"郁可唯","thumb":"http://qukufile2.qianqian.com/data2/pic/4da2d193769c88f39c95b88b474fd1a8/578956117/578956117.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"1656","all_artist_id":"1656","resource_type":"0","havehigh":2,"charge":0,"all_rate":"32,64,128,256,320,flac"},{"songid":"450485","title":"吹吹风","artist":"曹格","thumb":"http://qukufile2.qianqian.com/data2/pic/4354abbe4e167688703e2da1ae5c4a2d/145111/145111.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"488","all_artist_id":"488","resource_type":"0","havehigh":2,"charge":0,"all_rate":"64,96,128,224,320,flac"},{"songid":"1249078","title":"一整片天空","artist":"易桀齐","thumb":"http://qukufile2.qianqian.com/data2/music/12D27ABDE0E75BD0C32D86AAF2C8188F/252191207/252191207.jpg@s_0,w_90","method":0,"flow":0,"artist_id":"931","all_artist_id":"931","resource_type":"0","havehigh":2,"charge":0,"all_rate":"96,128,224,320,flac"},{"songid":"621079","title":"下午三点","artist":"陈绮贞","thumb":"http://qukufile2.qianqian.com/data2/pic/829663acc8201cb05dbe484a6d6c672f/186087/186087.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"429","all_artist_id":"429","resource_type":"0","havehigh":2,"charge":0,"all_rate":"64,96,128,224,320,flac"},{"songid":"537254","title":"蓝短裤儿童版","artist":"郁可唯","thumb":"http://qukufile2.qianqian.com/data2/pic/4da2d193769c88f39c95b88b474fd1a8/578956117/578956117.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"1656","all_artist_id":"1656","resource_type":"0","havehigh":2,"charge":0,"all_rate":"32,64,128,256,320,flac"},{"songid":"435225","title":"在晴朗的一天出发","artist":"梁静茹","thumb":"http://qukufile2.qianqian.com/data2/pic/8b9d8b1ebaeb8981213d02601fe14a11/583747637/583747637.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"120","all_artist_id":"120","resource_type":"0","havehigh":2,"charge":0,"all_rate":"64,96,128,224,320,flac"},{"songid":null,"title":null,"artist":null,"thumb":"","method":0,"flow":0,"artist_id":null,"all_artist_id":null,"resource_type":null,"havehigh":0,"charge":0,"all_rate":""}]}
     */
    public int error_code;
    public ResultBean result;

    /**
     * Extra
     */
    public List<MusicModel> datas;

    public static class ResultBean {

        /**
         * channel : 漫步春天
         * channelid : null
         * ch_name : public_tuijian_spring
         * artistid : null
         * avatar : null
         * count : null
         * songlist : [{"songid":"1123781","title":"花香","artist":"许绍洋","thumb":"http://qukufile2.qianqian.com/data2/pic/a04eccb139402920c53fca6e8b1614fe/108466/108466.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"333","all_artist_id":"333","resource_type":"0","havehigh":2,"charge":0,"all_rate":"96,128,224,320,flac"},{"songid":"283493","title":"让我想一想","artist":"陈绮贞","thumb":"http://qukufile2.qianqian.com/data2/pic/9f63c6b3c2795395d422f9818d83287d/68624/68624.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"429","all_artist_id":"429","resource_type":"0","havehigh":2,"charge":0,"all_rate":"64,96,128,224,320,flac"},{"songid":"1000922","title":"春风","artist":"王筝","thumb":"http://qukufile2.qianqian.com/data2/pic/540d21363e5317e431070ac4016a5b98/601786768/601786768.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"383","all_artist_id":"383","resource_type":"0","havehigh":2,"charge":0,"all_rate":"96,128,224,320,flac"},{"songid":"1159960","title":"深呼吸","artist":"羽泉","thumb":"http://qukufile2.qianqian.com/data2/music/5C123C4E11A70AB61A3EB2CC1E203942/252256504/252256504.jpg@s_0,w_90","method":0,"flow":0,"artist_id":"786","all_artist_id":"786","resource_type":"0","havehigh":2,"charge":0,"all_rate":"96,128,224,320,flac"},{"songid":"537345","title":"暖心","artist":"郁可唯","thumb":"http://qukufile2.qianqian.com/data2/pic/4da2d193769c88f39c95b88b474fd1a8/578956117/578956117.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"1656","all_artist_id":"1656","resource_type":"0","havehigh":2,"charge":0,"all_rate":"32,64,128,256,320,flac"},{"songid":"450485","title":"吹吹风","artist":"曹格","thumb":"http://qukufile2.qianqian.com/data2/pic/4354abbe4e167688703e2da1ae5c4a2d/145111/145111.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"488","all_artist_id":"488","resource_type":"0","havehigh":2,"charge":0,"all_rate":"64,96,128,224,320,flac"},{"songid":"1249078","title":"一整片天空","artist":"易桀齐","thumb":"http://qukufile2.qianqian.com/data2/music/12D27ABDE0E75BD0C32D86AAF2C8188F/252191207/252191207.jpg@s_0,w_90","method":0,"flow":0,"artist_id":"931","all_artist_id":"931","resource_type":"0","havehigh":2,"charge":0,"all_rate":"96,128,224,320,flac"},{"songid":"621079","title":"下午三点","artist":"陈绮贞","thumb":"http://qukufile2.qianqian.com/data2/pic/829663acc8201cb05dbe484a6d6c672f/186087/186087.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"429","all_artist_id":"429","resource_type":"0","havehigh":2,"charge":0,"all_rate":"64,96,128,224,320,flac"},{"songid":"537254","title":"蓝短裤儿童版","artist":"郁可唯","thumb":"http://qukufile2.qianqian.com/data2/pic/4da2d193769c88f39c95b88b474fd1a8/578956117/578956117.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"1656","all_artist_id":"1656","resource_type":"0","havehigh":2,"charge":0,"all_rate":"32,64,128,256,320,flac"},{"songid":"435225","title":"在晴朗的一天出发","artist":"梁静茹","thumb":"http://qukufile2.qianqian.com/data2/pic/8b9d8b1ebaeb8981213d02601fe14a11/583747637/583747637.jpg@s_1,w_90,h_90","method":0,"flow":0,"artist_id":"120","all_artist_id":"120","resource_type":"0","havehigh":2,"charge":0,"all_rate":"64,96,128,224,320,flac"},{"songid":null,"title":null,"artist":null,"thumb":"","method":0,"flow":0,"artist_id":null,"all_artist_id":null,"resource_type":null,"havehigh":0,"charge":0,"all_rate":""}]
         */
        public String channel;
        public String channelid;
        public String ch_name;
        public String artistid;
        public String avatar;
        public String count;
        public List<RadioSongsModel> songlist;
    }
}
