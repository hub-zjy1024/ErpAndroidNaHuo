package com.b1b.js.erpandroid_nahuo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.activity.base.SavedLoginInfoActivity;
import com.b1b.js.erpandroid_nahuo.adapter.ViewPicAdapter;
import com.b1b.js.erpandroid_nahuo.entity.FTPImgInfo;

import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import utils.DialogUtils;
import utils.MyFileUtils;
import utils.MyToast;
import utils.SoftKeyboardUtils;
import utils.net.ftp.FTPUtils;
import utils.net.ftp.FtpManager;
import utils.wsdelegate.MartService;

public class ViewPicByPidActivity extends SavedLoginInfoActivity {

    private EditText edPid;
    private GridView gv;
    private Button btnSearch;
    private List<FTPImgInfo> imgsData;
    private ViewPicAdapter adapter;
    private ProgressDialog pd;
    private boolean deleteOk = true;
    boolean isConn = false;
    int downCounts = 0;
    private AlertDialog alertDialog;
    private String downloadResult = "";

    private Context mContext = ViewPicByPidActivity.this;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter.notifyDataSetChanged();
                    dismissDialog();
                    if (downloadResult.length() > 100) {
                        downloadResult = downloadResult.substring(0, 100);
                        downloadResult += "....";
                    }
                    if (!downloadResult.equals("")) {
                        downloadResult += "总共找到" + imgsData.size() + "张图片";
                        alertDialog.setMessage(downloadResult);
                    } else {
                        alertDialog.setMessage("没有数据");
                    }
                    alertDialog.show();
                    SoftKeyboardUtils.closeInputMethod(edPid, mContext);
                    break;
                case 1:
                    dismissDialog();
                    MyToast.showToast(mContext, "当前单据没有对应的图片");
                    break;
                case 2:
                    dismissDialog();
                    MyToast.showToast(mContext, "当前网络质量较差，请重试");
                    break;
                case 3:
                    dismissDialog();
                    MyToast.showToast(mContext, "图片上传地址不在本地服务器，无法访问");
                    break;
                case 4:
                    int totalSize = msg.arg1;
                    int current = msg.arg2 + 1;
                    pd.setMessage("正在下载图片" + current + "/" + totalSize);
                    break;
                case 5:
                    dismissDialog();
                    MyToast.showToast(mContext, "图片上传地址不在本地服务器，无法访问");
                    break;
            }
        }
    };

    private void dismissDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pic_by_pid);
        edPid = (EditText) findViewById(R.id.view_pic_edpid);
        gv = (GridView) findViewById(R.id.view_pic_gv);
        btnSearch = (Button) findViewById(R.id.view_pic_btn_search);
        imgsData = new ArrayList<>();
        adapter = new ViewPicAdapter(imgsData, mContext, R.layout.item_viewpicbypid);
        gv.setAdapter(adapter);
        pd = new ProgressDialog(mContext);
        pd.setCancelable(false);
        btnSearch.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             final String pid = edPid.getText().toString().trim();
                                             imgsData.clear();
                                             if (pid.equals("")) {
                                                 MyToast.showToast(mContext, "请输入单据号");
                                                 return;
                                             }
                                             adapter.notifyDataSetChanged();
                                             File imgFile = MyFileUtils.getFileParent();
                                             if (imgFile == null) {
                                                 MyToast.showToast(mContext, "当前无可用的存储设备");
                                                 return;
                                             }
                                             final File file = new File(imgFile, "dyj_img/");
                                             MyFileUtils.checkImgFileSize(file, 100, mContext);
                                             startSearch(pid);
                                         }
                                     }
        );
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FTPImgInfo item = (FTPImgInfo) parent.getItemAtPosition(position);
                if (item != null) {
                    Intent mIntent = new Intent(mContext, PicDetailActivity.class);
                    mIntent.putExtra("path", item.getImgPath());
                    ArrayList<String> paths = new ArrayList<>();
                    for (int i = 0; i < imgsData.size(); i++) {
                        paths.add(imgsData.get(i).getImgPath());
                    }
                    mIntent.putStringArrayListExtra("paths", paths);
                    mIntent.putExtra("pos", position);
                    startActivity(mIntent);
                }
            }
        });
        alertDialog = (AlertDialog) DialogUtils.getSpAlert(this, "结果", "提示");
    }

    @Override
    protected void onResume() {
        super.onResume();
        imgsData.clear();
        adapter.notifyDataSetChanged();
        String pid = getIntent().getStringExtra("pid");
        if (pid != null) {
            edPid.setText(pid);
            File imgFile = MyFileUtils.getFileParent();
            if (imgFile == null) {
                MyToast.showToast(mContext, "当前无可用的存储设备");
                return;
            }
            if (pid.equals("")) {
                MyToast.showToast(mContext, "请输入单据号");
                return;
            }
            startSearch(pid);
        }

    }

    private void startSearch(final String pid) {
        showProgressDialog();
        new Thread() {
            @Override
            public void run() {
                super.run();
                downCounts = 0;
                String result = "";
                try {
                    //                    result = getRelativePicInfoByPid("", pid);
                    result = getYHPIC("SCCG_" + pid);
                } catch (IOException e) {
                    mHandler.sendEmptyMessage(2);
                    e.printStackTrace();
                    return;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject root = new JSONObject(result);
                    JSONArray array = root.getJSONArray("表");
                    Log.e("zjy", "ViewPicByPidActivity.java->run():search pic count=" + array.length());
                    FTPClient client = new FTPClient();
                    List<FTPImgInfo> list = new ArrayList<>();
                    int searchSize = array.length();
                    FTPUtils mFtpClient = null;
                    String tempUrl = "";
                    downloadResult = "";
                    for (int i = 0; i < searchSize; i++) {
                        JSONObject tObj = array.getJSONObject(i);
                        //                        String imgName = tObj.getString("pictureName");
                        //                        String imgUrl = tObj.getString("pictureURL");
                        String imgName = tObj.getString("PictureName");
                        String imgUrl = tObj.getString("PictureURL");
                        String urlNoShema = imgUrl.substring(6);
                        String remoteAbsolutePath = urlNoShema.substring(urlNoShema.indexOf("/"));
                        try {
                            remoteAbsolutePath = new String(remoteAbsolutePath.getBytes("utf-8"), "iso-8859-1");
                            String imgFtp = urlNoShema.substring(0, urlNoShema.indexOf("/"));
                            int index = imgFtp.indexOf(":");
                            String finalHost = imgFtp;
                            int port = 21;
                            if (index != -1) {
                                String tp = imgFtp.substring(index + 1);
                                finalHost = imgFtp.substring(0, index);
                                try {
                                    port = Integer.parseInt(tp);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                            FTPImgInfo fti = new FTPImgInfo();
                            File fileParent = MyFileUtils.getFileParent();
                            File file = new File(fileParent, "dyj_img/" + imgName);
                            //图片未下载的需要下载
                            if (!file.exists()) {
                                if (!tempUrl.equals(imgFtp)) {
                                    if (finalHost.equals(FtpManager.DB_ADDRESS)) {
                                        mFtpClient = FTPUtils.getGlobalFTP();
                                    } else {
                                        mFtpClient = FTPUtils.getLocalFTP(finalHost);
                                    }
                                } else {
                                    if (finalHost.equals(FtpManager.DB_ADDRESS)) {
                                        mFtpClient = FTPUtils.getGlobalFTP();
                                    } else {
                                        mFtpClient = FTPUtils.getLocalFTP(finalHost);
                                    }
                                }
                                Log.e("zjy", "ViewPicByPidActivity->run(): fileName==" + imgUrl);
                                if (!mFtpClient.serverIsOpen()) {
                                    mFtpClient.login();
                                }
                                boolean exitsFIle = mFtpClient.fileExists(remoteAbsolutePath);
                                Log.e("zjy", "ViewPicByPidActivity->run(): file exist==" + exitsFIle);
                                if (exitsFIle) {
                                    mFtpClient.download(file.getAbsolutePath(), remoteAbsolutePath);
                                    downCounts++;
                                    fti.setImgPath(file.getAbsolutePath());
                                    list.add(fti);
                                    downloadResult += "第" + (i + 1) + "张,下载成功\r\n";
                                    mHandler.obtainMessage(4, searchSize, i).sendToTarget();
                                } else {
                                    throw new IOException("ftp文件不存在");
                                }

                            } else {
                                downloadResult += "第" + (i + 1) + "张,已从手机找到\r\n";
                                fti.setImgPath(file.getAbsolutePath());
                                list.add(fti);
                            }
                            tempUrl = imgFtp;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                            downloadResult += "第" + (i + 1) + "张,下载失败，原因：网络连接错误\r\n";
                        }
                    }
                    imgsData.addAll(list);
                    Message msg = mHandler.obtainMessage(0, downloadResult);
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    mHandler.sendEmptyMessage(1);
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public void showProgressDialog() {
        pd.setMessage("正在查询中");
        if (pd != null && !pd.isShowing()) {
            pd.show();
        }
    }


    public static String getYHPIC(String typeAndPid) throws IOException, XmlPullParserException {
        return MartService.GetYHPicInfo(typeAndPid);
    }
}
