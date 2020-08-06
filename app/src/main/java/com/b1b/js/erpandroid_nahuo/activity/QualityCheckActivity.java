package com.b1b.js.erpandroid_nahuo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.entity.YanhuoInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import utils.DialogUtils;
import utils.MyToast;
import utils.SoftKeyboardUtils;
import utils.net.ftp.UploadUtils;
import utils.wsdelegate.MartService;

/**
 * {@link MenuActivity#tag_QualityCheck}
 *
 */
public class QualityCheckActivity extends BaseScanActivity {

    private EditText edPid;
    private Button btnSearch;
    private Button btnScan;
    private final int reqCode = 500;
    private EditText edInfo;
    private ProgressDialog pd;
    private Context mContext = this;
    private Handler mhandler = new Handler();
    private Button btnCommit;
    private TextView tvDetail;
    private AlertDialog resultDialog;
    private TextView tvInfo;
    private boolean autoCommit = true;
    private CheckBox cboAuto;
    private String defTitle = "质检中心";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_check);
        edPid = (EditText) findViewById(R.id.activity_quality_ed_pid);
        edInfo = (EditText) findViewById(R.id.activity_quality_ed_info);
        cboAuto = (CheckBox) findViewById(R.id.activity_quality_cbo_autocommit);
        tvInfo = (TextView) findViewById(R.id.activity_quality_tv_info);
        btnSearch = (Button) findViewById(R.id.activity_quality_btn_search);
        btnScan = (Button) findViewById(R.id.activity_quality_btn_scan);
        btnCommit = (Button) findViewById(R.id.activity_quality_btnCommit);
        tvDetail = (TextView) findViewById(R.id.activity_quality_tvdetail);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pid = edPid.getText().toString();
                if (pid.equals("")) {
                    MyToast.showToast(QualityCheckActivity.this, "请输入PID");
                    return;
                }
                SoftKeyboardUtils.closeInputMethod(edPid, QualityCheckActivity.this);
                getDate(pid, "");
                updateInfo(pid);
            }
        });
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pid = edPid.getText().toString();
                if (pid.equals("")) {
                    MyToast.showToast(QualityCheckActivity.this, "请输入PID");
                    return;
                }
                String inputInfo = edInfo.getText().toString();
                String oprName = getSharedPreferences("UserInfo", MODE_PRIVATE).getString("oprName", "");
                String commitInfo = oprName + "(" + MyApp.id + ")" + UploadUtils.getCurrentAtSS();
                if (inputInfo.equals("")) {
                    commitInfo += "同意";
                } else {
                    commitInfo += inputInfo;
                }
                commitInfo += " :" + defTitle;
                String currentInfo = tvInfo.getText().toString();
                if (currentInfo.contains("质检中心")) {
                    MyToast.showToast(QualityCheckActivity.this, "当前已经审核过了");
                    return;
                }
                insertCheckInfo(pid, commitInfo);
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startScanActivity();
            }
        });
        pd = new ProgressDialog(this);
        pd.setTitle("提示");
        pd.setMessage("正在查询");
        resultDialog = DialogUtils.createAlertDialog(this, "");

    }

    @Override
    public String getToobarTittle() {
        return getResString(R.string.title_quality_check);
    }

    @Override
    public void init() {

    }

    @Override
    public void setListeners() {

    }

    @Override
    public void resultBack(String result) {
        scanUpdate(result);
    }

    private void scanUpdate(String result) {
        edPid.setText(result);
        try {
            int tempPid = Integer.parseInt(result);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            MyToast.showToast(QualityCheckActivity.this, "扫码结果有误，必须为数字");
            return;
        }
        autoCommit = cboAuto.isChecked();
        final String pid = edPid.getText().toString();
        if (pid.equals("")) {
            MyToast.showToast(QualityCheckActivity.this, "请输入PID");
            return;
        }
        String inputInfo = edInfo.getText().toString();
        String oprName = getSharedPreferences("UserInfo", MODE_PRIVATE).getString("oprName", "");
        String commitInfo = oprName + "(" + MyApp.id + ")" + UploadUtils.getCurrentAtSS();
        if (inputInfo.equals("")) {
            commitInfo += "同意";
        } else {
            commitInfo += inputInfo;
        }
        commitInfo += " :" + defTitle;
        final String tempCommitInfo=commitInfo;
        getDate(pid, "");
        new Thread() {
            @Override
            public void run() {
                super.run();
                String checkInfo = null;
                try {
                    checkInfo = getCheckInfo(pid);
                } catch (IOException e) {
                    checkInfo = "网络连接错误";
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                final String tempInfo = checkInfo;

                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!"anyType{}".equals(tempInfo)) {
                            tvInfo.setText(tempInfo);
                            if (!tempInfo.contains("质检中心")) {
                                if (autoCommit) {
                                    insertCheckInfo(pid, tempCommitInfo);
                                }
                            } else {
                                MyToast.showToast(QualityCheckActivity.this, "当前单据已经质检过了");
                            }
                        } else {
                            tvInfo.setText("暂无");
                        }
                    }
                });
            }
        }.start();
    }

    public void getDate(final String pid, final String partno) {
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    String result = CaigouYanhuoActivity.getResponse(partno, pid);
                    YanhuoInfo tempInfo = new YanhuoInfo();
                    JSONObject object = new JSONObject(result);
                    JSONArray jArray = object.getJSONArray("表");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject tObj = jArray.getJSONObject(i);
                        String pid = tObj.getString("PID");
                        String caigouPlace = tObj.getString("采购地");
                        String pidDate = tObj.getString("制单日期");
                        String company = tObj.getString("公司");
                        String deptName = tObj.getString("部门");
                        String saleMan = tObj.getString("业务员");
                        String pidState = tObj.getString("单据状态");
                        String payType = tObj.getString("收款");
                        String userFapiao = tObj.getString("客户开票");
                        String providerFapiao = tObj.getString("供应商开票");
                        String providerName = tObj.getString("供应商");
                        String caigouMan = tObj.getString("采购员");
                        String askPriceBy = tObj.getString("询价员");
                        YanhuoInfo yhInfo = new YanhuoInfo(pid, caigouPlace, pidDate,
                                company, deptName, saleMan, pidState, payType,
                                userFapiao, providerFapiao, providerName, caigouMan,
                                askPriceBy);
                        tempInfo = yhInfo;
                        if (jArray.length() == 1) {
                            String detailInfo = YanhuoCheckActivity.getDetailInfo("", pid);
                            try {
                                JSONObject dObje = new JSONObject(detailInfo);
                                String buider = new String();
                                JSONArray dArray = dObje.getJSONArray("表");
                                for (int j = 0; j < dArray.length(); j++) {
                                    JSONObject tDobj = dArray.getJSONObject(i);
                                    buider += tDobj.getString("型号") + ",";
                                }
                                int h = buider.lastIndexOf(",");
                                if (h != -1) {
                                    buider = buider.substring(0, h);
                                    detailInfo = buider + "$" + detailInfo;
                                    yhInfo.detail = detailInfo;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    final YanhuoInfo finalTempInfo = tempInfo;
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvDetail.setText("型号:"+
                                    finalTempInfo.detail.substring(0, finalTempInfo.detail.indexOf("$"))
                                            + "\n" +finalTempInfo.toString());
                            pd.cancel();
                        }
                    });

                } catch (IOException e) {
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showToast(mContext, "连接服务器超时，请重试");
                            pd.cancel();
                        }
                    });
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showToast(mContext, "查询不到此单据");
                            pd.cancel();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }.start();
    }

    ;

    public void updateInfo(final String pid) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                 String checkInfo = null;
                try {
                    checkInfo = getCheckInfo(pid);
                } catch (IOException e) {
                    checkInfo = "网络连接错误";
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                final String tempInfo = checkInfo;
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!"anyType{}".equals(tempInfo)) {
                            tvInfo.setText(tempInfo);
                        } else {
                            tvInfo.setText("暂无");
                        }
                    }
                });
            }
        }.start();
    }

    public void insertCheckInfo(final String pid, final String content) {

        new Thread() {
            @Override
            public void run() {
                super.run();
//                质检中心
//                        信息备注
                final String insertResult;
                try {
                    insertResult = setCheckInfo(pid, defTitle, MyApp.id, content, "市场采购");
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if ("1".equals(insertResult)) {
                                resultDialog.setMessage("返回结果：成功");
                            } else {
                                resultDialog.setMessage("返回结果：失败！！！");
                            }
                            MyApp.myLogger.writeInfo("quality check res:" + insertResult);
                            resultDialog.show();
                        }
                    });
                } catch (IOException e) {
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            resultDialog.setMessage("返回结果：网络连接错误！！！");
                            resultDialog.show();
                        }
                    });
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                String checkInfo = null;
                try {
                    checkInfo = getCheckInfo(pid);
                } catch (IOException e) {
                    checkInfo = "网络连接错误";
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                final String tempInfo = checkInfo;
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!"anyType{}".equals(tempInfo)) {
                            tvInfo.setText(tempInfo);
                        } else {
                            tvInfo.setText("暂无");
                        }
                    }
                });

            }
        }.start();
    }

    public String getCheckInfo(String pid) throws IOException, XmlPullParserException {
        String result = MartService.GetCheckInfo(Integer.parseInt(pid), "市场采购");
        Log.e("zjy", "QualityCheckActivity->getCheckInfo(): reuslt==" +result);
        return result;

    }

    public String setCheckInfo(String pid, String title, String uid, String note, String type) throws IOException, XmlPullParserException {
        String result = MartService.SetCheckInfo(pid, title, uid, note, type);
        Log.e("zjy", "QualityCheckActivity->getCheckInfo(): reuslt==" + result);
        return result;

    }

    @Override
    public void getCameraScanResult(String result, int code) {
        super.getCameraScanResult(result, code);
        if (code == REQ_CODE) {
            scanUpdate(result);
        }
    }

    //    质检中心
//            信息备注
//    int SetCheckInfo(string pid, string title, string uid, string note, string type);SCCG
//    string GetCheckInfo(int pid, string cbtype);SCCG
}
