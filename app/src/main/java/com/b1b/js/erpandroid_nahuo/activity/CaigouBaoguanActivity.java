package com.b1b.js.erpandroid_nahuo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.adapter.BaoguanAutoAdapter;
import com.b1b.js.erpandroid_nahuo.adapter.CaigouBaoguanAdapter;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.entity.Baoguan;
import com.b1b.js.erpandroid_nahuo.entity.BaoguanDetail;
import com.b1b.js.erpandroid_nahuo.entity.CaigouBaoguanInfo;
import com.b1b.js.erpandroid_nahuo.handler.NoLeakHandler;
import com.b1b.js.erpandroid_nahuo.utils.JsonParser;
import com.b1b.js.erpandroid_nahuo.utils.TaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import utils.CheckUtils;
import utils.DialogUtils;
import utils.MyToast;
import utils.SoftKeyboardUtils;
import utils.wsdelegate.MartService;

public class CaigouBaoguanActivity extends AppCompatActivity implements NoLeakHandler.NoLeakCallback {

    private List<CaigouBaoguanInfo> bgComp;
    private List<CaigouBaoguanInfo> providers;
    private List<CaigouBaoguanInfo> pays;
    public List<BaoguanDetail> listData;
    private Handler mHandler = new NoLeakHandler(this);
    private AutoCompleteTextView autoBaoguanComp;
    private final int MSG_ERROR = 2;
    private final int MSG_Alert = 6;
    private final int MSG_OK = 1;
    private final int MSG_Detail = 3;
    private final int MSG_GetProvider = 4;
    private final int MSG_GetPay = 5;
    private final int MSG_GetBaoguan = 0;
    private com.b1b.js.erpandroid_nahuo.adapter.CaigouBaoguanAdapter dAdapter;

    private double rate = -1;
    private ListView lv;
    private AutoCompleteTextView autoPayComp;
    private AutoCompleteTextView autoOrderComp;
    private AutoCompleteTextView autoNewProvider;
    private TextView tvSrcProvider;
    private String loginID = MyApp.id;
    private Spinner spiBaoguan;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("loginID", loginID);
    }

    @Override
    public void handleMessage(Message msg) {
        Object obj = msg.obj;
        switch (msg.what) {
            case MSG_OK:
                Snackbar alertBar = Snackbar.make(autoOrderComp, "报关成功", Snackbar.LENGTH_LONG);
                alertBar.setAction("返回", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                alertBar.show();
                break;
            case MSG_GetBaoguan:
                if (obj != null) {
                    bgComp = (List<CaigouBaoguanInfo>) obj;
                    String[] s = new String[bgComp.size()];
                    for (int i = 0; i < bgComp.size(); i++) {
                        CaigouBaoguanInfo caigouBaoguanInfo = bgComp.get(i);
                        s[i] = caigouBaoguanInfo.getName();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_comons_tv, R.id.item_common_tv, s);
//                    autoBaoguanComp.setAdapter(adapter);
                    spiBaoguan.setAdapter(adapter);
                    if (bgComp.size() > 5) {
//                        autoBaoguanComp.setText(bgComp.get(4).getName());
                        spiBaoguan.setSelection(4);
                    }
                } else {
                    MyToast.showToast(this, "获取报关公司出现异常!!!");
                }
                break;
            case MSG_GetPay:
                pays = (List<CaigouBaoguanInfo>) obj;
                String[] s = new String[pays.size()];
                for (int i = 0; i < pays.size(); i++) {
                    CaigouBaoguanInfo caigouBaoguanInfo = pays.get(i);
                    s[i] = caigouBaoguanInfo.getName();
                }
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_comons_tv, R.id.item_common_tv, s);
                BaoguanAutoAdapter adapter = new BaoguanAutoAdapter(this, pays);
                autoPayComp.setAdapter(adapter);
                if (pays.size() > 0) {
                    String name = pays.get(0).getName();
                    autoOrderComp.setText(name);
                    autoPayComp.setText(name);
                }
                autoOrderComp.setAdapter(adapter);
                break;
            case MSG_Alert:
                String alert = "未知错误";
                if (obj != null) {
                    alert = obj.toString();
                }
                DialogUtils.getSpAlert(CaigouBaoguanActivity.this, alert, "提示").show();
                break;

            case MSG_Detail:
                init();
                break;
            case MSG_ERROR:
                String errorMsg = "未知错误";
                if (obj != null) {
                    errorMsg = obj.toString();
                }
                MyToast.showToast(this, errorMsg);
                break;
            case MSG_GetProvider:
                if (obj != null) {
                    providers = (List<CaigouBaoguanInfo>) obj;
                    String[] pNameArray = new String[providers.size()];
                    for (int i = 0; i < providers.size(); i++) {
                        pNameArray[i] = providers.get(i).getName();
                    }
                    ArrayAdapter<String> providerAdapter = new ArrayAdapter<>(this, R.layout.item_comons_tv, R.id
                            .item_common_tv, pNameArray);
                    autoNewProvider.setAdapter(providerAdapter);
                }
                break;
        }
    }

    private Baoguan baoguan = new Baoguan();
    private String pid;
    private String detailJson;
    private int did;
    private String nName;
    private boolean isStoped = false;
    private int lastTouch = 0;
    private int lastVisiablePosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            loginID = savedInstanceState.getString("loginID");
        }
        SoftKeyboardUtils.hideKeyBoard(this);
        setContentView(R.layout.activity_caigou_baoguan);
        Toolbar toobar = (Toolbar) findViewById(R.id.toolbar);
        autoBaoguanComp = (AutoCompleteTextView) findViewById(R.id.activity_baoguan_auto_ctv_baoguancomp);
        spiBaoguan = (Spinner) findViewById(R.id.activity_baoguan_spin_baoguancomp);
        lv = (ListView) findViewById(R.id.activity_baoguan_lv);
        TextView tvBgCompId = (TextView) findViewById(R.id.activity_baoguan_tv_bgcomp_id);
        autoPayComp = (AutoCompleteTextView) findViewById(R.id.activity_baoguan_auto_ctv_paycomp);
        autoOrderComp = (AutoCompleteTextView) findViewById(R.id.activity_baoguan_auto_ctv_ordercomp);
        autoNewProvider = (AutoCompleteTextView) findViewById(R.id.activity_baoguan_tv_new_provider);
        Button btnOk = (Button) findViewById(R.id.activity_baoguan_btn_ok);
        Button btnReturn = (Button) findViewById(R.id.activity_baoguan_btn_return);
        tvSrcProvider = (TextView) findViewById(R.id.activity_baoguan_tv_src_provider);
        final ScrollView myscollview = (ScrollView) findViewById(R.id.activity_baoguan_myscollview);
        Intent intent = getIntent();
        listData = new ArrayList<>();
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getAdapter().getCount() - 1) {
                        MyToast.showToast(CaigouBaoguanActivity.this, "已经到底了");
                        myscollview.requestDisallowInterceptTouchEvent(false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                myscollview.requestDisallowInterceptTouchEvent(true);
            }
        });

        //设置listveiw的触屏事件；
        lv.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (lv.getLastVisiblePosition() == lv.getAdapter().getCount() - 1) {
                        if (lastVisiablePosition > lv.getLastVisiblePosition()) {
                            myscollview.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                } else {
                    //当listview在滚动时，不拦截listview的滚动事件,就是listview可以滚动，
                    //
                    int y = 0;
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        lastTouch = (int) event.getY();
                        lastVisiablePosition = lv.getLastVisiblePosition();
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        int moveY = (int) (event.getY() - lastTouch);
                        if (moveY > 0) {
                            if (lv.getFirstVisiblePosition() == 0) {
                                myscollview.requestDisallowInterceptTouchEvent(false);
                            } else {
                                myscollview.requestDisallowInterceptTouchEvent(true);
                            }
                        } else {
                            if (lv.getLastVisiblePosition() == lv.getAdapter().getCount() - 1) {
                                myscollview.requestDisallowInterceptTouchEvent(false);
                            } else {
                                myscollview.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                    }
                }
                return false;
            }
        });
        pid = intent.getStringExtra("pid");
        detailJson = intent.getStringExtra("detailJson");
        bgComp = new ArrayList<>();
        //        autoBaoguanComp.setAdapter(new ArrayList<String>());
        toobar.setSubtitleTextColor(getResources().getColor(R.color.colorAccent));
        toobar.setTitleTextColor(Color.WHITE);
        toobar.setTitle("市场采购");
        toobar.setSubtitle("报关:" + pid);
        setSupportActionBar(toobar);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //测试Snackbar
                if (true) {
                    Snackbar alertBar = Snackbar.make(autoOrderComp, "报关成功", Snackbar.LENGTH_LONG);
                    alertBar.setAction("返回", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                    alertBar.show();
                    return;
                }
//                String bgCompName = autoBaoguanComp.getText().toString();
                String bgCompName = spiBaoguan.getSelectedItem().toString();
                String providerSrc = tvSrcProvider.getText().toString();
                String newProvider = autoNewProvider.getText().toString();
                String orderComp = autoOrderComp.getText().toString();
                String payCompName = autoPayComp.getText().toString();
//                if (true) {
//                    int index = autoPayComp.getListSelection();
//                    if (index != -1) {
//                        Log.e("zjy", "CaigouBaoguanActivity->onClick(): PayComp==" + autoPayComp.getAdapter().getItem(index));
//                    }
//                    return;
//                }
                String bgCompID = getIDByName(bgComp, bgCompName);
                String xdId = getIDByName(pays, orderComp);
                String payID = getIDByName(pays, payCompName);
                String providerID = "";
                if (newProvider.equals(providerSrc)) {
                    List<BaoguanDetail> result = baoguan.getResult();
                    if (result != null && result.size() > 0) {
                        String providerID1 = result.get(0).getProviderID();
                        providerID = providerID1;
                    }
                } else {
                    providerID = getIDByName(providers, newProvider);
                }
                if ("".equals(bgCompID)) {
                    MyToast.showToast(CaigouBaoguanActivity.this, "请选择正确的报关公司名称");
                    return;
                }
                if ("".equals(xdId)) {
                    MyToast.showToast(CaigouBaoguanActivity.this, "请选择正确的下单公司名称");
                    return;
                }
                if ("".equals(payID)) {
                    MyToast.showToast(CaigouBaoguanActivity.this, "请选择正确的付款公司名称");
                    return;
                }
                if ("".equals(providerID)) {
                    MyToast.showToast(CaigouBaoguanActivity.this, "请选择正确的供应商名称");
                    return;
                }
                double amount = 0;
                //修改后的值
                baoguan.setObjBGCompare(bgCompID);
                baoguan.setObjBGCompareName(bgCompName);
                baoguan.setObjname(loginID);
                baoguan.setObjvalue(nName);
                baoguan.setObjYProvider(providerSrc);
                baoguan.setObjNProvider(newProvider);
                baoguan.setObjPayID(payID);
                baoguan.setObjPayName(payCompName);
                baoguan.setObjXDID(xdId);
                baoguan.setObjXDName(orderComp);
                //美金价格修改值
                for (BaoguanDetail d : listData) {
                    String price = d.getCost();
                    String counts = d.counts;
                    double p = 0;
                    d.setPayComID(payID);
                    d.setPayComName(payCompName);
                    d.setCustomsCompanyID(bgCompID);
                    d.setProviderID(providerID);
                    d.setInvoiceCorp(xdId);
                    try {
                        p = Double.parseDouble(price);
                        int intCounts = Integer.parseInt(counts);
                        amount += p * intCounts;
                    } catch (Exception e) {
                        MyToast.showToast(CaigouBaoguanActivity.this, String.format("型号：%s,单价或者数量有误,单价[%s],数量[%s]", d.parno,
                                price,
                                counts));
                        return;
                    }
                }
                baoguan.setObjAmount(amount + "");
                Runnable ok = new Runnable() {
                    @Override
                    public void run() {
                        String Json = null;
                        try {
                            Json = JsonParser.parseBaoguanToJson(baoguan);
                            Log.e("zjy", "CaigouBaoguanActivity->run(): commitJson==" + Json);
                            String data = getData(pid, Json);
                            if ("报关成功".equals(data)) {
                                mHandler.obtainMessage(MSG_OK, "报关成功").sendToTarget();
                            } else {
                                sendAlertMsg("报关错误");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sendAlertMsg("报关失败:" + getResources().getString(R.string.req_error_badnetwork));
                    }
                };
                TaskManager.getInstance().execute(ok);
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GetProvider("");
        getBaoguanComp();
        getDollarRate();
        SharedPreferences sp = getSharedPreferences(SettingActivity.PREF_USERINFO, MODE_PRIVATE);
        String limitCorp = sp.getString("limitCorp", "");
        did = sp.getInt("did", -1);
        nName = sp.getString("name", "");
        // TODO: 2018/7/12 测试限定付款公司
        if (CheckUtils.isAdmin()) {
            limitCorp = "29,30";
        }
        getPayComp(limitCorp);
    }

    public void sendErrorMsg(String mssage) {
        Message msg = mHandler.obtainMessage(MSG_ERROR, mssage);
        msg.sendToTarget();
    }

    public void sendAlertMsg(String mssage) {
        Message msg = mHandler.obtainMessage(MSG_Alert, mssage);
        msg.sendToTarget();
    }

    public String getIDByName(List<CaigouBaoguanInfo> list, String nName) {
        String id = "";
        if (list == null) {
            return "";
        }
        for (CaigouBaoguanInfo info : list) {
            if (nName.equals(info.getName())) {
                id = info.getId();
            }
        }
        return id;

    }

    public void init() {
        try {
            String provider = "";
            JSONObject obj = new JSONObject(detailJson);
//            Log.e("zjy", "CaigouBaoguanActivity->init(): detailJson==" + detailJson);
            final JSONArray jarray = obj.getJSONArray("表");
            JSONObject temp = jarray.getJSONObject(0);
            provider = temp.getString("供应商");
            autoNewProvider.setText(provider);
            tvSrcProvider.setText(provider);
            BaoguanDetail tempD = null;
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject tempObj = jarray.getJSONObject(i);
                BaoguanDetail detailInfo = new BaoguanDetail();
                detailInfo.parno = tempObj.getString("型号");
                detailInfo.pihao = tempObj.getString("批号");
                detailInfo.factory = tempObj.getString("厂家");
                detailInfo.description = tempObj.getString("描述");
                detailInfo.counts = tempObj.getString("数量");
                detailInfo.fengzhuang = tempObj.getString("封装");
                detailInfo.setPid(tempObj.getString("PID"));

                detailInfo.setArea(tempObj.getString("位置"));
                detailInfo.setRePrice(tempObj.getString("批复价"));
                String price = tempObj.getString("进价");
                double usPrice = Double.parseDouble(price) / rate;
                BigDecimal bPrice = new BigDecimal(usPrice).setScale(4, BigDecimal.ROUND_HALF_UP);
                detailInfo.setCost("" + bPrice);
                detailInfo.setCostRMB(price);
                detailInfo.setDeptID(tempObj.getString("DeptID"));
                detailInfo.setCorpID(tempObj.getString("CorpID"));
                detailInfo.setEmployeeID(tempObj.getString("EmployeeID"));
                detailInfo.setMakerID(tempObj.getString("MartID"));
                detailInfo.setFormDetailID(tempObj.getString("DID"));
                //等待后续赋值
                detailInfo.setProviderID(tempObj.getString("ProviderID"));
                detailInfo.setPayComID("");
                detailInfo.setPayComName("");
                detailInfo.setPartTypeID("");
                detailInfo.setPartTypeValue("");
                detailInfo.setInvoiceCorp("");
                detailInfo.setCustomsCompanyID("");
                listData.add(detailInfo);
            }
//                        for (int i = 0; i <2; i++) {
//                            JSONObject tempObj = jarray.getJSONObject(0);
//                            BaoguanDetail detailInfo = new BaoguanDetail();
//                           detailInfo.parno = tempObj.getString("型号") + i;
//                            detailInfo.pihao = tempObj.getString("批号");
//                            detailInfo.factory = tempObj.getString("厂家");
//                            detailInfo.description = tempObj.getString("描述");
//                            detailInfo.counts = tempObj.getString("数量");
//                            detailInfo.fengzhuang = tempObj.getString("封装");
//                            detailInfo.setPid(tempObj.getString("PID"));
//
//                            detailInfo.setArea(tempObj.getString("位置"));
//                            detailInfo.setRePrice(tempObj.getString("批复价"));
//                            String price = tempObj.getString("进价");
//                            double usPrice = Double.parseDouble(price) / rate;
//                            BigDecimal bPrice = new BigDecimal(usPrice).setScale(4, BigDecimal.ROUND_HALF_UP);
//                            detailInfo.setCost("" + bPrice);
//                            detailInfo.setCostRMB(price);
//                            detailInfo.setDeptID(tempObj.getString("DeptID"));
//                            detailInfo.setCorpID(tempObj.getString("CorpID"));
//                            detailInfo.setEmployeeID(tempObj.getString("EmployeeID"));
//                            detailInfo.setMakerID(tempObj.getString("MartID"));
//                            detailInfo.setFormDetailID(tempObj.getString("DID"));
//                            //等待后续赋值
//                            detailInfo.setProviderID(tempObj.getString("ProviderID"));
//                            detailInfo.setPayComID("");
//                            detailInfo.setPayComName("");
//                            detailInfo.setPartTypeID("");
//                            detailInfo.setPartTypeValue("");
//                            detailInfo.setInvoiceCorp("");
//                            detailInfo.setCustomsCompanyID("");
//                            listData.add(detailInfo);
//                        }
            baoguan.setResult(listData);
            if (dAdapter == null) {
                dAdapter = new CaigouBaoguanAdapter(CaigouBaoguanActivity.this, listData, rate);
                lv.setAdapter(dAdapter);
            } else {
                dAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDollarRate() {
        Runnable getHuilv = new Runnable() {
            @Override
            public void run() {
                String soapResult = null;
                try {
                    soapResult = MartService.GetModuleInfoByID("2");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                if (soapResult == null || soapResult.equals("")) {
                    sendErrorMsg("获取汇率为空");
                } else {
                    try {
                        rate = Double.parseDouble(soapResult);
                        Log.e("zjy", "CaigouBaoguanActivity->run(): Rate==" + rate);
                        dAdapter = new CaigouBaoguanAdapter(CaigouBaoguanActivity.this, listData, rate);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                lv.setAdapter(dAdapter);
                            }
                        });
                        if (detailJson != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    init();
                                }
                            });
                        } else {
                            getDetailInfo();
                        }
                    } catch (Exception e) {
                        sendErrorMsg("获取汇率格式异常");
                    }
                }
            }
        };
        TaskManager.getInstance().execute(getHuilv);
    }

    public void getDetailInfo() {
        Runnable runD = new Runnable() {
            @Override
            public void run() {
                try {
                    detailJson = MartService.GetSSCGInfoByDDYHList("", pid);
                    mHandler.obtainMessage(MSG_Detail);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        };
        TaskManager.getInstance().execute(runD);
    }

    public void getBaoguanComp() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    String bgComp = MartService.GetBGCompare();
                    JSONArray array = new JSONObject(bgComp).getJSONArray("表");
                    List<CaigouBaoguanInfo> tempList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String id = obj.getString("id");
                        String name = obj.getString("name");
                        tempList.add(new CaigouBaoguanInfo(id, name));
                    }
                    Message msg = mHandler.obtainMessage(MSG_GetBaoguan, tempList);
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        TaskManager.getInstance().execute(run);
    }

    public void getPayComp(String limitCorp) {
        Runnable mRun = new Runnable() {
            @Override
            public void run() {
                try {
                    String bgComp = MartService.GetGNPayCompare(limitCorp);
                    String bgGwComp = MartService.GetGWPayCompare(limitCorp);
                    List<CaigouBaoguanInfo> tempList = new ArrayList<>();
                    addPayCopare(tempList, bgComp);
                    addPayCopare(tempList, bgGwComp);
                    Message msg = mHandler.obtainMessage(MSG_GetPay, tempList);
                    mHandler.sendMessage(msg);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        TaskManager.getInstance().execute(mRun);
    }

    public void addPayCopare(List<CaigouBaoguanInfo> tempList, String jsonStr) {
        try {
            JSONArray array = new JSONObject(jsonStr).getJSONArray("表");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String id = obj.getString("objid");
                String name = obj.getString("objname");
                tempList.add(new CaigouBaoguanInfo(id, name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getData(String pid, String json) throws IOException, XmlPullParserException {
        String result = MartService.SetGuoHuoTongZhiApplyCustom(pid, json);
        Log.e("zjy", "CaigouBaoguanActivity->getData(): Result==" + result);
        return result;
    }

    public void GetProvider(String provideName) {
        if (loginID == null) {
            return;
        }
        if ("101".equals(loginID)) {
            did = 100;
        }
        Runnable run = new Runnable() {
            @Override
            public void run() {
                List<CaigouBaoguanInfo> data = new ArrayList<>();
                try {
                    String result = MartService.GetMyProvider("", Integer.parseInt(loginID), did, provideName);
                    String tag = "SUCCESS";
                    int tagIndex = result.indexOf(tag);
                    if (tagIndex != -1) {
                        String content = result.substring(tag.length());
                        if (!"".equals(content)) {
                            String[] dataArray = content.split(",");
                            for (String s : dataArray) {
                                if (s.length() >= 2) {
                                    String id = s.split("-")[0];
                                    String name = s.split("-")[1];
                                    data.add(new CaigouBaoguanInfo(id, name));
                                }
                            }
                        }
                    }
                    mHandler.obtainMessage(MSG_GetProvider, data).sendToTarget();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        TaskManager.getInstance().execute(run);
    }
}
