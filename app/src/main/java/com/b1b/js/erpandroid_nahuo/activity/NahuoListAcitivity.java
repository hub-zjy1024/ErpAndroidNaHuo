package com.b1b.js.erpandroid_nahuo.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.activity.base.SavedLoginInfoActivity;
import com.b1b.js.erpandroid_nahuo.adapter.NahuoAdapter2;
import com.b1b.js.erpandroid_nahuo.adapter.ViewHolder;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.contract.YanhuoPicMgr;
import com.b1b.js.erpandroid_nahuo.entity.NahuoInfoN;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import utils.DialogUtils;
import utils.MyToast;
import utils.wsdelegate.WebserviceUtils;

public class NahuoListAcitivity extends SavedLoginInfoActivity {

    private NahuoAdapter2 nhAdapter;
    private List<NahuoInfoN> mLists;
    ProgressDialog pd;
    public boolean showCheckBox = false;
    private Context mContext = NahuoListAcitivity.this;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    nhAdapter.notifyDataSetChanged();

                    break;
                case 1:
                    break;
                case 2:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_nahuo_list_acitivity);
        final ListView lv = (ListView) findViewById(R.id.nahuo_lv);
        Button btnSearch = (Button) findViewById(R.id.nahuo_btn_search);
        final Button btnPiLiang = (Button) findViewById(R.id.nahuo_btn_piliang);
        Button btnConfirmAll = (Button) findViewById(R.id.nahuo_btn_confirmAll);
        final CheckBox cboType = (CheckBox) findViewById(R.id.nahuo_cho_type);
        final EditText editText = (EditText) findViewById(R.id.nahuo_ed_pid);
        btnPiLiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnPiLiang.getText().equals("批量处理")) {
                    btnPiLiang.setText("取消批量");
                } else {
                    btnPiLiang.setText("批量处理");
                }
                nhAdapter.showCheckBox = !nhAdapter.showCheckBox;
                nhAdapter.notifyDataSetChanged();
            }
        });
        btnConfirmAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mLists = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.setMessage("提交中");
        pd.setCancelable(false);
        nhAdapter = new NahuoAdapter2(this, R.layout.item_nahuo_lv, mLists, new
                NahuoAdapter2.BtnOkOnClickListner() {

                    @Override
                    public void onClick(ViewHolder helper, final NahuoInfoN item) {

                        DialogUtils.safeShowDialog(mContext, pd);

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    final String result = setNahuoOk(item.getPid(), "已成功拿货");
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Dialog spAlert = DialogUtils.getSpAlert
                                                    (mContext,
                                                            "处理" + item.getPid() + "的返回结果：" + result, "提示");
                                            DialogUtils.safeShowDialog(mContext,
                                                    spAlert);
                                            DialogUtils.dismissDialog(pd);
                                        }
                                    });
                                } catch (IOException e) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Dialog spAlert = DialogUtils.getSpAlert
                                                    (mContext,
                                                            "网络连接失败", "提示");
                                            DialogUtils.safeShowDialog(mContext,
                                                    spAlert);
                                            DialogUtils.dismissDialog(pd);
                                        }
                                    });
                                    e.printStackTrace();
                                } catch (XmlPullParserException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }, new NahuoAdapter2.BtnOkOnClickListner() {
            @Override
            public void onClick(ViewHolder helper, NahuoInfoN item) {
                final String pid = item.getPid();
                /*AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("上传图片：" + pid);
                builder.setItems(new String[]{"拍照", "从手机选择", "连拍"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        switch (which) {
                            case 0:
                                intent = new Intent(mContext, YhTakepicActivity
                                        .class);
                                intent.putExtra("pid", pid);
                                intent.putExtra("flag", "caigou");
                                startActivity(intent);
                                Log.e("zjy", "NahuoListAcitivity->onClick(): ==" + getClass());
                                MyApp.myLogger.writeInfo(getLocalClassName() + "checkpage-take");
                                break;
                            case 1:
                                intent = new Intent(mContext, ObtainPicFromPhone2.class);
                                intent.putExtra("pid", pid);
                                intent.putExtra("flag", "caigou");
                                startActivity(intent);
                                MyApp.myLogger.writeInfo("checkpage-obtain");
                                break;
                            case 2:
                                intent = new Intent(mContext, CaigouTakePic2Activity.class);
                                intent.putExtra("pid", pid);
                                intent.putExtra("flag", "caigou");
                                startActivity(intent);
                                MyApp.myLogger.writeInfo("checkpage-take2");
                                break;
                        }
                    }
                });
                builder.create().show();*/
                YanhuoPicMgr mPicManager = new YanhuoPicMgr(mContext);
                mPicManager.takePic(pid);
            }
        });
        lv.setAdapter(nhAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApp.id == null) {
                    showToast("程序出现异常，请重新登录");
                    return;
                }
                String pid = editText.getText().toString();
                String type = cboType.isChecked() ? "0" : "1";
                if (mLists.size() > 0) {
                    mLists.clear();
                    nhAdapter.notifyDataSetChanged();
                }
                getData(MyApp.id, pid, type);
            }
        });
        Intent intent = getIntent();
        String content = intent.getStringExtra("content");
        if (content != null) {
            Log.e("zjy", "NahuoListAcitivity->onCreate(): getIntentID==" + getIntent().getStringExtra("uid"));

            MyApp.id = intent.getStringExtra("uid");
            getData(MyApp.id, "", "1");

        }
    }

    public static String getNahuoList(String uid, String pid, String type) throws
            IOException,
            XmlPullParserException {
        //        GetMHWCInfo
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("uid", uid);
        map.put("pid", pid);
        map.put("type", type);
        return WebserviceUtils.getWcfResult(map, "GetMHWCInfo", WebserviceUtils.MartService);
    }

    public String setNahuoOk(String pid, String checkInfo) throws
            IOException,
            XmlPullParserException {
        //        GetMHWCInfo
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("pid", pid);
        map.put("checkinfo", checkInfo);
        return WebserviceUtils.getWcfResult(map, "SetNaHuoWanChengInfo", WebserviceUtils.MartService);
    }

    public void getData(final String uid, final String pid, final String type) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String nahuoList = getNahuoList(uid, pid, type);
                    Log.e("zjy", "NahuoListAcitivity->run(): reuslt==" + nahuoList);
                    JSONObject obj = new JSONObject(nahuoList);
                    JSONArray table = obj.getJSONArray("表");
                    for (int i = 0; i < table.length(); i++) {
                        JSONObject tobj = table.getJSONObject(i);
                        String pid = tobj.getString("PID");
                        String weituoBy = tobj.getString("委托人");
                        String zdPerson = tobj.getString("指定人");
                        String providerID = tobj.getString("ProviderID");
                        String providerName = tobj.getString("供应商");
                        String address = tobj.getString("地址");
                        String phone = tobj.getString("电话");
                        String main = tobj.getString("联系人");
                        String partNO = tobj.getString("型号");
                        String counts = tobj.getString("数量");
                        String price = tobj.getString("单价");
                        String pihao = tobj.getString("批号");
                        String kehuKaipiao = tobj.getString("客户开票");
                        String kaiPiaoCompany = tobj.getString("开票公司");
                        String companyFaPiao = tobj.getString("开票公司信息");
                        String notes = tobj.getString("备注");
                        String detail = tobj.getString("NH_CheckInfo");
                        NahuoInfoN info = new NahuoInfoN(pid, weituoBy, zdPerson,
                                providerID, providerName, address, phone, main, partNO,
                                counts, price, pihao, kehuKaipiao, kaiPiaoCompany,
                                companyFaPiao, notes, detail);
                        mLists.add(info);

                    }
                    mHandler.sendEmptyMessage(0);
                } catch (IOException e) {
                    showToast("网络连接出现错误，请重试");
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    showToast("条件有误，查询结果为空");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void showToast(final String mes) {

        if (Looper.getMainLooper() == Looper.myLooper()) {
            MyToast.showToast(this, mes);

        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MyToast.showToast(mContext, mes);
                }
            });
        }
    }
}
