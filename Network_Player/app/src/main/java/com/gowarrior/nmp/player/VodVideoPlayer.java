package com.gowarrior.nmp.player;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gowarrior.nmp.R;
import com.gowarrior.nmp.common.VodData;
import com.gowarrior.nmp.common.VodData.ItemPageInfoProperty;
import com.gowarrior.nmp.common.VodData.ItemProperty;
import com.gowarrior.nmp.common.VodData.ItemDetailProperty;
import com.gowarrior.nmp.common.VodData.ItemRecordPageInfoProperty;
import com.gowarrior.nmp.common.VodData.ItemRecordProperty;
import com.gowarrior.nmp.common.VodModel;

import java.io.IOException;

/**
 * Created by GoWarrior on 2015/7/3.
 */
public class VodVideoPlayer extends PlayerActivity {
    public final static String TAG = "VodVideoPlayer";

    private ImageView mPause;
    private TextView mBuffering;
    private MediaPlayBar mPlayBar;

    private boolean isPause = false;

    private String mPlayUrl;
    private int mPosition;
    private String mTitle;

    boolean isDialogShowing = false;
    private Handler mDialogHandler = new Handler();
    private Runnable mDialogRunnable = new Runnable() {
        @Override
        public void run() {
            Log.v(TAG, "mDialogRunnable run");
            isDialogShowing = false;
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //mPlayUrl = "http://127.0.0.1:3932/film/test.m3u8";
        //mPlayUrl = "http://10.7.7.101/m3u8test/test.m3u8";
        //mPlayUrl = "/data/data/huoying443.3gp";
        mPosition = 0;
        //mTitle = "aging_test";

        setContentView(R.layout.activity_player);
        super.onCreate(savedInstanceState);

        mPause = (ImageView) findViewById(R.id.playpause);
        mBuffering = (TextView) findViewById(R.id.playbuffering);
        mPlayBar = (MediaPlayBar) findViewById(R.id.playbar);

        Intent intent = getIntent();
        int id = (int) intent.getSerializableExtra("id");
        mTitle = getTitle(id);
        mPlayUrl = getPlayUrl(id);
        mPosition = getPosition(id);

        mPause.setVisibility(View.GONE);
        mBuffering.setVisibility(View.GONE);
        mPlayBar.setMediaPlayer(this);
        mPlayBar.setFreezing(false);
        mPlayBar.setTitle(mTitle);
    }

    private ItemPageInfoProperty pageInfo = null;
    private VodModel vodUtil = null;
    private ItemProperty itemProperty = null;
    private ItemDetailProperty itemDetailProperty = null;
    private ItemRecordProperty itemRecordProperty = null;
    private String getTitle(int id) {
        initPageInfo(id);
        if ( itemProperty != null) return itemProperty.getTitle();
        else return null;
    }
    private String getPlayUrl(int id){
        initPageInfo(id);
        if (itemDetailProperty != null ) return itemDetailProperty.getPlayUrl();
        else return null;
    }
    private int getPosition(int id){
        initPageInfo(id);
        if (itemRecordProperty != null ) return itemRecordProperty.getPosition();
        else return 0;
    }
    private void initPageInfo(int id) {
        if (pageInfo == null) {
            pageInfo = new ItemPageInfoProperty();
            vodUtil = new VodModel();
            vodUtil.setInterFilePath(this.getFilesDir().getAbsolutePath());
            vodUtil.setInterCachePath(this.getCacheDir().getAbsolutePath());
            try {
                vodUtil.getItems(0, 0, pageInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int count = pageInfo.getTotalNum();
            int i = 0;
            for (i = 0; i < count; i++) {
                if (id == pageInfo.getItemList().get(i).getId()) {
                    itemProperty = pageInfo.getItemList().get(i);
                    break;
                }
            }
            if (i != count) {
                String detailUrl = itemProperty.getDetail();
                itemDetailProperty = new ItemDetailProperty();
                vodUtil.getItemDetail(detailUrl, itemDetailProperty);
            }
            ItemRecordPageInfoProperty hisInfo = new ItemRecordPageInfoProperty();
            vodUtil.getHisItems(0, 0, hisInfo);
            for (i=0;i< hisInfo.getTotalNum();++i) {
                if (id == hisInfo.getRecordList().get(i).getId()) {
                    itemRecordProperty = hisInfo.getRecordList().get(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();

        if (!mPlayBar.isShowing()) {
            mPlayBar.show();
        }
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        mPlayBar.hide();
        mPlayBar.stopGetCurPos();
        super.onDestroy();
    }

    @Override
    public void finish() {
        Log.v(TAG, "finish");
        addHistoryRecord();
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (!isPlaying()) {
                start();
                mPause.setVisibility(View.GONE);
                mPlayBar.setFreezing(false);
                mPlayBar.show();
                isPause = false;
            } else {
                pause();
                mPause.setVisibility(View.VISIBLE);
                mPlayBar.setFreezing(true);
                mPlayBar.show();
                isPause = true;
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ) {
            Log.d(TAG, "KEYCODE_DPAD_RIGHT");
            if (!mPlayBar.isShowing())
                mPlayBar.show();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBufferingUpdate(AbsMediaPlayer player, int paramInt) {
        Log.v(TAG, "onBufferingUpdate");
        if (mPlayBar.isSeeking()) {
            mPlayBar.setSeeking(false);
            mPlayBar.startGetCurPos(500);
        }
    }

    @Override
    public void onSeekComplete(AbsMediaPlayer player) {
        Log.v(TAG, "onSeekComplete "+getCurrentPosition());
        if (mPlayBar.isSeeking()) {
            mPlayBar.setSeeking(false);
            mPlayBar.startGetCurPos(500);
        }
    }

    @Override
    protected String onGetSource() {
        return mPlayUrl;
    }

    @Override
    public String getSourceName() {
        return super.getSourceName();
    }

    @Override
    protected void onPlaybackError(int err) {
        Log.v(TAG, "onPlaybackError " + err);
        mPlayBar.setFreezing(true);
        if (!mPlayBar.isShowing())
            mPlayBar.show();
        dialogMessage(R.string.formatdisable);
    }

    @Override
    public void onPrepared(AbsMediaPlayer player) {
        Log.v(TAG, "onPrepared");
        super.onPrepared(player);
        mPlayBar.initBar(mPosition);
    }

    @Override
    public void onCompletion(AbsMediaPlayer player) {
        Log.v(TAG, "onCompletion");
        super.onCompletion(player);

        mPosition = 0;
        finish();
    }

    private void dialogMessage(int rscId) {
        if (isDialogShowing)
            return;

        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialogsimple, null);

        final TextView text = (TextView) view.findViewById(R.id.dialog_content);
        text.setText(rscId);

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        dialog.getWindow().setLayout(width / 2, height / 3);
        dialog.getWindow().setContentView(view);

        isDialogShowing = true;

        mDialogHandler.postDelayed(mDialogRunnable, 3000);
    }

    private void addHistoryRecord() {
        mPosition = getCurrentPosition();
        ItemRecordProperty item = new ItemRecordProperty();
        item.setId(itemProperty.getId());
        item.setSmallPoster(itemProperty.getSmallPoster());
        item.setBigPoster(itemProperty.getBigPoster());
        item.setTitle(itemProperty.getTitle());
        item.setDetail(itemDetailProperty.getDesc());
        item.setPosition(mPosition);
        vodUtil.addHisItem(item);
        Log.v(TAG, "addHistoryRecord");
    }
}
