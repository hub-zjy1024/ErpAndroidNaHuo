package com.b1b.js.erpandroid_nahuo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.b1b.js.erpandroid_nahuo.application.MyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import utils.DialogUtils;
import utils.MyToast;
import utils.net.ftp.UploadUtils;
import utils.wsdelegate.WebserviceUtils;

/**
 Created by 张建宇 on 2018/7/4. */
public class UpdateClient {
    private Context mContext;
    private Handler handler = new Handler();
    ProgressDialog downPd = null;
    private String downPath = "DownLoad/dyj_nahuo/";

    private String specialUrl =
            WebserviceUtils.ROOT_URL + downPath+"debug-update.txt";
    private String checkUpdateURL = WebserviceUtils.ROOT_URL + downPath+"updateXml.txt";
    private String checkAvailableVersion = WebserviceUtils.ROOT_URL + downPath+"versionControl.txt";
    private String downUrl = WebserviceUtils.ROOT_URL + downPath +"dyjapp_nahuo.apk";
    private String saveName = "dyjapp_nahuo.apk";
    File updateFile = new File(Environment.getExternalStorageDirectory(), saveName);

    @MainThread
    public UpdateClient(Context mContext) {
        this.mContext = mContext;
        downPd = new ProgressDialog(mContext);
    }

    private int getAppVersion() {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean startUpdate() {
        downPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downPd.setTitle("更新");
        downPd.setMax(100);
        downPd.setMessage("下载中");
        downPd.setProgress(0);
        SharedPreferences spUpdate = mContext.getSharedPreferences("updatechecker", Context.MODE_PRIVATE);
        boolean needDownload = false;
        int lastCode = spUpdate.getInt("lastcode", -1);

        try {
            HashMap<String, String> updateInfo = getUpdateXml(checkUpdateURL);
            if (updateInfo != null) {
                getUpdateInfo(updateInfo);
                String sCode = updateInfo.get("code");
                int nowCode = getAppVersion();
                int sIntCode = Integer.parseInt(sCode);
                if (sIntCode > nowCode) {
                    if (updateFile.exists()) {
                        if (sIntCode > lastCode) {
                            updateFile.delete();
                            needDownload = true;
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtils.getSpAlert(mContext, "当前存在更新文件，是否安装", "提示", new DialogInterface.OnClickListener
                                            () {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            installApk(updateFile);
                                        }
                                    }, "是", null, "否").show();
                                }
                            });
                        }
                    } else {
                        needDownload = true;
                    }
                    spUpdate.edit().putInt("lastcode", sIntCode).apply();
                }
                if (needDownload) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            downPd.show();
                        }
                    });
                    downloadApk();
                } else {
                    HashMap<String, String> map = specialUpdate(specialUrl);
                    if (map == null) {
                        return false;
                    }
                    SharedPreferences speUpdate = mContext.getSharedPreferences("speUpdate", Context
                            .MODE_PRIVATE);
                    String localCheckID = speUpdate.getString("checkid", "");
                    String deviceCode = UploadUtils.getDeviceID(mContext);
                    String onlineCode = map.get("deviceID");
                    String apkUrl = map.get("url");
                    String onlineCheckID = map.get("checkid");
                    if (apkUrl != null) {
                        if (localCheckID.equals("")) {
                            speUpdate.edit().putString("checkid", onlineCheckID).commit();
                            return false;
                        }
                        if (!localCheckID.equals(onlineCheckID)) {
                            boolean startDown = false;
                            if ("all".equals(onlineCheckID)) {
                                startDown = true;
                            } else if (onlineCheckID != null && onlineCode.equals(deviceCode)) {
                                startDown = true;
                            }
                            if (startDown) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        downPd.show();
                                    }
                                });
                                downloadApk(apkUrl);
                                speUpdate.edit().putString("checkid", onlineCheckID).commit();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    downPd.cancel();
                }
            });
            MyApp.myLogger.writeError(e, "自动更新出现问题");
            e.printStackTrace();
        }
        return needDownload;
    }

    public void getUpdateInfo(HashMap<String, String> map) {

    }

    /**
     @return
     @throws IOException
     */
    private HashMap<String, String> specialUpdate(String url) throws IOException {
        URL urll = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
        conn.setConnectTimeout(30 * 1000);
        conn.setReadTimeout(30 * 1000);
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String len = reader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            HashMap<String, String> map = new HashMap<>();
            while (len != null) {
                String[] parm = len.split("=");
                if (parm.length == 2) {
                    map.put(parm[0], parm[1]);
                    stringBuilder.append(len);
                    stringBuilder.append("\n");
                }
                len = reader.readLine();
            }
            return map;
        } else {
            InputStream errorStream = conn.getErrorStream();
            String substring = "";
            if (errorStream != null) {
                int len = 0;
                byte[] buf = new byte[1024];
                String s = "";
                while ((len = errorStream.read(buf)) != -1) {
                    s += new String(buf, 0, len, "gb2312");
                }
                String startTag = "<body>";
                int index1 = s.indexOf(startTag);
                int index2 = s.indexOf("</body>");
                substring = s;
                if (index1 != -1) {
                    substring = s.substring(index1 + startTag.length(), index2);
                }
                substring = "返回码:" + conn.getResponseCode() + "\n" + substring;
                throw new IOException(substring);
            } else {
                substring = "返回码:" + conn.getResponseCode() + "\n" + "not Error Msg";
                throw new IOException(substring);
            }
        }
    }

    private void downloadApk() throws
            IOException {
        downloadApk(downUrl);
    }

    private void downloadApk(String downUrl) throws
            IOException {
        URL url = new URL(downUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(30 * 1000);
        conn.setReadTimeout(30 * 1000);
        if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            int size = conn.getContentLength();
            FileOutputStream fos = new FileOutputStream(updateFile);
            int len = 0;
            int hasRead = 0;
            int percent = 0;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                hasRead = hasRead + len;
                percent = (hasRead * 100) / size;
                final int tempPercent = percent;
                if (hasRead < 0) {
                    Log.e("zjy", "MainActivity.java->updateAPK(): hasRead==" + hasRead);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int percent = tempPercent;
                        if (percent < 0) {
                            return;
                        }
                        downPd.setProgress(percent);
                        if (percent == 100) {
                            downPd.cancel();
                            MyToast.showToast(mContext, "下载完成");
                        }
                    }
                });
                fos.write(buf, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
            MyApp.myLogger.writeInfo("update download OK");
            installApk(updateFile);
        }
    }

    private void installApk(File file) {
        if (file.exists()) {
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk");
            Log.e("zjy", "UpdateClient->installApk(): Apk install by==" + type);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android" +
                    ".package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    private HashMap<String, String> getUpdateXml(String url) {
        HashMap<String, String> result = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(url);
            Document xmlDoc = docBuilder.parse(inputSource);
            NodeList newVersion = xmlDoc.getElementsByTagName("latest-version");
            Node item = newVersion.item(0);
            NodeList childNodes = item.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node n = childNodes.item(i);
                String nName = n.getNodeName();
                if (nName.equals("code")) {
                    result.put("code", n.getTextContent());
                } else if (nName.equals("content")) {
                    result.put("content", n.getTextContent());
                } else if (nName.equals("date")) {
                    result.put("date", n.getTextContent());
                }
            }
            return result;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkVersionAvailable() throws IOException {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            String vName = info.versionName;
            HttpURLConnection conn = (HttpURLConnection) new URL(checkAvailableVersion).openConnection();
            conn.setConnectTimeout(30 * 1000);
            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String s = null;
            StringBuilder sb = new StringBuilder();
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
            String versions = sb.toString();
            Log.e("zjy", "UpdateClient->checkVersionAvailable(): OnlineVersoins==" + versions);
            JSONArray jsonArray = new JSONArray(versions);
            for (int i = 0; i < jsonArray.length(); i++) {
                String tempV = jsonArray.getString(i);
                if (tempV.equals(vName)) {
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("连接更新服务器异常");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
