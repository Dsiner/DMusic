package com.d.dmusic.view.lrc;

import java.util.List;

/**
 * ILrcParser
 * Edited by D on 2017/5/16.
 */
interface ILrcParser {
    List<LrcRow> getLrcRows(String str);
}
