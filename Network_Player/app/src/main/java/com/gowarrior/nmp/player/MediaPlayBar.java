package com.gowarrior.nmp.player;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gowarrior.nmp.R;

import java.lang.ref.WeakReference;

/**
 * Created by GoWarrior on 2015/7/3.
 */
public class MediaPlayBar extends RelativeLayout {
    public final static String TAG = "MediaPlaybar";

    private SeekBar mSeekBar;
    private TextView mTitle;
    private TextView mTime;
    private MediaPlayerControl  mPlayer;
    private Handler mHandler = new MessageHandler(this);
    private Runnable mRunnable = null;

    private static final int MSG_FADE_OUT = 1;
    private static final int MSG_SHOW_PROGRESS = 2;

    private int mTimeout = 5000;
    private int mPosition;

    private boolean mShowing = false;
    private boolean mFreezing = false;
    private boolean mSeeking = false;
    private boolean mPositionChanged = false;

    public MediaPlayBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MediaPlayBar(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.mediaplaybar, this);

        mSeekBar = (SeekBar) findViewById(R.id.playbar_slider);
        mTitle = (TextView) findViewById(R.id.playbar_title);
        mTime = (TextView) findViewById(R.id.playbar_time);

        mSeekBar.setOnSeekBarChangeListener(new seekBarListener());
    }

    public void initBar(int position) {
        if (mSeekBar != null) {
            mSeekBar.setMax(mPlayer.getDuration());
            mSeekBar.setProgress(position);
            mSeekBar.setKeyProgressIncrement(1);
        }
        doSeek(position);

    }

    class seekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            Log.v(TAG, "onProgressChanged :" + i +" fromuser:"+ b);

            setPosition(i);

            if (!b) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            resetAutoHideTimer(mTimeout);

            mPositionChanged = true;
            mPosition = i;
            mHandler.removeCallbacks(mRunnable);
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    Log.v(TAG, "mPosition:"+mPosition+" mPositionChanged:"+mPositionChanged);
                    if (mPositionChanged) {
                        doSeek(mPosition);
                    }
                }
            };
            mHandler.postDelayed(mRunnable, 1500);

            mSeeking = true;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.v(TAG, "onStartTrackingTouch");
            setProgress();
            startGetCurPos(0);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.v(TAG, "onStopTrackingTouch");
            setProgress();
            startGetCurPos(0);
        }
    }

    private static class MessageHandler extends Handler {
        private final WeakReference<MediaPlayBar> mView;

        MessageHandler(MediaPlayBar view) {
            mView = new WeakReference<MediaPlayBar>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            MediaPlayBar view = mView.get();
            if (view == null) {
                return;
            }

            int pos;

            switch (msg.what) {
                case MSG_FADE_OUT:
                    view.hide();
                    break;
                case MSG_SHOW_PROGRESS:
                    if (!view.isSeeking()) {
                        pos = view.setProgress();
                        msg = obtainMessage(MSG_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    }
/*
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean skipKey = false;

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (!isSeeking()) {
                        skipKey = true;
                    }
                    break;
            }
        }

        if (skipKey)
            return false;

        return super.dispatchKeyEvent(event);
    }
*/
    public interface MediaPlayerControl {
        void    start();
        void    pause();
        int     getDuration();
        int     getCurrentPosition();
        void    seekTo(int pos);
        boolean isPlaying();
        int     getBufferPercentage();
        String   getSourceName();
    }

    private void resetAutoHideTimer(int timeout) {
        mHandler.removeMessages(MSG_FADE_OUT);
        if (timeout > 0) {
            Log.v(TAG, "reset MSG_FADE_OUT");
            Message msg = mHandler.obtainMessage(MSG_FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    private String stringForTime(int time) {
        String str;
        int seconds = time % 60;
        int minutes = (time / 60) % 60;
        int hours   = time / 3600;

        if (hours > 0) {
            str = String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            str = String.format("%02d:%02d", minutes, seconds);
        }

        return str;
    }

    private int setProgress() {
        int position;

        if (mPlayer == null) {
            return 0;
        }

        if (isSeeking()) {
            return 0;
        }

        position = mPlayer.getCurrentPosition();

        Log.v(TAG, "setProgress :"+position);

        mSeekBar.setProgress(position);

        return position;
    }

    private void setPosition(int position) {
        int duration;
        Log.v(TAG, "setPosition :"+position);
        duration = mPlayer.getDuration();

        if (mTime != null) {
            mTime.setText(stringForTime(position)+"/"+stringForTime(duration));
        }
    }

    private void doSeek(int pos) {
        int total = mPlayer.getDuration();
        int target;

        mPositionChanged = false;

        if (pos >= total)
            target = total;
        else
            target = pos;

        Log.v(TAG, "doSeek pos:"+pos+" target:"+target);
        mPlayer.seekTo(target);
    }

    public void setTimeout(int timeout) {
        mTimeout = timeout;
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
    }

    public void show(int timeout) {
        Log.v(TAG, "show :"+timeout);
        if (!mShowing) {
            setVisibility(VISIBLE);
            mShowing = true;
        }

        resetAutoHideTimer(timeout);
        startGetCurPos(0);
    }

    public void show() {
        if (mFreezing)
            show(0);
        else
            show(mTimeout);
    }

    public void hide() {
        Log.v(TAG, "hide");
        setVisibility(INVISIBLE);
        mPositionChanged = false;
        mShowing = false;
        mSeeking = false;
    }

    public boolean isShowing() {
        return mShowing;
    }

    public boolean isFreezing() {
        return mFreezing;
    }

    public void setFreezing(boolean b) {
        mFreezing = b;
    }

    public boolean isSeeking() {
        return mSeeking;
    }

    public void setSeeking(boolean b) {
        mSeeking = b;
    }

    public void startGetCurPos(int timeout) {
        mHandler.removeMessages(MSG_SHOW_PROGRESS);
        mHandler.sendEmptyMessageDelayed(MSG_SHOW_PROGRESS, timeout);
    }

    public void stopGetCurPos() {
        mHandler.removeMessages(MSG_SHOW_PROGRESS);
    }

    public void setTitle(String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }
}