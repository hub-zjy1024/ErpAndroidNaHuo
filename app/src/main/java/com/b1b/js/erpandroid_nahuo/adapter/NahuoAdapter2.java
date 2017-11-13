package com.b1b.js.erpandroid_nahuo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.entity.NahuoInfoN;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 张建宇 on 2017/10/17.
 */

public class NahuoAdapter2 extends NahuoBaseAdapter<NahuoInfoN> {

    public NahuoAdapter2(Context mContext, int itemLaoutID, List<NahuoInfoN> data) {
        super(mContext, itemLaoutID, data);
    }

    private Set<String> selectPids = new HashSet<>();
    private List<Boolean> checkedList = new ArrayList<>();
    public boolean showCheckBox = false;

    public interface BtnOkOnClickListner {
        void onClick(ViewHolder helper, NahuoInfoN item);
    }

    private BtnOkOnClickListner listner;

    public NahuoAdapter2(Context mContext, int itemLaoutID, List<NahuoInfoN> data, BtnOkOnClickListner
            listner, BtnOkOnClickListner upListner) {
        super(mContext, itemLaoutID, data);
        this.listner = listner;
        this.upListner = upListner;
    }

    private BtnOkOnClickListner upListner;


    @Override
    public void convert(final ViewHolder helper, final NahuoInfoN item) {
        helper.setText(R.id.item_nahuo_tv, item.toString());
        TextView tv = helper.getView(R.id.item_nahuo_tv);

        Button btnOk = helper.getView(R.id.item_nahuo_btn_ok);
        Button btnUpload = helper.getView(R.id.item_nahuo_btn_uploadpic);
        if ("".equals(item.getDetailInfo())) {
            btnOk.setVisibility(View.VISIBLE);
        } else {
            btnOk.setVisibility(View.GONE);
        }
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onClick(helper, item);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upListner.onClick(helper, item);
            }
        });

        final CheckBox checkBOx = helper.getView(R.id.item_nahuo_checkbox);
        if (showCheckBox) {
            checkBOx.setVisibility(View.VISIBLE);
        } else {
            checkBOx.setVisibility(View.GONE);
            item.setChecked(false);
        }
        boolean b = item.isChecked();
        checkBOx.setChecked(b);
        checkBOx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBOx.isChecked()) {
                    item.setChecked(true);
                    selectPids.add(item.getPid());
                } else {
                    item.setChecked(false);
                    selectPids.remove(item.getPid());
                }
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setChecked(!item.isChecked());
                checkBOx.setChecked(item.isChecked());
            }
        });
    }

    public Set<String> getSelectList() {
        return selectPids;
    }
}
