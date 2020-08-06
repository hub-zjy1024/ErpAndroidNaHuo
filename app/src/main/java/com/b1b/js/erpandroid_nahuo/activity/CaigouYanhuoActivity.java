package com.b1b.js.erpandroid_nahuo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.adapter.YanhuoAdapter;
import com.b1b.js.erpandroid_nahuo.entity.YanhuoInfo;

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
import utils.wsdelegate.MartService;

/**
 * 等待验货列表
 *  * {@link MenuActivity#itemYanhuo}
 */
public class CaigouYanhuoActivity extends BaseScanActivity {

    private Handler mHandler = new Handler();
    private ListView lv;
    private EditText edpid;
    private YanhuoAdapter mAdapter;
    private List<YanhuoInfo> yanhuoInfos;
    private ProgressDialog pd;
    private Context mContext = this;
    private EditText edpartno;
    private AlertDialog infoDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SoftKeyboardUtils.hideKeyBoard(this);
        setContentView(R.layout.activity_caigou_yanhuo);
        String mTitle = getResString(R.string.title_ddyanhuo);
        Toolbar tb = (Toolbar) findViewById(R.id.yanhuoTitle);
        tb.setTitle(mTitle);
        tb.setSubtitle("");
        setSupportActionBar(tb);
        edpid = (EditText) findViewById(R.id.activity_caigou_yanhuo_edpid);
        edpid.requestFocus();
        edpartno = (EditText) findViewById(R.id
                .activity_caigou_yanhuo_edpartno);
        lv = (ListView) findViewById(R.id.activity_caigou_yanhuo_lv);
        yanhuoInfos = new ArrayList<>();
        pd = new ProgressDialog(this);
        pd.setTitle("提示");
        pd.setMessage("正在搜索");
        infoDialog = DialogUtils.createAlertDialog(this, "这是");
//        mAdapter = new YanhuoAdapter(yanhuoInfos, mContext, R.layout.activity_caigouyanhuo_item);
        mAdapter = new YanhuoAdapter(yanhuoInfos, mContext, R.layout.activity_caigouyanhuo_item_table);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, YanhuoCheckActivity.class);
                if (yanhuoInfos.size() > position) {
                    YanhuoInfo info = yanhuoInfos.get(position);
                    intent.putExtra("pid", info.getPid());
                    intent.putExtra("flag", "caigou");
                    intent.putExtra("detail", info.detail);
                    startActivity(intent);
                }
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                YanhuoInfo item = (YanhuoInfo) parent.getItemAtPosition(position);
                infoDialog.setTitle("详情："+item.getPid());
                infoDialog.setMessage(item.toString());
                DialogUtils.safeShowDialog(CaigouYanhuoActivity.this, infoDialog);
                return true;
            }
        });
        Button btnSearch = (Button) findViewById(R.id.activity_caigou_yanhuo_btn_search);
        Button btnScan = (Button) findViewById(R.id.activity_caigou_yanhuo_btn_scan);
        btnSearch.setFocusable(true);
        btnSearch.requestFocus();
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanActivity();
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pid = edpid.getText().toString();
                String partno = edpartno.getText().toString();
                SoftKeyboardUtils.closeInputMethod(edpid, mContext);
                if (yanhuoInfos.size() > 0) {
                    yanhuoInfos.clear();
                    mAdapter.notifyDataSetChanged();
                }
                getDate(pid, partno);
            }
        });
    }

    @Override
    public String getToobarTittle() {
        return getResString(R.string.title_ddyanhuo);
    }

    @Override
    public void init() {

    }

    @Override
    public void setListeners() {

    }

    @Override
    public void resultBack(String result) {
        edpid.setText(result);
        String pid =result;
        String partno = edpartno.getText().toString();
        SoftKeyboardUtils.closeInputMethod(edpid, mContext);
        if (yanhuoInfos.size() > 0) {
            yanhuoInfos.clear();
            mAdapter.notifyDataSetChanged();
        }
        getDate(pid, partno);
    }

    public void getDate(final String pid, final String partno) {
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    String result = getResponse(partno, pid);
//                    Log.e("zjy", "CaigouYanhuoActivity->run(): CaigouYanhuoInfo==" + result);
                    JSONObject object = new JSONObject(result);
                    JSONArray jArray = object.getJSONArray("表");
                    for(int i=0;i<jArray.length();i++) {
                        JSONObject tObj = jArray.getJSONObject(i);
                        String pid = tObj.getString("PID");
                        String caigouPlace = tObj.getString("采购地");
                        String pidDate = tObj.getString("制单日期");
                        String company = tObj.getString("公司");
                        String deptName = tObj.getString("部门");
                        String saleMan = tObj.getString("业务员");
                        String pidState= tObj.getString("单据状态");
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
                        if (jArray.length() == 1) {
                            String detailInfo = YanhuoCheckActivity.getDetailInfo("", pid);
                            Log.e("zjy", "CaigouYanhuoActivity->run(): DetailInfo==" + detailInfo);
                            try {
                                JSONObject dObje = new JSONObject(detailInfo);
                                String buider = "";
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
                        yanhuoInfos.add(yhInfo);
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            pd.cancel();
                        }
                    });
                } catch (IOException e) {
                    mHandler.post(new Runnable() {
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
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showToast(mContext, "查询条件有误");
                            pd.cancel();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }.start();
    }

    ;

    @Override
    public void getCameraScanResult(String result, int code) {
        super.getCameraScanResult(result, code);
        if (code == REQ_CODE) {
            final String pid = result;
            edpid.setText(pid);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (yanhuoInfos.size() > 0) {
            yanhuoInfos.clear();
            mAdapter.notifyDataSetChanged();
        }
        String pid = edpid.getText().toString();
        String partno = edpartno.getText().toString();
        if (!pid.equals("")) {
            getDate(pid, partno);
        }

    }

    public static String getResponse(String partno, String pid) throws IOException,
            XmlPullParserException {
        return MartService.GetSSCGInfoByDDYH(partno, pid);
    }
}
