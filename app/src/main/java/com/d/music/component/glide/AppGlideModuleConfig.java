package com.d.music.component.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.annotation.GlideModule;
import com.d.lib.common.component.glide.AbstractGlideModule;
import com.d.music.data.Constants;

/**
 * Glide global configuration
 * Created by D on 2017/8/10.
 */
@GlideModule
public class AppGlideModuleConfig extends AbstractGlideModule {

    @NonNull
    @Override
    protected String getCachePath() {
        return Constants.Path.GLIDE_CACHE;
    }
}
