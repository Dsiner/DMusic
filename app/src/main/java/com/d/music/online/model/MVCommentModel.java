package com.d.music.online.model;

import java.util.List;

/**
 * MVCommentModel
 * Created by D on 2018/8/13.
 */
public class MVCommentModel extends MVDetailModel {

    /**
     * user : user
     * beReplied : []
     * pendantData : null
     * liked : false
     * commentId : 1214282259
     * expressionUrl : null
     * likedCount : 0
     * time : 1534146743300
     * content : 别的不说，吴宣仪真的好看啊
     * isRemoveHotComment : false
     */
    public UserBean user;
    public Object pendantData;
    public boolean liked;
    public int commentId;
    public Object expressionUrl;
    public int likedCount;
    public long time;
    public String content;
    public boolean isRemoveHotComment;
    public List<UserBean> beReplied;

    public MVCommentModel() {
        this.view_type = TYPE_COMMENT;
    }

    public static class UserBean {

        /**
         * locationInfo : null
         * authStatus : 0
         * remarkName : null
         * avatarUrl : http://p1.music.126.net/NH8bex9lPWhknY_1Jas_sQ==/3264450023090497.jpg
         * experts : null
         * vipType : 10
         * nickname : Asia阿傻天后
         * vipRights : {"musicPackage":{"vipCode":200,"rights":true}}
         * userId : 84784198
         * userType : 0
         * expertTags : null
         */
        public Object locationInfo;
        public int authStatus;
        public Object remarkName;
        public String avatarUrl;
        public Object experts;
        public int vipType;
        public String nickname;
        public VipRightsBean vipRights;
        public int userId;
        public int userType;
        public Object expertTags;

        public static class VipRightsBean {

            /**
             * musicPackage : {"vipCode":200,"rights":true}
             */
            public MusicPackageBean musicPackage;

            public static class MusicPackageBean {

                /**
                 * vipCode : 200
                 * rights : true
                 */
                public int vipCode;
                public boolean rights;
            }
        }
    }
}