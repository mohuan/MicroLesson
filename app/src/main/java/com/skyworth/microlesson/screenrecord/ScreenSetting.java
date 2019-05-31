package com.skyworth.microlesson.screenrecord;

import android.content.Context;
import android.media.MediaCodecInfo;
import android.widget.SpinnerAdapter;

import com.skyworth.microlesson.ui.main.activity.MainActivity;

/**
 * 分离主类方法
 *
 */
public class ScreenSetting {

    private Context context;


    public ScreenSetting(Context context ){
        this.context = context;
    }


    public VideoEncodeConfig createVideoConfig() {

        // 1.  OMX.google.h264.encoder
        // 2.

        final String codec = "OMX.google.h264.encoder";
        // video size
        int width = 1920;
        int height = 1080;
        int framerate = 15;
        int iframe = 1;
        int bitrate = 800;
        MediaCodecInfo.CodecProfileLevel profileLevel = Utils.toProfileLevel("Default");
        return new VideoEncodeConfig(width, height, bitrate,
                framerate, iframe, codec, ScreenRecorder.VIDEO_AVC, profileLevel);
    }

    public AudioEncodeConfig createAudioConfig() {
        String codec = "OMX.google.aac.encoder";
        int bitrate = 80;
        int samplerate = 44100;
        int channelCount = 1;
        int profile = MediaCodecInfo.CodecProfileLevel.AACObjectMain;

        return new AudioEncodeConfig(codec, ScreenRecorder.AUDIO_AAC, bitrate, samplerate, channelCount, profile);
    }


}
