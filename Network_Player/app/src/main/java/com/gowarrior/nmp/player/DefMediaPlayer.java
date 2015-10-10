package com.gowarrior.nmp.player;

import android.media.MediaPlayer;
import android.media.TimedText;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by hong.zhang on 2015/7/2.
 */
public class DefMediaPlayer extends AbsMediaPlayer implements
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnTimedTextListener{
    public final static String TAG = "DefMediaPlayer";

    protected static DefMediaPlayer sInstance = null;

    private MediaPlayer mPlayer = null;

    protected AbsMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = null;
    protected AbsMediaPlayer.OnCompletionListener mOnCompletionListener = null;
    protected AbsMediaPlayer.OnErrorListener mOnErrorListener = null;
    protected AbsMediaPlayer.OnInfoListener mOnInfoListener = null;
    protected AbsMediaPlayer.OnPreparedListener mOnPreparedListener = null;
    protected AbsMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = null;
    protected AbsMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = null;
    protected AbsMediaPlayer.OnTimedTextListener mOnTimedTextListener = null;

    private boolean mIsReady = false;

    protected DefMediaPlayer() {

    }
    public static AbsMediaPlayer getInstance() {
        if (sInstance == null)
            sInstance = new DefMediaPlayer();

        sInstance.createPlayer();
        return sInstance;
    }

    private void createPlayer() {
        if (mPlayer == null) {
            Log.d(TAG, "new MediaPlayer");
            mIsReady = false;
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnInfoListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnVideoSizeChangedListener(this);
            mPlayer.setOnBufferingUpdateListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnSeekCompleteListener(this);
            mPlayer.setOnTimedTextListener(this);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        return mOnErrorListener.onError(this, i, i2);
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        return mOnInfoListener.onInfo(this, i, i2);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mOnCompletionListener.onCompletion(this);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
        mOnVideoSizeChangedListener.onVideoSizeChanged(this, i, i2);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        mOnBufferingUpdateListener.onBufferingUpdate(this, i);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mIsReady = true;
        mOnPreparedListener.onPrepared(this);
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mOnSeekCompleteListener.onSeekComplete(this);
    }

    @Override
    public void onTimedText(MediaPlayer mediaPlayer, TimedText timedText) {
        if (mOnTimedTextListener == null || timedText == null)
            return;

        mOnTimedTextListener.onTimedText(this, timedText.getText());
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        mOnBufferingUpdateListener = listener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        mOnErrorListener = listener;
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        mOnInfoListener = listener;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        mOnSeekCompleteListener = listener;
    }

    @Override
    public void setOnTimedTextListener(OnTimedTextListener listener) {
        mOnTimedTextListener = listener;
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        mOnVideoSizeChangedListener = listener;
    }

    @Override
    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        if (mIsReady)
            return mPlayer.getDuration();
        else
            return -1;
    }

    @Override
    public int getVideoHeight() {
        return mPlayer.getVideoHeight();
    }

    @Override
    public int getVideoWidth() {
        return mPlayer.getVideoWidth();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        mPlayer.prepare();
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mPlayer.prepareAsync();
    }

    @Override
    public void release() {
        mIsReady = false;
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void reset() {
        mIsReady = false;
        mPlayer.reset();
    }

    @Override
    public void seekTo(int pos) {
        if (mIsReady)
            mPlayer.seekTo(pos);
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException,
            SecurityException, IllegalStateException {
        mIsReady = false;
        mPlayer.setDataSource(path);
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException,
            SecurityException, IllegalStateException {
        mIsReady = false;
        mPlayer.setDataSource(fd);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        mPlayer.setDisplay(holder);
    }

    @Override
    public void start() {
        mPlayer.start();
    }

    @Override
    public void stop() {
        mIsReady = false;
        mPlayer.stop();
    }

    @Override
    public void addTimedTextSource(String path) {
        try {
            if (path == null || path.isEmpty())
                return;

            mPlayer.addTimedTextSource(path, MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void selectTrack(int index) {
        try {
            mPlayer.selectTrack(index);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public Integer[] findTrackIndexFor(int mediaTrackType) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        try {
            MediaPlayer.TrackInfo[] trackInfo = mPlayer.getTrackInfo();
            for (int i = 0; i < trackInfo.length; i++) {
                if (trackInfo[i].getTrackType() == mediaTrackType) {
                    list.add(i);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return list.toArray(new Integer[0]);
    }

    @Override
    public MediaPlayer.TrackInfo[] getTrackInfo() {
        MediaPlayer.TrackInfo[] trackInfo = null;
        try {
            trackInfo = mPlayer.getTrackInfo();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return trackInfo;
    }

    @Override
    public void setVideoScalingMode(int mode) {
        try {
            mPlayer.setVideoScalingMode(mode);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
