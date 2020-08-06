package com.b1b.js.erpandroid_nahuo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.contract.YanhuoPicMgr;
import com.b1b.js.erpandroid_nahuo.uploader.TomcatTransferUploader;
import com.b1b.js.erpandroid_nahuo.utils.ReuseFtpRunnable;
import com.b1b.js.erpandroid_nahuo.utils.TaskManager;

import java.io.IOException;
import java.io.InputStream;

import utils.CheckUtils;
import utils.net.ftp.UploadUtils;

/**
 * Created by 张建宇 on 2020/8/6.
 */

public class YhTakepicActivity extends TakePicActivity {
    protected String sig ="bug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sig = UploadUtils.getDeviceID(mContext);
    }

    public void upLoadPic(final int cRotate, final byte[] picData) {
        if (waterBitmap == null||waterBitmap.isRecycled()) {
            waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.waterpic);
        }
        final Bitmap bitmap = waterBitmap;
        //以上为限定条件
        showProgressDialog();
        String remotePath, insertPath;
        remotePath = getUploadRemotePath();
        insertPath = getInsertPath(remotePath);


        TomcatTransferUploader mUploader = new TomcatTransferUploader(sig);

        Log.e("zjy", "YhTakepicActivity->upLoadPic():TomcatTransferUploader ==");

        Runnable runable = new ReuseFtpRunnable(remotePath, insertPath, ftpUtil) {

            @Override
            public void run() {
                Message msg = picHandler.obtainMessage(PICUPLOAD_ERROR);

                try {
                  InputStream in=  getTransformedImg(cRotate, picData, bitmap);
                    String insertType = YanhuoPicMgr.insertType;
                    mUploader.upload(pid,in,remotePath,loginID,""+cid,""+did,getRemoteName(),insertType,insertPath);
                    msg.what = PICUPLOAD_SUCCESS;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.obj = e.getMessage();
                }
                msg.sendToTarget();

            }

            @Override
            public void onResult(int code, String err) {
                Message msg = picHandler.obtainMessage(PICUPLOAD_ERROR);
                if (code == SUCCESS) {
                    msg.what = PICUPLOAD_SUCCESS;
                } else {
                    msg.obj = err;
                }
                msg.sendToTarget();
            }

            @Override
            public InputStream getInputStream() throws Exception {
                return getTransformedImg(cRotate, picData, bitmap);
            }

            @Override
            public boolean getInsertResult() throws Exception {
                String remoteName = getRemoteName();
                String insertPath = getInsertpath();
                if (CheckUtils.isAdmin()) {
                    return true;
                }
                return getInsertResultMain(remoteName, insertPath);
            }
        };
        TaskManager.getInstance().execute(runable);
    }
}
