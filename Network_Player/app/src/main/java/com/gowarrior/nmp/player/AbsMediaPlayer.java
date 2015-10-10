package com.gowarrior.nmp.player;

import android.media.MediaPlayer;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by hong.zhang on 2015/7/2.
 */
public abstract class AbsMediaPlayer {

    protected static AbsMediaPlayer getDefMediaPlayer() {
        return DefMediaPlayer.getInstance();
    }

    public static AbsMediaPlayer getMediaPlayer() {
        return getDefMediaPlayer();
    }
    public abstract void setOnBufferingUpdateListener(OnBufferingUpdateListener listener);
    public abstract void setOnCompletionListener(OnCompletionListener listener);
    public abstract void setOnErrorListener(OnErrorListener listener);
    public abstract void setOnInfoListener(OnInfoListener listener);
    public abstract void setOnPreparedListener(OnPreparedListener listener);
    public abstract void setOnSeekCompleteListener(OnSeekCompleteListener listener);
    public abstract void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener);
    public abstract void setOnTimedTextListener(OnTimedTextListener listener);

    public static interface OnBufferingUpdateListener {
        public abstract void onBufferingUpdate(AbsMediaPlayer player, int paramInt);
    }

    public static interface OnCompletionListener {
        public abstract void onCompletion(AbsMediaPlayer player);
    }

    public static interface OnErrorListener {
        public abstract boolean onError(AbsMediaPlayer player, int whatError, int extra);
    }

    public static interface OnInfoListener{
        public abstract boolean onInfo(AbsMediaPlayer player, int whatInfo, int extra);
    }

    public static interface OnPreparedListener {
        public abstract void onPrepared(AbsMediaPlayer player);
    }

    public static interface OnSeekCompleteListener {
        public abstract void onSeekComplete(AbsMediaPlayer player);
    }

    public static interface OnVideoSizeChangedListener {
        public abstract void onVideoSizeChanged(AbsMediaPlayer player, int width, int height);
    }
    public static interface OnTimedTextListener {
        public abstract void onTimedText(AbsMediaPlayer player, String text);
    }

    public abstract int getCurrentPosition();
    public abstract int getDuration();
    public abstract int getVideoHeight();
    public abstract int getVideoWidth();

    public abstract boolean isPlaying();
    public abstract void pause();
    public abstract void prepare() throws IOException, IllegalStateException;
    public abstract void prepareAsync() throws IllegalStateException ;
    public abstract void release();
    public abstract void reset();
    public abstract void seekTo(int pos);
    public abstract void setDataSource(String path) throws IOException,
            IllegalArgumentException, SecurityException, IllegalStateException;
    public abstract void setDataSource(FileDescriptor fd) throws IOException,
            IllegalArgumentException, SecurityException, IllegalStateException;
    public abstract void setDisplay(SurfaceHolder holder);
    public abstract void start();
    public abstract void stop();

    public abstract void addTimedTextSource(String path);

    public abstract void selectTrack(int index);
    public abstract Integer[] findTrackIndexFor(int mediaTrackType);
    public MediaPlayer.TrackInfo[] getTrackInfo() {
        return null;
    }

    public abstract void setVideoScalingMode(int mode);
}
