package com.d.music.view.sort;

import android.text.TextUtils;

import com.d.music.module.greendao.music.base.MusicModel;
import com.d.lib.common.utils.log.ULog;

import java.util.Comparator;

/**
 * PinyinComparator
 * Created by D on 2017/6/7.
 */
class PinyinComparator implements Comparator<MusicModel> {
    @Override
    public int compare(MusicModel o1, MusicModel o2) {
        int ret;
        if (TextUtils.equals(o1.letter, "#")) {
            ret = 1;
//            return 1;
        } else if (TextUtils.equals(o2.letter, "#")) {
            ret = -1;
//            return -1;
        } else {
            ret = o1.pinyin.compareTo(o2.pinyin);
//            return o1.pinyin.compareTo(o2.pinyin);
        }
        ULog.d("D_compare:" + ret + "--o1:" + o1.pinyin + "--o2:" + o2.pinyin);
        return ret;
    }
}
