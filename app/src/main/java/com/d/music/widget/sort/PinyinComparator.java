package com.d.music.widget.sort;

import android.text.TextUtils;

import com.d.lib.common.util.log.ULog;
import com.d.music.data.database.greendao.bean.MusicModel;

import java.util.Comparator;

/**
 * PinyinComparator
 * Created by D on 2017/6/7.
 */
class PinyinComparator implements Comparator<MusicModel> {
    @Override
    public int compare(MusicModel o1, MusicModel o2) {
        int ret;
        if (TextUtils.equals(o1.exLetter, "#")) {
            ret = 1;
        } else if (TextUtils.equals(o2.exLetter, "#")) {
            ret = -1;
        } else {
            ret = o1.exPinyin.compareTo(o2.exPinyin);
        }
        ULog.d("D_compare:" + ret + " --o1:" + o1.exPinyin + " --o2:" + o2.exPinyin);
        return ret;
    }
}
