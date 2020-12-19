package com.d.music.online.presenter;

import android.content.Context;

import com.d.lib.aster.Aster;
import com.d.lib.aster.base.Params;
import com.d.lib.aster.callback.SimpleCallback;
import com.d.lib.common.component.mvp.MvpBasePresenter;
import com.d.lib.pulllayout.loader.CommonLoader;
import com.d.music.component.aster.API;
import com.d.music.online.model.MVCommentRespModel;
import com.d.music.online.model.MVDetailModel;
import com.d.music.online.model.MVInfoRespModel;
import com.d.music.online.model.MVSimilarRespModel;
import com.d.music.online.view.IMVDetailView;

import java.util.ArrayList;
import java.util.List;

/**
 * MVDetailPresenter
 * Created by D on 2018/8/11.
 */
public class MVDetailPresenter extends MvpBasePresenter<IMVDetailView> {

    public MVDetailPresenter(Context context) {
        super(context);
    }

    public void getMvDetailInfo(final long id) {
        Params params = new Params(API.MvDetailInfo.rtpType);
        params.addParam(API.MvDetailInfo.mvid, "" + id);
        Aster.get(API.MvDetailInfo.rtpType, params)
                .request(new SimpleCallback<MVInfoRespModel>() {
                    @Override
                    public void onSuccess(MVInfoRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        getView().setInfo(response.data);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void getSimilarMV(final long id) {
        Params params = new Params(API.SimilarMV.rtpType);
        params.addParam(API.SimilarMV.mvid, "" + id);
        Aster.get(API.SimilarMV.rtpType, params)
                .request(new SimpleCallback<MVSimilarRespModel>() {
                    @Override
                    public void onSuccess(MVSimilarRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        List<MVDetailModel> similar = new ArrayList<>();
                        if (response.mvs != null && response.mvs.size() > 0) {
                            similar.add(new MVDetailModel(MVDetailModel.TYPE_SIMILAR_HEAD));
                            similar.addAll(response.mvs);
                        }
                        getView().setSimilar(similar);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void getMVComment(final long id, final int page) {
        int offset = CommonLoader.PAGE_COUNT * (page - 1);
        int limit = CommonLoader.PAGE_COUNT;
        Params params = new Params(API.MVComment.rtpType);
        params.addParam(API.MVComment.id, "" + id);
        Aster.get(API.MVComment.rtpType, params)
                .request(new SimpleCallback<MVCommentRespModel>() {
                    @Override
                    public void onSuccess(MVCommentRespModel response) {
                        if (getView() == null) {
                            return;
                        }
                        List<MVDetailModel> comments = new ArrayList<>();
                        if (response.topComments != null) {
                            comments.addAll(response.topComments);
                        }
                        if (response.hotComments != null) {
                            comments.addAll(response.hotComments);
                        }
                        if (response.comments != null) {
                            comments.addAll(response.comments);
                        }
                        if (page == 1 && comments.size() > 0) {
                            comments.add(0, new MVDetailModel(MVDetailModel.TYPE_COMMENT_HEAD));
                        }
                        getView().loadSuccess(comments);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
}
