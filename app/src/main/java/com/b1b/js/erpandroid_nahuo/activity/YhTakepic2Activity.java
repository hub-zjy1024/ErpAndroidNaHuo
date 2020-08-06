package com.b1b.js.erpandroid_nahuo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.contract.YanhuoPicMgr;
import com.b1b.js.erpandroid_nahuo.uploader.TomcatTransferUploader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import utils.net.ftp.UploadUtils;

/**
 * Created by 张建宇 on 2020/8/6.
 */

public class YhTakepic2Activity extends YanhuoTakepic2 {
    protected String sig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sig = UploadUtils.getDeviceID(mContext);
    }

    @Override
    public void upLoadPic(int cRotate, byte[] picData) {
        //        super.upLoadPic(cRotate, picData);
        if (waterBitmap == null || waterBitmap.isRecycled()) {
            waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.waterpic);
        }
        final Bitmap bitmap = waterBitmap;
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
                        //                        FTPUtils ftpUtil;
                        //                        mUrl = FTPUtils.DB_HOST;
                        //                        ftpUtil = FTPUtils.getGlobalFTP();
                        //                        if (CheckUtils.isAdmin()) {
                        //                            ftpUtil = FtpManager.getTestFTP();
                        //                            mUrl = FtpManager.mainAddress;
                        //                        }
                        //                        String remotePath = UploadUtils.getCaigouRemotePath(pid);
                        //                        try {
                        //                            //上传
                        //                            ftpUtil.login();
                        //                            boolean uploadOk = ftpUtil.upload(transformedImg,
                        //                            remotePath);
                        //                            if (uploadOk) {
                        //                                transformedImg.close();
                        //                                String dbPath = UploadUtils.createInsertPath
                        //                                (mUrl, remotePath);
                        //                                String remoteName = remotePath.substring
                        //                                (remotePath.lastIndexOf("/") + 1);
                        //                                Log.e("zjy", "YanhuoTakepic2->run(): inertPath=="
                        //                                + dbPath);
                        //                                try {
                        //                                    String result = ObtainPicFromPhone
                        //                                    .setSSCGPicInfo(WebserviceUtils
                        //                                    .WebServiceCheckWord, cid,
                        //                                            did, Integer.parseInt(loginID), pid,
                        //                                            remoteName, dbPath, "SCCG");
                        //                                    if ("操作成功".equals(result)) {
                        //                                        MyApp.totoalTask.remove(this);
                        //                                        MyApp.myLogger.writeInfo(mCla, "upload
                        //                                        success" + remoteName);
                        //                                        handler2.obtainMessage
                        //                                                (PICUPLOAD_SUCCESS, textView)
                        //                                                .sendToTarget();
                        //                                        break;
                        //                                    }
                        //                                } catch (XmlPullParserException e) {
                        //                                    e.printStackTrace();
                        //                                }
                        //                            }
                        //                        } catch (IOException inner) {
                        //                            String message = inner.getMessage();
                        //                        }

                        TomcatTransferUploader mUploader = new TomcatTransferUploader(sig);
                        String remotePath = UploadUtils.getCaigouRemotePath(pid);
                        String remoteName = remotePath.substring(remotePath.lastIndexOf("/") + 1);
                        String dbPath = UploadUtils.createInsertPath(mUrl, remotePath);

                        //
                        try {
                            String insertType = YanhuoPicMgr.insertType;

                            mUploader.upload(pid, transformedImg, remotePath, loginID, cid + "", "" + did,
                                    remoteName, insertType, dbPath);
                            MyApp.totoalTask.remove(this);
                            MyApp.myLogger.writeInfo(mCla, "upload  success" + remoteName);
                            handler2.obtainMessage
                                    (PICUPLOAD_SUCCESS, textView)
                                    .sendToTarget();
                            break;
                        } catch (Exception e) {
                            errorCounts++;
                            handler2.obtainMessage
                                    (PICUPLOAD_ERROR, errorCounts, 0, textView).sendToTarget();
                        }
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

}
