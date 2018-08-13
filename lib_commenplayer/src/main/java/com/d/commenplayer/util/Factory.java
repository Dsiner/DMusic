package com.d.commenplayer.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.d.commenplayer.listener.IRenderView;
import com.d.commenplayer.media.SurfaceRenderView;
import com.d.commenplayer.media.TextureRenderView;

import java.util.Locale;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;

/**
 * Factory
 * Created by D on 2017/5/28.
 */
public class Factory {
    //-------------------------
    // Extend: Render
    //-------------------------
    public static final int RENDER_NONE = 0;
    public static final int RENDER_SURFACE_VIEW = 1;
    public static final int RENDER_TEXTURE_VIEW = 2;

    public static IRenderView initRenders(Context context) {
        int render = RENDER_NONE;
        Settings settings = new Settings(context.getApplicationContext());

        if (settings.getEnableTextureView()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                render = RENDER_TEXTURE_VIEW;
            } else {
                render = RENDER_SURFACE_VIEW;
            }
        } else if (settings.getEnableSurfaceView()) {
            render = RENDER_SURFACE_VIEW;
        } else if (settings.getEnableNoView()) {
            render = RENDER_NONE;
        }

        switch (render) {
            case RENDER_NONE:
                return null;
            case RENDER_TEXTURE_VIEW:
                return new TextureRenderView(context);
            case RENDER_SURFACE_VIEW:
                return new SurfaceRenderView(context);
            default:
                MLog.e(String.format(Locale.getDefault(), "invalid render %d", render));
                return null;
        }
    }

    public static IMediaPlayer createPlayer(Context context, int playerType) {
        IMediaPlayer mediaPlayer = null;
        Settings mSettings = new Settings(context.getApplicationContext());

        switch (playerType) {
            case Settings.PV_PLAYER__IjkExoMediaPlayer: {
                IjkExoMediaPlayer IjkExoMediaPlayer = new IjkExoMediaPlayer(context.getApplicationContext());
                mediaPlayer = IjkExoMediaPlayer;
            }
            break;
            case Settings.PV_PLAYER__AndroidMediaPlayer: {
                AndroidMediaPlayer androidMediaPlayer = new AndroidMediaPlayer();
                mediaPlayer = androidMediaPlayer;
            }
            break;
            case Settings.PV_PLAYER__IjkMediaPlayer:
            default: {
                IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
                ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
                if (mSettings.getUsingMediaCodec()) {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
                    if (mSettings.getUsingMediaCodecAutoRotate()) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
                    }
                    if (mSettings.getMediaCodecHandleResolutionChange()) {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
                    } else {
                        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
                    }
                } else {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
                }

                if (mSettings.getUsingOpenSLES()) {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
                } else {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
                }

                String pixelFormat = mSettings.getPixelFormat();
                if (TextUtils.isEmpty(pixelFormat)) {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
                } else {
                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
                }
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);

                //设置rtsp的连接模式为：tcp/udp
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "rtsp_transport", "tcp");

                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

                mediaPlayer = ijkMediaPlayer;
            }
            break;
        }

        if (mSettings.getEnableDetachedSurfaceTextureView()) {
            mediaPlayer = new TextureMediaPlayer(mediaPlayer);
        }

        return mediaPlayer;
    }
}
