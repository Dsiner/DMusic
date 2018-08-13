package com.d.music.online.model;

import java.util.List;

/**
 * MVCommentRespModel
 * Created by D on 2018/8/13.
 */
public class MVCommentRespModel {

    /**
     * isMusician : false
     * userId : -1
     * topComments : []
     * moreHot : true
     * hotComments : hotComments
     * code : 200
     * comments : comments
     * total : 6821
     * more : true
     */
    public boolean isMusician;
    public int userId;
    public boolean moreHot;
    public int code;
    public int total;
    public boolean more;
    public List<MVCommentModel> topComments;
    public List<MVCommentModel> hotComments;
    public List<MVCommentModel> comments;
}
