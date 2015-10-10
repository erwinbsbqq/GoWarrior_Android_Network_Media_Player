package com.gowarrior.nmp;

import android.os.AsyncTask;
import android.util.Log;

import com.gowarrior.nmp.common.VodData;
import com.gowarrior.nmp.common.VodData.ItemListType_E;
import com.gowarrior.nmp.common.VodData.RetStatus_E;
import com.gowarrior.nmp.common.VodData.ItemProperty;
import com.gowarrior.nmp.common.VodData.ItemPageInfoProperty;
import com.gowarrior.nmp.common.VodData.ItemRecordPageInfoProperty;
import com.gowarrior.nmp.common.VodData.ItemDetailProperty;

import com.gowarrior.nmp.common.VodModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by GoWarrior on 2015/7/4.
 */
public class ItemDataModel {
    private static final String TAG = "ItemDataModel";

    private Callbacks mCallbacks;
    private VodModel vodModel = null;
    private ArrayList<HttpGetPara> requestList;
    private int mMaxHttpGetRetryTimes = 30;
    private int mMaxThreadNum = 4;
    private ThreadPoolExecutor LIMITED_TASK_EXECUTOR_GETITEM;

    public interface Callbacks {
        public int updateItems( ItemPageInfoProperty pageInfo,int pageSize,int pageIndex, ItemListType_E type);
        public int updateFavItems( ItemRecordPageInfoProperty pageInfo,int pageSize,int pageIndex, ItemListType_E type);
        public int updateHisItems( ItemRecordPageInfoProperty pageInfo,int pageSize,int pageIndex, ItemListType_E type);
        public int updateDetailItem( ItemDetailProperty itemInfo, ItemListType_E type );
        public int updatePosterItem( String url, ItemListType_E type );
    }

    public ItemDataModel() {
        super();

        vodModel = new VodModel();
        requestList = new ArrayList<HttpGetPara>();

        LIMITED_TASK_EXECUTOR_GETITEM =  new ThreadPoolExecutor(mMaxThreadNum,mMaxThreadNum,0,TimeUnit.SECONDS,
                                                    new ArrayBlockingQueue<Runnable>(mMaxThreadNum),
                                                    new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void initialize( Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void requestItemAdd(HttpGetPara para){
        requestList.add(para);
        if( requestList.size() > mMaxThreadNum ){
            requestList.remove( 0 );
        }
    }

    public void requestItemRemove(HttpGetPara para){
        boolean result = requestList.contains(para);
        if( true == result ){
            requestList.remove(para);
        }
    }

    public boolean getItems( int pageSize,int pageIndex,ItemListType_E type){
        Log.d(TAG, "getItems(), requestList.size() = " + requestList.size()
                + ", pageSize = " + pageSize
                + ", pageIndex = " + pageIndex);

        for(int i = 0; i < requestList.size(); i++){
            if( pageSize == requestList.get(i).getPageSize() &&
                    pageIndex == requestList.get(i).getPageIndex() &&
                    type == requestList.get(i).getType()){
                Log.d(TAG, "getItems() is working. ");
                return false;
            }
        }

        HttpGetPara para= new HttpGetPara();
        para.setPageIndex(pageIndex);
        para.setPageSize(pageSize);
        para.setType(type);
        requestItemAdd(para);

        GetItemTask task = new GetItemTask();
        task.executeOnExecutor(LIMITED_TASK_EXECUTOR_GETITEM, para);

        return true;
    }

    public boolean getFavItems(int pageSize,int pageIndex,ItemListType_E type){
        Log.d(TAG, "getFavItems(), requestList.size() = " + requestList.size()
                    + ", pageSize = " + pageSize
                    + ", pageIndex = " + pageIndex);

        for(int i = 0; i < requestList.size(); i++){
            if( pageSize == requestList.get(i).getPageSize() &&
                    pageIndex == requestList.get(i).getPageIndex() &&
                    type == requestList.get(i).getType()){
                Log.d(TAG, "getFavItems() is working." );
                return false;
            }
        }

        HttpGetPara para= new HttpGetPara();
        para.setPageIndex(pageIndex);
        para.setPageSize(pageSize);
        para.setType(type);
        requestItemAdd(para);

        GetFavItemTask task = new GetFavItemTask();
        task.executeOnExecutor(LIMITED_TASK_EXECUTOR_GETITEM, para);

        return true;
    }

    public boolean getHisItems(int pageSize,int pageIndex,ItemListType_E type){
        Log.d(TAG, "getHisItems(), requestList.size() = " + requestList.size()
                + ", pageSize = " + pageSize
                + ", pageIndex = " + pageIndex);

        for(int i = 0; i < requestList.size(); i++){
            if( pageSize == requestList.get(i).getPageSize() &&
                    pageIndex == requestList.get(i).getPageIndex() &&
                    type == requestList.get(i).getType()){
                Log.d(TAG, "getHisItems() is working." );
                return false;
            }
        }

        HttpGetPara para= new HttpGetPara();
        para.setPageIndex(pageIndex);
        para.setPageSize(pageSize);
        para.setType(type);
        requestItemAdd(para);

        GetHisItemTask task = new GetHisItemTask();
        task.executeOnExecutor(LIMITED_TASK_EXECUTOR_GETITEM, para);

        return true;
    }

    public boolean getHomeItems( int pageSize,int pageIndex,ItemListType_E type){
        Log.d(TAG, "getHomeItems(), requestList.size() = " + requestList.size()
                + ", pageSize = " + pageSize
                + ", pageIndex = " + pageIndex);

        for(int i = 0; i < requestList.size(); i++){
            if( pageSize == requestList.get(i).getPageSize() &&
                    pageIndex == requestList.get(i).getPageIndex() &&
                    type == requestList.get(i).getType()){
                Log.d(TAG, "getHomeItems() is working.");
                return false;
            }
        }

        HttpGetPara para= new HttpGetPara();
        para.setPageIndex(pageIndex);
        para.setPageSize(pageSize);
        para.setType(type);
        requestItemAdd(para);

        GetHomeItemTask task = new GetHomeItemTask();
        task.executeOnExecutor(LIMITED_TASK_EXECUTOR_GETITEM, para);

        return true;
    }

    public boolean getHomeFavItems(int pageSize,int pageIndex,ItemListType_E type){
        Log.d( TAG, "getHomeFavItems(), requestList.size() = " + requestList.size()
                + ", pageSize = " + pageSize
                + ", pageIndex = " + pageIndex);

        for(int i = 0; i < requestList.size(); i++){
            if( pageSize == requestList.get(i).getPageSize() &&
                    pageIndex == requestList.get(i).getPageIndex() &&
                    type == requestList.get(i).getType()){
                Log.d(TAG, "getHomeFavItems() is working." );
                return false;
            }
        }

        HttpGetPara para= new HttpGetPara();
        para.setPageIndex(pageIndex);
        para.setPageSize(pageSize);
        para.setType(type);
        requestItemAdd(para);

        GetHomeFavItemTask task = new GetHomeFavItemTask();
        task.executeOnExecutor(LIMITED_TASK_EXECUTOR_GETITEM, para);

        return true;
    }

    public boolean getHomeHisItems(int pageSize,int pageIndex,ItemListType_E type){
        Log.d(TAG, "getHomeHisItems(), requestList.size() = " + requestList.size()
                + ", pageSize = " + pageSize
                + ", pageIndex = " + pageIndex);

        for(int i = 0; i < requestList.size(); i++){
            if( pageSize == requestList.get(i).getPageSize() &&
                    pageIndex == requestList.get(i).getPageIndex() &&
                    type == requestList.get(i).getType()){
                Log.d(TAG, "getHomeHisItems() is working." );
                return false;
            }
        }

        HttpGetPara para= new HttpGetPara();
        para.setPageIndex(pageIndex);
        para.setPageSize(pageSize);
        para.setType(type);
        requestItemAdd(para);

        GetHomeHisItemTask task = new GetHomeHisItemTask();
        task.executeOnExecutor(LIMITED_TASK_EXECUTOR_GETITEM, para);

        return true;
    }

    public boolean getDetailItem( int progId, ItemListType_E type){
        Log.d(TAG, "getDetailItem(), requestList.size() = " + requestList.size()
                + ", progId = " + progId );

        for(int i = 0; i < requestList.size(); i++){
            if( progId == requestList.get(i).getPageSize() &&
                    type == requestList.get(i).getType()){
                Log.d(TAG, "getDetailItem() is working. ");
                return false;
            }
        }

        HttpGetPara para= new HttpGetPara();
        para.setPageIndex(0);
        para.setPageSize(progId);
        para.setType(type);
        requestItemAdd(para);

        GetDetailItemTask task = new GetDetailItemTask();
        task.executeOnExecutor(LIMITED_TASK_EXECUTOR_GETITEM, para);

        return true;
    }

    public boolean getPosterItem( int progId, ItemListType_E type){
        Log.d(TAG, "getPosterItem(), requestList.size() = " + requestList.size()
                + ", progId = " + progId );

        for(int i = 0; i < requestList.size(); i++){
            if( progId == requestList.get(i).getPageSize() &&
                    type == requestList.get(i).getType()){
                Log.d(TAG, "getPosterItem() is working. ");
                return false;
            }
        }

        HttpGetPara para= new HttpGetPara();
        para.setPageIndex(0);
        para.setPageSize(progId);
        para.setType(type);
        requestItemAdd(para);

        GetPosterTask task = new GetPosterTask();
        task.executeOnExecutor(LIMITED_TASK_EXECUTOR_GETITEM, para);

        return true;
    }


    private class HttpGetPara{
        private int pageSize;
        private int pageIndex;
        private ItemListType_E type;

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public ItemListType_E getType() {
            return type;
        }

        public void setType(ItemListType_E type) {
            this.type = type;
        }
    }

    private class GetItemTask extends AsyncTask<HttpGetPara, Integer, ItemPageInfoProperty> {
        private int pageSize;
        private int pageIndex;
        private ItemListType_E type;

        @Override
        protected ItemPageInfoProperty doInBackground(HttpGetPara... params) {
            Log.d(TAG, "GetItemTask(), doInBackground");
            HttpGetPara para = params[0];
            pageSize = params[0].getPageSize();
            pageIndex = params[0].getPageIndex();
            type = params[0].getType();
            if(isCancelled()){
                Log.d(TAG, "GetItemTask isCancelled");
                requestItemRemove(para);
                return null;
            }

            ItemPageInfoProperty pageInfo = new ItemPageInfoProperty();
            RetStatus_E ret = VodData.RetStatus_E.Ret_Fail;
            int tryTime = 1;
            while( ret != RetStatus_E.Ret_Success && tryTime < mMaxHttpGetRetryTimes ){
                if(isCancelled()){
                    requestItemRemove(para);
                    return null;
                }

                try {
                    if( false == vodModel.checkItemsStatus())
                        vodModel.getResourceFromServer();
                    ret = vodModel.getItems( pageSize,pageIndex,pageInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tryTime++;
            }

            if(tryTime == mMaxHttpGetRetryTimes){
                Log.d(TAG, "GetItemTask(), fail to get info!");
                requestItemRemove(para);
                return null;
            }

            if( ret == RetStatus_E.Ret_Success){
                int count = 0;
                if( 0 == pageInfo.getPageSize() && 0 == pageInfo.getPageIndex() )
                    count = pageInfo.getTotalNum();
                else{
                    int endIdx = (pageInfo.getPageIndex() + 1) * (pageInfo.getPageSize());
                    if(endIdx <= pageInfo.getTotalNum())
                        count = pageInfo.getPageSize();
                    else{
                        count = pageInfo.getTotalNum() + pageInfo.getPageSize() - endIdx;
                        pageInfo.setPageSize(count);
                    }
                }

                for (int i = 0; i < count; i++) {
                    if(isCancelled())
                        return null;

                    String imageUrl = pageInfo.getItemList().get(i).getSmallPoster();
                    if(imageUrl != null) {
                        String localPosterUrl = vodModel.getPosterUrl(imageUrl);
                    }else
                        Log.d( TAG, "Fail to get the poster,index=" + i);
                }
            }else{
                Log.d(TAG, "Fail to get the program information");
            }

            return pageInfo;
        }

        @Override
        protected void onPostExecute(ItemPageInfoProperty result) {
            super.onPostExecute(result);

            if(isCancelled()){
                Log.d(TAG, "GetItemTask is Cancelled");
                return;
            }

            if(result==null){
                Log.e(TAG, "return null");
                return;
            }
            mCallbacks.updateItems(result, pageSize, pageIndex, type);
        }
    }

    private class GetFavItemTask extends AsyncTask<HttpGetPara, Integer, ItemRecordPageInfoProperty>{
        private int pageSize;
        private int pageIndex;
        private ItemListType_E type;

        @Override
        protected ItemRecordPageInfoProperty doInBackground(HttpGetPara... params) {
            Log.d(TAG, "GetFavItemTask(), doInBackground");
            HttpGetPara para = params[0];
            pageSize = para.getPageSize();
            pageIndex = para.getPageIndex();
            type = para.getType();

            ItemRecordPageInfoProperty pageInfo = new ItemRecordPageInfoProperty();
            RetStatus_E ret = RetStatus_E.Ret_Fail;
            int tryTime = 1;

            while( ret != RetStatus_E.Ret_Success && tryTime < mMaxHttpGetRetryTimes ){
                if(isCancelled()){
                    requestItemRemove(para);
                    return null;
                }

                ret = vodModel.getFavItems( pageSize, pageIndex, pageInfo);
                tryTime++;
            }

            if(tryTime == mMaxHttpGetRetryTimes){
                Log.d(TAG, "GetFavItemTask(),fail to get info!");
                requestItemRemove(para);
                return null;
            }

            if( ret == RetStatus_E.Ret_Success){
                int count = 0;
                if( 0 == pageInfo.getPageSize() && 0 == pageInfo.getPageIndex() )
                    count = pageInfo.getTotalNum();
                else{
                    int endIdx = (pageInfo.getPageIndex() + 1) * (pageInfo.getPageSize());
                    if(endIdx <= pageInfo.getTotalNum())
                        count = pageInfo.getPageSize();
                    else{
                        count = pageInfo.getTotalNum() + pageInfo.getPageSize() - endIdx;
                        pageInfo.setPageSize(count);
                    }
                }

                for (int i = 0; i < count; i++) {
                    if(isCancelled()){
                        requestItemRemove(para);
                        return null;
                    }

                    String imageUrl = pageInfo.getRecordList().get(i).getSmallPoster();
                    if(imageUrl != null) {
                        String localPosterUrl = vodModel.getPosterUrl(imageUrl);
                    }else
                        Log.d( TAG, "Fail to get the poster,index=" + i);
                }
            }else {
                Log.d(TAG, "Fail to get the rec program information");
            }

            requestItemRemove(para);

            return pageInfo;
        }

        @Override
        protected void onPostExecute(ItemRecordPageInfoProperty result) {
            super.onPostExecute(result);

            if(isCancelled()){
                Log.d(TAG, "GetFavItemTask is Cancelled");
                return;
            }

            if(result==null){
                Log.e(TAG, "return null");
                return;
            }

            mCallbacks.updateFavItems(result, pageSize, pageIndex, type);
        }
    }


    private class GetHisItemTask extends AsyncTask<HttpGetPara, Integer, ItemRecordPageInfoProperty>{
        private int pageSize;
        private int pageIndex;
        private ItemListType_E type;

        @Override
        protected ItemRecordPageInfoProperty doInBackground(HttpGetPara... params) {
            Log.d(TAG, "GetHisItemTask(), doInBackground");
            HttpGetPara para = params[0];
            pageSize = para.getPageSize();
            pageIndex = para.getPageIndex();
            type = para.getType();

            ItemRecordPageInfoProperty pageInfo = new ItemRecordPageInfoProperty();
            RetStatus_E ret = RetStatus_E.Ret_Fail;
            int tryTime = 1;

            while( ret != RetStatus_E.Ret_Success && tryTime < mMaxHttpGetRetryTimes ){
                if(isCancelled()){
                    requestItemRemove(para);
                    return null;
                }

                ret = vodModel.getHisItems(pageSize, pageIndex, pageInfo);
                tryTime++;
            }

            if(tryTime == mMaxHttpGetRetryTimes){
                Log.d(TAG, "GetHisItemTask(),fail to get info!");
                requestItemRemove(para);
                return null;
            }

            if( ret == RetStatus_E.Ret_Success){
                int count = 0;
                if( 0 == pageInfo.getPageSize() && 0 == pageInfo.getPageIndex() )
                    count = pageInfo.getTotalNum();
                else{
                    int endIdx = (pageInfo.getPageIndex() + 1) * (pageInfo.getPageSize());
                    if(endIdx <= pageInfo.getTotalNum())
                        count = pageInfo.getPageSize();
                    else{
                        count = pageInfo.getTotalNum() + pageInfo.getPageSize() - endIdx;
                        pageInfo.setPageSize(count);
                    }
                }

                for (int i = 0; i < count; i++) {
                    if(isCancelled()){
                        requestItemRemove(para);
                        return null;
                    }

                    String imageUrl = pageInfo.getRecordList().get(i).getSmallPoster();
                    if(imageUrl != null) {
                        String localPosterUrl = vodModel.getPosterUrl(imageUrl);
                    }else
                        Log.d( TAG, "Fail to get the poster,index=" + i);
                }
            }else{
                Log.d(TAG, "Fail to get the rec program information");
            }

            return pageInfo;
        }

        @Override
        protected void onPostExecute(ItemRecordPageInfoProperty result) {
            super.onPostExecute(result);

            if(isCancelled()){
                Log.d(TAG, "GetHisItemTask is Cancelled");
                return;
            }

            if(result==null){
                Log.e(TAG, "return null");
                return;
            }

            mCallbacks.updateHisItems(result, pageSize, pageIndex, type);
        }
    }

    private class GetHomeItemTask extends AsyncTask<HttpGetPara, Integer, ItemPageInfoProperty> {
        private int pageSize;
        private int pageIndex;
        private ItemListType_E type;

        @Override
        protected ItemPageInfoProperty doInBackground(HttpGetPara... params) {
            Log.d(TAG, "GetHomeItemTask(), doInBackground");
            HttpGetPara para = params[0];
            pageSize = params[0].getPageSize();
            pageIndex = params[0].getPageIndex();
            type = params[0].getType();
            if(isCancelled()){
                Log.d(TAG, "GetHomeItemTask is Cancelled");
                requestItemRemove(para);
                return null;
            }

            ItemPageInfoProperty pageInfo = new ItemPageInfoProperty();
            RetStatus_E ret = VodData.RetStatus_E.Ret_Fail;
            int tryTime = 1;
            while( ret != RetStatus_E.Ret_Success && tryTime < mMaxHttpGetRetryTimes ){
                if(isCancelled()){
                    requestItemRemove(para);
                    return null;
                }

                try {
                    if( false == vodModel.checkItemsStatus() )
                        vodModel.getResourceFromServer();
                    ret = vodModel.getItems( 0,0,pageInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tryTime++;
            }

            if(tryTime == mMaxHttpGetRetryTimes){
                Log.d(TAG, "GetHomeItemTask(),return fail!");
                requestItemRemove(para);
                return null;
            }

            if( ret == RetStatus_E.Ret_Success){
                int count = 0;
                count = pageInfo.getTotalNum();
                if( count > 0 ){
                    for( int i = 0; i < count && i < pageSize; i++){
                        if(isCancelled()){
                            requestItemRemove(para);
                            return null;
                        }

                        int index = pageInfo.getTotalNum() - 1 - i;
                        String imageUrl = pageInfo.getItemList().get(index).getBigPoster();
                        if(imageUrl != null) {
                            String localPosterUrl = vodModel.getPosterUrl(imageUrl);
                        }else
                            Log.d( TAG, "GetHomeItemTask(),Fail to get poster,index=" + index);
                    }
                }else{
                    Log.d(TAG, "GetHomeItemTask(), no valid program");
                }
            }else {
                Log.d(TAG, "GetHomeItemTask(),Fail to get program");
            }

            requestItemRemove(para);

            return pageInfo;
        }

        @Override
        protected void onPostExecute(ItemPageInfoProperty result) {
            super.onPostExecute(result);

            if(isCancelled()){
                Log.d(TAG, "GetItemTask is Cancelled");
                return;
            }

            if(result==null){
                Log.e(TAG, "return null");
                return;
            }
            mCallbacks.updateItems(result, pageSize, pageIndex, type);
        }
    }

    private class GetHomeFavItemTask extends AsyncTask<HttpGetPara, Integer, ItemRecordPageInfoProperty> {
        private int pageSize;
        private int pageIndex;
        private ItemListType_E type;

        @Override
        protected ItemRecordPageInfoProperty doInBackground(HttpGetPara... params) {
            Log.d(TAG, "GetHomeFavItemTask(), doInBackground");
            HttpGetPara para = params[0];
            pageSize = para.getPageSize();
            pageIndex = para.getPageIndex();
            type = para.getType();

            ItemRecordPageInfoProperty pageInfo = new ItemRecordPageInfoProperty();
            RetStatus_E ret = RetStatus_E.Ret_Fail;
            int tryTime = 1;

            while( ret != RetStatus_E.Ret_Success && tryTime < mMaxHttpGetRetryTimes ){
                if(isCancelled()){
                    requestItemRemove(para);
                    return null;
                }

                ret = vodModel.getFavItems( 0, 0, pageInfo);
                tryTime++;
            }

            if(tryTime == mMaxHttpGetRetryTimes){
                Log.d(TAG, "GetHomeFavItemTask(),return fail!");
                requestItemRemove(para);
                return null;
            }

            if( ret == RetStatus_E.Ret_Success){
                int count = 0;
                count = pageInfo.getTotalNum();
                if( count > 0 ){
                    for( int i = 0; i < count && i < pageSize; i++){
                        if(isCancelled()){
                            requestItemRemove(para);
                            return null;
                        }

                        int index = pageInfo.getTotalNum() - 1 - i;
                        String imageUrl = pageInfo.getRecordList().get(index).getBigPoster();
                        if(imageUrl != null) {
                            String localPosterUrl = vodModel.getPosterUrl(imageUrl);
                        }else
                            Log.d( TAG, "GetHomeFavItemTask(),Fail to get poster,index=" + index);
                    }
                }else{
                    Log.d(TAG, "GetHomeFavItemTask(), no valid rec program");
                }
            }else{
                Log.d(TAG, "GetHomeFavItemTask(),Fail to get rec program");
            }

            requestItemRemove(para);

            return pageInfo;
        }

        @Override
        protected void onPostExecute(ItemRecordPageInfoProperty result) {
            super.onPostExecute(result);

            if(isCancelled()){
                Log.d(TAG, "GetHomeFavItemTask is Cancelled");
                return;
            }

            if(result==null){
                Log.e(TAG, "return null");
                return;
            }

            mCallbacks.updateFavItems(result, pageSize, pageIndex, type);
        }
    }

    private class GetHomeHisItemTask extends AsyncTask<HttpGetPara, Integer, ItemRecordPageInfoProperty> {
        private int pageSize;
        private int pageIndex;
        private ItemListType_E type;

        @Override
        protected ItemRecordPageInfoProperty doInBackground(HttpGetPara... params) {
            Log.d(TAG, "GetHomeHisItemTask(), doInBackground");
            HttpGetPara para = params[0];
            pageSize = para.getPageSize();
            pageIndex = para.getPageIndex();
            type = para.getType();

            ItemRecordPageInfoProperty pageInfo = new ItemRecordPageInfoProperty();
            RetStatus_E ret = RetStatus_E.Ret_Fail;
            int tryTime = 1;

            while( ret != RetStatus_E.Ret_Success && tryTime < mMaxHttpGetRetryTimes ){
                if(isCancelled()){
                    requestItemRemove(para);
                    return null;
                }

                ret = vodModel.getHisItems(0, 0, pageInfo);
                tryTime++;
            }

            if(tryTime == mMaxHttpGetRetryTimes){
                Log.d(TAG, "GetHomeHisItemTask(),return fail!");
                requestItemRemove(para);
                return null;
            }

            if( ret == RetStatus_E.Ret_Success){
                int count = 0;
                count = pageInfo.getTotalNum();
                if( count > 0 ){
                    for( int i = 0; i < count && i < pageSize; i++){
                        if(isCancelled()){
                            requestItemRemove(para);
                            return null;
                        }

                        int index = pageInfo.getTotalNum() - 1 - i;
                        String imageUrl = pageInfo.getRecordList().get(index).getBigPoster();
                        if(imageUrl != null) {
                            String localPosterUrl = vodModel.getPosterUrl(imageUrl);
                        }else
                            Log.d( TAG, "GetHomeHisItemTask(),Fail to get poster,index=" + index);
                    }
                }else{
                    Log.d(TAG, "GetHomeHisItemTask(), no valid rec program");
                }
            }else{
                Log.d(TAG, "GetHomeHisItemTask(),Fail to get rec program");
            }

            requestItemRemove(para);

            return pageInfo;
        }

        @Override
        protected void onPostExecute(ItemRecordPageInfoProperty result) {
            super.onPostExecute(result);

            if(isCancelled()){
                Log.d(TAG, "GetHomeHisItemTask is Cancelled");
                return;
            }

            if(result==null){
                Log.e(TAG, "return null");
                return;
            }

            mCallbacks.updateHisItems(result, pageSize, pageIndex, type);
        }
    }

    private class GetDetailItemTask extends AsyncTask<HttpGetPara, Integer, ItemDetailProperty> {
        private int progId;
        private ItemListType_E type;

        @Override
        protected ItemDetailProperty doInBackground(HttpGetPara... params) {
            Log.d(TAG, "GetDetailItemTask(), doInBackground");
            HttpGetPara para = params[0];
            progId = params[0].getPageSize();
            type = params[0].getType();
            if(isCancelled()){
                Log.d(TAG, "GetDetailItemTask() isCancelled");
                requestItemRemove(para);
                return null;
            }

            ItemPageInfoProperty pageInfo = new ItemPageInfoProperty();
            RetStatus_E ret = VodData.RetStatus_E.Ret_Fail;
            try {
                ret = vodModel.getItems( 0, 0, pageInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ItemDetailProperty itemInfo = new ItemDetailProperty();
            if( ret == RetStatus_E.Ret_Success){
                int count = pageInfo.getTotalNum();
                int i = 0;
                for (i = count - 1; i >= 0 ; i--) {
                    if (progId == pageInfo.getItemList().get(i).getId()) {
                        break;
                    }
                }
                if( i < 0 ){
                    Log.d( TAG, "GetDetailItemTask(), invalid progId.");
                    return null;
                }

                if(isCancelled()){
                    requestItemRemove(para);
                    return null;
                }

                // prepare data for the detail menu
                /*
                String imageUrl = pageInfo.getItemList().get(i).getBigPoster();
                String localPosterUrl = vodModel.getPosterUrl(imageUrl);
                if( localPosterUrl == null )
                    Log.d( TAG, "GetDetailItemTask(), fail to get the poster");
                */

                String detailUrl = pageInfo.getItemList().get(i).getDetail();
                ret = vodModel.getItemDetail(detailUrl, itemInfo);
                if( ret != RetStatus_E.Ret_Success )
                    Log.d( TAG, "GetDetailItemTask(), fail to get the detail program");

                //Log.d(TAG, "desc=" + itemInfo.getDesc());
            }else{
                Log.d(TAG, "Fail to get the program information");
            }

            requestItemRemove(para);

            return itemInfo;
        }

        @Override
        protected void onPostExecute(ItemDetailProperty result) {
            super.onPostExecute(result);

            if(isCancelled()){
                Log.d(TAG, "GetDetailItemTask() is Cancelled");
                return;
            }

            if(result==null){
                Log.e(TAG, "return null");
                return;
            }
            mCallbacks.updateDetailItem(result, type);
        }
    }

    private class GetPosterTask extends AsyncTask<HttpGetPara, Integer, String> {
        private int progId;
        private ItemListType_E type;

        @Override
        protected String doInBackground(HttpGetPara... params) {
            Log.d(TAG, "GetPosterTask(), doInBackground");
            HttpGetPara para = params[0];
            progId = params[0].getPageSize();
            type = params[0].getType();
            if(isCancelled()){
                Log.d(TAG, "GetPosterTask() isCancelled");
                requestItemRemove(para);
                return null;
            }

            ItemPageInfoProperty pageInfo = new ItemPageInfoProperty();
            RetStatus_E ret = VodData.RetStatus_E.Ret_Fail;
            try {
                ret = vodModel.getItems( 0, 0, pageInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String localPosterUrl = null;
            if( ret == RetStatus_E.Ret_Success){
                int count = pageInfo.getTotalNum();
                int i = 0;
                for (i = count -1; i >= 0; i--) {
                    if (progId == pageInfo.getItemList().get(i).getId()) {
                        break;
                    }
                }
                if( i <  0){
                    Log.d( TAG, "GetPosterTask(), invalid progId.");
                    return null;
                }

                if(isCancelled()){
                    requestItemRemove(para);
                    return null;
                }

                // prepare the Poster
                String imageUrl = pageInfo.getItemList().get(i).getBigPoster();
                localPosterUrl = vodModel.getPosterUrl(imageUrl);
                if( localPosterUrl == null )
                    Log.d( TAG, "GetPosterTask(), fail to get the poster");
            }else{
                Log.d(TAG, "Fail to get the program information");
            }

            requestItemRemove(para);

            return localPosterUrl;
        }

        @Override
        protected void onPostExecute( String result) {
            super.onPostExecute(result);

            if(isCancelled()){
                Log.d(TAG, "GetPosterTask() is Cancelled");
                return;
            }

            if(result==null){
                Log.e(TAG, "return null");
                return;
            }
            mCallbacks.updatePosterItem(result,type);
        }
    }
}
