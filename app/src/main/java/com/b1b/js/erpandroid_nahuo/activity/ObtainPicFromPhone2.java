package com.b1b.js.erpandroid_nahuo.activity;

import android.app.NotificationManager;
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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.activity.base.SavedLoginInfoActivity;
import com.b1b.js.erpandroid_nahuo.adapter.UploadPicAdapter;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.entity.UploadPicInfo;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import utils.CheckUtils;
import utils.MyToast;
import utils.image.ImageWaterUtils;
import utils.image.MyImageUtls;
import utils.net.ftp.FTPUtils;
import utils.net.ftp.FtpManager;
import utils.net.ftp.UploadUtils;
import utils.wsdelegate.WebserviceUtils;
import zhy.imageloader.MyAdapter;
import zhy.imageloader.PickPicActivity;

public class ObtainPicFromPhone2 extends SavedLoginInfoActivity implements View.OnClickListener {

    private Button btn_commit;
    private Button btn_commitOrigin;
    private ProgressDialog pd;
    private boolean isFirst;
    private int onclickPosition;
    private GridView gv;
    private int currentIndex = 0;
    private final int PICUPLOAD_SUCCESS = 1;
    private final int PICLISTUPLOAD_SUCCESS = 4;
    private final int PICUPLOAD_ERROR = 2;
    private final int PIC_OOM = 3;
    private final int FTP_ERROR = 8;
    private String pid;
    private static final Object lock = new Object();
    private String failPid;
    private MaterialDialog resultDialog;
    private Context mContext = ObtainPicFromPhone2.this;
    //更新progressDialog
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PICUPLOAD_SUCCESS:
                    showFinalDialog("上传成功");
                    int nfId = getIntent().getIntExtra("nfId", 0);
                    UploadPicInfo upInfo = uploadPicInfos.get(onclickPosition);
                    if (nfId != 0) {
                        String path = upInfo.getPath();
                        String name = path.substring(path.lastIndexOf("/") + 1);
                        NotificationManager nManager = (NotificationManager) getSystemService
                                (NOTIFICATION_SERVICE);
                        //                        NotificationCompat.Builder builder = new
                        // NotificationCompat.Builder
                        // (mContext);
                        //                        Bitmap largeIcon = BitmapFactory.decodeResource
                        // (getResources(), R.mipmap
                        // .notify_icon_large);
                        //                        builder.setContentTitle("上传" + failPid + "图片");
                        //                        builder.setContentText(name + "上传成功").setSmallIcon(R
                        // .mipmap.notify_icon)
                        // .setLargeIcon(largeIcon);
                        nManager.cancel(nfId);
                    }
                    upInfo.setState("1");
                    mGvAdapter.notifyDataSetChanged();
                    break;
                case PICUPLOAD_ERROR:
                    showFinalDialog("上传失败");
                    mGvAdapter.notifyDataSetChanged();
                    break;
                case 6:
                    pd.setMessage("上传了" + (currentIndex + 1) + "/" + uploadPicInfos.size());
                    UploadPicInfo up1 = uploadPicInfos.get(currentIndex);
                    up1.setState("1");
                    mGvAdapter.notifyDataSetChanged();
                    break;
                case PIC_OOM:
                    showFinalDialog("上传失败,图片过大，超出可用内存");
                    mGvAdapter.notifyDataSetChanged();
                    break;
                case PICLISTUPLOAD_SUCCESS:
                    showFinalDialog("批量上传成功");
                    mGvAdapter.notifyDataSetChanged();
                    break;
                case FTP_ERROR:
                    MyToast.showToast(mContext, "连接ftp失败，请检查网络");
                    mGvAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void showFinalDialog(String message) {
        pd.cancel();
        resultDialog.setMessage(message);
        resultDialog.show();
    }

    private EditText edName;
    private List<UploadPicInfo> uploadPicInfos;
    private UploadPicAdapter mGvAdapter;
    private EditText edPid;
    private String failPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_re_view);
        isFirst = true;
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
        failPath = intent.getStringExtra("failPath");
        if (failPath != null) {
            uploadPicInfos.add(new UploadPicInfo("-1", failPath));
        }
        if (uploadPicInfos.size() > 0) {
            btn_commit.setEnabled(true);
        }
        failPid = intent.getStringExtra("failPid");
        if (failPid != null) {
            edPid.setText(failPid);
        }
        if (pid != null) {
            edPid.setText(pid);
        }
        //初始化ftp
        mGvAdapter = new UploadPicAdapter(mContext, uploadPicInfos, new UploadPicAdapter
                .OnItemBtnClickListener() {
            @Override
            public void onClick(View v, int position) {
                final UploadPicInfo uploadPicInfo = uploadPicInfos.get(position);
                pid = edPid.getText().toString().trim();
                if (TakePicActivity.checkPid(mContext, pid, 5))
                    return;
                if (uploadPicInfo.getState().equals("-1")) {
                    Button btn = (Button) v;
                    btn.setText("正在上传");
                    onclickPosition = position;
                    SharedPreferences sp = getSharedPreferences("UserInfo", 0);
                    final int cid = sp.getInt("cid", -1);
                    final int did = sp.getInt("did", -1);
                    showProgressDialog();
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                if (commitImage(uploadPicInfo, cid, did, pid)) {
                                    handler.sendEmptyMessage(PICUPLOAD_SUCCESS);
                                    MyApp.myLogger.writeInfo("obtainpic ok:" + pid);
                                } else {
                                    handler.sendEmptyMessage(PICUPLOAD_ERROR);
                                }
                            } catch (OutOfMemoryError e) {
                                handler.sendEmptyMessage(PIC_OOM);
                                int[] wh = MyImageUtls.getBitmapWH(uploadPicInfo.getPath());
                                MyApp.myLogger.writeError("obtainpic oom:" + wh[0] + "X" + wh[1] +
                                        "-memory:" + Runtime.getRuntime().freeMemory() + "/" + Runtime
                                        .getRuntime().maxMemory());
                                e.printStackTrace();
                            } catch (FileNotFoundException e) {
                                handler.sendEmptyMessage(PICUPLOAD_ERROR);
                                e.printStackTrace();
                            } catch (XmlPullParserException e) {
                                handler.sendEmptyMessage(PICUPLOAD_ERROR);
                                e.printStackTrace();
                            } catch (IOException e) {
                                MyApp.myLogger.writeError("obtainpic IO:" + e.getMessage());
                                handler.sendEmptyMessage(PICUPLOAD_ERROR);
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                } else {
                    MyToast.showToast(mContext, "当前图片已经上传完成");
                }
            }
        });
        gv.setAdapter(mGvAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.review_commit:
                pid = edPid.getText().toString().trim();
                if (TakePicActivity.checkPid(mContext, pid, 5))
                    return;
                showProgressDialog();
                new Thread() {
                    @Override
                    public void run() {
                        if (commitImages(uploadPicInfos)) {
                            handler.sendEmptyMessage(PICLISTUPLOAD_SUCCESS);
                        }
                    }
                }.start();
                break;
            case R.id.review_getFromPhone:
                Intent intent = new Intent(mContext, PickPicActivity.class);
                startActivityForResult(intent, 100);
                break;
        }
    }

    /**
     * @param uploadPicInfos
     * @throws IOException
     * @throws XmlPullParserException
     */
    private boolean commitImages(List<UploadPicInfo> uploadPicInfos) {
        boolean success = false;
        SharedPreferences sp = getSharedPreferences("UserInfo", 0);
        final int cid = sp.getInt("cid", -1);
        final int did = sp.getInt("did", -1);
        for (int i = 0; i < uploadPicInfos.size(); i++) {
            if (uploadPicInfos.get(i).getState().equals("-1")) {
                try {
                    if (commitImage(uploadPicInfos.get(i), cid, did, pid)) {
                        currentIndex = i;
                        handler.sendEmptyMessage(6);
                    } else {
                        handler.sendEmptyMessage(PICUPLOAD_ERROR);
                        return success;
                    }
                } catch (OutOfMemoryError e) {
                    handler.sendEmptyMessage(PIC_OOM);
                    int[] wh = MyImageUtls.getBitmapWH(uploadPicInfos.get(i).getPath());
                    MyApp.myLogger.writeError("obtainpic oom:" + wh[0] + "X" + wh[1] + "-memory:" + Runtime
                            .getRuntime().freeMemory() + "/" + Runtime.getRuntime().maxMemory());
                    e.printStackTrace();
                    return success;
                } catch (IOException e) {
                    handler.sendEmptyMessage(PICUPLOAD_ERROR);
                    e.printStackTrace();
                    MyApp.myLogger.writeError("obtainpic IO:" + e.getMessage());
                    return success;
                } catch (XmlPullParserException e) {
                    handler.sendEmptyMessage(PICUPLOAD_ERROR);
                    e.printStackTrace();
                    return success;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        success = true;
        return success;
    }

    private boolean commitImage(UploadPicInfo uploadPicInfo, int cid, int did, String pid) throws Exception {
        InputStream inputStream = new FileInputStream(uploadPicInfo.getPath());
        boolean flag = false;
        String fileName = UploadUtils.getChukuRemoteName(pid);
        if (failPid != null) {
            //重新上传失败的文件
            fileName = failPath.substring(failPath.lastIndexOf("/") + 1, failPath.lastIndexOf("."));
            fileName = getRemarkName(fileName, false);
        } else {
            fileName = getRemarkName(fileName, true);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap waterBitmap = null;
            if (bitmap != null) {
                if (bitmap.getWidth() > 1080 && bitmap.getHeight() > 1080) {
                    waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.waterpic);
                } else {
                    waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water_small);
                }
                Bitmap textBitmap = ImageWaterUtils.drawTextToRightTopByDef(mContext, bitmap,
                        pid, (int) (bitmap.getWidth() * 0.015));
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                Bitmap compressImage = ImageWaterUtils.createWaterMaskRightBottom(mContext,
                        textBitmap,
                        waterBitmap, 0, 0);
                if (!waterBitmap.isRecycled()) {
                    waterBitmap.recycle();
                }
                if (!textBitmap.isRecycled()) {
                    textBitmap.recycle();
                }
                ByteArrayInputStream bai = new ByteArrayInputStream(MyImageUtls.compressBitmapAtsize
                        (compressImage, 0.4f));
                if (compressImage != null && !compressImage.isRecycled()) {
                    compressImage.recycle();
                }
                String intentFlag = getIntent().getStringExtra("flag");
                if (intentFlag != null && intentFlag.equals("caigou")) {
                    boolean isSuccess;
                    //文件名或者目录中有中文需要转码 new String(fileName.getBytes("UTF-8"), "iso-8859-1")
                    String insertPath;
                    String remoteName = UploadUtils.createSCCGRemoteName(pid);
                    String ftpAddress = FtpManager.mainAddress;
                    FTPUtils FTPUtils = new FTPUtils(ftpAddress, FtpManager
                            .mainName, FtpManager.mainPwd);
                    FTPUtils.login();
                    remoteName = getRemarkName(remoteName, false);
                    String remotePath = "";
                    if (CheckUtils.isAdmin()) {
                        remotePath = UploadUtils.CG_DIR + remoteName + ".jpg";
                    } else {
                        remotePath = UploadUtils.getCaigouRemoteDir(remoteName + ".jpg");
                    }
                    Log.e("zjy", "ObtainPicFromPhone2->commitImage(): remote==" + remotePath);
                    isSuccess = FTPUtils.upload(bai, new String(remotePath.getBytes("UTF-8"), "iso-8859-1"));
                    bai.close();
                    FTPUtils.exitServer();
                    insertPath = UploadUtils.createInsertPath(FtpManager.DB_ADDRESS, remotePath);
                    Log.e("zjy", "ObtainPicFromPhone2->commitImage(): SCCGPATH==" + insertPath);
                    if (isSuccess) {
                        String result = setSSCGPicInfo(WebserviceUtils.WebServiceCheckWord, cid, did, Integer
                                .parseInt(MyApp.id), pid, remoteName + ".jpg", insertPath, "SCCG");
                        Log.e("zjy", "ObtainPicFromPhone2.java->run(): SCCG==" + result);
                        if (result.equals("操作成功")) {
                            flag = true;
                        }
                    }
                }
            }
        }
        return flag;
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

    //    billID 单据号：billType 单据类型（市场采购）

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
        return WebserviceUtils.getWcfResult(map,"InsertPicYHInfo",
                WebserviceUtils.MartService);
    }


    @NonNull
    private String getRemarkName(String fileName, boolean hasSuffix) {
        String name = fileName;
        String remark = edName.getText().toString().trim();
        //从手机取的图片，文件后缀加"_o"
        String suffix = "_o";
        if (hasSuffix) {
            if (!"".equals(remark)) {
                name = fileName + "_" + remark + suffix;
            } else {
                name = fileName + suffix;
            }
        } else {
            if (!"".equals(remark)) {
                name = fileName + "_" + remark;
            }
        }
        return name;
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
        if (requestCode == 100 & resultCode == RESULT_OK) {
            ArrayList<String> returnPaths = data.getStringArrayListExtra("imgPaths");
            if (returnPaths.size() > 0) {
                btn_commit.setEnabled(true);
            }
            uploadPicInfos.clear();
            Log.e("zjy", "ObtainPicFromPhone2.java->onActivityResult(): imgPaths==" + returnPaths.size());
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
