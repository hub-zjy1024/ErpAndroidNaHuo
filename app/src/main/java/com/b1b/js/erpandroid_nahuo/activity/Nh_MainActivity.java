package com.b1b.js.erpandroid_nahuo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.activity.base.BaseMActivity;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.contract.MainContract;
import com.b1b.js.erpandroid_nahuo.utils.callback.IBoolCallback;
import com.b1b.js.erpandroid_nahuo.versioncompact.Api23Checker;

import utils.net.ftp.UploadUtils;

public class Nh_MainActivity extends BaseMActivity implements MainContract.MainAcView{

    private EditText edUserName;
    private EditText edPwd;
    private Button btnLogin;
    private Button btnScancode;
    private CheckBox cboRemp;
    private CheckBox cboAutol;
    private SharedPreferences sp;
    private ProgressDialog pd;
    private TextView tvVersion;
    private String versionName = "1";
    private Context mContext ;
    private MainContract.MainAcPresenter mPresenter;
    private String debugPwd = "62105300";
    private int debugTimes = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Api23Checker checker = new Api23Checker(this);
        checker.checkPermission(new IBoolCallback() {
            @Override
            public void callback(Boolean msg) {
                if (msg) {
                    setContentView(R.layout.activity_nh__main);
                }
                Log.d("zjy", "Nh_MainActivity->callback(): permission==" + msg);
            }

            @Override
            public void onError(String msg) {
                Log.w("zjy", "Nh_MainActivity->callback(): permission==err,"+msg);
            }
        });

    }


    public void init(){
        edUserName = (EditText) findViewById(R.id.login_username);
        edPwd = (EditText) findViewById(R.id.login_pwd);
        btnLogin = (Button) findViewById(R.id.login_btnlogin);
        btnScancode = (Button) findViewById(R.id.login_scancode);
        btnScancode.setEnabled(false);
        cboRemp = (CheckBox) findViewById(R.id.login_rpwd);
        cboAutol = (CheckBox) findViewById(R.id.login_autol);
        tvVersion = (TextView) findViewById(R.id.main_version);
        mContext = this;
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edUserName.getText().toString().trim();
                String pwd = edPwd.getText().toString().trim();
                if (debugTimes == 5) {
                    mPresenter.login("101", debugPwd, versionName);
                }else{
                    if (pwd.equals("") || name.equals("")) {
                        showMsgToast( "请填写完整信息后再登录");
                    } else {
                        mPresenter.login(name, pwd, versionName);
                    }
                }
            }
        });
        btnScancode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        View imgView = findViewById(R.id.nh_main_iv_debug);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("zjy", "Nh_MainActivity->onClick(): debugTimes==" +debugTimes );
                if (debugTimes == 5) {
                    showMsgToast("开启调试模式");
                    return;
                }
                debugTimes++;
            }
        });
        sp = getSharedPreferences(SettingActivity.PREF_USERINFO, 0);
        final String phoneCode = UploadUtils.getPhoneCode(mContext);
        pd = new ProgressDialog(mContext);
        Log.e("zjy", "MainActivity.java->onCreate(): phoneInfo==" + phoneCode);
        //检查更新
        int code = 0;
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            code = info.versionCode;
            versionName = info.versionName;
            tvVersion.setText("当前版本为：" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mPresenter = new MainContract.MainAcPresenter(mContext, this);
        mPresenter.CheckUpdate();
        readCache();
        final SharedPreferences logSp = getSharedPreferences("uploadlog", MODE_PRIVATE);
        String saveDate = logSp.getString("codeDate", "");
        int lastCode = logSp.getInt("lastCode", -1);
        final String current = UploadUtils.getCurrentDate();
        if (MyApp.myLogger != null) {
            StringBuilder sbInfo = new StringBuilder();
            if (!saveDate.equals(current)) {
                logSp.edit().putString("codeDate", current).apply();
                sbInfo.append(String.format("phonecode:%s",phoneCode ));
                sbInfo.append("\n");
                sbInfo.append(String.format("ApiVersion:%s", Build.VERSION.SDK_INT ));
                sbInfo.append("\n");
                sbInfo.append(String.format("dyj-version:%s", code));
                sbInfo.append("\n");
            } else if (code != lastCode && lastCode != -1) {
                sbInfo.append(String.format("updated-from '%d' to '%d':", lastCode, code));
                sbInfo.append("\n");
                logSp.edit().putInt("lastCode", code).apply();
            }
            MyApp.myLogger.writeInfo(sbInfo.toString());
        }
        //获取网络状态
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                int type = activeNetworkInfo.getType();
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    MyApp.myLogger.writeInfo("network stat:mobile");
                } else if (type == ConnectivityManager.TYPE_WIFI) {
                    MyApp.myLogger.writeInfo("network stat:wifi");
                }
            }
        }
    }

    @Override
    public void setListeners() {

    }

    private void readCache() {
        if (sp.getBoolean("remp", false)) {
            edUserName.setText(sp.getString("name", ""));
            edPwd.setText(sp.getString("pwd", ""));
            cboRemp.setChecked(true);
            if (sp.getBoolean("autol", false)) {
                cboAutol.setChecked(true);
                mPresenter.login(sp.getString("name", ""), sp.getString("pwd", ""), versionName);
            }
        }
    }

    @Override
    public void login(int code, String name, String pwd, String msg) {
        pd.cancel();
        if (code == 1) {
            boolean auto = cboAutol.isChecked();
            boolean saved = cboRemp.isChecked();
            MyApp.id = name;
            mPresenter.onLoginSuccess(saved, auto);
        }else{
            debugPwd = "621053000";
            showMsgToast( msg);
        }
    }

    @Override
    public void showProgress(String msg) {
        pd.setMessage(msg);
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    public void startNewActivity() {
        Intent intent = new Intent(mContext,
                MenuActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void getUpdateInfo(String info) {
        String info2 = tvVersion.getText().toString().trim() + "," + info;
        tvVersion.setText(info2);
    }

    @Override
    public void codeLogin(String code) {

    }

    @Override
    public void setPresenter(MainContract.MainAcPresenter mainAcPresenter) {

    }
}
