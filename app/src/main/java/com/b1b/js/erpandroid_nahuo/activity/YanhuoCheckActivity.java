package com.b1b.js.erpandroid_nahuo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.adapter.YhDetailAdapter;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.entity.Baseinfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import utils.DialogUtils;
import utils.MyToast;
import utils.SoftKeyboardUtils;
import utils.UploadUtils;
import utils.WebserviceUtils;

public class YanhuoCheckActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private ProgressDialog pd;
    private TextView tvPid;
    private ListView lv;
    private List<Baseinfo> data;
    private YhDetailAdapter adapter;
    private Context mContext = this;
    private Button btnViewPic;
    private ProgressDialog pdDialog;
    private TextView tvCounts;

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
         lv = (ListView) findViewById(R.id.activity_yanhuocheck_lv);
        data = new ArrayList<>();
//        adapter = new YhDetailAdapter(this, R.layout.item_yhdetail, data);
        adapter = new YhDetailAdapter(this, R.layout.item_yhdetail2, data);
        lv.setAdapter(adapter);
        pdDialog = new ProgressDialog(this);
        pdDialog.setTitle("提示");
        pdDialog.setMessage("正在获取型号信息……");
        pd = new ProgressDialog(this);
        pd.setTitle("提示");
        pd.setMessage("正在验货");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApp.id == null) {
                    MyToast.showToast(YanhuoCheckActivity.this, "登陆人为空，请重启");
                    return;
                }
                String content = getYanhuoStr(MyApp.id, "同意");
                Log.e("zjy", "YanhuoCheckActivity->onClick(): tv.Txt==" + tvPid.getText
                        ().toString());
                yanhuo(tvPid.getText().toString(), "等待入库", content);
            }
        });
        btnViewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YanhuoCheckActivity.this, ViewPicByPidActivity.class);
                intent.putExtra("pid", tvPid.getText().toString());
                startActivity(intent);
            }
        });
        SoftKeyboardUtils.closeInputMethod(edNote, this);
//        btnOk.requestFocus();
        btnFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApp.id == null) {
                    MyToast.showToast(YanhuoCheckActivity.this, "登陆人为空，请重启");
                    return;
                }

                String note = edNote.getText().toString();
                if (note.equals("")) {
                    MyToast.showToast(YanhuoCheckActivity.this, "请输入不通过理由");
                    return;
                }
                String content = getYanhuoStr(MyApp.id, note);
                yanhuo(tvPid.getText().toString(), "未能入库", content);
            }
        });
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pid = tvPid.getText().toString();

                takePic(pid);
            }
        });
        Intent intent = getIntent();
        tvPid.setText(intent.getStringExtra("pid"));
        pdDialog.show();
        final String detail = intent.getStringExtra("detail");
        new Thread(){
            @Override
            public void run() {
                super.run();
                String result = null;
                try {
                    if (!"".equals(detail)) {
                        result = detail.substring(detail.indexOf("$") + 1);
                    } else {
                        result = getDetailInfo("", getIntent().getStringExtra("pid"));
                    }
                    Log.e("zjy", "YanhuoCheckActivity->run(): detaile==" + result);
                    JSONObject obj = new JSONObject(result);
                    final JSONArray jarray = obj.getJSONArray("表");
                    for(int i=0;i<jarray.length();i++) {
//                        {"表":[{"PID":"837688","采购地":"深圳市场","制单日期":
//                            "2017/11/20 16:05:30","公司":"远大创新分公司","部门":"远大国际部","业务员":
//                            "孙盼盼","单据状态":"等待验货","收款":"现款现货","客户开票":"普通发票",
//                                    "供应商开票":"普通发票","供应商":"深圳市创域兴泰电子科技有限公司",
//                            "采购员":"周雨佳","询价员":"周雨佳","型号":"AD603ARZ-REEL7","数量":"1925",
//                                    "进价":"17.9550","售价":"19.2850","批复价":"17.9550","封装":"NA",
//                                    "描述":"0","申请批号":"17+","批号":"17+","厂家":"ADI",
//                                "备注":"发国际","毛利率":"0.07","成本":"34563.3750","指定拿货人ID":
//                            "3653","拿货人回复":""}] }
                        JSONObject temp = jarray.getJSONObject(i);
                        Baseinfo info = new Baseinfo();
                        info.parno= temp.getString("型号");
                        info.counts=temp.getString("数量");
                        info.description=temp.getString("描述");
                        info.fengzhuang=temp.getString("封装");
                        info.pihao=temp.getString("批号");
                        info.factory=temp.getString("厂家");
                        info.notes = temp.getString("备注");
                        data.add(info);
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
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
        }.start();
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
                        intent.putExtra("pid", pid);
                        intent.putExtra("flag", "caigou");
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(YanhuoCheckActivity.this,
                                CaigouTakePic2Activity.class);
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
                //                UpdateSSCSState
                final LinkedHashMap<String, Object> map = new LinkedHashMap<String,
                        Object>();
                map.put("pid", pid);
                map.put("state", state);
                map.put("chkNote", note);
                Log.e("zjy", "YanhuoCheckActivity->run(): checkNote==" + note);
                SoapObject req = WebserviceUtils.getRequest(map, "UpdateSSCSState");
                try {
                    SoapPrimitive res = WebserviceUtils.getSoapPrimitiveResponse(req,
                            SoapEnvelope.VER11, WebserviceUtils.MartService);
                    Log.e("zjy", "YanhuoCheckActivity->run(): reuslt==" + res.toString());
                    String result = res.toString();
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
                                                final String pid = tvPid.getText()
                                                        .toString();
                                                takePic(pid);
                                            }
                                        }, "是", null, "否").show();

                                DialogUtils.dismissDialog(pd);
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.getSpAlert(YanhuoCheckActivity.this,
                                        "验货失败！！！", "提示").show();
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
        String s = "库房：" + uid + "\t" + UploadUtils
                .getCurrentAtSS() + " " + content;
        return s;
    }
    public String getRelativePicInfoByPid( String pid) throws IOException, XmlPullParserException {
        return ViewPicByPidActivity.getYHPIC("SCCG_" + pid);
    }

    public static String getDetailInfo(String partno, String pid) throws IOException, XmlPullParserException {
        LinkedHashMap<String, Object> obj = new LinkedHashMap<>();
        obj.put("partno", partno);
        obj.put("pid", pid);
        SoapObject req = WebserviceUtils.getRequest(obj, "GetSSCGInfoByDDYHList");
        SoapObject resultObj = WebserviceUtils.getSoapObjResponse(req, SoapEnvelope.VER11, WebserviceUtils
                .MartService, 30 * 1000);
        String result = "";
        Log.e("zjy", "YanhuoCheckActivity->getDetailInfo(): response==" + resultObj.toString());
        if (resultObj != null) {
            result = resultObj.getPropertySafelyAsString("GetSSCGInfoByDDYHListResult");
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String res = getRelativePicInfoByPid(getIntent().getStringExtra("pid"));
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
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
