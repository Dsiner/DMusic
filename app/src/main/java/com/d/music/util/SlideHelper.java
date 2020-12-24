package com.d.music.util;

import com.d.lib.slidelayout.SlideLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Help manage the slide
 * Created by D on 2017/5/30.
 */
public class SlideHelper {
    private final List<SlideLayout> mSlides = new ArrayList<>();

    public SlideHelper() {
    }

    public void onStateChanged(SlideLayout layout, boolean open) {
        if (open) {
            mSlides.add(layout);
        } else {
            mSlides.remove(layout);
        }
    }

    public boolean closeAll(SlideLayout layout) {
        if (mSlides.size() <= 0) {
            return false;
        }
        boolean result = false;
        for (int i = 0; i < mSlides.size(); i++) {
            SlideLayout slide = mSlides.get(i);
            if (slide != null && slide != layout) {
                slide.close();
                mSlides.remove(slide); // Unnecessary
                result = true;
                i--;
            }
        }
        return result;
    }
}
