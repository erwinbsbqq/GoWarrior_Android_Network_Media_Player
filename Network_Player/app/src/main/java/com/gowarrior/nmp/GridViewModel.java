package com.gowarrior.nmp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gowarrior.nmp.common.VodData;
import com.gowarrior.nmp.common.VodData.RetStatus_E;
import com.gowarrior.nmp.common.VodData.ItemListType_E;
import com.gowarrior.nmp.common.VodData.ItemPageInfoProperty;
import com.gowarrior.nmp.common.VodData.ItemRecordPageInfoProperty;
import com.gowarrior.nmp.common.VodModel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by GoWarrior on 2015/6/29.
 */
public class GridViewModel extends Activity implements View.OnFocusChangeListener, ItemDataModel.Callbacks{
    private final String TAG = "GridViewModel";
    private final int INVALID_NUM = -1;

    private GridView mGridview;
    private View     mPreView = null;
    private TextView titleTxt = null;
    private String   titleIdx = null;
    private VodModel vodUtil = null;
    private ItemDataModel listMode = null;
    private int ColumnNum;
    private int ColumnStartIdx;
    private ItemListType_E curType;
    private int preFavSize;
    private int preFavSelectIdx;
    private boolean removeFavFlag;
    ArrayList<HashMap<String, Object>> lists = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridmenu);

        mGridview = (GridView) findViewById(R.id.gridview);
        mGridview.setAdapter(new MyAdapter());
        mGridview.setOnFocusChangeListener(this);
        mGridview.setOnItemClickListener(new ItemClickListener());
        mGridview.setOnItemSelectedListener(new ItemSelectedListener());

        int count = initList();

        mGridview.setSelection(0);
    }

    @Override
    protected void onResume(){
        super.onResume();

        if( curType == ItemListType_E.ItemListType_Favorite
                && INVALID_NUM != preFavSize ){
            removeFavFlag = false;
            initFavList(curType);
        }
    }

    public int initList(){
        int count = 0;
        vodUtil = new VodModel();
        vodUtil.setInterFilePath(this.getFilesDir().getAbsolutePath());
        vodUtil.setInterCachePath(this.getCacheDir().getAbsolutePath());

        listMode = new ItemDataModel();
        listMode.initialize(this);

        ColumnNum = 6;
        ColumnStartIdx = 0;

        // the title name and content of the Program List Menu
        Intent intent = getIntent();
        String title = null;

        curType = (ItemListType_E) intent.getSerializableExtra("type");
        if(curType == ItemListType_E.ItemListType_Latest) {
            title = this.getString(R.string.latest);
            initLatestList(curType);
        }else if(curType == ItemListType_E.ItemListType_Favorite) {
            title = this.getString(R.string.favorite);
            initFavList(curType);

            preFavSelectIdx = INVALID_NUM;
            preFavSize = INVALID_NUM;
        }else if(curType == ItemListType_E.ItemListType_Watched) {
            title = this.getString(R.string.watched);
            initHisList(curType);
        }else{
            Log.d(TAG,"The title type is invalid ");
        }

        titleTxt=(TextView)findViewById(R.id.gridtitle);
        titleTxt.setText(title);

        return count;
    }

    public boolean initLatestList(ItemListType_E type){
        int pageSize = ColumnNum;
        int pageIndex = ColumnStartIdx;
        boolean ret = false;

        ret = listMode.getItems(pageSize, pageIndex, type);
        if(ret == false)
            Log.d( TAG," initLatestList(),Fail to get the Latest List");

        return ret;
    }

    public boolean initFavList(ItemListType_E type){
        int pageSize = ColumnNum;
        int pageIndex = ColumnStartIdx;
        boolean ret = false;

        ret = listMode.getFavItems(pageSize, pageIndex, type);
        if(ret == false)
            Log.d( TAG," initFavList(),Fail to get the Fav List");

        return ret;
    }

    public boolean initHisList(ItemListType_E type){
        int pageSize = ColumnNum;
        int pageIndex = ColumnStartIdx;
        boolean ret = false;

        ret = listMode.getHisItems(pageSize, pageIndex, type);
        if(ret == false)
            Log.d( TAG," initHisList(),Fail to get the His List");

        return ret;
    }

    public int updateItems(ItemPageInfoProperty pageInfo,int pageSize,int pageIndex,ItemListType_E type){
        int count = 0;
        RetStatus_E ret = null;

        if( pageInfo != null ){
            if( 0 == pageInfo.getPageSize() &&  0 == pageInfo.getPageIndex() )
                count = pageInfo.getTotalNum();
            else
                count = pageInfo.getPageSize();

            for (int i = 0; i < count; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemID", pageInfo.getItemList().get(i).getId());
                map.put("ItemImage", pageInfo.getItemList().get(i).getSmallPoster());
                map.put("ItemText", pageInfo.getItemList().get(i).getTitle());
                lists.add(map);
            }

            MyAdapter mAdapter = (MyAdapter)mGridview.getAdapter();
            mAdapter.notifyDataSetChanged();

            //To get the left program information
            int curIdx = lists.size();
            if( curIdx < pageInfo.getTotalNum() ){
                Log.d(TAG, "updateItems(), curIdx = " + curIdx + ", preIndex=" + pageIndex);
                boolean result = listMode.getItems(pageInfo.getPageSize(), pageIndex + 1, type);
                if(result == false)
                    Log.d( TAG," updateItems(),Fail to get the Rec List");
            }
        } else{
            Log.d(TAG, "Fail to get the program information");
        }

        return count;
    }

    @Override
    public int updateFavItems(ItemRecordPageInfoProperty pageInfo, int pageSize, int pageIndex, ItemListType_E type) {
        int count = 0;
        RetStatus_E ret = null;

        if( pageInfo != null ){
            if( 0 == pageInfo.getPageSize() &&  0 == pageInfo.getPageIndex() )
                count = pageInfo.getTotalNum();
            else
                count = pageInfo.getPageSize();

            if( preFavSize  == pageInfo.getTotalNum() + 1
                    && false == removeFavFlag ){
                // Have removed one Fav program
                if( preFavSelectIdx >= 1)
                    mGridview.setSelection(preFavSelectIdx - 1);
                else
                    mGridview.setSelection(0);

                //Remove the list record
                lists.remove( preFavSelectIdx );
                removeFavFlag = true;
            }

            for (int i = 0; i < count; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemID", pageInfo.getRecordList().get(i).getId());
                map.put("ItemImage", pageInfo.getRecordList().get(i).getSmallPoster());
                map.put("ItemText", pageInfo.getRecordList().get(i).getTitle());

                if( false == lists.contains(map))
                    lists.add(map);
            }

            MyAdapter mAdapter = (MyAdapter)mGridview.getAdapter();
            mAdapter.notifyDataSetChanged();

            //To get the left program information
            int curIdx = lists.size();
            if( curIdx < pageInfo.getTotalNum() ){
                boolean result = listMode.getFavItems(pageInfo.getPageSize(), pageIndex + 1, type);
                if(result == false)
                    Log.d( TAG," updateFavItems(),Fail to get the Rec List");
            }

        } else{
            Log.d(TAG, "Fail to get the Fav Rec program information");
        }

        return count;
    }

    @Override
    public int updateHisItems(ItemRecordPageInfoProperty pageInfo, int pageSize, int pageIndex, ItemListType_E type) {
        int count = 0;
        RetStatus_E ret = null;

        if( pageInfo != null ){
            if( 0 == pageInfo.getPageSize() &&  0 == pageInfo.getPageIndex() )
                count = pageInfo.getTotalNum();
            else
                count = pageInfo.getPageSize();

            for (int i = 0; i < count; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemID", pageInfo.getRecordList().get(i).getId());
                map.put("ItemImage", pageInfo.getRecordList().get(i).getSmallPoster());
                map.put("ItemText", pageInfo.getRecordList().get(i).getTitle());
                lists.add(map);
            }

            MyAdapter mAdapter = (MyAdapter)mGridview.getAdapter();
            mAdapter.notifyDataSetChanged();


            //To get the left program information
            int curIdx = lists.size();
            if( curIdx < pageInfo.getTotalNum() ){
                boolean result = listMode.getHisItems(pageInfo.getPageSize(), pageIndex + 1, type);
                if(result == false)
                    Log.d( TAG," updateHisItems(),Fail to get the Rec List");
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

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int i) {
            return lists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        private String getItemText(int i) {
            HashMap<String, Object> item = (HashMap<String, Object>)getItem(i);
            String text = (String)item.get("ItemText");
            return text;
        }

        private String getItemImage(int i) {
            HashMap<String, Object> item = (HashMap<String, Object>)getItem(i);
            String imageUrl = (String)item.get("ItemImage");
            return imageUrl;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            //Log.d(TAG, "getView "+i);

            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.griditem,null);

                int width = (int)getResources().getDimension(R.dimen.grid_item_width);
                int height = (int)getResources().getDimension(R.dimen.grid_item_height);
                view.setLayoutParams(new GridView.LayoutParams(width,height));

                holder.hover = (ImageView) view.findViewById(R.id.itemHover);
                holder.image = (ImageView) view.findViewById(R.id.itemImage);
                holder.text = (TextView) view.findViewById(R.id.itemText);
                holder.flag = false;
                holder.idx = i;
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (i == mGridview.getSelectedItemPosition()) {
                holder.hover.setVisibility(View.VISIBLE);
            } else {
                holder.hover.setVisibility(View.GONE);
            }

            String text = getItemText(i);
            holder.text.setText(text);

            //Patch for entering the ProgList Menu from the Detail Menu
            if(curType == ItemListType_E.ItemListType_Favorite){
                if ( i >= preFavSelectIdx && INVALID_NUM != preFavSelectIdx ){
                    holder.flag = false;
                    preFavSelectIdx++;
                }
            }

            if(i != holder.idx){
                holder.idx = i;
                holder.flag = false;
            }

            if(!holder.flag) {
                String imageUrl = getItemImage(i);
                if( imageUrl != null){
                    String localPosterUrl = vodUtil.getPosterUrl(imageUrl);
                    Bitmap bm = BitmapFactory.decodeFile(localPosterUrl);
                    holder.image.setImageBitmap(bm);
                    //Log.d(TAG, "local fileName=" + localPosterUrl);
                    holder.flag = true;
                    //Log.d(TAG, "getView done");
                }else{
                    holder.image.setImageResource(R.drawable.grid_bg);
                }
            }
            return view;
        }

        class ViewHolder {
            ImageView hover;
            ImageView image;
            TextView text;
            boolean flag;
            int idx;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            Log.d(TAG, view.getContentDescription().toString()+" onFocusChange ");

            View mView = mGridview.getSelectedView();
            if (mView != null) {
                zoomOut(mView);
                mPreView = mView;
            } else {
                if (mPreView != null) {
                    zoomIn(mPreView);
                    mPreView = null;
                }
            }
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d(TAG, view.getContentDescription().toString()+" onItemClick position "+i+" rowid "+l);
            Intent intent = new Intent(GridViewModel.this, DetailMenu.class);
            int id = (int) lists.get(i).get("ItemID");
            intent.putExtra("id", id);
            if(curType == ItemListType_E.ItemListType_Favorite){
                preFavSelectIdx = mGridview.getSelectedItemPosition();
                preFavSize = lists.size();
                Log.d( TAG, "preFavSelectIdx =" + preFavSelectIdx +", preFavSize =" + preFavSize);
            }
            startActivity(intent);
        }
    }

    private class ItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d(TAG, view.getContentDescription().toString()+" onItemSelected "+i);

            if (adapterView.hasFocus()) {
                if (mPreView != null) {
                    zoomIn(mPreView);
                }
                zoomOut(view);
                mPreView = view;
            } else {
                if (mPreView != null) {
                    zoomIn(mPreView);
                    mPreView = null;
                }
            }

            MyAdapter mAdapter = (MyAdapter)adapterView.getAdapter();
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            if (mPreView != null) {
                zoomIn(mPreView);
                mPreView = null;
            }
        }
    }

    private void zoomOut(View view) {
        Animation anim = AnimationUtils.loadAnimation(GridViewModel.this,
                R.anim.item_zoomout);
        view.startAnimation(anim);
    }

    private void zoomIn(View view) {
        Animation anim = AnimationUtils.loadAnimation(GridViewModel.this,
                R.anim.item_zoomin);
        view.startAnimation(anim);
    }
}

