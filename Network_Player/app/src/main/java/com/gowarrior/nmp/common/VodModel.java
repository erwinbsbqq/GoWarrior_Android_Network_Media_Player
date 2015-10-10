package com.gowarrior.nmp.common;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gowarrior.nmp.common.TransUtility;

import com.gowarrior.nmp.common.MediaDataFetch;
import com.gowarrior.nmp.common.VodData.ItemRelevantProperty;
import com.gowarrior.nmp.common.VodData.RetStatus_E;
import com.gowarrior.nmp.common.VodData.RecordType_E;
import com.gowarrior.nmp.common.VodData.ItemProperty;
import com.gowarrior.nmp.common.VodData.ItemDetailProperty;
import com.gowarrior.nmp.common.VodData.ItemCheckProperty;
import com.gowarrior.nmp.common.VodData.ItemTotalProperty;
import com.gowarrior.nmp.common.VodData.ItemPageInfoProperty;
import com.gowarrior.nmp.common.VodData.ItemRecordPageInfoProperty;
import com.gowarrior.nmp.common.VodData.ItemRecordProperty;

import java.util.Comparator;
import java.util.Objects;


public class VodModel {
    private static final String TAG = "VodModel";
    //The maximum number of the recorded history program
    private static int	 hisRecMaxNum = 100;
    private static int    retryTimes = 5;
    private static String interFilePath = null;
    private static String interCachePath = null;
    private static String jsFavFileName = "vodFavList.js";
    private static String jsHisFileName = "vodHisList.js";
    private HttpFetcher httpFetcher;
    private static String httpUrl = "http://127.0.0.1:3932";

    private int compare(ItemRecordProperty item1, ItemRecordProperty item2){
        int ret = 1;
        String str1 = item1.getId()+ ", "
                + item1.getTitle()+ ", "
                + item1.getType()+", "
                + item1.getDetail() +", "
                + item1.getSmallPoster() +", "
                + item1.getBigPoster();

        String str2 = item2.getId()+ ", "
                + item2.getTitle()+ ", "
                + item2.getType()+", "
                + item2.getDetail() +", "
                + item2.getSmallPoster() +", "
                + item2.getBigPoster();

        if(str1.equals(str2))
            ret = 0;

        return ret;
    }

    public VodModel(){
        super();
        httpFetcher = new HttpFetcher();
    }

    public void setInterFilePath(String filePath){
        interFilePath = filePath;
    }

    public String getInterFilePath(){
        return interFilePath;
    }

    public void setInterCachePath(String cachePath){
        interCachePath = cachePath;
    }

    public String getInterCachePath(){
        return interCachePath;
    }

    public RetStatus_E getResourceFromServer(){
        RetStatus_E ret = RetStatus_E.Ret_Fail;
        String  vodListUrl = null;
        ItemCheckProperty item = new ItemCheckProperty();

        //Get the addrList.xml from Server;
        httpFetcher.get(httpUrl + "/addrList.xml", this.getInterFilePath());

        //Analyse the addList.xml to get the URL for vodList.xml
        ret = getItemCheck(null,item);
        if(ret == RetStatus_E.Ret_Success)
            vodListUrl = item.getListUrl();
        else
            return ret;

        //Get the vodList.xml from Server
        httpFetcher.get(vodListUrl, this.getInterFilePath());

        return ret;
    }

    public boolean checkItemsStatus(){
        String xmlFilePath = this.getInterFilePath() + "/" + "vodList.xml";
        String jsFilePath = this.getInterFilePath() + "/"+"vodList.js";
        File xmlFin = new File(xmlFilePath);
        File jsFin = new File(jsFilePath);
        if( false == xmlFin.exists() && false == jsFin.exists())
            return false;
        else
            return true;
    }

    public RetStatus_E getItems(int pageSize, int pageIndex,ItemPageInfoProperty pageInfo) throws IOException{
        RetStatus_E ret = RetStatus_E.Ret_Fail;
        TransUtility transUtil = new TransUtility();
        String retData = null;

        String jsFilePath = this.getInterFilePath() + "/"+"vodList.js";
        File fin = new File(jsFilePath);
        if(false == fin.exists()){
            // To transform the XML file into JS file, and save the JS file
            String xmlFilePath = this.getInterFilePath() + "/" + "vodList.xml";
            //Log.d(TAG,"To analyse XML File: " + xmlFilePath);
            fin = new File(xmlFilePath);
            if(fin.exists()){
                //Log.d(TAG, " read XML File=" + xmlFilePath);
                retData = transUtil.xmlFile2JsonStr(xmlFilePath);
                try {
                    transUtil.writeFile(jsFilePath, retData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                return RetStatus_E.Ret_NoData;
            }
        }else{
            try{
                //Log.d(TAG," read JS File=" + jsFilePath);
                retData = transUtil.readFile(jsFilePath);
            }catch (IOException e){
                e.printStackTrace();
                return RetStatus_E.Ret_Fail;
            }
        }

        if(retData.isEmpty()){
            Log.e(TAG, "return empty!");
            return RetStatus_E.Ret_Fail;
        }

        pageInfo.setPageIndex(pageIndex);
        pageInfo.setPageSize(pageSize);
        MediaDataFetch mdf = new MediaDataFetch();
        ret = mdf.getItemsFromJSON(retData, pageInfo);

        return ret;
    }

    public RetStatus_E getItemDetail(String pData, ItemDetailProperty itemDetail){
        RetStatus_E ret = RetStatus_E.Ret_Fail;
        TransUtility transUtil = new TransUtility();

        // To get the JS String through pData;
        // 1.Step1: Use pData to get the XML file
        String retData = null;
        httpFetcher.get(pData, this.getInterCachePath());

        String fileName = getPosterNameFromUrl(pData);
        String xmlFilePath = this.getInterCachePath() + fileName;
        File fin = new File(xmlFilePath);
        if(fin.exists()){
            //Log.d(TAG,"read XML File=" + xmlFilePath);
            retData = transUtil.xmlFile2JsonStr(xmlFilePath);
        }else{
            return RetStatus_E.Ret_NoData;
        }

        if(retData.isEmpty()){
            Log.e(TAG, "return empty!");
            return RetStatus_E.Ret_Fail;
        }

        MediaDataFetch mdf = new MediaDataFetch();
        ret = mdf.getItemDetailFromJSON(retData, itemDetail);

        return ret;
    }

    public RetStatus_E getItemCheck(String pData, ItemCheckProperty itemCheck){
        RetStatus_E ret = RetStatus_E.Ret_Fail;
        TransUtility transUtil = new TransUtility();

        // To get the JS String
        String retData = null;

        String xmlFilePath = this.getInterFilePath() + "/" + "addrList.xml";
        File fin = new File(xmlFilePath);
        if(fin.exists()){
            //Log.d(TAG, "read XML File=" + xmlFilePath);
            retData = transUtil.xmlFile2JsonStr(xmlFilePath);
        }else{
            Log.e(TAG, "no valid file to analyse!");
            return RetStatus_E.Ret_NoData;
        }

        if(retData.isEmpty()){
            Log.e(TAG, "return empty!");
            return RetStatus_E.Ret_Fail;
        }

        MediaDataFetch mdf = new MediaDataFetch();
        ret = mdf.getItemCheckFromJSON(retData, itemCheck);

        return ret;
    }

    public RetStatus_E getFavItems(int pageSize, int pageIndex, ItemRecordPageInfoProperty pageInfo){
        RetStatus_E ret = RetStatus_E.Ret_Fail;
        TransUtility transUtil = new TransUtility();
        String retData = null;

        // To get the JS String about vodFavList.js
        String jsFilePath = this.getInterFilePath() + "/" + "vodFavList.js";
        File fin = new File(jsFilePath);
        if(false == fin.exists()){
            // Have no favorite program list
            return RetStatus_E.Ret_NoData;
        }else{
            try {
                //Log.d(TAG,"read JS File="+jsFilePath);
                retData = transUtil.readFile(jsFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                return RetStatus_E.Ret_Fail;
            }
        }

        if(retData.isEmpty()){
            Log.e(TAG, "return empty!");
            return RetStatus_E.Ret_Fail;
        }

        pageInfo.setPageIndex(pageIndex);
        pageInfo.setPageSize(pageSize);
        MediaDataFetch mdf = new MediaDataFetch();
        ret = mdf.getFavItemsFromJSON(retData, pageInfo);

        return ret;
    }

    public RetStatus_E getHisItems(int pageSize, int pageIndex, ItemRecordPageInfoProperty pageInfo){
        RetStatus_E ret = RetStatus_E.Ret_Fail;
        TransUtility transUtil = new TransUtility();
        String retData = null;

        // To get the JS String about vodHisList.js
        String jsFilePath = this.getInterFilePath() + "/" + "vodHisList.js";
        File fin = new File(jsFilePath);
        if(false == fin.exists()){
            // Have no history record program list
            return RetStatus_E.Ret_NoData;
        }else{
            try{
                //Log.d(TAG, "read JS File="+jsFilePath);
                retData = transUtil.readFile(jsFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                return RetStatus_E.Ret_Fail;
            }
        }

        if(retData.isEmpty()){
            Log.e(TAG, "return empty!");
            return RetStatus_E.Ret_Fail;
        }

        pageInfo.setPageIndex(pageIndex);
        pageInfo.setPageSize(pageSize);
        MediaDataFetch mdf = new MediaDataFetch();
        ret = mdf.getHisItemsFromJSON(retData, pageInfo);

        return ret;
    }

    private RetStatus_E addItemRecord(ItemRecordProperty item,RecordType_E type){
        RetStatus_E ret = RetStatus_E.Ret_Fail;
        TransUtility transUtil = new TransUtility();
        MediaDataFetch mdf = new MediaDataFetch();
        ItemRecordPageInfoProperty recItemsInfo= new ItemRecordPageInfoProperty();
        String retData = null;
        String jsFilePath = null;

        if(type == RecordType_E.Rec_Fav){
            // Favorite Program
            jsFilePath = this.getInterFilePath() + "/" + jsFavFileName;
        }else if(type == RecordType_E.Rec_His){
            // History Record Program
            jsFilePath = this.getInterFilePath() + "/" + jsHisFileName;
        }else{
            Log.e(TAG,"Invalid parameter!!");
        }

        File fin = new File(jsFilePath);
        if(false != fin.exists()){
            try {
                retData = transUtil.readFile(jsFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                return RetStatus_E.Ret_Fail;
            }

            if(retData.isEmpty()){
                Log.e(TAG, "The original JS File is empty!");
            }else{
                // Get the Favorite program list through the js string;
                if(type == RecordType_E.Rec_Fav)
                    ret = mdf.getFavItemsFromJSON(retData, recItemsInfo);
                else if(type == RecordType_E.Rec_His)
                    ret = mdf.getHisItemsFromJSON(retData, recItemsInfo);
            }
        }

        if(recItemsInfo.getRecordList()==null){
            ArrayList<ItemRecordProperty> favRecList = new ArrayList<VodData.ItemRecordProperty>();
            Log.d(TAG, " No record list; item.getItem : id= " + item.getId());
            favRecList.add(item);
            recItemsInfo.setRecordList(favRecList);
        }else{
            //Generate the new JS String
            int i =  0;
            ItemRecordProperty tmp = new ItemRecordProperty();
            for(i = 0; i < recItemsInfo.getRecordList().size(); i++){
                tmp = recItemsInfo.getRecordList().get(i);
                if(compare(tmp,item) == 0){
                    break;
                }
            }
            if(i >= recItemsInfo.getRecordList().size()){
                Log.d(TAG, "item.getItem : id= " + item.getId());
                if(type == RecordType_E.Rec_His
                        && recItemsInfo.getRecordList().size() >= hisRecMaxNum){
                    // Delete the first recorded item
                    recItemsInfo.getRecordList().remove(0);
                }
                recItemsInfo.getRecordList().add(item);
            }else{
                if(type == RecordType_E.Rec_His){
                    // For the history record program to replace the old recorded item;
                    recItemsInfo.getRecordList().remove(i);
                    recItemsInfo.getRecordList().add(item);
                }
            }
        }

        //Save the new file
        try {
            String jsRecStr = null;
            if(type == RecordType_E.Rec_Fav)
                jsRecStr = mdf.getJsonFromFavItems(recItemsInfo);
            else if(type == RecordType_E.Rec_His)
                jsRecStr = mdf.getJasnFromHisItems(recItemsInfo);

            transUtil.writeFile(jsFilePath, jsRecStr);
        } catch (IOException e) {
            e.printStackTrace();
            ret = RetStatus_E.Ret_Fail;
        }

        return ret;
    }

    private RetStatus_E deleteItemRecord(ItemRecordProperty item,RecordType_E type){
        RetStatus_E ret = RetStatus_E.Ret_Fail;
        TransUtility transUtil = new TransUtility();
        MediaDataFetch mdf = new MediaDataFetch();
        ItemRecordPageInfoProperty recItemsInfo= new ItemRecordPageInfoProperty();
        String retData = null;
        String jsFilePath = null;

        if(type == RecordType_E.Rec_Fav){
            // Favorite Program
            jsFilePath = this.getInterFilePath() +  "/" + jsFavFileName;
        }else if(type == RecordType_E.Rec_His){
            // History Record Program
            jsFilePath = this.getInterFilePath() +  "/" + jsHisFileName;
        }else{
            Log.e(TAG,"Invalid Parameter!!");
        }

        File fin = new File(jsFilePath);
        if(false != fin.exists()){
            try {
                retData = transUtil.readFile(jsFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                return RetStatus_E.Ret_Fail;
            }

            if(retData.isEmpty()){
                Log.e(TAG, " The original JS File is empty!");
                return RetStatus_E.Ret_Fail;
            }else{
                // Get the Favorite program list through the js string;
                if(type == RecordType_E.Rec_Fav)
                    ret = mdf.getFavItemsFromJSON(retData, recItemsInfo);
                else if(type == RecordType_E.Rec_His)
                    ret = mdf.getHisItemsFromJSON(retData, recItemsInfo);
            }
        }

        if(recItemsInfo.getRecordList()==null){
            Log.d(TAG, "the original JS File is empty");
            return RetStatus_E.Ret_NoData;
        }else{
            //Generate the new JS String
            int i =  0;
            int len = recItemsInfo.getRecordList().size();
            ItemRecordProperty tmp = new ItemRecordProperty();
            for(i = 0; i < len; i++){
                tmp = recItemsInfo.getRecordList().get(i);
                if(compare(tmp,item) == 0){
                    recItemsInfo.getRecordList().remove(i);
                    break;
                }
            }
            if(i > len){
                Log.e(TAG, "Don't find the item in the record list!");
                return RetStatus_E.Ret_Fail;
            }
        }

        //Save the new file
        try {
            String jsRecStr = null;
            if(type == RecordType_E.Rec_Fav)
                jsRecStr = mdf.getJsonFromFavItems(recItemsInfo);
            else if(type == RecordType_E.Rec_His)
                jsRecStr = mdf.getJasnFromHisItems(recItemsInfo);
            transUtil.writeFile(jsFilePath, jsRecStr);
        } catch (IOException e) {
            e.printStackTrace();
            ret = RetStatus_E.Ret_Fail;
        }

        return ret;
    }

    public RetStatus_E addFavItem(ItemRecordProperty item){
        return addItemRecord(item, RecordType_E.Rec_Fav);
    }

    public RetStatus_E deleteFavItem(ItemRecordProperty item){
        return deleteItemRecord(item, RecordType_E.Rec_Fav);
    }

    public RetStatus_E addHisItem(ItemRecordProperty item){
        return addItemRecord(item, RecordType_E.Rec_His);
    }

    public RetStatus_E deleteHisItem(ItemRecordProperty item){
        return deleteItemRecord(item, RecordType_E.Rec_His);
    }

    private String getPosterNameFromUrl(String pData){
        String fileName = null;
        fileName = pData.substring(pData.lastIndexOf('/'));
        //Log.d(TAG, "poster file name=" + fileName);
        return fileName;
    }

    public RetStatus_E getPosterFromServer(String pData){
        RetStatus_E ret = RetStatus_E.Ret_Success;
        String fileName = null;
        String localPath = null;
        int     i = 0;

        fileName = getPosterNameFromUrl(pData);
        if(null == fileName){
            ret = RetStatus_E.Ret_Fail;
            Log.d(TAG,"No valid poster");
        }else{
            //Get the poster picture from Server;
            while( i++ < retryTimes){
                httpFetcher.get(pData, this.getInterCachePath());
                localPath = this.getInterCachePath() + fileName;

                File file = new File(localPath);
                if(false == file.exists()){
                    Log.d(TAG, "Failed to get the poster:" + localPath);
                    ret = RetStatus_E.Ret_Fail;
                }else{
                    ret = RetStatus_E.Ret_Success;
                    break;
                }
            }
        }
        return ret;
    }

    public String getPosterUrl(String pData){
        String  fileName = null;
        String  localPosterUrl = null;
        RetStatus_E ret;

        if( pData == null){
            Log.d(TAG, " Invalid input parameters!");
            return null;
        }

        fileName = getPosterNameFromUrl(pData);
        if(null != fileName) {
            localPosterUrl = this.getInterCachePath() + fileName;

            File file = new File(localPosterUrl);
            if (false == file.exists()) {
                //Get the poster URL in the local path
                ret = getPosterFromServer(pData);
                if(ret != RetStatus_E.Ret_Success){
                    Log.d(TAG, "Fail to get the local poster:" + localPosterUrl);
                    localPosterUrl = null;
                }
            }
        }

        return localPosterUrl;
    }
}

