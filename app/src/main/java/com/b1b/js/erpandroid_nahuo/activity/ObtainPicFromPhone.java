package com.b1b.js.erpandroid_nahuo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.activity.base.SavedLoginInfoActivity;
import com.b1b.js.erpandroid_nahuo.adapter.UploadPicAdapter;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.entity.UploadPicInfo;
import com.b1b.js.erpandroid_nahuo.handler.NoLeakHandler;
import com.b1b.js.erpandroid_nahuo.utils.TaskManager;
import com.b1b.js.erpandroid_nahuo.utils.UpLoadPicRunable;
import com.b1b.js.erpandroid_nahuo.utils.UploadPicRunnable2;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import utils.CheckUtils;
import utils.net.ftp.FTPUtils;
import utils.net.ftp.FtpManager;
import utils.image.ImageWaterUtils;
import utils.image.MyImageUtls;
import utils.MyToast;
import utils.net.ftp.UploadUtils;
import utils.wsdelegate.WebserviceUtils;
import utils.wsdelegate.ChuKuServer;
import zhy.imageloader.MyAdapter;
import zhy.imageloader.PickPicActivity;

public class ObtainPicFromPhone extends SavedLoginInfoActivity implements NoLeakHandler.NoLeakCallback, View.OnClickListener {

    protected Button btn_commit;
    private Button btn_commitOrigin;
    protected ProgressDialog pd;
    protected GridView gv;
    private final int PICUPLOAD_SUCCESS = 1;
    private final int PICUPLOAD_ERROR = 2;
    private final int REQ_SELECT_PIC = 100;
    protected String pid;
    private MaterialDialog resultDialog;
    protected Context mContext = ObtainPicFromPhone.this;
    protected int count = 0;
    protected String uploadResult = "";

    @Override
    public void handleMessage(Message msg) {
        int arg1 = msg.arg1;
        int arg2 = msg.arg2;
        Object obj = msg.obj;
        int picSize = uploadPicInfos.size();
        String err = "上传失败";
        switch (msg.what) {
            case PICUPLOAD_SUCCESS:
                err = "上传成功";
                uploadResult += "图片" + arg1 + ":" + err + "\n";
                solveResult(arg1, arg2, picSize, err);
                UploadPicInfo upInfo = uploadPicInfos.get(arg1);
                upInfo.setState("1");
                mGvAdapter.notifyDataSetChanged();
                break;
            case PICUPLOAD_ERROR:
                if (obj != null) {
                    err = obj.toString();
                }
                err = "上传失败:" + err;
                uploadResult += "图片" + arg1 + ":" + err + "！！！\n";
                solveResult(arg1, arg2, picSize, err);
                break;
        }
    }

    private void solveResult(int arg1, int arg2, int picSize, String err) {
        count++;
        if (arg2 == 1) {
            uploadResult = "图片" + arg1 + ":" + err + "\n";
            showFinalDialog(uploadResult);
        } else {
            pd.setMessage("上传了" + (count) + "/" + uploadPicInfos.size());
            if (count >= picSize) {
                showFinalDialog(uploadResult);
            }
        }
    }

    private Handler nHandler = new NoLeakHandler(this);
    protected int cid;
    protected int did;

    protected void showFinalDialog(String message) {
        pd.cancel();
        resultDialog.setMessage(message);
        resultDialog.show();
    }

    protected EditText edName;
    protected List<UploadPicInfo> uploadPicInfos;
    protected UploadPicAdapter mGvAdapter;
    protected EditText edPid;
    protected String failPath;

    private SharedPreferences userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_view);
        btn_commitOrigin = (Button) findViewById(R.id.review_getFromPhone);
        btn_commit = (Button) findViewById(R.id.review_commit);
        edName = (EditText) findViewById(R.id.review_name);
        edPid = (EditText) findViewById(R.id.review_pid);
        btn_commit.setOnClickListener(this);
        btn_commitOrigin.setOnClickListener(this);
        gv = (GridView) findViewById(R.id.review_gv);
        uploadPicInfos = new ArrayList<>();
        pd = new ProgressDialog(this);
        //初始化结果对话框
        resultDialog = new MaterialDialog(mContext);
        resultDialog.setTitle("提示");
        resultDialog.setPositiveButton("返回", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultDialog.dismiss();
                finish();
            }
        });
        resultDialog.setCanceledOnTouchOutside(true);
        Intent intent = getIntent();
        pid = intent.getStringExtra("pid");
        if (pid != null) {
            edPid.setText(pid);
        }
        userInfo = getSharedPreferences(SettingActivity.PREF_USERINFO, 0);
        SharedPreferences sp = userInfo;
        cid = sp.getInt("cid", -1);
        did = sp.getInt("did", -1);
        mGvAdapter = new UploadPicAdapter(mContext, uploadPicInfos, new UploadPicAdapter.OnItemBtnClickListener() {
            @Override
            public void onClick(View v, final int position) {
                final UploadPicInfo uploadPicInfo = uploadPicInfos.get(position);
                pid = edPid.getText().toString().trim();
                if (checkPid(5)) {
                    return;
                }
                if (!uploadPicInfo.getState().equals("-1")) {
                    MyToast.showToast(mContext, "当前图片已经上传完成");
                    return;
                }
                Button btn = (Button) v;
                btn.setText("正在上传");
                showProgressDialog();
                uploadResult = "";
                nUpload(position, uploadPicInfo, 1);
            }
        });
        gv.setAdapter(mGvAdapter);
    }

    public boolean checkPid(int len) {
        return TakePicActivity.checkPid(mContext, pid, len);
    }

    private void nUpload(final int position, final UploadPicInfo uploadPicInfo, final int arg2) {
        final String intentFlag = getIntent().getStringExtra("flag");
        String insertPath;
        String remoteName;
        String remotePath;
        String mUrl;
        FTPUtils ftpUtil = null;
        UpLoadPicRunable runable = null;
        if (intentFlag != null && intentFlag.equals("caigou")) {
            mUrl = FtpManager.DB_ADDRESS;
            ftpUtil = FTPUtils.getGlobalFTP();
            String tempRemoteName = UploadUtils.createSCCGRemoteName(pid);
            remoteName = getRemarkName(tempRemoteName, true);
            remotePath = UploadUtils.getCaigouRemoteDir(remoteName + ".jpg");
        } else {
            mUrl = MyApp.ftpUrl;
            ftpUtil = new FTPUtils(mUrl, FtpManager.ftpName, FtpManager.ftpPassword);
            String tempRemoteName = UploadUtils.getChukuRemoteName(pid);
            remoteName = getRemarkName(tempRemoteName, true);
            remotePath = UploadUtils.getChukuRemotePath(remoteName, pid);
        }
        if (CheckUtils.isAdmin()) {
            mUrl = FtpManager.DB_ADDRESS;
            String tempRemoteName  = UploadUtils.getChukuRemoteName(pid);
            remoteName = getRemarkName(tempRemoteName, true);
            remotePath = "/Zjy/kf/" + remoteName + ".jpg";
            ftpUtil = FtpManager.getTestFTPMain();
        }
        insertPath = UploadUtils.createInsertPath(mUrl, remotePath);
        runable = new UploadPicRunnable2(remotePath, insertPath, ftpUtil) {

            @Override
            public void onResult(int code, String err) {
                Message msg = nHandler.obtainMessage();
                msg.arg1 = position;
                msg.arg2 = arg2;
                if (code == SUCCESS) {
                    msg.what = PICUPLOAD_SUCCESS;
                } else {
                    MyApp.myLogger.writeError(ObtainPicFromPhone.class, "ftp:" + MyApp.ftpUrl + "\t");
                    msg.what = PICUPLOAD_ERROR;
                    msg.obj = err;
                }
                nHandler.sendMessage(msg);
            }

            @Override
            public boolean getInsertResult() throws Exception {
                String remoteName = getRemoteName();
                String insertPath = getInsertpath();
                Log.e("zjy", "ObtainPicFromPhone->getInsertResult(): insertpath==" + insertPath);
                if (CheckUtils.isAdmin()) {
                    return true;
                }
                String res = "";
                if ("caigou".equals(intentFlag)) {
                    res = setSSCGPicInfo("", cid,
                            did, Integer.parseInt(loginID), pid, remoteName, insertPath, "SCCG");
                } else {
                    res = setInsertPicInfo("", cid,
                            did, Integer.parseInt(loginID), pid, remoteName, insertPath, "CKTZ");
                }
                return res.equals("操作成功");
            }

            @Override
            public InputStream getInputStream() throws Exception {
                String fPath = uploadPicInfo.getPath();
                return getTransferedImg(fPath);
            }
        };
        TaskManager.getInstance().execute(runable);
    }

    public String getFormateName(String tPid) {
        String encoding = "iso-8859-1";
        String remoteName = UploadUtils.getChukuRemoteName(tPid);
        remoteName = getRemarkName(remoteName, true);
        try {
            remoteName = new String(remoteName.getBytes(), encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return remoteName;
    }

    public InputStream getTransferedImg(String filePath) throws IOException {
        String fPath = filePath;
        FileInputStream inputStream = new FileInputStream(fPath);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap waterBitmap = null;
            if (bitmap.getWidth() >= 1080 && bitmap.getHeight() > 1080) {
                waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.waterpic);
            } else {
                waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water_small);
            }
            Bitmap textBitmap = ImageWaterUtils.drawTextToRightTopByDef(mContext, bitmap, pid, (int) (bitmap
                    .getWidth() * 0.015));
            Bitmap compressImage = ImageWaterUtils.createWaterMaskRightBottom(mContext, textBitmap,
                    waterBitmap);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            MyImageUtls.compressBitmapAtsize(compressImage, bao, 0.4f);
            ByteArrayInputStream bai = new ByteArrayInputStream(bao.toByteArray());
            MyImageUtls.releaseBitmap(waterBitmap);
            MyImageUtls.releaseBitmap(bitmap);
            MyImageUtls.releaseBitmap(textBitmap);
            MyImageUtls.releaseBitmap(compressImage);
            return bai;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    public String setInsertPicInfo(String checkWord, int cid, int did, int uid, String pid, String fileName, String filePath,
                                   String stypeID) throws IOException, XmlPullParserException {
        String str = ChuKuServer.SetInsertPicInfo(checkWord, cid, did, uid, pid, fileName, filePath, stypeID);
        return str;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.review_commit:
                pid = edPid.getText().toString().trim();
                if (checkPid(5))
                    return;
                if (uploadPicInfos.size() == 0) {
                    MyToast.showToast(mContext, "请先添加一张图片");
                    return;
                }
                showProgressDialog();
                count = 0;
                uploadResult = "";
                for (int i = 0; i < uploadPicInfos.size(); i++) {
                    UploadPicInfo item = uploadPicInfos.get(i);
                    if (!item.getState().equals("1")) {
                        nUpload(i, item, 2);
                    } else {
                        count++;
                        MyToast.showToast(mContext, "图片" + i + "已上传完成");
                    }
                }
                if (count == uploadPicInfos.size()) {
                    MyToast.showToast(mContext, "所有图片已上传完成");
                    pd.cancel();
                }
                break;
            case R.id.review_getFromPhone:
                Intent intent = new Intent(mContext, PickPicActivity.class);
                startActivityForResult(intent, REQ_SELECT_PIC);
                break;
        }
    }


    public static String insertPICYH(String PictureName, String PictureURL, String MakerID, String CorpID,
                                     String DeptID, String UserID, String pid, String billType)
            throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        //        (string PictureName, string PictureURL, string MakerID,
        //                string CorpID, string DeptID, string UserID,
        //                string billID, string billType)
        map.put("PictureName", PictureName);
        map.put("PictureURL", PictureURL);
        map.put("MakerID", MakerID);
        map.put("CorpID", CorpID);
        map.put("DeptID", DeptID);
        map.put("UserID", UserID);
        map.put("billID", pid);
        map.put("billType", "市场采购单");//标记，固定为"CKTZ"
        return WebserviceUtils.getWcfResult(map, "InsertPicYHInfo", WebserviceUtils.MartService);
    }

    public synchronized static String setSSCGPicInfo(String checkWord, int cid, int did, int uid, String
            pid, String fileName, String filePath, String stypeID) throws IOException,
            XmlPullParserException {
        String str = "";
        str = insertPICYH(fileName, filePath, String.valueOf(uid), String.valueOf(cid), String.valueOf(did),
                String.valueOf(uid), pid, "市场采购单");
        if ("保存成功".equals(str)) {
            return "操作成功";
        }
        return "失败";
    }
    @NonNull
    protected String getRemarkName(String fileName, boolean hasSuffix) {
        String name = fileName;
        String remark = edName.getText().toString().trim();
        //从手机取的图片，文件后缀加"_o"
        String suffix = "_o";
        if (hasSuffix) {
            if (!TextUtils.isEmpty(remark)) {
                name = fileName + "_" + remark + suffix;
            } else {
                name = fileName + suffix;
            }
        } else {
            if (!TextUtils.isEmpty(remark)) {
                name = fileName + "_" + remark;
            }
        }
        String encoding = "iso-8859-1";
        String finalName = name;
        try {
            finalName = new String(name.getBytes(), encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return finalName;
    }

    public void showProgressDialog() {
        pd.setMessage("正在上传");
        if (!pd.isShowing()) {
            pd.show();
        }
        pd.setCancelable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SELECT_PIC & resultCode == RESULT_OK) {
            ArrayList<String> returnPaths = data.getStringArrayListExtra("imgPaths");
            if (returnPaths.size() > 0) {
                btn_commit.setEnabled(true);
            }
            uploadPicInfos.clear();
            for (int i = 0; i < returnPaths.size(); i++) {
                UploadPicInfo info = new UploadPicInfo("-1", returnPaths.get(i));
                uploadPicInfos.add(info);
            }
            mGvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyAdapter.mSelectedImage.clear();
    }
}
