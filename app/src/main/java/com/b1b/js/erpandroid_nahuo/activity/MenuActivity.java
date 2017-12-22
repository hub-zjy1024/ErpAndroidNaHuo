package com.b1b.js.erpandroid_nahuo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.services.LogUploadService;
import com.b1b.js.erpandroid_nahuo.services.NahuoPushService;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuActivity extends AppCompatActivity {

    private final String itemYanhuo = "等待验货";
    private final String tag_QualityCheck = "质检中心";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
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
                        intent.setClass(MenuActivity.this, NahuoListAcitivity.class);
                        startActivity(intent);
                        MyApp.myLogger.writeInfo("<page> nahuo");
                        break;
                    case itemYanhuo:
                        intent.setClass(MenuActivity.this, CaigouYanhuoActivity.class);
                        startActivity(intent);
                        MyApp.myLogger.writeInfo("<page> yanhuo");
                        break;
                    case tag_QualityCheck:
                        intent.setClass(MenuActivity.this, QualityCheckActivity.class);
                        startActivity(intent);
                        MyApp.myLogger.writeInfo("<page> QualityCheck");
                        break;
                }
            }
        });
        Intent intent = new Intent(this, NahuoPushService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, LogUploadService.class));
    }
}
