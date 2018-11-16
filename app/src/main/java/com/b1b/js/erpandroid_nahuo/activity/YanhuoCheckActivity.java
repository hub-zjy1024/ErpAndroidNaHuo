package com.b1b.js.erpandroid_nahuo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.adapter.YHDetailAdapter2;
import com.b1b.js.erpandroid_nahuo.adapter.YhDetailAdapter;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.entity.BaoguanDetail;
import com.b1b.js.erpandroid_nahuo.entity.Baseinfo;
import com.b1b.js.erpandroid_nahuo.utils.DelayClickControler;
import com.b1b.js.erpandroid_nahuo.utils.TaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.DialogUtils;
import utils.MyToast;
import utils.SoftKeyboardUtils;
import utils.net.ftp.UploadUtils;
import utils.wsdelegate.MartService;

public class YanhuoCheckActivity extends SavedLoginInfoActivity {

    private Handler mHandler = new Handler();
    private ProgressDialog pd;
    private TextView tvPid;
    private ListView lv;
    private List<Baseinfo> data;
    private YhDetailAdapter adapter;
    private YHDetailAdapter2 adapter2;
    private Context mContext = this;
    private Button btnViewPic;
    private ProgressDialog pdDialog;
    private TextView tvCounts;
    private String provider = "";
    List<BaoguanDetail> baoguanDetails = new ArrayList<>();
    public String detailJson;
    public boolean isBaoguan = false;
    private TextView tvKaipiao;
    private TextView tvProvider;
    private TextView tvPayBy;
    private TextView tvBank;
    private TextView tvBankAccount;
    private String uname;
    private String nowPid;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SoftKeyboardUtils.hideKeyBoard(this);
        setContentView(R.layout.activity_yanhuo_check);
        tvPid = (TextView) findViewById(R.id.activity_yanhuocheck_tvpid);
        final EditText edNote = (EditText) findViewById(R.id.activity_yanhuocheck_ed_mark);
        Button btnTakePic = (Button) findViewById(R.id.activity_yanhuocheck_btn_takepic);
        btnViewPic = (Button) findViewById(R.id.activity_yanhuocheck_btn_viewpic);
        Button btnOk = (Button) findViewById(R.id.activity_yanhuocheck_btn_ok);

        Button btnFail = (Button) findViewById(R.id.activity_yanhuocheck_btn_fail);
        tvCounts = (TextView) findViewById(R.id.activity_yanhuocheck_counts);
        tvKaipiao = (TextView) findViewById(R.id.activity_yanhuocheck_tv_kapiao);
        tvProvider = (TextView) findViewById(R.id.activity_yanhuocheck_tv_provider);
        tvPayBy = (TextView) findViewById(R.id.activity_yanhuocheck_tv_payby);
        tvBank = (TextView) findViewById(R.id.activity_yanhuocheck_tv_bank);
        tvBankAccount = (TextView) findViewById(R.id.activity_yanhuocheck_tv_bankaccount);

        lv = (ListView) findViewById(R.id.activity_yanhuocheck_lv);
        Button btnBg = (Button) findViewById(R.id.activity_yanhuocheck_btn_baoguan);
         toolbar = (Toolbar) findViewById(R.id.dyjkf_normalTb);
        toolbar.setTitle("验货");
        nowPid = getIntent().getStringExtra("pid");
        toolbar.setSubtitle("当前采购单：" + nowPid);
        setSupportActionBar(toolbar);
        data = new ArrayList<>();
        //        adapter = new YhDetailAdapter(this, R.layout.item_yhdetail, data);
        adapter = new YhDetailAdapter(this, R.layout.item_yhdetail2, data);
        //        lv.setAdapter(adapter);

        adapter2 = new YHDetailAdapter2(this, R.layout.item_yhdetail2, baoguanDetails);
        lv.setAdapter(adapter2);
        pdDialog = new ProgressDialog(this);
        pdDialog.setTitle("提示");
        pdDialog.setMessage("正在获取型号信息……");
        pd = new ProgressDialog(this);
        pd.setTitle("提示");
        pd.setMessage("正在验货");
        btnOk.setOnClickListener(new DelayClickControler(500) {
            @Override
            public void finalClick(View v, boolean isFast) {
                if (isFast) {
                    MyToast.showToast(mContext, "请不要点击过快！！！！");
                } else {
                    if (loginID == null) {
                        MyToast.showToast(YanhuoCheckActivity.this, "登陆人为空，请重启");
                        return;
                    }
                    String content = getYanhuoStr(loginID, "同意");
                    Log.e("zjy", "YanhuoCheckActivity->onClick(): tv.Txt==" + nowPid);
                    if (isBaoguan) {
                        pd.show();
                        startBaoguan();
                    } else {
                        yanhuo(nowPid, "等待入库", content);
                    }
                } 
            }
        });
        btnViewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YanhuoCheckActivity.this, ViewPicByPidActivity.class);
                intent.putExtra("pid", nowPid);
                startActivity(intent);
            }
        });
        SoftKeyboardUtils.closeInputMethod(edNote, this);
        btnFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginID== null) {
                    MyToast.showToast(YanhuoCheckActivity.this, "登陆人为空，请重启");
                    return;
                }

                String note = edNote.getText().toString();
                if (note.equals("")) {
                    MyToast.showToast(YanhuoCheckActivity.this, "请输入不通过理由");
                    return;
                }
                String content = getYanhuoStr(loginID, note);
                yanhuo(nowPid, "未能入库", content);
            }
        });
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pid = nowPid;

                takePic(pid);
            }
        });
        final Intent intent = getIntent();
        btnBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(YanhuoCheckActivity.this, CaigouBaoguanActivity.class);
                mIntent.putExtra("provider", "");
                mIntent.putExtra("pid",nowPid);
                mIntent.putExtra("detailJson", detailJson);
                startActivity(mIntent);
            }
        });
        SharedPreferences userSp = getSharedPreferences(SettingActivity.PREF_USERINFO, MODE_PRIVATE);
        uname = userSp.getString("oprName","");
        tvPid.setText(nowPid);
        pdDialog.show();
        final String detail = intent.getStringExtra("detail");
        Runnable getDataRun = new Runnable() {
            @Override
            public void run() {
                String result = null;
                try {
                    if (!"".equals(detail)) {
                        result = detail.substring(detail.indexOf("$") + 1);
                    } else {
                        result = getDetailInfo("", nowPid);
                    }
                    detailJson = result;
                    Log.e("zjy", "YanhuoCheckActivity->run(): detailInfo==" + result);
                    JSONObject obj = new JSONObject(result);
                    final JSONArray jarray = obj.getJSONArray("表");
                    String flag = jarray.getJSONObject(0).getString("ToStyle");
                    String provider = jarray.getJSONObject(0).getString("供应商");
                    String providerKp = jarray.getJSONObject(0).getString("供应商开票");
                    String providerID = jarray.getJSONObject(0).getString("ProviderID");
                    String providerDetail = MartService.GetProviderDataInfoByName(providerID);
                    try {
                        JSONObject jobj = new JSONObject(providerDetail);
                        JSONArray providerArray = jobj.getJSONArray("表");
                        String finalProvider = provider;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvProvider.setText(finalProvider);
                                if (providerArray.length() > 0) {
                                    try {
                                        tvPayBy.setText(providerArray.getJSONObject(0).getString("收款人"));
                                        tvBank.setText(providerArray.getJSONObject(0).getString("开户行"));
                                        tvBankAccount.setText(providerArray.getJSONObject(0).getString("帐号"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!"".equals(flag)) {
                        isBaoguan = true;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                TextView baoguanState = (TextView) findViewById(R.id.activity_yanhuocheck_tv_state);
                                baoguanState.setVisibility(View.VISIBLE);
                                baoguanState.setText("报关单");
                                baoguanState.setTextColor(Color.RED);
                            }
                        });
                    }
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject temp = jarray.getJSONObject(i);
                        Baseinfo info = new Baseinfo();
                        info.parno = temp.getString("型号");
                        info.counts = temp.getString("数量");
                        info.description = temp.getString("描述");
                        info.fengzhuang = temp.getString("封装");
                        info.pihao = temp.getString("批号");
                        info.counts = temp.getString("批号");
                        info.factory = temp.getString("厂家");
                        info.notes = temp.getString("备注");
                        provider = temp.getString("供应商");
                        data.add(info);
                        BaoguanDetail detailInfo = new BaoguanDetail();
                        detailInfo.parno = temp.getString("型号");
                        detailInfo.counts = temp.getString("数量");
                        detailInfo.description = temp.getString("描述");
                        detailInfo.fengzhuang = temp.getString("封装");
                        detailInfo.pihao = temp.getString("批号");
                        detailInfo.factory = temp.getString("厂家");
                        detailInfo.notes = temp.getString("备注");
                        detailInfo.setCostRMB(temp.getString("进价"));
                        detailInfo.setArea(temp.getString("产地"));
                        detailInfo.setProviderID(temp.getString("产地"));
                        baoguanDetails.add(detailInfo);
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            adapter.notifyDataSetChanged();
                            adapter2.notifyDataSetChanged();
                            tvKaipiao.setText(providerKp);
                            tvCounts.setText("数据条数：" + jarray.length());
                            DialogUtils.dismissDialog(pdDialog);

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showToast(mContext, "当前数据解析失败！！");
                        }
                    });
                    e.printStackTrace();
                }
            }
        };
        TaskManager.getInstance().execute(getDataRun);
    }

    public void takePic(final String pid) {
        AlertDialog.Builder builder = new AlertDialog.Builder
                (YanhuoCheckActivity.this);
        builder.setTitle("图片上传方式：" + pid);
        builder.setItems(new String[]{"拍照", "从手机选择", "连拍"}, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                switch (which) {
                    case 0:
                        intent = new Intent(YanhuoCheckActivity.this,
                                TakePicActivity.class);
                        intent.putExtra("pid", pid);
                        intent.putExtra("flag", "caigou");
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(YanhuoCheckActivity.this,
                                ObtainPicFromPhone.class);
//                                ObtainPicFromPhone2.class);
                        intent.putExtra("pid", pid);
                        intent.putExtra("flag", "caigou");
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(YanhuoCheckActivity.this,
                                //                                CaigouTakePic2Activity.class);
                                YanhuoTakepic2.class);
                        intent.putExtra("pid", pid);
                        intent.putExtra("flag", "caigou");
                        startActivity(intent);
                        break;
                }
            }
        });
        builder.create().show();
    }

    public void yanhuo(final String pid, final String state, final String note) {

        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    String result = MartService.UpdateSSCSState(pid, state, note);
                    if (result.equals("成功")) {
                        MyApp.myLogger.writeInfo("yanhuo-ok:" + pid + "\t" + state);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.getSpAlert(YanhuoCheckActivity.this,
                                        "验货完成，是否拍照", "提示", new DialogInterface
                                                .OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                takePic(nowPid);
                                            }
                                        }, "是", null, "否").show();

                                DialogUtils.dismissDialog(pd);
                            }
                        });
                    } else {
                        MyApp.myLogger.writeBug("yanhuo Error:" + pid);
                        final String msg = result;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.getSpAlert(YanhuoCheckActivity.this,
                                        "验货失败！！！," + msg, "提示").show();
                                DialogUtils.dismissDialog(pd);
                            }
                        });
                    }
                } catch (IOException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.getSpAlert(YanhuoCheckActivity.this,
                                    "连接服务器失败！！！", "提示").show();
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

    public String getYanhuoStr(String uid, String content) {
        String s = "库房：" + uid + "\t" + UploadUtils.getCurrentAtSS() + " " + content;
        return s;
    }

    public String getRelativePicInfoByPid(String pid) throws IOException, XmlPullParserException {
        return ViewPicByPidActivity.getYHPIC("SCCG_" + pid);
    }

    public static String getDetailInfo(String partno, String pid) throws IOException, XmlPullParserException {
        return MartService.GetSSCGInfoByDDYHList(partno, pid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRelativePic();
    }

    public void startBaoguan() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                  /*  Log.e("zjy", "YanhuoCheckActivity->run(): nowId==" + nowPid);
                    Log.e("zjy", "YanhuoCheckActivity->run(): loginID==" + loginID);
                    Log.e("zjy", "YanhuoCheckActivity->run(): uname==" + uname);*/
                    String baoguanRes = MartService.SCCGCreatApplyCustoms_new(nowPid, loginID, uname);
                    String msg = "解析返回的Json失败";
                    String code = "0";
                    try {
                        JSONObject object = new JSONObject(baoguanRes);
                        code = object.getString("code");
                        msg = object.getString("msg");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (code.equals("1")) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.getSpAlert(YanhuoCheckActivity.this,
                                        "验货完成，是否拍照", "提示", new DialogInterface
                                                .OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                takePic(nowPid);
                                            }
                                        }, "是", null, "否").show();
                                DialogUtils.dismissDialog(pd);
                            }
                        });
                    } else {
                        String finalMsg = msg;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.getSpAlert(YanhuoCheckActivity.this,
                                        "验货失败，" + finalMsg, "提示").show();
                                DialogUtils.dismissDialog(pd);
                            }
                        });
                    }
                } catch (IOException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            DialogUtils.getSpAlert(YanhuoCheckActivity.this,
                                    "验货失败，" + e.getMessage(), "提示").show();
                        }
                    });
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();
                            DialogUtils.getSpAlert(YanhuoCheckActivity.this,
                                    "验货失败，" + e.getMessage(), "提示").show();
                        }
                    });
                }
            }
        };
        TaskManager.getInstance().execute(run);
    }

    public void getRelativePic() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String res = getRelativePicInfoByPid(nowPid);
                    Log.e("zjy", "CaigoudanEditActivity->run(): pidpic==" + res);
                    JSONObject jobj = new JSONObject(res);
                    JSONArray jsonArray = jobj.getJSONArray("表");
                    if (!"{\"表\":] }".equals(res)) {
                        final int size = jsonArray.length();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                btnViewPic.setText("查看图片（" + size + "张)");
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                btnViewPic.setText("查看图片（暂无）");
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            btnViewPic.setText("查看图片（暂无）");
                        }
                    });
                }
            }
        }.start();
    }

    private void getProviderInfo(String providerName) {
        SharedPreferences sp = getSharedPreferences(SettingActivity.PREF_USERINFO, MODE_PRIVATE);
        int did = sp.getInt("did", -1);
        Runnable run = new Runnable() {
            @Override
            public void run() {

            }
        };
        try {
            String soapRes = MartService.GetMyProviderInfoByName("", Integer.parseInt(loginID), did, providerName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }
}
