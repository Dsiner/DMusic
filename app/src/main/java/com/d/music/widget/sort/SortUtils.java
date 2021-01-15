package com.d.music.widget.sort;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.d.music.data.database.greendao.bean.MusicModel;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SortUtil
 * Created by D on 2017/6/7.
 */
public class SortUtils {
    private List<MusicModel> datas;
    private Map<String, int[]> letterMap;
    private int lastFirstVisibleItem = -1;

    public void onScrolled(RecyclerView recyclerView, View layout, TextView tvLetter) {
        if (recyclerView == null || layout == null || tvLetter == null || letterMap == null || datas == null) {
            return;
        }
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager == null || !(manager instanceof LinearLayoutManager)) {
            return;
        }
        int firstVisibleItemPosition = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();

        int[] value = letterMap.get(datas.get(firstVisibleItemPosition).exLetter);
        int nextSectionPosition = value != null ? value[1] : -1;
        if (firstVisibleItemPosition != lastFirstVisibleItem) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
            params.topMargin = 0;
            layout.setLayoutParams(params);
            tvLetter.setText(datas.get(firstVisibleItemPosition).exLetter);
        }
        if (nextSectionPosition == firstVisibleItemPosition + 1) {
            View childView = recyclerView.getChildAt(0);
            if (childView != null) {
                int titleHeight = layout.getHeight();
                int bottom = childView.getBottom();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
                if (bottom < titleHeight) {
                    float pushedDistance = bottom - titleHeight;
                    params.topMargin = (int) pushedDistance;
                    layout.setLayoutParams(params);
                } else {
                    if (params.topMargin != 0) {
                        params.topMargin = 0;
                        layout.setLayoutParams(params);
                    }
                }
            }
        }
        lastFirstVisibleItem = firstVisibleItemPosition;
    }

    public void onChange(int index, String c, RecyclerView recyclerView) {
        if (recyclerView == null || letterMap == null) {
            return;
        }
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm == null || !(lm instanceof LinearLayoutManager)) {
            return;
        }
        LinearLayoutManager manager = (LinearLayoutManager) lm;
        if (index == 0) {
            manager.scrollToPositionWithOffset(0, 0);//置顶
            return;
        }
        int[] value = letterMap.get(c);
        if (value != null) {
            manager.scrollToPositionWithOffset(value[0] + 2, 0);//+1 refresh header、+1 header
        }
    }

    public Map<String, int[]> sortDatas(List<MusicModel> list) {
        letterMap = new HashMap<>();
        if (list == null || list.size() <= 0) {
            return letterMap;
        }
        int count = list.size();
        for (int i = 0; i < count; i++) {
            MusicModel bean = list.get(i);
            String pinyin = Pinyin.toPinyin(bean.songName, "");
            pinyin = TextUtils.isEmpty(pinyin) ? "" : pinyin.toUpperCase();
            String letter = pinyin.substring(0, 1);
            //正则表达式，判断首字母是否是英文字母/数字/其他
            if (letter.matches("[0-9]")) {
                letter = "☆";
            } else if (!letter.matches("[A-Z]")) {
                letter = "#";
            }
            bean.exPinyin = pinyin;
            bean.exLetter = letter;
            bean.exIsLetter = false;
        }
        Collections.sort(list, new PinyinComparator());
        String key = null;
        int[] value = null;//is[0]:thisSectionPosition,is[1]:nextSectionPosition
        for (int i = 0; i < count; i++) {
            MusicModel b = list.get(i);
            if (!TextUtils.equals(key, b.exLetter)) {
                key = b.exLetter;
                b.exIsLetter = true;
                if (value != null) {
                    value[1] = i;
                }
                value = new int[]{i, -1};
                letterMap.put(key, value);
            }
        }
        datas = list;
        return letterMap;
    }
}
