package utils.wsdelegate;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedHashMap;

public class ChuKuServer{
	private static String serverName = WebserviceUtils.ChuKuServer;
//checkWord:xs:string
//uid:xs:string
//stime:xs:string
//etime:xs:string
//pid:xs:string
//partNo:xs:string
		public static String GetChuKuTongZhiInfoList(String checkWord,String uid,String stime,String etime,String pid,String partNo)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("checkWord", checkWord);
		properties.put("uid", uid);
		properties.put("stime", stime);
		properties.put("etime", etime);
		properties.put("pid", pid);
		properties.put("partNo", partNo);
		return WebserviceUtils.getWcfResult(properties, "GetChuKuTongZhiInfoList", serverName);
	}

//checkWord:xs:string
//uid:xs:string
//stime:xs:string
//etime:xs:string
//pid:xs:string
//partNo:xs:string
		public static String GetChuKuInfoList(String checkWord,String uid,String stime,String etime,String pid,String partNo)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("checkWord", checkWord);
		properties.put("uid", uid);
		properties.put("stime", stime);
		properties.put("etime", etime);
		properties.put("pid", pid);
		properties.put("partNo", partNo);
		return WebserviceUtils.getWcfResult(properties, "GetChuKuInfoList", serverName);
	}

//checkWord:xs:string
//pid:xs:string
		public static String GetChuKuTongZhiInfo(String checkWord,String pid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("checkWord", checkWord);
		properties.put("pid", pid);
		return WebserviceUtils.getWcfResult(properties, "GetChuKuTongZhiInfo", serverName);
	}

//checkWord:xs:string
//pid:xs:string
		public static String GetChKuTongZhiDetailInfo(String checkWord,String pid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("checkWord", checkWord);
		properties.put("pid", pid);
		return WebserviceUtils.getWcfResult(properties, "GetChKuTongZhiDetailInfo", serverName);
	}

//checkWord:xs:string
//pid:xs:string
		public static String GetChuKuInfo(String checkWord,String pid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("checkWord", checkWord);
		properties.put("pid", pid);
		return WebserviceUtils.getWcfResult(properties, "GetChuKuInfo", serverName);
	}

//checkWord:xs:string
//pid:xs:string
		public static String GetChuKuDetailInfo(String checkWord,String pid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("checkWord", checkWord);
		properties.put("pid", pid);
		return WebserviceUtils.getWcfResult(properties, "GetChuKuDetailInfo", serverName);
	}

//checkWord:xs:string
//ID:xs:string
		public static String GetBILL_PictureRelatenfoByID(String checkWord,String ID)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("checkWord", checkWord);
		properties.put("ID", ID);
		return WebserviceUtils.getWcfResult(properties, "GetBILL_PictureRelatenfoByID", serverName);
	}

//checkWord:xs:string
//typeid:xs:int
//pid:xs:string
//partNo:xs:string
//uid:xs:string
		public static String GetChuKuCheckInfoByTypeID(String checkWord,int typeid,String pid,String partNo,String uid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("checkWord", checkWord);
		properties.put("typeid", typeid);
		properties.put("pid", pid);
		properties.put("partNo", partNo);
		properties.put("uid", uid);
		return WebserviceUtils.getWcfResult(properties, "GetChuKuCheckInfoByTypeID", serverName);
	}

//checkWord:xs:string
//t:xs:int
//info:xs:string
//pid:xs:string
//tp:xs:int
//uname:xs:string
//uid:xs:string
		public static String GetSetCheckInfo(String checkWord,int t,String info,String pid,int tp,String uname,String uid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("checkWord", checkWord);
		properties.put("t", t);
		properties.put("info", info);
		properties.put("pid", pid);
		properties.put("tp", tp);
		properties.put("uname", uname);
		properties.put("uid", uid);
		return WebserviceUtils.getWcfResult(properties, "GetSetCheckInfo", serverName);
	}

//checkWord:xs:string
//cid:xs:int
//did:xs:int
//uid:xs:int
//pid:xs:string
//filename:xs:string
//filepath:xs:string
//stypeID:xs:string
		public static String SetInsertPicInfo(String checkWord,int cid,int did,int uid,String pid,String filename,String filepath,String stypeID)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("checkWord", checkWord);
		properties.put("cid", cid);
		properties.put("did", did);
		properties.put("uid", uid);
		properties.put("pid", pid);
		properties.put("filename", filename);
		properties.put("filepath", filepath);
		properties.put("stypeID", stypeID);
		return WebserviceUtils.getWcfResult(properties, "SetInsertPicInfo", serverName);
	}

//id:xs:string
//part:xs:string
		public static String GetDataListForPanKu(String id,String part)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("id", id);
		properties.put("part", part);
		return WebserviceUtils.getWcfResult(properties, "GetDataListForPanKu", serverName);
	}

//InstorageDetailID:xs:int
//OldPartNo:xs:string
//OldQuantity:xs:int
//PKPartNo:xs:string
//PKQuantity:xs:string
//PKmfc:xs:string
//PKDescription:xs:string
//PKPack:xs:string
//PKBatchNo:xs:string
//MinPack:xs:int
//OperID:xs:int
//OperName:xs:string
//DiskID:xs:string
//Note:xs:string
//PKPlace:xs:string
		public static String PanKu(int InstorageDetailID,String OldPartNo,int OldQuantity,String PKPartNo,String PKQuantity,String PKmfc,String PKDescription,String PKPack,String PKBatchNo,int MinPack,int OperID,String OperName,String DiskID,String Note,String PKPlace)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("InstorageDetailID", InstorageDetailID);
		properties.put("OldPartNo", OldPartNo);
		properties.put("OldQuantity", OldQuantity);
		properties.put("PKPartNo", PKPartNo);
		properties.put("PKQuantity", PKQuantity);
		properties.put("PKmfc", PKmfc);
		properties.put("PKDescription", PKDescription);
		properties.put("PKPack", PKPack);
		properties.put("PKBatchNo", PKBatchNo);
		properties.put("MinPack", MinPack);
		properties.put("OperID", OperID);
		properties.put("OperName", OperName);
		properties.put("DiskID", DiskID);
		properties.put("Note", Note);
		properties.put("PKPlace", PKPlace);
		return WebserviceUtils.getWcfResult(properties, "PanKu", serverName);
	}

//instoragedetailPID:xs:int
		public static String CancelPanKuFlag(int instoragedetailPID)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("instoragedetailPID", instoragedetailPID);
		return WebserviceUtils.getWcfResult(properties, "CancelPanKuFlag", serverName);
	}

//id:xs:string
		public static String GetPauKuDataInfoByID(String id)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("id", id);
		return WebserviceUtils.getWcfResult(properties, "GetPauKuDataInfoByID", serverName);
	}

//uid:xs:string
//part:xs:string
		public static String GetDataListForNaHuo(String uid,String part)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("uid", uid);
		properties.put("part", part);
		return WebserviceUtils.getWcfResult(properties, "GetDataListForNaHuo", serverName);
	}

//id:xs:string
//main:xs:string
//detail:xs:string
		public static String GetDataInfoForNaHuoByID(String id,String main,String detail)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("id", id);
		properties.put("main", main);
		properties.put("detail", detail);
		return WebserviceUtils.getWcfResult(properties, "GetDataInfoForNaHuoByID", serverName);
	}

//id:xs:string
		public static String GetDataInfoByNaHuoMainID(String id)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("id", id);
		return WebserviceUtils.getWcfResult(properties, "GetDataInfoByNaHuoMainID", serverName);
	}

//id:xs:string
		public static String GetDataInfoByNaHuoDetailID(String id)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("id", id);
		return WebserviceUtils.getWcfResult(properties, "GetDataInfoByNaHuoDetailID", serverName);
	}

//uid:xs:string
		public static String GetSupplierByUserID(String uid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("uid", uid);
		return WebserviceUtils.getWcfResult(properties, "GetSupplierByUserID", serverName);
	}

//id:xs:int
//ProviderID:xs:string
//details:q1:ArrayOfstring
//NotFormate Void:SaveMarketInfo
	@Deprecated
	public static String SaveMarketInfo(int id,String ProviderID)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("id", id);
		properties.put("ProviderID", ProviderID);
		return WebserviceUtils.getWcfResult(properties, "SaveMarketInfo", serverName);
	}

//beginDate:xs:string
//endDate:xs:string
//partNo:xs:string
//pid:xs:int
//uid:xs:int
		public static String GetOutStorageNotifyPrintViewList(String beginDate,String endDate,String partNo,int pid,int uid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("beginDate", beginDate);
		properties.put("endDate", endDate);
		properties.put("partNo", partNo);
		properties.put("pid", pid);
		properties.put("uid", uid);
		return WebserviceUtils.getWcfResult(properties, "GetOutStorageNotifyPrintViewList", serverName);
	}

//pid:xs:int
//uid:xs:int
		public static String GetOutStoragePrintViewPriviceInfo(int pid,int uid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("pid", pid);
		properties.put("uid", uid);
		return WebserviceUtils.getWcfResult(properties, "GetOutStoragePrintViewPriviceInfo", serverName);
	}

//pid:xs:int
//uid:xs:int
		public static String GetOutStorageNotifyPrintView(int pid,int uid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("pid", pid);
		properties.put("uid", uid);
		return WebserviceUtils.getWcfResult(properties, "GetOutStorageNotifyPrintView", serverName);
	}

//pid:xs:int
//uid:xs:int
		public static String GetBillInfoByPID(int pid,int uid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("pid", pid);
		properties.put("uid", uid);
		return WebserviceUtils.getWcfResult(properties, "GetBillInfoByPID", serverName);
	}

//pid:xs:int
		public static String UpdatePrintCKTZCount(int pid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("pid", pid);
		return WebserviceUtils.getWcfResult(properties, "UpdatePrintCKTZCount", serverName);
	}

//pid:xs:int
		public static String GetInstorectInfo(int pid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("pid", pid);
		return WebserviceUtils.getWcfResult(properties, "GetInstorectInfo", serverName);
	}

//mxid:xs:int
		public static String GetInstorectInfoByMXID(int mxid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("mxid", mxid);
		return WebserviceUtils.getWcfResult(properties, "GetInstorectInfoByMXID", serverName);
	}

//tb:q2:ArrayOfKeyValueOfanyTypeanyType
//NotFormate Void:ReturnHashTableInfo
	@Deprecated
	public static String ReturnHashTableInfo()throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		return WebserviceUtils.getWcfResult(properties, "ReturnHashTableInfo", serverName);
	}

//pid:xs:int
//partno:xs:string
//storageid:xs:string
		public static String GetStorageBlanceInfoByID(int pid,String partno,String storageid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("pid", pid);
		properties.put("partno", partno);
		properties.put("storageid", storageid);
		return WebserviceUtils.getWcfResult(properties, "GetStorageBlanceInfoByID", serverName);
	}

//ip:xs:string
		public static String GetStoreRoomIDByIP(String ip)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("ip", ip);
		return WebserviceUtils.getWcfResult(properties, "GetStoreRoomIDByIP", serverName);
	}

//detailID:xs:string
//place:xs:string
//kuQu:xs:string
//operDescript:xs:string
//uid:xs:string
//ip:xs:string
		public static String Shangjia(String detailID,String place,String kuQu,String operDescript,String uid,String ip)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("detailID", detailID);
		properties.put("place", place);
		properties.put("kuQu", kuQu);
		properties.put("operDescript", operDescript);
		properties.put("uid", uid);
		properties.put("ip", ip);
		return WebserviceUtils.getWcfResult(properties, "Shangjia", serverName);
	}

}