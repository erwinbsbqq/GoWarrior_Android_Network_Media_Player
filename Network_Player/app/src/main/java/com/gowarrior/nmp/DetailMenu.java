package com.gowarrior.nmp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gowarrior.nmp.common.VodData;
import com.gowarrior.nmp.player.VodVideoPlayer;
import com.gowarrior.nmp.common.VodData.ItemPageInfoProperty;
import com.gowarrior.nmp.common.VodData.ItemDetailProperty;
import com.gowarrior.nmp.common.VodData.ItemRecordProperty;
import com.gowarrior.nmp.common.VodData.ItemRecordPageInfoProperty;
import com.gowarrior.nmp.common.VodData.ItemListType_E;
import com.gowarrior.nmp.common.VodModel;
import com.gowarrior.nmp.ItemDataModel;

import java.io.IOException;

/**
 * Created by GoWarrior on 2015/6/29.
 */
public class DetailMenu extends Activity implements ItemDataModel.Callbacks{
    private final String TAG = "DetailMenu";

    private ImageView mViewPoster;
    private TextView mViewTitle;
    private TextView mViewDescription;
    private ImageButton mButtonPlay;
    private ImageButton mButtonFav;
    private VodModel vodUtil = null;
    private ItemDataModel listMode = null;
    private ItemDetailProperty itemDetailProperty = null;
    private ItemRecordProperty itemRecordProperty = null;
    private ItemPageInfoProperty pageInfo = null;
    private int id = -1;
    private int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        mViewPoster = (ImageView) findViewById(R.id.detailimg);
        mViewTitle = (TextView) findViewById(R.id.detailtitle);
        mViewDescription = (TextView) findViewById(R.id.detaildesc);
        mButtonPlay = (ImageButton) findViewById(R.id.detailplay);
        mButtonFav = (ImageButton) findViewById(R.id.detailfav);

        listMode = new ItemDataModel();
        listMode.initialize(this);

        vodUtil = new VodModel();
        vodUtil.setInterFilePath(this.getFilesDir().getAbsolutePath());
        vodUtil.setInterCachePath(this.getCacheDir().getAbsolutePath());

        Intent intent = getIntent();
        id = -1;
        index = -1;
        id = (int) intent.getSerializableExtra("id");
        Log.d(TAG, "id=" + id);

        if (id >= 0) {
            int pageSize = 0;
            int pageIndex = 0;
            pageInfo = new ItemPageInfoProperty();

            try {
                vodUtil.getItems(pageSize, pageIndex, pageInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int count = pageInfo.getTotalNum();
            int i = 0;
            for (i = count - 1; i >= 0; i--) {
                if (id == pageInfo.getItemList().get(i).getId()) {
                    break;
                }
            }

            if( i < 0 ){
                Log.d(TAG, "invalid program id");

            }else{
                index = i;
                boolean ret = listMode.getDetailItem(id, ItemListType_E.ItemListType_Detail);
                if(ret == false)
                    Log.d( TAG," getDetailItem(),Fail to get the Detail");
                else{
                    mViewTitle.setVisibility(View.INVISIBLE);
                    //mViewPoster.setVisibility(View.INVISIBLE);
                    mViewDescription.setVisibility(View.INVISIBLE);
                    mButtonPlay.setVisibility(View.INVISIBLE);
                    mButtonFav.setVisibility(View.INVISIBLE);

                    ret = listMode.getPosterItem(id, ItemListType_E.ItemListType_Poster);
                    if(ret == false)
                        Log.d( TAG," getDetailItem(),Fail to get the Poster");
                    else{
                        mViewPoster.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    public void onClickPlay(View view) {
        if (id >= 0 ) {
            //Log.d(TAG, "playUrl=" + itemDetailProperty.getPlayUrl());
            Intent intent = new Intent(DetailMenu.this, VodVideoPlayer.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    public void onClickFav(View view) {
        if (id >= 0) {
            itemRecordProperty = new ItemRecordProperty();
            itemRecordProperty.setId(id);
            itemRecordProperty.setTitle(pageInfo.getItemList().get(index).getTitle());
            itemRecordProperty.setSmallPoster(pageInfo.getItemList().get(index).getSmallPoster());
            itemRecordProperty.setBigPoster(pageInfo.getItemList().get(index).getBigPoster());
            itemRecordProperty.setDetail(pageInfo.getItemList().get(index).getDetail());
            if( false == isFavID(id) ){
                vodUtil.addFavItem(itemRecordProperty);
                mButtonFav.setImageDrawable(getResources().getDrawable(R.drawable.detail_fav_selected));
            }else{
                vodUtil.deleteFavItem(itemRecordProperty);
                mButtonFav.setImageDrawable(getResources().getDrawable(R.drawable.detail_fav_nonselect));
            }
            Log.d(TAG, "Fav id="+ id);
        }
    }

    private boolean isFavID(int id){
        ItemRecordPageInfoProperty recordInfo = new ItemRecordPageInfoProperty();
        vodUtil.getFavItems(0, 0, recordInfo);
        int recordCount = recordInfo.getTotalNum();
        for (int i = 0;i< recordCount; ++i) {
            if (id == recordInfo.getRecordList().get(i).getId())
                return true;
        }
        return false;
    }

    @Override
    public int updateItems(ItemPageInfoProperty pageInfo, int pageSize, int pageIndex, VodData.ItemListType_E type) {
        return 0;
    }

    @Override
    public int updateFavItems(ItemRecordPageInfoProperty pageInfo, int pageSize, int pageIndex, VodData.ItemListType_E type) {
        return 0;
    }

    @Override
    public int updateHisItems(ItemRecordPageInfoProperty pageInfo, int pageSize, int pageIndex, VodData.ItemListType_E type) {
        return 0;
    }

    @Override
    public int updateDetailItem(ItemDetailProperty itemInfo, VodData.ItemListType_E type) {

        String title = pageInfo.getItemList().get(index).getTitle();
        mViewTitle.setText(title);
        mViewTitle.setVisibility(View.VISIBLE);
        mViewTitle.invalidate();

        /*
        String imageUrl = pageInfo.getItemList().get(index).getBigPoster();
        String localUrl = vodUtil.getPosterUrl(imageUrl);
        Log.d(TAG, "localUrl=" + localUrl);
        Bitmap bm = BitmapFactory.decodeFile(localUrl);
        mViewPoster.setImageBitmap(bm);
        mViewPoster.setVisibility(View.VISIBLE);
        mViewPoster.invalidate();
        */

        //Log.d(TAG, "desc=" + itemInfo.getDesc());
        mViewDescription.setText(itemInfo.getDesc());
        mViewDescription.setVisibility(View.VISIBLE);
        mViewDescription.invalidate();

        if(isFavID(id))
            mButtonFav.setImageDrawable(getResources().getDrawable(R.drawable.detail_fav_selected));
        mButtonPlay.setVisibility(View.VISIBLE);
        mButtonFav.setVisibility(View.VISIBLE);

        return 0;
    }

    @Override
    public int updatePosterItem(String url, VodData.ItemListType_E type) {

        String imageUrl = pageInfo.getItemList().get(index).getBigPoster();
        String localUrl = vodUtil.getPosterUrl(imageUrl);
        //Log.d(TAG, "localUrl=" + localUrl);
        Bitmap bm = BitmapFactory.decodeFile(localUrl);
        mViewPoster.setImageBitmap(bm);
        mViewPoster.setVisibility(View.VISIBLE);
        mViewPoster.invalidate();

        return 0;
    }
}
