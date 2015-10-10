package com.gowarrior.nmp.common;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;  
import org.json.JSONObject;  

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import com.gowarrior.nmp.common.VodData.ItemRecordPageInfoProperty;
import com.gowarrior.nmp.common.VodData.RetStatus_E;
import com.gowarrior.nmp.common.VodData.ItemProperty;
import com.gowarrior.nmp.common.VodData.ItemRelevantProperty;
import com.gowarrior.nmp.common.VodData.ItemCheckProperty;
import com.gowarrior.nmp.common.VodData.ItemDetailProperty;
import com.gowarrior.nmp.common.VodData.ItemTotalProperty;
import com.gowarrior.nmp.common.VodData.ItemPageInfoProperty;
import com.gowarrior.nmp.common.VodData.ItemRecordProperty;


public class MediaDataFetch {
	private final static String TAG = "MediaDataFetch";

	public MediaDataFetch() {

	}

	public RetStatus_E getItemsFromJSON(String jsonData, ItemPageInfoProperty pageInfo){
		RetStatus_E ret = null;

		if(jsonData == null || pageInfo == null){
			Log.e(TAG,"Invalid Parameter ");
			return RetStatus_E.Ret_Fail;
		}

		try {
			JSONObject all = new JSONObject(jsonData);
			JSONObject retResStatus = all.getJSONObject("response");
			JSONObject retAtrStatus = retResStatus.getJSONObject("attributes");
			int version = retAtrStatus.getInt("ver");
			JSONArray retItemList = retAtrStatus.getJSONArray("item");

			//Save the total vod prog number
			pageInfo.setTotalNum(retItemList.length());
			ArrayList<ItemProperty> itemList = new ArrayList<VodData.ItemProperty>();
			if(version == 1){
				int startPos = pageInfo.getPageIndex()*pageInfo.getPageSize();
				int len = 0;

				if(pageInfo.getPageIndex() == 0 && pageInfo.getPageSize() == 0){
					//Return all the programs
					len = pageInfo.getTotalNum();
				}else{
					if((startPos + pageInfo.getPageSize()) <= pageInfo.getTotalNum())
						len = pageInfo.getPageSize();
					else
						len = pageInfo.getTotalNum() - startPos;
				}

				for( int i = 0 ; i < len; i++){
					ItemProperty ip = new ItemProperty();
					JSONObject element = (JSONObject) retItemList.getJSONObject(i+startPos);

					ip.setId(element.getInt("id"));
					ip.setTitle(element.getString("name"));
					ip.setType(element.getString("type"));
					ip.setDetail(element.getString("detailUrl"));
					ip.setSmallPoster(element.getString("posterUrl"));
					ip.setBigPoster(element.getString("posterBigUrl"));

					itemList.add(ip);
				}
			}

			pageInfo.setItemList(itemList);
			ret = RetStatus_E.Ret_Success;
		} catch (JSONException e) {
			ret = RetStatus_E.Ret_Fail;
		}

		return ret;
	}

	public RetStatus_E getItemDetailFromJSON(String jsonData, ItemDetailProperty itemDetail){
		RetStatus_E ret = null;

		if(jsonData == null || itemDetail == null){
			Log.e(TAG,"Invalid Parameter ");
			return RetStatus_E.Ret_Fail;
		}

		try {
			JSONObject all = new JSONObject(jsonData);
			JSONObject retResStatus = all.getJSONObject("response");
			JSONObject retDetailStatus = retResStatus.getJSONObject("attributes");
			int version = retDetailStatus.getInt("ver");
			ArrayList<ItemRelevantProperty> itemList = new ArrayList<VodData.ItemRelevantProperty>();

			if(version == 1){
				itemDetail.setId(retDetailStatus.getInt("id"));
				itemDetail.setDesc(retDetailStatus.getString("description"));
				itemDetail.setPlayUrl(retDetailStatus.getString("videoUrl"));
				itemDetail.setSubtitleUrl(retDetailStatus.getString("subtitleUrl"));

				JSONArray retItemList = retDetailStatus.getJSONArray("relevantItem");
				for(int i=0 ; i < retItemList.length(); i++){
					ItemRelevantProperty ip = new ItemRelevantProperty();
					JSONObject element = (JSONObject) retItemList.getJSONObject(i);

					ip.setId(element.getInt("relId"));
					ip.setName(element.getString("name"));
					ip.setDetail(element.getString("detailUrl"));
					ip.setSmallPoster(element.getString("posterUrl"));

					itemList.add(ip);
				}
			}
			itemDetail.setItemList(itemList);
			ret=RetStatus_E.Ret_Success;
		} catch (JSONException e) {
			ret=RetStatus_E.Ret_Fail;
		}

		return ret;
	}

	public RetStatus_E getItemCheckFromJSON(String jsonData, ItemCheckProperty itemCheck){
		RetStatus_E ret = null;

		if(jsonData == null || itemCheck == null){
			Log.e(TAG,"Invalid Parameter ");
			return RetStatus_E.Ret_Fail;
		}
		
		try {
			JSONObject all = new JSONObject(jsonData);
			JSONObject retResStatus = all.getJSONObject("response");
			JSONObject retCheckStatus = retResStatus.getJSONObject("attributes");
			int version = retCheckStatus.getInt("ver");
			if(version == 1){
				itemCheck.setInterval(retCheckStatus.getLong("queryInterval"));
				itemCheck.setListUrl(retCheckStatus.getString("vodListUrl"));
				itemCheck.setListMd5(retCheckStatus.getString("vodListMD5"));
			}
			ret=RetStatus_E.Ret_Success;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			ret=RetStatus_E.Ret_Fail;
		}
		return ret;
	}
	
	
	public RetStatus_E getItemTotalFromJSON(String jsonData,ItemTotalProperty detailedItem){
		RetStatus_E ret = null;
		//The fucntion is not used temporarily
		if(jsonData == null || detailedItem == null){
			Log.e(TAG,"Invalid Parameter ");
			return RetStatus_E.Ret_Fail;
		}
		
		return ret;
	}
	
	public RetStatus_E getFavItemsFromJSON(String jsonData, ItemRecordPageInfoProperty pageInfo){
		RetStatus_E ret = null;
		if(jsonData == null || pageInfo == null){
			Log.e(TAG,"Invalid Parameter ");
			return RetStatus_E.Ret_Fail;
		}

		try {
			JSONObject all = new JSONObject(jsonData);
			JSONArray retItemList  = all.getJSONArray("favItems");
			pageInfo.setTotalNum(retItemList.length());
			ArrayList<ItemRecordProperty> itemList = new ArrayList<VodData.ItemRecordProperty>();

			int startPos = pageInfo.getPageIndex()*pageInfo.getPageSize();
			int len = 0;
			if(pageInfo.getPageIndex() == 0 && pageInfo.getPageSize() == 0){
			    //Return all the programs
			    len = pageInfo.getTotalNum();
            }else{
                if((startPos + pageInfo.getPageSize()) <= pageInfo.getTotalNum())
                    len = pageInfo.getPageSize();
                else
                    len = pageInfo.getTotalNum() - startPos;
            }

			for( int i = 0 ; i < len; i++){
				ItemRecordProperty ip = new ItemRecordProperty();
				JSONObject element = (JSONObject) retItemList.getJSONObject(i+startPos);

				ip.setId(element.getInt("id"));
				ip.setTitle(element.getString("name"));
				ip.setType(element.getString("type"));
				ip.setDetail(element.getString("detailUrl"));
				ip.setSmallPoster(element.getString("posterUrl"));
				ip.setBigPoster(element.getString("posterBigUrl"));
				ip.setPosition(element.getInt("position"));
				ip.setSourcePdata(element.getString("sourcePdata"));
				
				itemList.add(ip);
			}
			pageInfo.setRecordList(itemList);
			ret=RetStatus_E.Ret_Success;
		} catch (JSONException e) {
			ret=RetStatus_E.Ret_Fail;
		}
		return ret;
	}
	
	public String getJsonFromFavItems(ItemRecordPageInfoProperty pageInfo){
		String jsRet= null;

		if(pageInfo == null){
			Log.d(TAG,"Invalid Parameter ");
			return jsRet;
		}

		ArrayList<ItemRecordProperty> itemList = new ArrayList<VodData.ItemRecordProperty>();

		jsRet = "{"+"\"favItems\":"+"[\n";
		itemList = pageInfo.getRecordList();
		for( int i = 0 ; i < itemList.size(); i++){
			if(i == 0)
				jsRet +="{";
			else
				jsRet +=",\n{";
			
			jsRet +="\"id\":\""+itemList.get(i).getId()+"\",";
			jsRet +="\"name\":\""+itemList.get(i).getTitle()+"\",";
			jsRet +="\"type\":\""+itemList.get(i).getType()+"\",";
			jsRet +="\"detailUrl\":\""+itemList.get(i).getDetail()+"\",";
			jsRet +="\"posterUrl\":\""+itemList.get(i).getSmallPoster()+"\",";
			jsRet +="\"posterBigUrl\":\""+itemList.get(i).getBigPoster()+"\",";

			jsRet +="\"position\":\""+itemList.get(i).getPosition()+"\",";
			jsRet +="\"sourcePdata\":\""+itemList.get(i).getSourcePdata()+"\"";
			
			jsRet +="}";		
		}		
		jsRet += "\n]"+"}";
		
		return jsRet;
	}
	
	public RetStatus_E getHisItemsFromJSON(String jsonData, ItemRecordPageInfoProperty pageInfo){
		RetStatus_E ret = null;

		if(jsonData == null || pageInfo == null){
			Log.d(TAG,"Invalid Parameter ");
			return RetStatus_E.Ret_Fail;
		}
		
		try {
			JSONObject all = new JSONObject(jsonData);
			JSONArray retItemList  = all.getJSONArray("hisItems");
			pageInfo.setTotalNum(retItemList.length());

			ArrayList<ItemRecordProperty> itemList = new ArrayList<VodData.ItemRecordProperty>();
            int startPos = pageInfo.getPageIndex()*pageInfo.getPageSize();
            int len = 0;
            if(pageInfo.getPageIndex() == 0 && pageInfo.getPageSize() == 0){
                //Return all the programs
                len = pageInfo.getTotalNum();
            }else{
                if((startPos + pageInfo.getPageSize()) <= pageInfo.getTotalNum())
                    len = pageInfo.getPageSize();
                else
                    len = pageInfo.getTotalNum() - startPos;
            }

			for( int i = 0 ; i < len; i++){
				ItemRecordProperty ip = new ItemRecordProperty();
				JSONObject element = (JSONObject) retItemList.getJSONObject(i + startPos);;

				ip.setId(element.getInt("id"));
				ip.setTitle(element.getString("name"));
				ip.setType(element.getString("type"));
				ip.setDetail(element.getString("detailUrl"));
				ip.setSmallPoster(element.getString("posterUrl"));
				ip.setBigPoster(element.getString("posterBigUrl"));
				ip.setPosition(element.getInt("position"));
				ip.setSourcePdata(element.getString("sourcePdata"));
				
				itemList.add(ip);
			}
			pageInfo.setRecordList(itemList);
			ret=RetStatus_E.Ret_Success;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			ret=RetStatus_E.Ret_Fail;
		}
		return ret;
	}
	
	public String getJasnFromHisItems(ItemRecordPageInfoProperty pageInfo){	
		String jsRet= null;

		if(pageInfo == null){
			Log.d(TAG,"Invalid Parameter ");
			return jsRet;
		}

		ArrayList<ItemRecordProperty> itemList = new ArrayList<VodData.ItemRecordProperty>();
		
		jsRet = "{"+"\"hisItems\":"+"[\n";
		itemList = pageInfo.getRecordList();
		for( int i = 0 ; i < itemList.size(); i++){
			if(i == 0)
				jsRet +="{";
			else
				jsRet +=",\n{";
			
			jsRet +="\"id\":\""+itemList.get(i).getId()+"\",";
			jsRet +="\"name\":\""+itemList.get(i).getTitle()+"\",";
			jsRet +="\"type\":\""+itemList.get(i).getType()+"\",";
			jsRet +="\"detailUrl\":\""+itemList.get(i).getDetail()+"\",";
			jsRet +="\"posterUrl\":\""+itemList.get(i).getSmallPoster()+"\",";
			jsRet +="\"posterBigUrl\":\""+itemList.get(i).getBigPoster()+"\",";

			jsRet +="\"position\":\""+itemList.get(i).getPosition()+"\",";
			jsRet +="\"sourcePdata\":\""+itemList.get(i).getSourcePdata()+"\"";
			
			jsRet +="}";		
		}		
		jsRet += "\n]"+"}";
		
		return jsRet;
	}

}

