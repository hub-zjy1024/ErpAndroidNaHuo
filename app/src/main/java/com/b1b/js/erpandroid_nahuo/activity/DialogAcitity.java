package com.b1b.js.erpandroid_nahuo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.b1b.js.erpandroid_nahuo.R;

public class DialogAcitity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_acitity);
        layout = (LinearLayout) findViewById(R.id.exit_layout);
        TextView tv = (TextView) findViewById(R.id.activity_dialog_tvinfo);
        Intent intent = getIntent();
        String info = intent.getStringExtra("content");
        if (info != null) {
            tv.setText(info);
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }

        });

    }

    private LinearLayout layout;

    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;

    }

    public void exitbutton1(View v) {
        this.finish();

    }


    public void exitbutton0(View v) {
        this.finish();
    }

}
