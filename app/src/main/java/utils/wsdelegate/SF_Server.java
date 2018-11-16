package utils.wsdelegate;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedHashMap;

public class SF_Server{
	private static String serverName = WebserviceUtils.SF_Server;
//pid:xs:string
		public static String GetYunDanInfos(String pid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("pid", pid);
		return WebserviceUtils.getWcfResult(properties, "GetYunDanInfos", serverName);
	}

//pid:xs:string
//xh:xs:string
		public static String GetYunDanList(String pid,String xh)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("pid", pid);
		properties.put("xh", xh);
		return WebserviceUtils.getWcfResult(properties, "GetYunDanList", serverName);
	}

//pid:xs:string
//xh:xs:string
//SID:xs:string
		public static String GetYunDanListNew(String pid,String xh,String SID)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("pid", pid);
		properties.put("xh", xh);
		properties.put("SID", SID);
		return WebserviceUtils.getWcfResult(properties, "GetYunDanListNew", serverName);
	}

//expressName:xs:string
//corpID:xs:int
		public static String GetCorpExpressAccountNo(String expressName,int corpID)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("expressName", expressName);
		properties.put("corpID", corpID);
		return WebserviceUtils.getWcfResult(properties, "GetCorpExpressAccountNo", serverName);
	}

//id:xs:string
		public static String GetClientPCCInfo(String id)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("id", id);
		return WebserviceUtils.getWcfResult(properties, "GetClientPCCInfo", serverName);
	}

//id:xs:string
//Province:xs:string
//City:xs:string
//County:xs:string
		public static String SetClientPCCInfo(String id,String Province,String City,String County)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("id", id);
		properties.put("Province", Province);
		properties.put("City", City);
		properties.put("County", County);
		return WebserviceUtils.getWcfResult(properties, "SetClientPCCInfo", serverName);
	}

	public static String GetBD_DHAddress()throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		 String res=WebserviceUtils.getWcfResult(properties, "GetBD_DHAddress", WebserviceUtils.SF_Server);
		return res;
	}


//pid:xs:string
//yundanID:xs:string
		public static String UpdateYunDanInfoByPrintCount(String pid,String yundanID)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("pid", pid);
		properties.put("yundanID", yundanID);
		return WebserviceUtils.getWcfResult(properties, "UpdateYunDanInfoByPrintCount", serverName);
	}

//objname:xs:string
//objvalue:xs:string
//express:xs:string
		public static String InsertBD_YunDanInfo(String objname,String objvalue,String express)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("objname", objname);
		properties.put("objvalue", objvalue);
		properties.put("express", express);
		return WebserviceUtils.getWcfResult(properties, "InsertBD_YunDanInfo", serverName);
	}

//objname:xs:string
//objvalue:xs:string
//express:xs:string
//objtype:xs:string
		public static String InsertBD_YunDanInfoOfType(String objname,String objvalue,String express,String objtype)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("objname", objname);
		properties.put("objvalue", objvalue);
		properties.put("express", express);
		properties.put("objtype", objtype);
		return WebserviceUtils.getWcfResult(properties, "InsertBD_YunDanInfoOfType", serverName);
	}

//pid:xs:string
		public static String GetBD_YunDanInfoByID(String pid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("pid", pid);
		return WebserviceUtils.getWcfResult(properties, "GetBD_YunDanInfoByID", serverName);
	}

//fpid:xs:string
		public static String GetFPInfoByFP(String fpid)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("fpid", fpid);
		return WebserviceUtils.getWcfResult(properties, "GetFPInfoByFP", serverName);
	}

//yundanid:xs:string
//fph:xs:string
		public static String SetKYYunDanInfo(String yundanid,String fph)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("yundanid", yundanid);
		properties.put("fph", fph);
		return WebserviceUtils.getWcfResult(properties, "SetKYYunDanInfo", serverName);
	}

//yundanID:xs:string
//note:xs:string
//uid:xs:string
//uname:xs:string
		public static String SetFileExpress(String yundanID,String note,String uid,String uname)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("yundanID", yundanID);
		properties.put("note", note);
		properties.put("uid", uid);
		properties.put("uname", uname);
		return WebserviceUtils.getWcfResult(properties, "SetFileExpress", serverName);
	}

}