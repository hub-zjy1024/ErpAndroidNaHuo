package com.b1b.js.erpandroid_nahuo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.activity.base.SavedLoginInfoActivity;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.services.LogUploadService2;
import com.b1b.js.erpandroid_nahuo.services.NahuoPushService;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import utils.RoomSizeUtils;

public class MenuActivity extends SavedLoginInfoActivity {

    public static final String itemYanhuo = "等待验货";
    public static final String tag_QualityCheck = "质检中心";
    private final String tag_Baoguan = "采购单报关";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar tb = (Toolbar) findViewById(R.id.dyjkf_normalTb);
        tb.setTitle("菜单");
        tb.setSubtitle("登录人:" + MyApp.id);
        setSupportActionBar(tb);
        ListView lv = ((ListView) findViewById(R.id.nahuo_menu_lv));
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("title", itemYanhuo);
        map.put("img", R.drawable.menu_icon_nahuo);
        map.put("description", "等待处理的拿货列表");
        list.add(map);
        map = new HashMap<>();
        map.put("title", tag_QualityCheck);
        map.put("img", R.drawable.menu_icon_quality_check);
        map.put("description", "等待质检");
        list.add(map);
        //        map = new HashMap<>();
        //        map.put("title", tag_Baoguan);
        //        map.put("img", R.drawable.menu_icon_quality_check);
        //        map.put("description", "等待质检");
        //        list.add(map);
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.item_nahuomenu_lv,
                new String[]{"title", "img", "description"}, new int[]{R.id
                .item_menu_nahuo_tv, R.id.item_nahuo_iv, R.id.item_nahuo_tv_description});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String
                    textRepresentation) {
                if (view instanceof TextView) {
                    TextView tv = (TextView) view;
                    tv.setText(data.toString());
                    return true;
                } else if (view instanceof ImageView) {
                    int resID = (int) data;
                    ImageView iv = (ImageView) view;
                    iv.setImageResource(resID);
                    return true;
                }
                return false;
            }
        });
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> map = (HashMap<String, Object>) parent
                        .getItemAtPosition(position);
                String value = (String) map.get("title");
                Intent intent = new Intent();
                switch (value) {
                    case "拿货":
                        intent.setClass(mContext, NahuoListAcitivity.class);
                        startActivity(intent);
                        MyApp.myLogger.writeInfo("<page> nahuo");
                        break;
                    case itemYanhuo:
                        intent.setClass(mContext, CaigouYanhuoActivity.class);
                        startActivity(intent);
                        MyApp.myLogger.writeInfo("<page> yanhuo");
                        break;
                    case tag_QualityCheck:
                        intent.setClass(mContext, QualityCheckActivity.class);
                        startActivity(intent);
                        MyApp.myLogger.writeInfo("<page> QualityCheck");
                        break;
                    case tag_Baoguan:
                        intent.setClass(mContext, CaigouBaoguanActivity.class);
                        startActivity(intent);
                        MyApp.myLogger.writeInfo("<page> baoguan");
                        break;
                }
            }
        });
        Intent intent = new Intent(this, NahuoPushService.class);
        startService(intent);
        MyApp.myLogger.writeInfo("login:" + MyApp.id);
        SharedPreferences tempsp = getSharedPreferences("temp_check", MODE_PRIVATE);
        String hasWrite = tempsp.getString("hasWrite", "");
        if (hasWrite.equals("")) {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            RoomSizeUtils sizeUtils = new RoomSizeUtils(this);
            String rootDirectoryTotal = sizeUtils.getSizeByPath(externalStoragePublicDirectory, 0);
            String rootDirectoryAv = sizeUtils.getSizeByPath(externalStoragePublicDirectory, 1);
            Log.e("zjy", "MenuActivity->onCreate(): totalSize==" + externalStoragePublicDirectory.getAbsolutePath() + "\t" + rootDirectoryAv +
                    "/" + rootDirectoryTotal);
            MyApp.myLogger.writeInfo(getClass(), "SD_Size:" + rootDirectoryAv + "/" + rootDirectoryTotal);
            File newFile = new File(externalStoragePublicDirectory, "Camera/");
            String[] jpegs = newFile.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("jpeg") || name.endsWith("png") || name.endsWith("jpg");
                }
            });
            if (jpegs.length > 0) {
                Log.e("zjy", "MenuActivity->onCreate(): PicSize==" + jpegs.length + "\tpic1==" + jpegs[0]);
                MyApp.myLogger.writeInfo("nowSD:" + jpegs.length + "\tpic1==" + jpegs[0]);
            } else {
                File[] files = externalStoragePublicDirectory.listFiles();
                String tenmps = "";
                for (File s : files) {
                    tenmps += s.getName() + "\n";
                }
                Log.e("zjy", "MenuActivity->onCreate():CameraPath==null:" + newFile.getAbsolutePath());
                MyApp.myLogger.writeInfo("CameraPath==null:" + newFile.getAbsolutePath() + "\nFiles:" + tenmps);
            }
            tempsp.edit().putString("hasWrite", "yes").apply();
        }
    }

    public void printFile(RoomSizeUtils sizeUtils, File rootDirectory) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, LogUploadService2.class));
    }
}
