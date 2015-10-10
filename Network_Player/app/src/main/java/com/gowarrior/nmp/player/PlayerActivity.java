package com.gowarrior.nmp.player;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gowarrior.nmp.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by GoWarrior on 2015/7/3.
 */
public class PlayerActivity extends Activity implements
        SurfaceHolder.Callback,
        AbsMediaPlayer.OnErrorListener,
        AbsMediaPlayer.OnInfoListener,
        AbsMediaPlayer.OnCompletionListener,
        AbsMediaPlayer.OnVideoSizeChangedListener,
        AbsMediaPlayer.OnBufferingUpdateListener,
        AbsMediaPlayer.OnPreparedListener,
        AbsMediaPlayer.OnSeekCompleteListener,
        AbsMediaPlayer.OnTimedTextListener,
        MediaPlayBar.MediaPlayerControl{
    public final static String TAG = "PlayerActivity";

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private AbsMediaPlayer mPlayer;

    protected int mPosition = 0;

    private boolean isPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSurfaceView = (SurfaceView) findViewById(R.id.playsurface);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setKeepScreenOn(true);
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop ");
        super.onStop();

        if (isPlaying()) {
            mPosition = getCurrentPosition();
        }
        stop();
        destroyPlayer();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy ");
        destroyPlayer();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.v(TAG, "surfaceCreated ");
        createPlayer(surfaceHolder);
        prepareSource(onGetSource());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.v(TAG, "surfaceDestroyed ");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.v(TAG, "surfaceChanged ");
    }

    protected void createPlayer(SurfaceHolder holder) {
        Log.v(TAG, "createPlayer ");
        mPlayer = AbsMediaPlayer.getMediaPlayer();
        if (mPlayer == null)
            return;

        mPlayer.setOnErrorListener(this);
        mPlayer.setOnInfoListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnVideoSizeChangedListener(this);
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setOnTimedTextListener(this);

        reset();
        setDisplay(holder);
    }

    protected void destroyPlayer() {
        Log.v(TAG, "destroyPlayer ");
        setDisplay(null);
        release();
        mPlayer = null;
    }

    protected void stopPlayer() {
        Log.v(TAG, "stopPlayer ");

        if (isPlaying()) {
            pause();
        }

        stop();
    }

    protected void prepareSource(String uri) {
        Log.v(TAG, "prepareSource :"+uri);

        if (uri == null || uri.isEmpty()) {
            return;
        }

        try {
            setDataSource(uri);
        } catch (IllegalArgumentException e) {
            Log.v(TAG, e.getMessage().toString());
            finish();
            return;
        } catch (IllegalStateException e) {
            Log.v(TAG, e.getMessage().toString());
            finish();
            return;
        } catch (IOException e) {
            Log.v(TAG, e.getMessage().toString());
            onPlaybackError(1);
            return;
        } catch (Exception e) {
            finish();
            return;
        }

        try {
            prepareAsync();
        } catch (IllegalStateException e) {
            finish();
        } catch (Exception e) {
            finish();
        }
    }

    protected String onGetSource() {
        return null;
    }

    protected void onPlaybackError(int err) {
        return;
    }

    @Override
    public boolean onError(AbsMediaPlayer player, int whatError, int extra) {
        if (whatError == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            Log.v(TAG, "onError: Server Died " + extra);
        } else if (whatError == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            Log.v(TAG, "onError: Error Unknown " + extra);
        }

        onPlaybackError(whatError);
        return false;
    }

    @Override
    public boolean onInfo(AbsMediaPlayer player, int whatInfo, int extra) {
        if (whatInfo == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING) {
            Log.v(TAG, "onInfo: Bad Interleaving " + extra);
        } else if (whatInfo == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
            Log.v(TAG, "onInfo: Not Seekable " + extra);
        } else if (whatInfo == MediaPlayer.MEDIA_INFO_UNKNOWN) {
            Log.v(TAG, "onInfo: Unknown " + extra);
        } else if (whatInfo == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
            Log.v(TAG, "onInfo: Video Track Lagging " + extra);
        }
        return false;
    }

    @Override
    public void onCompletion(AbsMediaPlayer player) {
        Log.v(TAG, "onCompletion Called");
        mPosition = 0;
        stopPlayer();
    }

    @Override
    public void onVideoSizeChanged(AbsMediaPlayer player, int width, int height) {
        Log.v(TAG, "onVideoSizeChanged, width: " + width + ", height: " + height);
    }

    @Override
    public void onBufferingUpdate(AbsMediaPlayer player, int paramInt) {
        Log.v(TAG, "onBufferingUpdate");
    }

    @Override
    public void onPrepared(AbsMediaPlayer player) {
        Log.v(TAG, "onPrepared ");
        player.start();
        int currentPos = player.getCurrentPosition();
        int time = Math.abs(mPosition - currentPos);
        if (time > 500) {
            player.seekTo(mPosition);
        }
        mPosition = 0;
    }

    @Override
    public void onSeekComplete(AbsMediaPlayer player) {
        Log.v(TAG, "onSeekComplete");
    }

    @Override
    public void onTimedText(AbsMediaPlayer player, String text) {

    }

    public boolean isPlaying() {

        if (mPlayer != null)
            return mPlayer.isPlaying();

        return false;
    }

    public void start() {
        Log.v(TAG, "start");
        if (mPlayer != null)
            mPlayer.start();
    }

    public void pause() {
        Log.v(TAG, "pause");
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    public void stop() {
        Log.v(TAG, "stop");
        if (mPlayer != null)
            mPlayer.stop();
    }

    public void reset() {
        Log.v(TAG, "reset");
        if (mPlayer != null) {
            mPlayer.reset();
        }
    }

    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        Log.v(TAG, "setDataSource : " + path);
        if (mPlayer != null) {
            File file = new File(path);
            mPlayer.setDataSource(path);
        }
    }

    public void prepareAsync() throws IllegalStateException {
        Log.v(TAG, "prepareAsync");
        if (mPlayer != null) {
            mPlayer.prepareAsync();
        }
    }

    public void setDisplay(SurfaceHolder holder) {
        Log.v(TAG, "setDisplay : " + holder);
        if (mPlayer != null) {
            mPlayer.setDisplay(holder);
        }
    }

    public void release() {
        Log.v(TAG, "release");
        if (mPlayer != null) {
            mPlayer.release();
        }
    }

    public void seekTo(int pos) {
        Log.v(TAG, "seekTo "+pos);
        if (mPlayer != null)
            mPlayer.seekTo(pos*1000);
    }

    /* return unit: second */
    public int getCurrentPosition() {
        //Log.v(TAG, "getCurrentPosition");
        if (mPlayer != null)
            return mPlayer.getCurrentPosition()/1000;

        return 0;
    }

    /* return unit: second */
    public int getDuration() {
        if (mPlayer != null)
            return mPlayer.getDuration()/1000;

        return 0;
    }

    public int getBufferPercentage() {
        return 0;
    }

    public String getSourceName() {
        return null;
    }
}
