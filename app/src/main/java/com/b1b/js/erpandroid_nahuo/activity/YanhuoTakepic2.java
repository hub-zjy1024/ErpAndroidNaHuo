package com.b1b.js.erpandroid_nahuo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.handler.NoLeakHandler;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import utils.CheckUtils;
import utils.net.ftp.FTPUtils;
import utils.net.ftp.FtpManager;
import utils.MyToast;
import utils.net.ftp.UploadUtils;
import utils.wsdelegate.WebserviceUtils;

/**
 Created by 张建宇 on 2018/9/5. */
public class YanhuoTakepic2 extends TakePicActivity {
    private NoLeakHandler handler2 = new NoLeakHandler(this);
    private ViewGroup llResult;
    private final static int FTP_CONNECT_FAIL = 3;
    private final static int ERROR_NO_SD = 4;
    private static final int PICCREATE_ERROR = 5;
    private Executor cachePool = Executors.newCachedThreadPool();

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case PICUPLOAD_ERROR:
                final TextView tvError = (TextView) msg.obj;
                String lastTag = tvError.getTag().toString();
                int failCounts = msg.arg1;
                if (failCounts == 5) {
                    tvError.setText("图片:" + lastTag + "上传失败，请检查网络");
                } else {
                    tvError.setText("图片:" + lastTag + "上传失败，重试" + failCounts);
                }
                break;
            case PICUPLOAD_SUCCESS:
                Object obj = msg.obj;
                final TextView textView = (TextView) obj;
                String nowTag = textView.getTag().toString();
                textView.setText("图片:" + nowTag + "上传完成 OK···");
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llResult.removeView(textView);
                    }
                }, 2000);
                MyToast.showToast(mContext, "后台剩余图片：" + MyApp.totoalTask.size());
                break;
            case FTP_CONNECT_FAIL:
                MyToast.showToast(mContext, "连接ftp服务器失败，请检查网络");
                break;
            case ERROR_NO_SD:
                MyToast.showToast(mContext, "sd卡不存在，不可用后台上传");
                btn_commit.setEnabled(false);
                break;
            case PICCREATE_ERROR:
                final TextView tv3 = (TextView) msg.obj;
                String tv3Tag = tv3.getTag().toString();
                tv3.setText("图片:" + tv3Tag + "生成失败，请重新进入拍照界面");
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        llResult = (LinearLayout) findViewById(R.id.take_pic2_result_containner);
    }

    @Override
    public void upLoadPic(int cRotate, byte[] picData) {
//        super.upLoadPic(cRotate, picData);
        if (waterBitmap == null || waterBitmap.isRecycled()) {
            waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.waterpic);
        }
        final Bitmap bitmap = waterBitmap;
        if (!"caigou".equals(flag)) {
            if (kfFTP == null || "".equals(kfFTP)) {
                if (!CheckUtils.isAdmin()) {
                    MyToast.showToast(mContext, "读取上传地址失败，请重启程序");
                    return;
                }
            }
        }
        //添加提示
        Date date = new Date();
        int minute = date.getMinutes();
        int ss = date.getSeconds();
        String upTime = minute + ":" + ss;
        final TextView textView = new TextView(mContext);
        textView.setBackgroundColor(getResources().getColor(R.color.color_tv_result_transparent));
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
        float fontSize = 18;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        textView.setTag(upTime);
        textView.setText("图片:" + upTime + "正在上传");
        llResult.addView(textView);
        Runnable uploadRun = new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream transformedImg = getTransformedImg(cRotate, picData, waterBitmap);
                    boolean isInsertOk = false;
                    int errorCounts = 0;
                    while (true) {
                        FTPUtils ftpUtil;
                        mUrl = FTPUtils.DB_HOST;
                        ftpUtil = FTPUtils.getGlobalFTP();
                        if (CheckUtils.isAdmin()) {
                            ftpUtil = FtpManager.getTestFTP();
                            mUrl = FtpManager.mainAddress;
                        }
                        String remotePath = UploadUtils.getCaigouRemotePath(pid);
                        try {
                            //上传
                            ftpUtil.login();
                            boolean uploadOk = ftpUtil.upload(transformedImg, remotePath);
                            if (uploadOk) {
                                transformedImg.close();
                                String dbPath = UploadUtils.createInsertPath(mUrl, remotePath);
                                String remoteName = remotePath.substring(remotePath.lastIndexOf("/") + 1);
                                Log.e("zjy", "YanhuoTakepic2->run(): inertPath==" + dbPath);
                                try {
                                    String result = ObtainPicFromPhone.setSSCGPicInfo(WebserviceUtils.WebServiceCheckWord, cid,
                                            did, Integer.parseInt(loginID), pid, remoteName, dbPath, "SCCG");
                                    if ("操作成功".equals(result)) {
                                        MyApp.totoalTask.remove(this);
                                        MyApp.myLogger.writeInfo(mCla, "upload success" + remoteName);
                                        handler2.obtainMessage
                                                (PICUPLOAD_SUCCESS, textView).sendToTarget();
                                        break;
                                    }
                                } catch (XmlPullParserException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException inner) {
                            String message = inner.getMessage();
                        }
                        errorCounts++;
                        handler2.obtainMessage
                                (PICUPLOAD_ERROR, errorCounts, 0, textView).sendToTarget();
                        if (errorCounts == 5) {
                            MyApp.totoalTask.remove(this);
                            break;
                        }
                        try {
                            Thread.sleep(2 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    handler2.obtainMessage
                            (PICCREATE_ERROR, textView).sendToTarget();
                    e.printStackTrace();
                }
            }
        };
        MyApp.totoalTask.add(uploadRun);
        cachePool.execute(uploadRun);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_commit:
                btn_takepic.setEnabled(true);
                toolbar.setVisibility(View.GONE);
                mCamera.startPreview();
                final byte[] picData = Arrays.copyOf(tempBytes, tempBytes.length);
                final int cRotate = tempRotate;
                upLoadPic(cRotate, picData);
                break;
            default:
                super.onClick(v);
                break;
        }


    }

    @Override
    public void getUrlAndFtp() {
        mUrl = FTPUtils.DB_HOST;
        ftpUtil = FTPUtils.getGlobalFTP();
        if (CheckUtils.isAdmin()) {
            ftpUtil = FtpManager.getTestFTP();
            mUrl = FtpManager.mainAddress;
        }
    }

    @Override
    public String getUploadRemotePath() {
        String remoteName = UploadUtils.createSCCGRemoteName(pid);
        return CheckUtils.isAdmin() ? UploadUtils.getCaigouRemotePath(pid) : UploadUtils.getCaigouRemoteDir(remoteName + ".jpg");
    }
}
