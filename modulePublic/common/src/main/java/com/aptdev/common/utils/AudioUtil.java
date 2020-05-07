package com.aptdev.common.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by Administrator on 2017/11/27.
 */

public class AudioUtil {
    private AudioManager audioManager;
    private static volatile AudioUtil instance;

    private AudioUtil(Context mContext) {
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public static AudioUtil getInstance(Context mContext) {
        if (instance == null) {
            synchronized(AudioUtil.class) {
                if (instance == null) {
                    instance = new AudioUtil(mContext);
                }
            }
        }
        return instance;
    }


    //获得多媒体最大音量
    public int getMediaMaxVolume() {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public int getMediaVolume() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    //通话最大音量
    public int getCallMaxVolume() {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
    }

    //系统最大音量
    public int getSystemMaxVolume() {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    }

    public int getSystemVolume() {
        return audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    //提示音最大音量
    public int getAlarmMaxVolume() {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
    }

    //设置多媒体音量
    public void setMediaVolume(int volume) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    //设置通话音量
    public void setCallVolume(int volume) {
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, AudioManager.STREAM_VOICE_CALL);
    }

    //通话时可设置免提或者喇叭
    public void setSpeakerStatus(boolean on) {
        if (on) {
            audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_NORMAL);
        } else {
            int maxCallVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, maxCallVolume, AudioManager.STREAM_VOICE_CALL);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(false);
        }
    }

}
