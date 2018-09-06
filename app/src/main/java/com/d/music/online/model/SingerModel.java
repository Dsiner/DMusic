package com.d.music.online.model;

import com.d.lib.common.component.mvp.model.BaseModel;

/**
 * SingerModel
 * Created by D on 2017/4/29.
 */
public class SingerModel extends BaseModel {

    /**
     * name : 陈粒
     * id : 1007170
     * picId : 6641050233030995
     * img1v1Id : 6628955605123612
     * briefDesc :
     * picUrl : http://p3.music.126.net/3WhzK6ozFXUsNutDU566ZA==/6641050233030995.jpg
     * img1v1Url : http://p3.music.126.net/Q92YwJrk2f2tsK-7B0VIhQ==/6628955605123612.jpg
     * albumSize : 16
     * alias : []
     * trans :
     * musicSize : 86
     * lastRank : 0
     * score : 818744
     * topicPerson : 5193
     * picId_str : 109951162845812387
     * img1v1Id_str : 109951162845816035
     * transNames : ["江海迦"]
     */
    public String name;
    public int id;
    public long picId;
    public long img1v1Id;
    public String briefDesc;
    public String picUrl;
    public String img1v1Url;
    public int albumSize;
    public String trans;
    public int musicSize;
    public int lastRank;
    public int score;
    public int topicPerson;
    public String picId_str;
    public String img1v1Id_str;
    public String[] alias;
    public String[] transNames;
}
