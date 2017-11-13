package com.b1b.js.erpandroid_nahuo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.application.MyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import utils.MyFileUtils;
import utils.MyToast;
import utils.UploadUtils;
import utils.WebserviceUtils;

public class Nh_MainActivity extends AppCompatActivity {

    private EditText edUserName;
    private EditText edPwd;
    private Button btnLogin;
    private Button btnScancode;
    private CheckBox cboRemp;
    private CheckBox cboAutol;
    private SharedPreferences sp;
    private ProgressDialog pd;
    private ProgressDialog downPd;
    private ProgressDialog scanDialog;
    private String updateLog;
    private String downPath;
    private TextView tvVersion;
    private final int SCANCODE_LOGIN_SUCCESS = 4;
    private final int NEWWORK_ERROR = 2;
    private final int FTPCONNECTION_ERROR = 5;
    private AlertDialog permissionDialog;
    private Context mContext = this;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                //失败
                case 0:
                    pd.cancel();
                    MyToast.showToast(mContext, msg.obj.toString());
                    break;
                case 1:
                    //成功
                    HashMap<String, String> infoMap = (HashMap<String, String>) msg.obj;
                    //每次登录检查userInfo是否有变动，以免数据库更新（流量允许）
                    MyApp.id = infoMap.get("name");
                    //登陆用户名改变，清除缓存
                    if (!sp.getString("name", "").equals(MyApp.id)) {
                        sp.edit().clear().apply();
                        //登录成功之后调用，获取相关信息
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("name", infoMap.get("name"));
                        edit.putString("pwd", infoMap.get("pwd"));
                        edit.apply();
                        getUserInfoDetail(MyApp.id);
                    } else {
                        MyApp.ftpUrl = sp.getString("ftp", "");
                    }
                    //是否记住密码
                    if (cboRemp.isChecked()) {
                        ifSavePwd(true, infoMap.get("name"), infoMap.get("pwd"));
                    } else {
                        ifSavePwd(false, infoMap.get("name"), infoMap.get("pwd"));
                    }
                    pd.cancel();
                                        Intent intent = new Intent(mContext,
                     MenuActivity.class);
                                        startActivity(intent);
                                        finish();
//                                        Intent intent = new Intent(mContext,
//                     NahuoListAcitivity.class);
//                                        startActivity(intent);

                    //                    Intent intent = new Intent(mContext,
                    // DialogAcitity.class);
                    //                    startActivity(intent);
                    break;
                case NEWWORK_ERROR:
                    MyToast.showToast(mContext, "网络状态不佳,请检查网络状态");
                    if (pd != null) {
                        pd.cancel();
                    }
                    if (scanDialog != null && scanDialog.isShowing()) {
                        scanDialog.cancel();
                    }
                    break;
                //扫码登录处理
                case SCANCODE_LOGIN_SUCCESS:
                    try {
                        JSONObject object1 = new JSONObject(msg.obj.toString());
                        JSONArray main = object1.getJSONArray("表");
                        JSONObject obj = main.getJSONObject(0);
                        String url = obj.getString("PhotoFtpIP");
                        Log.e("zjy", "mContext.java->handleMessage(): ftpUrl==" + url);
                        String uid = obj.getString("UserID");
                        MyApp.id = uid;
                        String defUid = sp.getString("name", "");
                        //换用户则清除缓冲
                        final String[] urls = url.split("\\|");
                        String localUrl = sp.getString("ftp", "");
                        if (!defUid.equals(uid)) {
                            sp.edit().clear().commit();
                            getUserInfoDetail(MyApp.id);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("name", uid).apply();
                            //"|"为特殊字符，需要用"\\"转义
                            checkSaveFTP(url);
                        } else {
                            if (localUrl.equals("")) {
                                checkSaveFTP(url);
                            } else {
                                for (int i = 0; i < urls.length; i++) {
                                    if (urls[i].equals(localUrl)) {
                                        MyApp.ftpUrl = localUrl;
                                        if (scanDialog != null && scanDialog.isShowing
                                                ()) {
                                            scanDialog.cancel();
                                        }
                                        Intent intentScan = new Intent(mContext,
                                                MenuActivity.class);
                                        startActivity(intentScan);
                                        finish();
                                        break;
                                    } else {
                                        if (i == urls.length - 1) {
                                            checkSaveFTP(url);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (scanDialog != null && scanDialog.isShowing()) {
                            scanDialog.cancel();
                        }
                        MyToast.showToast(mContext, "扫描结果有误");
                    }
                    break;
                //获取ftp地址
                case FTPCONNECTION_ERROR:
                    //连接ftp失败
                    MyToast.showToast(mContext, "连接不到ftp服务器:" + msg.obj.toString() + "," +
                            "扫码登录失败");
                    if (scanDialog != null && scanDialog.isShowing()) {
                        scanDialog.cancel();
                    }
                    break;
                case 6:
                    MyToast.showToast(mContext, "获取ftp地址成功:" + MyApp.ftpUrl);
                    if (pd != null && pd.isShowing()) {
                        pd.cancel();
                    }
                    Intent intentScan = new Intent(mContext, MenuActivity.class);
                    startActivity(intentScan);
                    finish();
                    break;
                case 8:
                    int percent = msg.arg1;
                    if (percent < 0) {
                        return;
                    }
                    downPd.setProgress(percent);
                    if (percent == 100) {
                        downPd.dismiss();
                        MyToast.showToast(mContext, "下载完成");
                    }
                    break;
                case 7:
                    break;
                case 10:
                    MyToast.showToast(mContext, "部门号或公司号为空");
                    break;
                case 11:
                    downPd.cancel();
                    MyToast.showToast(mContext, "下载失败");
                    break;
                case 12:
                    String info = tvVersion.getText().toString().trim();
                    info = info + "，更新说明:" + "\n" + updateLog;
                    tvVersion.setText(info);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nh__main);
        edUserName = (EditText) findViewById(R.id.login_username);
        edPwd = (EditText) findViewById(R.id.login_pwd);
        btnLogin = (Button) findViewById(R.id.login_btnlogin);
        btnScancode = (Button) findViewById(R.id.login_scancode);
        btnScancode.setEnabled(false);
        cboRemp = (CheckBox) findViewById(R.id.login_rpwd);
        cboAutol = (CheckBox) findViewById(R.id.login_autol);
        tvVersion = (TextView) findViewById(R.id.main_version);
        sp = getSharedPreferences("UserInfo", 0);
        final String phoneCode = UploadUtils.getPhoneCode(mContext);

        Log.e("zjy", "MainActivity.java->onCreate(): phoneInfo==" + phoneCode);
        if (MyApp.myLogger != null) {
            MyApp.myLogger.writeInfo("phonecode:" + phoneCode);
        }
        //检查更新
        //        checkUpdate();
        PackageInfo info = null;
        PackageManager pm = getPackageManager();
        try {
            info = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int code = 0;
        if (info != null) {
            code = info.versionCode;
            tvVersion.setText("当前版本为：" + info.versionName);
            if (MyApp.myLogger != null) {
                MyApp.myLogger.writeInfo("version:" + info.versionCode);
            }
        }
        MyFileUtils.obtainFileDir(this);

        checkUpdate(code);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        readCache();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edUserName.getText().toString().trim();
                String pwd = edPwd.getText().toString().trim();
                if (pwd.equals("") || name.equals("")) {
                    MyToast.showToast(mContext, "请填写完整信息后再登录");
                } else {
                    login(name, pwd);
                }
            }
        });
        btnScancode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                Intent intent = new Intent(mContext, CaptureActivity
                // .class);
                //                startActivityForResult(intent, 200);
            }
        });
    }



    private void checkUpdate(final int nowCode) {
        if (nowCode == 0) {
            MyApp.myLogger.writeError("apk versioncode==0");
            return;
        }
        downPd = new ProgressDialog(mContext);

        downPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downPd.setTitle("更新");
        downPd.setMax(100);
        downPd.setMessage("下载中");
        downPd.setProgress(0);
        new Thread() {
            @Override
            public void run() {
                boolean ifUpdate = false;
                String downUrl = WebserviceUtils.ROOT_URL+"DownLoad/dyj_nahuo" +
                        "/dyjapp_nahuo.apk";
                String specialUrl =
                        WebserviceUtils.ROOT_URL+"DownLoad/dyj_nahuo/debug-update.txt";
                String saveName = "dyjapp_nahuo.apk";
                try {
                    //                    boolean ifUpdate = checkVersion(nowCode);
                    String checkUpdateURL =
                            WebserviceUtils.ROOT_URL+"DownLoad/dyj_nahuo/updateXml.txt";
                    HashMap<String, String> updateInfo = getUpdateXml(checkUpdateURL);
                    if (updateInfo != null) {
                        String sCode = updateInfo.get("code");
                        final String sContent = updateInfo.get("content");
                        final String sDate = updateInfo.get("date");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String info = tvVersion.getText().toString().trim();
                                info = info + "，更新说明:\n";
                                info += "更新时间:" + sDate + "\n";
                                info += "更新内容:" + sContent;
                                tvVersion.setText(info);
                            }
                        });
                        int sIntCode = Integer.parseInt(sCode);
                        if (sIntCode > nowCode) {
                            ifUpdate = true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (ifUpdate) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //必须设定进图条样式
                            downPd.show();
                        }
                    });
                    try {
                        updateAPK(mContext, handler, downUrl, saveName);
                    } catch (IOException e) {
                        handler.sendEmptyMessage(11);
                        e.printStackTrace();
                    }
                } else {
                    HashMap<String, String> map = null;
                    try {
                        map = specialUpdate(specialUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (map == null) {
                        return;
                    }
                    SharedPreferences speUpdate = getSharedPreferences("speUpdate",
                            Context.MODE_PRIVATE);
                    String localCheckID = speUpdate.getString("checkid", "");
                    String deviceCode = UploadUtils.getDeviceID(mContext);
                    String onlineCode = map.get("deviceID");
                    String apkUrl = map.get("url");
                    String onlineCheckID = map.get("checkid");
                    if (apkUrl != null) {
                        if (localCheckID.equals("")) {
                            speUpdate.edit().putString("checkid", onlineCheckID).commit();
                            return;
                        }
                        if (!localCheckID.equals(onlineCheckID)) {
                            if ("all".equals(onlineCheckID)) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //必须设定进图条样式
                                        downPd.show();
                                    }
                                });
                                try {
                                    updateAPK(mContext, handler, downUrl, saveName);
                                    speUpdate.edit().putString("checkid",
                                            onlineCheckID).commit();
                                } catch (IOException e) {
                                    handler.sendEmptyMessage(11);
                                    e.printStackTrace();
                                }
                            } else if (onlineCheckID != null && onlineCode.equals
                                    (deviceCode)) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //必须设定进图条样式
                                        downPd.show();
                                    }
                                });
                                try {
                                    updateAPK(mContext, handler, downUrl, saveName);
                                    speUpdate.edit().putString("checkid",
                                            onlineCheckID).commit();
                                } catch (IOException e) {
                                    handler.sendEmptyMessage(11);
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * @return
     * @throws
     * @throws IOException
     */
    public HashMap<String, String> specialUpdate(String url) throws IOException {
        boolean ifUpdate = false;
        URL urll = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setReadTimeout(10000);
        if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String len = reader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            HashMap<String, String> map = new HashMap<>();
            while (len != null) {
                String[] parm = len.split("=");
                map.put(parm[0], parm[1]);
                stringBuilder.append(len);
                len = reader.readLine();
            }
            Log.e("zjy", "MainActivity->specialUpdate(): result==" + stringBuilder
                    .toString());
            return map;
        }
        return null;
    }

    public void updateAPK(Context context, Handler mHandler, String downUrl, String
            saveName) throws
            IOException {

        URL url = new URL(downUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3 * 1000);
        conn.setReadTimeout(60000);
        if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            int size = conn.getContentLength();
            File targetDir = MyFileUtils.getFileParent();
            File file1 = new File(targetDir, saveName);
            FileOutputStream fos = new FileOutputStream(file1);
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
                mHandler.post(new Runnable() {
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
                //                Message msg = mHandler.obtainMessage(8);
                //                msg.arg1 = percent;
                //                mHandler.sendMessage(msg);
                //写入时第三个参数使用len
                fos.write(buf, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
            MyApp.myLogger.writeInfo("update download");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(targetDir,saveName);
            if (file.exists()) {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(".apk");
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android" +
                        ".package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                throw new FileNotFoundException();
            }
        }
    }

    public static HashMap<String, String> getUpdateXml(String url) throws IOException {
        URL urll = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
        conn.setConnectTimeout(30 * 1000);
        conn.setReadTimeout(30 * 1000);
        if (conn.getResponseCode() == 200) {
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                    "utf-8"));
            String len = reader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while (len != null) {
                stringBuilder.append(len);
                len = reader.readLine();
            }
            String res = stringBuilder.toString();
            HashMap<String, String> result = new HashMap<>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder docBuilder = factory.newDocumentBuilder();
                ByteArrayInputStream bin = new ByteArrayInputStream(res.getBytes
                        ("utf-8"));
                Document xmlDoc = docBuilder.parse(bin);
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
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    private void checkSaveFTP(final String url) {
        final String[] urls = url.split("\\|");
        new Thread() {
            @Override
            public void run() {
                int counts = urls.length;
                int times = 0;
                for (int i = 0; i < urls.length; i++) {
                    try {
                        Socket socket = new Socket();
                        SocketAddress remoteAddr = new InetSocketAddress(urls[i], 21);
                        socket.connect(remoteAddr, 10 * 1000);
                        MyApp.ftpUrl = urls[i];
                        sp.edit().putString("ftp", MyApp.ftpUrl).apply();
                        handler.sendEmptyMessage(6);
                        break;
                    } catch (IOException e) {
                        times++;
                        if (counts == times) {
                            Message msg = handler.obtainMessage(FTPCONNECTION_ERROR);
                            msg.obj = url;
                            handler.sendMessage(msg);
                        }
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    private void getUserInfoDetail(final String uid) {
        new Thread() {
            @Override
            public void run() {
                boolean success = false;
                while (!success) {
                    try {
                        Map<String, Object> result = getUserInfo(uid);
                        sp.edit().putInt("cid", (int) result.get("cid")).putInt("did",
                                (int) result.get("did")).
                                putString("oprName", (String) result.get("oprName"))
                                .apply();
                        success = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        handler.sendEmptyMessage(10);
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private Map<String, Object> getUserInfo(String uid) throws IOException,
            XmlPullParserException, JSONException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("checker", "1");
        map.put("uid", uid);
        SoapObject request = WebserviceUtils.getRequest(map, "GetUserInfoByUID");
        SoapPrimitive response = WebserviceUtils.getSoapPrimitiveResponse(request,
                SoapEnvelope.VER11, WebserviceUtils.Login);
        Log.e("zjy", "MainActivity.java->run(): info==" + MyApp.id + "\t" + response
                .toString());
        JSONObject object = new JSONObject(response.toString());
        JSONArray jarr = object.getJSONArray("表");
        JSONObject info = jarr.getJSONObject(0);
        String cid = info.getString("CorpID");
        String did = info.getString("DeptID");
        String name = info.getString("Name");
        HashMap<String, Object> result = new HashMap<>();
        result.put("cid", Integer.parseInt(cid));
        result.put("did", Integer.parseInt(did));
        result.put("oprName", name);
        return result;
    }

    private void ifSavePwd(boolean saveOrNot, String name, String pwd) {
        if (saveOrNot) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("name", name);
            editor.putString("pwd", pwd);
            editor.putBoolean("remp", saveOrNot);
            editor.putBoolean("autol", cboAutol.isChecked());
            editor.apply();
        } else {
            sp.edit().clear().commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            scanDialog = new ProgressDialog(mContext);
            scanDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            scanDialog.setMessage("登录中");
            scanDialog.setCancelable(false);
            scanDialog.show();
            readCode(data);
        }
    }

    /**
     * 读取条码信息
     *
     * @param data onActivtyResult()回调的data
     */
    private void readCode(final Intent data) {
        new Thread() {
            @Override
            public void run() {
                String code = data.getStringExtra("result");
                if (code != null) {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                    map.put("checkword", "");
                    map.put("code", code);
                    SoapObject object = WebserviceUtils.getRequest(map, "BarCodeLogin");
                    try {
                        SoapPrimitive response = WebserviceUtils
                                .getSoapPrimitiveResponse(object, SoapEnvelope.VER11,
                                        WebserviceUtils.MartService);
                        Message msg = handler.obtainMessage(SCANCODE_LOGIN_SUCCESS);
                        msg.obj = response.toString();
                        handler.sendMessage(msg);
                    } catch (IOException e) {
                        handler.sendEmptyMessage(NEWWORK_ERROR);
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        handler.sendEmptyMessage(NEWWORK_ERROR);
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    private void readCache() {
        if (sp.getBoolean("remp", false)) {
            edUserName.setText(sp.getString("name", ""));
            edPwd.setText(sp.getString("pwd", ""));
            cboRemp.setChecked(true);
            if (sp.getBoolean("autol", false)) {
                cboAutol.setChecked(true);
                login(sp.getString("name", ""), sp.getString("pwd", ""));
            }
        }
    }

    /**
     * 登录
     */
    private void login(final String name, final String pwd) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("登陆中");
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        pd.show();
        new Thread() {
            @Override
            public void run() {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                PackageManager pm = getPackageManager();
                String version = "";
                try {
                    PackageInfo info = pm.getPackageInfo(getPackageName(),
                            PackageManager.GET_ACTIVITIES);
                    version = info.versionName;
                    map.put("checkWord", "sdr454fgtre6e655t5rt4");
                    map.put("userID", name);
                    map.put("passWord", pwd);
                    map.put("DeviceID", WebserviceUtils.DeviceID + "," +
                            WebserviceUtils.DeviceNo);
                    map.put("version", version);
                    SoapPrimitive result = null;
                    SoapObject loginReq = WebserviceUtils.getRequest(map, "AndroidLogin");
                    result = WebserviceUtils.getSoapPrimitiveResponse(loginReq,
                            SoapEnvelope.VER11, WebserviceUtils.MartService);
                    String[] resArray = result.toString().split("-");
                    if (resArray[0].equals("SUCCESS")) {
                        Message msg1 = handler.obtainMessage();
                        HashMap<String, String> infoMap = new HashMap<>();
                        infoMap.put("name", name);
                        infoMap.put("pwd", pwd);
                        msg1.what = 1;
                        msg1.obj = infoMap;
                        handler.sendMessage(msg1);
                    } else {
                        Message msg = handler.obtainMessage(0);
                        msg.obj = result.toString();
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    handler.sendEmptyMessage(NEWWORK_ERROR);
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    handler.sendEmptyMessage(NEWWORK_ERROR);
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}