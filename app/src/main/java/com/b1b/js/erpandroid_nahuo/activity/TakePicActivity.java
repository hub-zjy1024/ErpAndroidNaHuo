package com.b1b.js.erpandroid_nahuo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.application.MyApp;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import utils.DialogUtils;
import utils.FTPUtils;
import utils.FtpManager;
import utils.ImageWaterUtils;
import utils.MyImageUtls;
import utils.MyToast;
import utils.UploadUtils;
import utils.WebserviceUtils;
import utils.camera.AutoFoucusMgr;

public class TakePicActivity extends AppCompatActivity implements View.OnClickListener {

    private int rotation = 0;
    private SurfaceView surfaceView;
    private Button btn_tryagain;
    private Button btn_setting;
    private Button btn_commit;
    private Button btn_takepic;
    private SurfaceHolder mHolder;
    private LinearLayout toolbar;
    private Camera.Parameters parameters;
    private Camera camera;
    private boolean isPreview = false;
    private Bitmap photo;
    private List<Camera.Size> picSizes;
    AutoFoucusMgr auto;
    private ProgressDialog pd;
    private String pid;
    private int commitTimes = 0;
    private MaterialDialog resultDialog;
    private final static int FTP_CONNECT_FAIL = 3;
    private final static int PICUPLOAD_SUCCESS = 0;
    private final static int PICUPLOAD_ERROR = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PICUPLOAD_ERROR:
                    showFinalDialog("上传图片失败，请检查网络并重新拍摄");
                    btn_takepic.setEnabled(true);
                    toolbar.setVisibility(View.GONE);
                    break;
                case PICUPLOAD_SUCCESS:
                    if (msg.obj.toString().equals("操作成功")) {
                        showFinalDialog("上传成功");
                    } else {
                        showFinalDialog("插入图片信息失败，请重新上传");
                    }
                    btn_takepic.setEnabled(true);
                    toolbar.setVisibility(View.GONE);
                    break;
                case FTP_CONNECT_FAIL:
                    MyToast.showToast(TakePicActivity.this, "连接ftp服务器失败，请检查网络");
                    break;
                case 4:
                    MyToast.showToast(TakePicActivity.this, "FTP地址为空，请重启程序");
                    break;
            }
        }
    };

    private OrientationEventListener mOrientationListener;
    private SharedPreferences sp;
    private int itemPosition;
    private AlertDialog inputDialog;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepic_main);
        final FrameLayout container = (FrameLayout) findViewById(R.id.take_pic2_container);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        btn_tryagain = (Button) findViewById(R.id.main_tryagain);
        btn_commit = (Button) findViewById(R.id.main_commit);
        btn_takepic = (Button) findViewById(R.id.btn_takepic);
        btn_setting = (Button) findViewById(R.id.takepic_btn_setting);
        toolbar = (LinearLayout) findViewById(R.id.main_toolbar);
        btn_setting.setOnClickListener(this);
        btn_takepic.setOnClickListener(this);
        btn_tryagain.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(TakePicActivity.this);
        builder.setTitle("请输入单据号");
        View v = LayoutInflater.from(TakePicActivity.this).inflate(R.layout.dialog_inputpid, null);
        final EditText dialogPid = (EditText) v.findViewById(R.id.dialog_inputpid_ed);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pid = dialogPid.getText().toString();
                checkPid(TakePicActivity.this, pid, 3);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(v);
        inputDialog = builder.create();
        pid = getIntent().getStringExtra("pid");
        flag = getIntent().getStringExtra("flag");
        if (pid != null) {
            dialogPid.setText(pid);
        }
        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                rotation = getProperRotation(orientation);
            }
        };
        //成功或失败的提示框
        resultDialog = new MaterialDialog(TakePicActivity.this);
        resultDialog.setTitle("提示");
        resultDialog.setPositiveButton("返回", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultDialog.dismiss();
                finish();
            }
        });
        resultDialog.setCanceledOnTouchOutside(true);
        attachToSensor(mOrientationListener);
        //获取surfaceholder
        mHolder = surfaceView.getHolder();
        mHolder.setKeepScreenOn(true);
        //添加SurfaceHolder回调
        if (mHolder != null) {
            mHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    int counts = Camera.getNumberOfCameras();
                    if (counts == 0) {
                        MyToast.showToast(TakePicActivity.this, "设备无摄像头");
                        return;
                    }
                    camera = Camera.open(0); // 打开摄像头
                    if (camera == null) {
                        MyToast.showToast(TakePicActivity.this, "检测不到摄像头");
                        return;
                    }
                    //设置旋转角度
                    camera.setDisplayOrientation(getPreviewDegree(TakePicActivity.this));
                    //设置parameter注意要检查相机是否支持，通过parameters.getSupportXXX()
                    parameters = camera.getParameters();
                    sp = getSharedPreferences("cameraInfo", 0);
                    try {
                        // 设置用于显示拍照影像的SurfaceHolder对象
                        camera.setPreviewDisplay(holder);
                        Camera.Size previewSize = parameters.getPreviewSize();
                        int width1 = previewSize.width;
                        int height1 = previewSize.height;
                        int sw = getWindowManager().getDefaultDisplay().getWidth();
                        int sh = getWindowManager().getDefaultDisplay().getHeight();
                        MyApp.myLogger.writeInfo("camera screen:" + sw + "\t" + sh);
                        MyApp.myLogger.writeInfo("camera def:" + width1 + "\t" + height1);
                        Log.e("zjy", "TakePicActivity->surfaceCreated(): camera.preview==" + camera.getParameters()
                                .getPreviewSize().width + "\t" + camera.getParameters().getPreviewSize().height
                        );
                        Point finalSize = getSuitablePreviewSize(parameters, sw, sh);
                        if (finalSize != null) {
                            parameters.setPreviewSize(finalSize.x, finalSize.y);
                        }
                        //初始化操作在开始预览之前完成
                        if (sp.getInt("width", -1) != -1) {
                            int width = sp.getInt("width", -1);
                            int height = sp.getInt("height", -1);
                            Log.e("zjy", "TakePicActivity.java->surfaceCreated(): ==readCacheSize width" + width + "\t" + height);
                            parameters.setPictureSize(width, height);
                            camera.setParameters(parameters);
                        } else {
                            showSizeChoiceDialog(parameters);
                        }
                        camera.startPreview();
                        isPreview = true;
                        //                        String brand = Build.BRAND;
                        //                        if (brand != null) {
                        //                            if (brand.toUpperCase().equals("HONOR")) {
                        //                                container.setOnClickListener(new View.OnClickListener() {
                        //                                    @Override
                        //                                    public void onClick(View v) {
                        //                                        camera.autoFocus(null);
                        //                                    }
                        //                                });
                        //                            } else {
                        //                                setAutoFoucs(parameters);
                        //                            }
                        //                        }
                        container.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (camera != null && isPreview) {
                                    camera.autoFocus(null);
                                }
                            }
                        });
                        auto = new AutoFoucusMgr(camera);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    if (pid == null) {
                        inputDialog.show();
                    }
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    auto.stop();
                    releaseCamera();
                }
            });
        }
    }


    /**
     默认使用最大的预览尺寸，以便于获取最清晰的预览画面(测试发现有些不兼容)
     @param parameters
     @deprecated
     */
    public static Point getSuitablePreviewSize(Camera.Parameters parameters, int screenW, int screenH) {
        Camera.Size defSize = parameters.getPreviewSize();
        if (defSize.width == screenH && defSize.height == screenW) {
            return null;
        }
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int px1 = lhs.width * lhs.height;
                int px2 = rhs.width * rhs.height;
                if (px1 > px2) {
                    return -1;
                } else if (px1 == px2) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        int tWidth = 0;
        int tHeight = 0;
        for (int i = 0; i < supportedPreviewSizes.size(); i++) {
            Camera.Size tSize = supportedPreviewSizes.get(i);
            if (screenH == tSize.width && tSize.height == screenW) {
                tWidth = tSize.width;
                tHeight = tSize.height;
                return new Point(tWidth, tHeight);
            } else if (screenH > tSize.width) {
                float rate = tSize.width / (float) tSize.height;
                float screenRate = screenH / (float) screenW;
                float res = Math.abs(rate - screenRate);
                if (res < 0.23) {
                    tWidth = tSize.width;
                    tHeight = tSize.height;
                    break;
                }
            }
        }
        if (tWidth == 0 && tHeight == 0) {
            return null;
        }
        return new Point(tWidth, tHeight);
    }

    /**
     如果相机支持设置自动聚焦
     @param parameters
     */
    private void setAutoFoucs(Camera.Parameters parameters) {
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        for (int i = 0; i < supportedFocusModes.size(); i++) {
            if (supportedFocusModes.get(i).equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                //如果支持自动聚焦，必须设定回调
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            //聚焦成功记得取消，不然不会自动聚焦了
                            camera.cancelAutoFocus();
                        }
                    }
                });
                break;
            }
        }
    }

    /**
     弹出尺寸选择对话框
     防止照出的图片太大，内存溢出
     */
    private void showSizeChoiceDialog(final Camera.Parameters parameters) {

        picSizes = parameters.getSupportedPictureSizes();
        //剔除出尺寸太小的，和尺寸太大的，宽度（1280-2048)
        for (int i = picSizes.size() - 1; i >= 0; i--) {
            int width = picSizes.get(i).width;
            Log.e("zjy", "TakePicActivity.java->showProgressDialog(): size==" + picSizes.get(i).width + "\t" + picSizes.get(i)
                    .height);
            if (width < 1920 || width > 2592) {
                picSizes.remove(i);
            }
        }
        if (picSizes.size() > 0) {
            String[] strs = new String[picSizes.size()];
            for (int i = 0; i < picSizes.size(); i++) {
                Camera.Size size = picSizes.get(i);
                String item = size.width + "X" + size.height;
                strs[i] = item;
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(TakePicActivity.this);
            dialog.setTitle("选择照片大小(尽量选择大的值)");//窗口名
            dialog.setSingleChoiceItems(strs, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            itemPosition = which;
                        }
                    }
            );
            dialog.setNegativeButton("完成", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int width = picSizes.get(itemPosition).width;
                    int height = picSizes.get(itemPosition).height;
                    Log.e("zjy", "TakePicActivity.java->selectSize: width==" + width + "\t" + height);
                    parameters.setPictureSize(width, height);
                    camera.setParameters(parameters);
                }
            });

            dialog.setPositiveButton("设为默认尺寸", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.e("zjy", "TakePicActivity.java->onClick(): default size pos==" + itemPosition);
                    SharedPreferences.Editor editor = sp.edit();
                    int width = picSizes.get(itemPosition).width;
                    int height = picSizes.get(itemPosition).height;
                    editor.putInt("width", width);
                    editor.putInt("height", height);
                    editor.apply();
                    parameters.setPictureSize(width, height);
                    camera.setParameters(parameters);
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } else {
            MyToast.showToast(TakePicActivity.this, "没有可选的尺寸");
            return;
        }
    }

    /**
     添加屏幕旋转监听
     @param mOrientationListener
     */
    private void attachToSensor(OrientationEventListener mOrientationListener) {
        if (mOrientationListener != null) {
            if (mOrientationListener.canDetectOrientation()) {
                mOrientationListener.enable();
            } else {
                MyApp.myLogger.writeError("获取相机方向失败,Detect fail");
                mOrientationListener.disable();
                rotation = 0;
            }
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    public static boolean checkPid(Context mContext, String pid) {
        if ("".equals(pid) || pid == null) {
            MyToast.showToast(mContext, "请输入单据号");
            return true;
        } else {
            if (pid.length() < 7) {
                MyToast.showToast(mContext, "请输入7位单据号");
                return true;
            }
        }
        return false;
    }

    public static boolean checkPid(Context mContext, String pid, int len) {

        if ("".equals(pid) || pid == null) {
            MyToast.showToast(mContext, "请输入单据号");
            return true;
        } else {
            if (pid.length() < len) {
                MyToast.showToast(mContext, "请输入" + len + "位单据号");
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //拍照
            case R.id.btn_takepic:
                //禁止点击拍照按钮
                if (checkPid(TakePicActivity.this, pid, 3))
                    return;
                btn_takepic.setEnabled(false);
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        isPreview = false;
                        try {
                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Matrix matrixs = new Matrix();
                            matrixs.setRotate(90 + rotation);
                            photo = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrixs, true);
                            //显示工具栏
                            toolbar.setVisibility(View.VISIBLE);
                        } catch (OutOfMemoryError error) {
                            MyApp.myLogger.writeError("pic too large");
                            error.printStackTrace();
                            MyToast.showToast(TakePicActivity.this, "当前尺寸太大，请选择合适的尺寸");
                            if (photo != null && !photo.isRecycled()) {
                                photo.recycle();
                            }
                            camera.startPreview();
                            isPreview = true;
                            showSizeChoiceDialog(parameters);
                            toolbar.setVisibility(View.GONE);

                        }
                    }
                });
                break;
            //重新拍摄
            case R.id.main_tryagain:
                if (photo != null) {
                    photo.recycle();
                    photo = null;
                }
                camera.startPreview();
                btn_takepic.setEnabled(true);
                toolbar.setVisibility(View.GONE);
                break;
            //提交
            case R.id.main_commit:
                commitTimes++;
                if (photo == null) {
                    MyToast.showToast(TakePicActivity.this, "请稍等，等图像稳定再上传");
                    return;
                }
                showProgressDialog();
                //载入水印图
                final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.waterpic);
                if (!photo.isRecycled()) {
                    new Thread() {
                        @Override
                        public void run() {
                            String remoteName = "";
                            try {
                                //加水印后的图片
                                Bitmap waterBitmap = ImageWaterUtils.createWaterMaskRightBottom(TakePicActivity.this, photo,
                                        bitmap, 0, 0);
                                Bitmap TextBitmap = ImageWaterUtils.drawTextToRightTop(TakePicActivity.this, waterBitmap, pid,
                                        (int) (photo.getWidth() * 0.015), Color.RED, 20, 20);
                                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                                //图片质量压缩到bao数组
                                MyImageUtls.compressBitmapAtsize(TextBitmap, bao, 0.4f);
                                final ByteArrayInputStream in = new ByteArrayInputStream(bao.toByteArray());
                                String insertPath;
                                //上传
                                boolean isConn = false;
                                FTPUtils ftpUtil;
                                if (flag != null && flag.equals("caigou")) {
                                    remoteName = UploadUtils.createSCCGRemoteName(pid);
                                    ftpUtil = new FTPUtils(FtpManager.mainAddress, 21, FtpManager
                                            .mainName,
                                            FtpManager.mainPwd);
                                    ftpUtil.login();
                                    String remotePath = UploadUtils.getCaigouRemoteDir(remoteName + ".jpg");
                                    if ("101".equals(MyApp.id)) {
                                        remotePath = UploadUtils.CG_DIR + remoteName + ".jpg";
                                    }
                                    isConn = ftpUtil.upload(in, remotePath);
                                    insertPath = UploadUtils.createInsertPath(FtpManager.DB_ADDRESS
                                            , remotePath);
                                } else {
                                    remoteName = UploadUtils.getChukuRemoteName(pid);
                                    String remotePath;
                                    String mUrl;
                                    if ("101".equals(MyApp.id)) {
                                        mUrl = FtpManager.mainAddress;
                                        ftpUtil = new FTPUtils(FtpManager.mainAddress, 21, FtpManager.mainName, FtpManager
                                                .mainPwd);
                                        remotePath = UploadUtils.KF_DIR + remoteName + ".jpg";
                                    } else {
                                        mUrl = MyApp.ftpUrl;
                                        ftpUtil = new FTPUtils(mUrl, 21, FtpManager.ftpName, FtpManager.ftpPassword);
                                        remotePath = "/" + UploadUtils.getCurrentDate() + "/" + remoteName + ".jpg";
                                    }
                                    ftpUtil.login();
                                    insertPath = UploadUtils.createInsertPath(mUrl, remotePath);
                                    isConn = ftpUtil.upload(in, remotePath);
                                    in.close();
                                }
                                ftpUtil.exitServer();
                                Log.e("zjy", "TakePicActivity.java->run(): insertPath==" + insertPath);
                                if (isConn) {
                                    //更新服务器信息
                                    SharedPreferences sp = getSharedPreferences("UserInfo", 0);
                                    final int cid = sp.getInt("cid", -1);
                                    final int did = sp.getInt("did", -1);
                                    String result = "";
                                    if (flag != null && flag.equals("caigou")) {
                                        result = ObtainPicFromPhone.setSSCGPicInfo(WebserviceUtils.WebServiceCheckWord, cid,
                                                did, Integer
                                                        .parseInt(MyApp.id), pid, remoteName + ".jpg", insertPath, "SCCG");
                                        Log.e("zjy", "TakePicActivity.java->run(): SCCG==" + result);
                                    } else {
                                        result = setInsertPicInfo(WebserviceUtils.WebServiceCheckWord, cid, did, Integer
                                                .parseInt(MyApp.id), pid, remoteName + ".jpg", insertPath, "CKTZ");
                                        Log.e("zjy", "TakePicActivity.java->run(): setInsert==" + result);
                                    }
                                    if (result.equals("操作成功")) {
                                        MyApp.myLogger.writeInfo("takepic success:" + pid + "\t" + remoteName);
                                    }
                                    Message msg = mHandler.obtainMessage(PICUPLOAD_SUCCESS);
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                } else {
                                    mHandler.sendEmptyMessage(PICUPLOAD_ERROR);
                                    MyApp.myLogger.writeError("takepic upload false:" + pid + "\t" + remoteName);
                                }
                            } catch (IOException e) {
                                MyApp.myLogger.writeError("takepic upload Exception:" + pid + "\t" + remoteName + "-" + e
                                        .getMessage());
                                mHandler.sendEmptyMessage(PICUPLOAD_ERROR);
                                e.printStackTrace();
                            } catch (XmlPullParserException e) {
                                mHandler.sendEmptyMessage(PICUPLOAD_ERROR);
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                break;
            //设置照片大小
            case R.id.takepic_btn_setting:
                showSizeChoiceDialog(parameters);
                break;
        }
    }

    private void showProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (photo != null)
                    photo.recycle();
                if (camera != null) {
                    camera.startPreview();
                    isPreview = true;
                }
            }
        });
        pd.setMessage("正在上传");
        pd.setCancelable(false);
        pd.show();
    }

    /**
     获取相机预览的画面旋转角度
     @param activity 当前Activity
     @return
     */
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该旋转的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    /**
     此方法配合OrientationEventListener使用
     @param rot 传感器的角度
     @return 成像图片应该旋转的角度
     */
    public static int getProperRotation(int rot) {
        int degree = 0;
        //根据传感器的方向获取拍照成像的方向
        if (rot > 240 && rot < 300) {
            degree = 270;
        } else if (rot > 60 && rot < 120) {
            degree = 90;
        }
        return degree;
    }
    //name="checkWord" type="string"
    //name="cid" type="int"   分公司id
    //name="did" type="int"    部门id
    //name="uid" type="int"   用户id

    public String setInsertPicInfo(String checkWord, int cid, int did, int uid, String pid, String fileName, String filePath,
                                   String stypeID) throws IOException, XmlPullParserException {
        String str = "";
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("checkWord", checkWord);
        map.put("cid", cid);
        map.put("did", did);
        map.put("uid", uid);
        map.put("pid", pid);
        map.put("filename", fileName);
        map.put("filepath", filePath);
        map.put("stypeID", stypeID);//标记，固定为"CKTZ"
        SoapObject request = WebserviceUtils.getRequest(map, "SetInsertPicInfo");
        SoapPrimitive response = WebserviceUtils.getSoapPrimitiveResponse(request, SoapEnvelope.VER11, WebserviceUtils
                .ChuKuServer);
        str = response.toString();
        return str;
    }

    public String setSSCGPicInfo(String checkWord, int cid, int did, int uid, String pid, String fileName, String filePath,
                                 String stypeID) throws IOException, XmlPullParserException {
        String str = "";
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("checkWord", checkWord);
        map.put("cid", cid);
        map.put("did", did);
        map.put("uid", uid);
        map.put("pid", pid);
        map.put("filename", fileName);
        map.put("filepath", filePath);
        map.put("stypeID", stypeID);//标记，固定为"SCCG"
        SoapObject request = WebserviceUtils.getRequest(map, "InsertSSCGPicInfo");
        SoapPrimitive response = WebserviceUtils.getSoapPrimitiveResponse(request, SoapEnvelope.VER11, WebserviceUtils
                .MartStock);
        str = response.toString();
        return str;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOrientationListener != null) {
            mOrientationListener.disable();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachToSensor(mOrientationListener);
    }

    private void showFinalDialog(String message) {
        DialogUtils.cancelDialog(pd);
        resultDialog.setMessage(message);
        resultDialog.show();
    }
}
