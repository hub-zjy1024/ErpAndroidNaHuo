package utils.wsdelegate;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedHashMap;

public class MartService {
    private static String serverName = WebserviceUtils.MartService;

    //txt:xs:string
    public static String HelloWorld(String txt) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("txt", txt);
        return WebserviceUtils.getWcfResult(properties, "HelloWorld", serverName);
    }

    public static String GetBGCompare() throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        return WebserviceUtils.getWcfResult(properties, "GetBGCompare", serverName);
    }

    //checkWord:xs:string
    //userID:xs:string
    //passWord:xs:string
    //DeviceID:xs:string
    //version:xs:string
    public static String AndroidLogin(String checkWord, String userID, String passWord, String DeviceID, String version) throws
            IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("userID", userID);
        properties.put("passWord", passWord);
        properties.put("DeviceID", DeviceID);
        properties.put("version", version);
        return WebserviceUtils.getWcfResult(properties, "AndroidLogin", serverName);
    }

    //checkWord:xs:string
    //buyerID:xs:int
    //partNo:xs:string
    public static String GetBillByPartNo(String checkWord, int buyerID, String partNo) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("buyerID", buyerID);
        properties.put("partNo", partNo);
        return WebserviceUtils.getWcfResult(properties, "GetBillByPartNo", serverName);
    }

    //checkWord:xs:string
    //buyerID:xs:int
    //pid:xs:string
    //partNo:xs:string
    public static String GetBillByPartNoAndPid(String checkWord, int buyerID, String pid, String partNo) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("buyerID", buyerID);
        properties.put("pid", pid);
        properties.put("partNo", partNo);
        return WebserviceUtils.getWcfResult(properties, "GetBillByPartNoAndPid", serverName);
    }

    //checkWord:xs:string
    //id:xs:int
    public static String GetMartStockInfoByID(String checkWord, int id) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("id", id);
        return WebserviceUtils.getWcfResult(properties, "GetMartStockInfoByID", serverName);
    }

    //checkWord:xs:string
    //userID:xs:int
    //myDeptID:xs:int
    //providerName:xs:string
    public static String GetMyProvider(String checkWord, int userID, int myDeptID, String providerName) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("userID", userID);
        properties.put("myDeptID", myDeptID);
        properties.put("providerName", providerName);
        return WebserviceUtils.getWcfResult(properties, "GetMyProvider", serverName);
    }

    //checkWord:xs:string
    //id:xs:int
    //providerID:xs:int
    //nofapiao:xs:int
    //details:xs:string
    //operID:xs:int
    //operName:xs:string
    //deviceID:xs:string
    //storageID:xs:int
    public static String SaveMartStock(String checkWord, int id, int providerID, int nofapiao, String details, int operID,
                                       String operName, String deviceID, int storageID) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("id", id);
        properties.put("providerID", providerID);
        properties.put("nofapiao", nofapiao);
        properties.put("details", details);
        properties.put("operID", operID);
        properties.put("operName", operName);
        properties.put("deviceID", deviceID);
        properties.put("storageID", storageID);
        return WebserviceUtils.getWcfResult(properties, "SaveMartStock", serverName);
    }

    //checkWord:xs:string
    //partNo:xs:string
    public static String GetBiJiaList(String checkWord, String partNo) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("partNo", partNo);
        return WebserviceUtils.getWcfResult(properties, "GetBiJiaList", serverName);
    }

    //checkWord:xs:string
    //mainID:xs:int
    public static String GetBiJiaDetail(String checkWord, int mainID) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("mainID", mainID);
        return WebserviceUtils.getWcfResult(properties, "GetBiJiaDetail", serverName);
    }

    //checkWord:xs:string
    //deptID:xs:int
    //martStockID:xs:int
    //note:xs:string
    //price:xs:string
    //providerID:xs:int
    //tempProviderName:xs:string
    //tempProviderPhone:xs:string
    //tempProviderLinkMen:xs:string
    //payType:xs:string
    //jzDate:xs:string
    //userID:xs:int
    //userName:xs:string
    //providerEnableKP:xs:int
    //faPiao:xs:int
    public static String InsertCompare(String checkWord, int deptID, int martStockID, String note, String price, int
            providerID, String tempProviderName, String tempProviderPhone, String tempProviderLinkMen, String payType, String
                                               jzDate, int userID, String userName, int providerEnableKP, int faPiao) throws
            IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("deptID", deptID);
        properties.put("martStockID", martStockID);
        properties.put("note", note);
        properties.put("price", price);
        properties.put("providerID", providerID);
        properties.put("tempProviderName", tempProviderName);
        properties.put("tempProviderPhone", tempProviderPhone);
        properties.put("tempProviderLinkMen", tempProviderLinkMen);
        properties.put("payType", payType);
        properties.put("jzDate", jzDate);
        properties.put("userID", userID);
        properties.put("userName", userName);
        properties.put("providerEnableKP", providerEnableKP);
        properties.put("faPiao", faPiao);
        return WebserviceUtils.getWcfResult(properties, "InsertCompare", serverName);
    }

    //checkWord:xs:string
    //userid:xs:int
    //filename:xs:string
    //image:xs:string
    public static String uploadImage(String checkWord, int userid, String filename, String image) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("userid", userid);
        properties.put("filename", filename);
        properties.put("image", image);
        return WebserviceUtils.getWcfResult(properties, "uploadImage", serverName);
    }

    //checkWord:xs:string
    //code:xs:string
    public static String BarCodeLogin(String checkWord, String code) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("code", code);
        return WebserviceUtils.getWcfResult(properties, "BarCodeLogin", serverName);
    }

    //sid:xs:string
    public static String GetFtpPhoneIP(String sid) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("sid", sid);
        return WebserviceUtils.getWcfResult(properties, "GetFtpPhoneIP", serverName);
    }

    public static String GetClientIP() throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        String res = WebserviceUtils.getWcfResult(properties, "GetClientIP", WebserviceUtils.MartService);
        return res;
    }


    //pid:xs:int
    //filepath:xs:string
    public static String UpdateHeTongFileInfo(int pid, String filepath) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        properties.put("filepath", filepath);
        return WebserviceUtils.getWcfResult(properties, "UpdateHeTongFileInfo", serverName);
    }

    //pid:xs:int
    public static String GetHeTongFileInfo(int pid) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        return WebserviceUtils.getWcfResult(properties, "GetHeTongFileInfo", serverName);
    }

    //id:xs:int
    public static String GetPriviteInfo(int id) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", id);
        return WebserviceUtils.getWcfResult(properties, "GetPriviteInfo", serverName);
    }

    //id:xs:int
    public static String GetInvoiceCorpInfo(int id) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", id);
        return WebserviceUtils.getWcfResult(properties, "GetInvoiceCorpInfo", serverName);
    }

    //pid:xs:string
    //partNo:xs:string
    //buyerID:xs:string
    public static String GetOLDMartStockView_ok(String pid, String partNo, String buyerID) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        properties.put("partNo", partNo);
        properties.put("buyerID", buyerID);
        return WebserviceUtils.getWcfResult(properties, "GetOLDMartStockView_ok", serverName);
    }

    //pid:xs:string
    public static String GetOLDMartStockView_mx(String pid) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        return WebserviceUtils.getWcfResult(properties, "GetOLDMartStockView_mx", serverName);
    }

    //ip:xs:string
    public static String GetChildStorageIDByIP(String ip) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("ip", ip);
        return WebserviceUtils.getWcfResult(properties, "GetChildStorageIDByIP", serverName);
    }

    //selValue:xs:string
    public static String GetXinHaoManageInfo(String selValue) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("selValue", selValue);
        return WebserviceUtils.getWcfResult(properties, "GetXinHaoManageInfo", serverName);
    }

    //pid:xs:string
    //uid:xs:string
    //strValue:xs:string
    public static String SetSC_BD_PartTypeInfoInfo(String pid, String uid, String strValue) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        properties.put("uid", uid);
        properties.put("strValue", strValue);
        return WebserviceUtils.getWcfResult(properties, "SetSC_BD_PartTypeInfoInfo", serverName);
    }

    //id:xs:string
    public static String GetProviderPartInfo(String id) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", id);
        return WebserviceUtils.getWcfResult(properties, "GetProviderPartInfo", serverName);
    }

    //objid:xs:string
    public static String GetProviderPartInfoByObjID(String objid) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("objid", objid);
        return WebserviceUtils.getWcfResult(properties, "GetProviderPartInfoByObjID", serverName);
    }

    //id:xs:string
    public static String DeleteProviderPartInfo(String id) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", id);
        return WebserviceUtils.getWcfResult(properties, "DeleteProviderPartInfo", serverName);
    }

    //checkWord:xs:string
    //userID:xs:int
    //myDeptID:xs:int
    //providerName:xs:string
    public static String GetMyProviderInfoByName(String checkWord, int userID, int myDeptID, String providerName) throws
            IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("checkWord", checkWord);
        properties.put("userID", userID);
        properties.put("myDeptID", myDeptID);
        properties.put("providerName", providerName);
        return WebserviceUtils.getWcfResult(properties, "GetMyProviderInfoByName", serverName);
    }

    //objid:xs:string
    //parentid:xs:string
    //objname:xs:string
    //objvalue:xs:string
    //objtype:xs:string
    //objexpress:xs:string
    public static String SetProviderPartInfoAdd(String objid, String parentid, String objname, String objvalue, String objtype,
                                                String objexpress) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("objid", objid);
        properties.put("parentid", parentid);
        properties.put("objname", objname);
        properties.put("objvalue", objvalue);
        properties.put("objtype", objtype);
        properties.put("objexpress", objexpress);
        return WebserviceUtils.getWcfResult(properties, "SetProviderPartInfoAdd", serverName);
    }

    //partno:xs:string
    //pid:xs:string
    public static String GetSSCGInfoByDDYH(String partno, String pid) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("partno", partno);
        properties.put("pid", pid);
        return WebserviceUtils.getWcfResult(properties, "GetSSCGInfoByDDYH", serverName);
    }

    //partno:xs:string
    //pid:xs:string
    public static String GetSSCGInfoByDDYHList(String partno, String pid) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("partno", partno);
        properties.put("pid", pid);
        return WebserviceUtils.getWcfResult(properties, "GetSSCGInfoByDDYHList", serverName);
    }

    //pid:xs:string
    public static String GetSSCGInfoByDDYHByID(String pid) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        return WebserviceUtils.getWcfResult(properties, "GetSSCGInfoByDDYHByID", serverName);
    }

    //pid:xs:string
    //state:xs:string
    //chkNote:xs:string
    public static String UpdateSSCSState(String pid, String state, String chkNote) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        properties.put("state", state);
        properties.put("chkNote", chkNote);
        return WebserviceUtils.getWcfResult(properties, "UpdateSSCSState", serverName);
    }

    //PictureName:xs:string
    //PictureURL:xs:string
    //MakerID:xs:string
    //CorpID:xs:string
    //DeptID:xs:string
    //UserID:xs:string
    //billID:xs:string
    //billType:xs:string
    public static String InsertPicYHInfo(String PictureName, String PictureURL, String MakerID, String CorpID, String DeptID,
                                         String UserID, String billID, String billType) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("PictureName", PictureName);
        properties.put("PictureURL", PictureURL);
        properties.put("MakerID", MakerID);
        properties.put("CorpID", CorpID);
        properties.put("DeptID", DeptID);
        properties.put("UserID", UserID);
        properties.put("billID", billID);
        properties.put("billType", billType);
        return WebserviceUtils.getWcfResult(properties, "InsertPicYHInfo", serverName);
    }

    //pid:xs:string
    public static String GetYHPicInfo(String pid) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        return WebserviceUtils.getWcfResult(properties, "GetYHPicInfo", serverName);
    }

    //pid:xs:int
    //cbtype:xs:string
    public static String GetCheckInfo(int pid, String cbtype) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        properties.put("cbtype", cbtype);
        return WebserviceUtils.getWcfResult(properties, "GetCheckInfo", serverName);
    }

    //pid:xs:string
    //title:xs:string
    //uid:xs:string
    //note:xs:string
    //type:xs:string
    public static String SetCheckInfo(String pid, String title, String uid, String note, String type) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        properties.put("title", title);
        properties.put("uid", uid);
        properties.put("note", note);
        properties.put("type", type);
        return WebserviceUtils.getWcfResult(properties, "SetCheckInfo", serverName);
    }

    //part:xs:string
    //pcount:xs:int
    //isbhbm:xs:boolean
    public static String GetInstorageBalanceInfoNew(String part, int pcount, boolean isbhbm) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("part", part);
        properties.put("pcount", pcount);
        properties.put("isbhbm", isbhbm);
        return WebserviceUtils.getWcfResult(properties, "GetInstorageBalanceInfoNew", serverName);
    }

    //detailID:xs:int
    //price:xs:decimal
    //uid:xs:int
    //ip:xs:string
    //dogSN:xs:string
    public static String SetPriceInfo(int detailID, float price, int uid, String ip, String dogSN) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("detailID", detailID);
        properties.put("price", price);
        properties.put("uid", uid);
        properties.put("ip", ip);
        properties.put("dogSN", dogSN);
        return WebserviceUtils.getWcfResult(properties, "SetPriceInfo", serverName);
    }

    //detailID:xs:int
    //isfb:xs:boolean
    //uid:xs:int
    //ip:xs:string
    //dogSN:xs:string
    public static String SetStypeInfo(int detailID, boolean isfb, int uid, String ip, String dogSN) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("detailID", detailID);
        properties.put("isfb", isfb);
        properties.put("uid", uid);
        properties.put("ip", ip);
        properties.put("dogSN", dogSN);
        return WebserviceUtils.getWcfResult(properties, "SetStypeInfo", serverName);
    }

    public static String GetBHBMDataInfo() throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        String res = WebserviceUtils.getWcfResult(properties, "GetBHBMDataInfo", WebserviceUtils.MartService);
        return res;
    }


    public static String GetInseorageBalanceInfoToSender() throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        String res = WebserviceUtils.getWcfResult(properties, "GetInseorageBalanceInfoToSender", WebserviceUtils.MartService);
        return res;
    }


    public static String GetInseorageBalanceInfoToCount() throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        String res = WebserviceUtils.getWcfResult(properties, "GetInseorageBalanceInfoToCount", WebserviceUtils.MartService);
        return res;
    }


    //pid:xs:string
    //uid:xs:string
    public static String SetNHWC_UserIDInfo(String pid, String uid) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        properties.put("uid", uid);
        return WebserviceUtils.getWcfResult(properties, "SetNHWC_UserIDInfo", serverName);
    }

    //uid:xs:string
    //pid:xs:string
    //type:xs:string
    public static String GetMHWCInfo(String uid, String pid, String type) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("uid", uid);
        properties.put("pid", pid);
        properties.put("type", type);
        return WebserviceUtils.getWcfResult(properties, "GetMHWCInfo", serverName);
    }

    //pid:xs:string
    //checkinfo:xs:string
    public static String SetNaHuoWanChengInfo(String pid, String checkinfo) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        properties.put("checkinfo", checkinfo);
        return WebserviceUtils.getWcfResult(properties, "SetNaHuoWanChengInfo", serverName);
    }

    public static String SetGuoHuoTongZhiApplyCustom(String pid, String applycustomInfo) throws IOException,
            XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        properties.put("applycustomInfo", applycustomInfo);
        return WebserviceUtils.getWcfResult(properties, "SetGuoHuoTongZhiApplyCustom", serverName);
    }

    public static String GetModuleInfoByID(String id) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", id);
        return WebserviceUtils.getWcfResult(properties, "GetModuleInfoByID", serverName);
    }

    public static String GetGWPayCompare(String limitCorp) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("limitCorp", limitCorp);
        return WebserviceUtils.getWcfResult(properties, "GetGWPayCompare", serverName);
    }

    public static String GetGNPayCompare(String limitCorp) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("limitCorp", limitCorp);
        return WebserviceUtils.getWcfResult(properties, "GetGNPayCompare", serverName);
    }
    public static String SCCGCreatApplyCustoms_new(String pid,String uid,String uname)throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("pid", pid);
        properties.put("uid", uid);
        properties.put("uname", uname);
        return WebserviceUtils.getWcfResult(properties, "SCCGCreatApplyCustoms_new", serverName);
    }
    public static String GetProviderDataInfoByName(String id)throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", id);
        return WebserviceUtils.getWcfResult(properties, "GetProviderDataInfoByName", serverName);
    }
}