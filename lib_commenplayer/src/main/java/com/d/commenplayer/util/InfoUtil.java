package com.d.commenplayer.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.Locale;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaFormat;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkMediaFormat;

/**
 * Util
 * Created by D on 2017/5/27.
 */
public class InfoUtil {

    //-------------------------
    // Extend: Background
    //-------------------------
    public static void showMediaInfo(Context context, IMediaPlayer mMediaPlayer) {
        if (mMediaPlayer == null)
            return;

        int mVideoWidth = mMediaPlayer.getVideoWidth();
        int mVideoHeight = mMediaPlayer.getVideoHeight();
        int mVideoSarNum = mMediaPlayer.getVideoSarNum();
        int mVideoSarDen = mMediaPlayer.getVideoSarDen();

        int selectedVideoTrack = MediaPlayerCompat.getSelectedTrack(mMediaPlayer, ITrackInfo.MEDIA_TRACK_TYPE_VIDEO);
        int selectedAudioTrack = MediaPlayerCompat.getSelectedTrack(mMediaPlayer, ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
        int selectedSubtitleTrack = MediaPlayerCompat.getSelectedTrack(mMediaPlayer, ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);

        //media_information
        MLog.d("Player" + MediaPlayerCompat.getName(mMediaPlayer));
        MLog.d("Resolution" + buildResolution(mVideoWidth, mVideoHeight, mVideoSarNum, mVideoSarDen));
        MLog.d("Length" + buildTimeMilli(mMediaPlayer.getDuration()));

        ITrackInfo trackInfos[] = mMediaPlayer.getTrackInfo();
        if (trackInfos != null) {
            int index = -1;
            for (ITrackInfo trackInfo : trackInfos) {
                index++;

                int trackType = trackInfo.getTrackType();
                if (index == selectedVideoTrack) {
                    MLog.d("Stream #" + index + " mi__selected_video_track");
                } else if (index == selectedAudioTrack) {
                    MLog.d("Stream #" + index + " mi__selected_audio_track");
                } else if (index == selectedSubtitleTrack) {
                    MLog.d("Stream #" + index + " mi__selected_subtitle_track");
                } else {
                    MLog.d("Stream #" + index);
                }
                MLog.d("Type" + buildTrackType(context, trackType));
                String language = TextUtils.isEmpty(trackInfo.getLanguage()) ? "und" : trackInfo.getLanguage();
                MLog.d("Language:" + language);

                IMediaFormat mediaFormat = trackInfo.getFormat();
                if (mediaFormat == null) {
                } else if (mediaFormat instanceof IjkMediaFormat) {
                    switch (trackType) {
                        case ITrackInfo.MEDIA_TRACK_TYPE_VIDEO:
                            MLog.d("Codec:" + mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI));
                            MLog.d("Profile level:" + mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI));
                            MLog.d("Pixel format:" + mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PIXEL_FORMAT_UI));
                            MLog.d("Resolution:" + mediaFormat.getString(IjkMediaFormat.KEY_IJK_RESOLUTION_UI));
                            MLog.d("Frame rate:" + mediaFormat.getString(IjkMediaFormat.KEY_IJK_FRAME_RATE_UI));
                            MLog.d("Bit rate:" + mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI));
                            break;
                        case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                            MLog.d("Codec:" + mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI));
                            MLog.d("Profile level:" + mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI));
                            MLog.d("Sample rate:" + mediaFormat.getString(IjkMediaFormat.KEY_IJK_SAMPLE_RATE_UI));
                            MLog.d("Channels:" + mediaFormat.getString(IjkMediaFormat.KEY_IJK_CHANNEL_UI));
                            MLog.d("Bit rate:" + mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private static String buildResolution(int width, int height, int sarNum, int sarDen) {
        StringBuilder sb = new StringBuilder();
        sb.append(width);
        sb.append(" x ");
        sb.append(height);

        if (sarNum > 1 || sarDen > 1) {
            sb.append("[");
            sb.append(sarNum);
            sb.append(":");
            sb.append(sarDen);
            sb.append("]");
        }

        return sb.toString();
    }

    private static String buildTimeMilli(long duration) {
        long total_seconds = duration / 1000;
        long hours = total_seconds / 3600;
        long minutes = (total_seconds % 3600) / 60;
        long seconds = total_seconds % 60;
        if (duration <= 0) {
            return "--:--";
        }
        if (hours >= 100) {
            return String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }

    private static String buildTrackType(Context context, int type) {
        switch (type) {
            case ITrackInfo.MEDIA_TRACK_TYPE_VIDEO:
                return "TrackType_video";
            case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                return "TrackType_audio";
            case ITrackInfo.MEDIA_TRACK_TYPE_SUBTITLE:
                return "TrackType_subtitle";
            case ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                return "TrackType_timedtext";
            case ITrackInfo.MEDIA_TRACK_TYPE_METADATA:
                return "TrackType_metadata";
            case ITrackInfo.MEDIA_TRACK_TYPE_UNKNOWN:
            default:
                return "TrackType_unknown";
        }
    }
}
