package com.zejian.myapplication.base;

import android.media.MediaPlayer;

import com.blankj.utilcode.util.ToastUtils;

public class DtAudioPlayer {

    private static final String TAG = DtAudioPlayer.class.getSimpleName();
    private static DtAudioPlayer sInstance = new DtAudioPlayer();

    private Callback mPlayCallback;
    private MediaPlayer mPlayer;
    private String playingUrl;

    private DtAudioPlayer() {

    }

    public static DtAudioPlayer getInstance() {
        return sInstance;
    }


    public boolean startPlay(String url, Callback callback) {
        if(url == null || url.isEmpty()){
            return false;
        }
        stopPlay();
        playingUrl = url;
        mPlayCallback = callback;
        try {
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(url);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopInternalPlay();
                    onPlayCompleted(true);
                }
            });
            mPlayer.prepare();
            mPlayer.start();
            return true;
        } catch (Exception e) {
            ToastUtils.showShort("语音文件已损坏或不存在");
            stopInternalPlay();
            onPlayCompleted(false);
        }
        return false;
    }

    public void stopPlay() {
        stopInternalPlay();
        onPlayCompleted(false);
        mPlayCallback = null;
    }


    public void stopPlayNotCallback() {
        stopInternalPlay();
        playingUrl = null;
        mPlayer = null;
        mPlayCallback = null;
    }


    private void stopInternalPlay() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.release();
        mPlayer = null;
    }

    public boolean isPlaying() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    private void onPlayCompleted(boolean success) {
        playingUrl = null;
        if (mPlayCallback != null) {
            mPlayCallback.onCompletion(success);
        }
        mPlayer = null;
    }


    public String getPlayingUrl() {
        return playingUrl;
    }


    public interface Callback {
        void onCompletion(Boolean success);
    }

}
