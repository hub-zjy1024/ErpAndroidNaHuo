package utils.net.ftp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.b1b.js.erpandroid_nahuo.application.MyApp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 Created by 张建宇 on 2017/2/21.
 主要是一些路径的获取 */

public class UploadUtils {
    public static String KF_DIR = "/Zjy/kf/";
    public static String CG_DIR = "/Zjy/caigou/";

    public static String getPankuRemoteName(String id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "a_" + id + "_" + sdf.format(new Date());
    }

    public static String getChukuRemoteName(String id) {
        String flag = String.valueOf(Math.random());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());
        return "and_" + id + "_" + date + "_" + flag.substring(2, 6);
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_M_d");
        Calendar calendar = Calendar.getInstance();
        String str = calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar
                .DAY_OF_MONTH);
        return sdf.format(new Date());
    }
    public static String getCurrentYearAndMonth() {
        Calendar calendar = Calendar.getInstance();
        String str = calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) ;
        return str;
    }
    public static String getCurrentYearAndMonth2() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
        return sdf.format(new Date());
    }
    public static String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        String str = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) ;
        return str;
    }

    public static String getCaigouRemoteDir(String fileName) {
        return  "/" + getCurrentDate() + "/" + fileName;
    }

    public static String getCaigouRemotePath(String pid) {
        return "/" + getCurrentDate() + "/" + createSCCGRemoteName(pid) + ".jpg";
    }

    public static String getChukuPath(String pid) {
        return "/" + getCurrentDate() + "/" + getChukuRemoteName(pid) + ".jpg";
    }
    public static String getCurrentAtSS() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
    public static String getDD(Date date) {
        SimpleDateFormat.getTimeInstance(1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String str = sdf.format(date);
        return str;
    }
    /**
     @param ftpUrl
     @param path
     @return 插入到数据库的图片地址
     */
    public static String createInsertPath(String ftpUrl, String path) {
        StringBuilder builder = new StringBuilder();
        builder.append("ftp://");
        builder.append(ftpUrl);
        builder.append(path);
        return builder.toString();
    }

    /**
     @return 插入到数据库的图片地址
     */
    public static String createSCCGRemoteName(String pid) {
        StringBuilder builder = new StringBuilder();
        builder.append("SCCG_");
        builder.append(pid);
        builder.append("_and_" + System.currentTimeMillis());
        return builder.toString();
    }
    @SuppressLint("MissingPermission")
    public static String getDeviceID(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String devId = "";
        try {
            devId = tm.getDeviceId();
            Log.i("zjy", "UploadUtils->getDeviceID(): getDeviceId==" + devId);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String imei = tm.getImei();
                String meid = tm.getMeid();
                Log.i("zjy", "UploadUtils->getDeviceID(): imei==" + imei + "\tmeid=" + meid);
            }
            String serial = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                serial = android.os.Build.getSerial();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = android.os.Build.getSerial();
            } else {
                serial = Build.SERIAL;
            }
            Log.w("zjy", "UploadUtils->getDeviceID(): getSerial==" + serial);

            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                //android 10兼容id
                String m_szDevIDShort = "35" +
                        Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                        Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                        Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                        Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                        Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                        Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                        Build.USER.length() % 10; //13 位
                if (serial == null || Build.UNKNOWN.equalsIgnoreCase(serial)) {
                    //                    serial=m_szDevIDShort;
                }
                devId = new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
                Log.w("zjy", "UploadUtils->getDeviceID(): api>27,id=" + devId);
                //            getGLESTextureLimitEqualAboveLollipop();
            }
            if (devId == null) {
                devId = getCommDevID();
                throw new IOException("错误信息");
            }
        } catch (NullPointerException e) {
            devId = "bug_" + System.currentTimeMillis();
            MyApp.myLogger.writeBug("no deviceID null," + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            MyApp.myLogger.writeBug("no deviceID," + e.getMessage());
        }
        return devId;
    }


   /* String serial;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        serial = android.os.Build.getSerial();
    } else {
        serial = Build.SERIAL;
    }
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        MyApp.myLogger.writeInfo("android-10 inUse");
        //android 10兼容id
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位
        deviceid = "";

        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }*/
    @SuppressLint("MissingPermission")
    public static String getCommDevID() {
        String serial = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            serial = android.os.Build.getSerial();
        } else  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serial = android.os.Build.getSerial();
            Log.w("zjy", "UploadUtils->getDeviceID(): getSerial==" + serial );
        } else {
            serial = Build.SERIAL;
        }
        Log.i("zjy", "UploadUtils->getDeviceID():serial ==" + serial);
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位
        if(serial==null||Build.UNKNOWN.equalsIgnoreCase(serial)){
//            serial=m_szDevIDShort;
        }
        String devId = new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        Log.w("zjy", "UploadUtils->getDeviceID():commonDev id api>27,id=" + devId);
        return devId;
    }
    @SuppressLint("MissingPermission")
    public static String getPhoneCode(Context context) {
        String deviceId = getDeviceID(context);
        String phoneModel = Build.MODEL;
        String phoneName = Build.BRAND;
        StringBuilder phoneId = new StringBuilder();
        phoneId.append(phoneModel);
        phoneId.append("-");
        phoneId.append(phoneName);
        phoneId.append("-");
        phoneId.append(deviceId);
        return phoneId.toString();
    }

    public static String getTestPath(String pid) {
        return KF_DIR + getChukuRemoteName(pid) + ".jpg";
    }

    public static String getChukuRemotePath(String nowName, String pid) {
        return "/" + getCurrentDate() + "/" + nowName + ".jpg";
    }

}
