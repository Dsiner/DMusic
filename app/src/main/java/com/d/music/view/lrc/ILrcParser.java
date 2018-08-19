package com.d.music.view.lrc;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * ILrcParser
 * Edited by D on 2017/5/16.
 */
interface ILrcParser {
    @NonNull
    List<LrcRow> getLrcRows(String str);
}
