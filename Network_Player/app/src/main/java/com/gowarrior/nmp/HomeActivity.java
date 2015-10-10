package com.gowarrior.nmp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.gowarrior.nmp.common.VodData;
import com.gowarrior.nmp.common.VodData.ItemPageInfoProperty;
import com.gowarrior.nmp.common.VodData.ItemListType_E;
import com.gowarrior.nmp.common.VodData.ItemRecordPageInfoProperty;
import com.gowarrior.nmp.common.VodModel;
import com.gowarrior.nmp.ItemDataModel;

import java.io.IOException;


public class HomeActivity extends Activity implements ItemDataModel.Callbacks{
    private final String TAG = "HomeActivity";

    private HomeItemView recent_1, recent_2;
    private HomeItemView favourite_1, favourite_2;
    private HomeItemView history_1, history_2;
    private HomeItemView recents, favourites, historys;
    private VodModel vodUtil = null;
    private ItemDataModel listMode = null;
    private ItemPageInfoProperty pageInfo = null;
    private ItemRecordPageInfoProperty recordInfo = null;
    private ItemRecordPageInfoProperty hisInfo = null;
    private int count = 0;
    private int recordCount = 0;
    private int hisCount = 0;
    private int ItemNumber = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        vodUtil = new VodModel();
        vodUtil.setInterFilePath(this.getFilesDir().getAbsolutePath());
        vodUtil.setInterCachePath(this.getCacheDir().getAbsolutePath());

        listMode = new ItemDataModel();
        listMode.initialize(this);

        recent_1 = (HomeItemView) findViewById(R.id.home_recent_1);
        recent_2 = (HomeItemView) findViewById(R.id.home_recent_2);
        favourite_1 = (HomeItemView) findViewById(R.id.home_favourite_1);
        favourite_2 = (HomeItemView) findViewById(R.id.home_favourite_2);
        history_1 = (HomeItemView) findViewById(R.id.home_history_1);
        history_2 = (HomeItemView) findViewById(R.id.home_history_2);
        recents = (HomeItemView) findViewById(R.id.home_recents);
        favourites = (HomeItemView) findViewById(R.id.home_favourites);
        historys = (HomeItemView) findViewById(R.id.home_historys);

        recent_1.setOnClickListener(new itemClickListener());
        recent_2.setOnClickListener(new itemClickListener());
        favourite_1.setOnClickListener(new itemClickListener());
        favourite_2.setOnClickListener(new itemClickListener());
        history_1.setOnClickListener(new itemClickListener());
        history_2.setOnClickListener(new itemClickListener());
        recents.setOnClickListener(new itemClickListener());
        favourites.setOnClickListener(new itemClickListener());
        historys.setOnClickListener(new itemClickListener());

        recent_1.setOnFocusChangeListener(new itemFocusListener());
        recent_2.setOnFocusChangeListener(new itemFocusListener());
        favourite_1.setOnFocusChangeListener(new itemFocusListener());
        favourite_2.setOnFocusChangeListener(new itemFocusListener());
        history_1.setOnFocusChangeListener(new itemFocusListener());
        history_2.setOnFocusChangeListener(new itemFocusListener());
        recents.setOnFocusChangeListener(new itemFocusListener());
        favourites.setOnFocusChangeListener(new itemFocusListener());
        historys.setOnFocusChangeListener(new itemFocusListener());

        recent_1.requestFocus();
        recent_1.setHoverVisibility(View.VISIBLE);
        zoomOut(recent_1.getContext(), recent_1);

        //init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume");
        init();
    }


    class itemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.d(TAG, v.getContentDescription().toString() + " onClick ");
            doSwitch(v);
        }
    }

    class itemFocusListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.d(TAG, v.getContentDescription().toString()+" onFocusChange "+hasFocus);
            HomeItemView view = (HomeItemView)v;
            if (hasFocus) {
                view.setHoverVisibility(View.VISIBLE);
                zoomOut(view.getContext(), view);
            } else {
                view.setHoverVisibility(View.GONE);
                zoomIn(view.getContext(), view);
            }
        }
    }

    private void zoomOut(Context context, View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.item_zoomout);
        view.startAnimation(anim);
    }

    private void zoomIn(Context context, View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.item_zoomin);
        view.startAnimation(anim);
    }

    private void doSwitch(View view) {
        Intent intent = null;
        String type = null;

        if (view == recent_1) {
            intent = new Intent(HomeActivity.this, DetailMenu.class);
            if (count > 0) {
                intent.putExtra("id", pageInfo.getItemList().get(count - 1).getId());
            } else
                intent.putExtra("id", -1);
        } else if (view == recent_2) {
            intent = new Intent(HomeActivity.this, DetailMenu.class);
            if (count > 1) {
                intent.putExtra("id", pageInfo.getItemList().get(count - 1 - 1).getId());
            } else
                intent.putExtra("id", -1);
        } else if (view == favourite_1) {
            intent = new Intent(HomeActivity.this, DetailMenu.class);
            if (recordCount > 0) {
                intent.putExtra("id", recordInfo.getRecordList().get(recordCount - 1).getId());
            } else
                intent.putExtra("id", -1);
        }
        else if (view == favourite_2) {
            intent = new Intent(HomeActivity.this, DetailMenu.class);
            if (recordCount > 1) {
                intent.putExtra("id", recordInfo.getRecordList().get(recordCount - 1 - 1).getId());
            } else
                intent.putExtra("id", -1);
        }
        else if (view == history_1) {
            intent = new Intent(HomeActivity.this, DetailMenu.class);
            if (hisCount > 1) {
                intent.putExtra("id", hisInfo.getRecordList().get(hisCount - 1).getId());
            } else
                intent.putExtra("id", -1);
        }
        else if (view == history_2) {
            intent = new Intent(HomeActivity.this, DetailMenu.class);
            if (hisCount > 1) {
                intent.putExtra("id", hisInfo.getRecordList().get(hisCount - 1 - 1).getId());
            } else
                intent.putExtra("id", -1);
        }
        else if (view == recents){
            intent = new Intent(HomeActivity.this, GridViewModel.class);
            intent.putExtra("type", ItemListType_E.ItemListType_Latest);
        }
        else if (view == favourites) {
            intent = new Intent(HomeActivity.this, GridViewModel.class);
            intent.putExtra("type", ItemListType_E.ItemListType_Favorite);
        }
        else if (view == historys) {
            intent = new Intent(HomeActivity.this, GridViewModel.class);
            intent.putExtra("type", ItemListType_E.ItemListType_Watched);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    private void init() {
        pageInfo = new ItemPageInfoProperty();
        recordInfo = new ItemRecordPageInfoProperty();
        hisInfo = new ItemRecordPageInfoProperty();
        boolean ret = false;

        Log.d(TAG, "init(),");

        ret = listMode.getHomeItems( ItemNumber, 0, ItemListType_E.ItemListType_Latest);
        if(ret == false)
            Log.d( TAG," init(),Fail to get the Latest List");

    }

    private void getHomeFavItem() {
        boolean ret = false;

        ret = listMode.getHomeFavItems(ItemNumber, 0, ItemListType_E.ItemListType_Favorite);
        if(ret == false)
            Log.d( TAG," getHomeFavItem(),Fail to get the Favorite List");
    }

    private void getHomeHisItem() {
        boolean ret = false;

        ret = listMode.getHomeHisItems( ItemNumber, 0, ItemListType_E.ItemListType_Watched);
        if(ret == false)
            Log.d( TAG," getHomeHisItem(),Fail to get the Watched List");
    }

    public int updateItems(ItemPageInfoProperty pageItemInfo,int pageSize,int pageIndex,ItemListType_E type){
        ImageView imageView = null;
        VodData.RetStatus_E ret = null;

        // prepare data for Fav Items
        getHomeFavItem();

        if( pageItemInfo != null ){
            pageInfo = pageItemInfo;
            count = pageInfo.getTotalNum();
            for ( int i=0; i < count && i < ItemNumber; ++i) {
                String imageUrl = pageInfo.getItemList().get(count - 1 - i).getBigPoster();
                String localPosterUrl = vodUtil.getPosterUrl(imageUrl);
                Bitmap bm = BitmapFactory.decodeFile(localPosterUrl);
                if (0 == i) {
                    imageView = (ImageView) recent_1.findViewById(R.id.homeItemImage);
                } else {
                    imageView = (ImageView) recent_2.findViewById(R.id.homeItemImage);
                }
                imageView.setImageBitmap(bm);
                imageView.invalidate();
            }

        } else{
            Log.d(TAG, "Fail to get the program information");
        }

        return count;
    }

    public int updateFavItems(ItemRecordPageInfoProperty pageInfo, int pageSize, int pageIndex, ItemListType_E type) {
        ImageView imageView = null;
        VodData.RetStatus_E ret = null;

        //Prepare the data for His Items
        getHomeHisItem();

        if( pageInfo != null ){
            recordInfo = pageInfo;
            recordCount = pageInfo.getTotalNum();
            for ( int i = 0; i < recordCount && i < ItemNumber; ++i) {
                String imageUrl = pageInfo.getRecordList().get(recordCount - 1 - i).getBigPoster();
                String localPosterUrl = vodUtil.getPosterUrl(imageUrl);
                Bitmap bm = BitmapFactory.decodeFile(localPosterUrl);
                if (0 == i) {
                    imageView = (ImageView) favourite_1.findViewById(R.id.homeItemImage);
                } else {
                    imageView = (ImageView) favourite_2.findViewById(R.id.homeItemImage);
                }

                imageView.setImageBitmap(bm);
                imageView.invalidate();
            }

        } else{
            Log.d(TAG, "Fail to get the Fav Rec program information");
        }

        return count;
    }

    public int updateHisItems(ItemRecordPageInfoProperty pageInfo, int pageSize, int pageIndex, ItemListType_E type) {
        ImageView imageView = null;
        VodData.RetStatus_E ret = null;

        if( pageInfo != null ){
            hisInfo = pageInfo;

            hisCount = pageInfo.getTotalNum();
            for ( int i = 0; i < hisCount && i < ItemNumber; ++i) {
                String imageUrl = pageInfo.getRecordList().get(hisCount - 1 - i).getBigPoster();
                String localPosterUrl = vodUtil.getPosterUrl(imageUrl);
                Bitmap bm = BitmapFactory.decodeFile(localPosterUrl);
                if (0 == i) {
                    imageView = (ImageView) history_1.findViewById(R.id.homeItemImage);
                } else {
                    imageView = (ImageView) history_2.findViewById(R.id.homeItemImage);
                }

                imageView.setImageBitmap(bm);
                imageView.invalidate();
            }

        } else{
            Log.d(TAG, "Fail to get the His Rec program information");
        }

        return count;
    }

    @Override
    public int updateDetailItem(VodData.ItemDetailProperty itemInfo, ItemListType_E type) {
        return 0;
    }

    @Override
    public int updatePosterItem(String url, ItemListType_E type) {
        return 0;
    }

}
