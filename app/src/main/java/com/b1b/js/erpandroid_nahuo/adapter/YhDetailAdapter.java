package com.b1b.js.erpandroid_nahuo.adapter;

import android.content.Context;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.entity.Baseinfo;

import java.util.List;

/**
 * Created by 张建宇 on 2017/11/20.
 */

public class YhDetailAdapter extends NahuoBaseAdapter<Baseinfo> {

    public YhDetailAdapter(Context mContext, int itemLaoutID, List<Baseinfo> data) {
        super(mContext, itemLaoutID, data);
    }

    @Override
    public void convert(ViewHolder helper, Baseinfo item) {
//        helper.setText(R.id.item_yhdetail_tv, item.toString());
        helper.setText(R.id.item_yhdetail_tv, "型号:" + item.parno);
        helper.setText(R.id.item_yhdetail_pihao, "批号:" + item.pihao);
        helper.setText(R.id.item_yhdetail_counts, "数量:" +item.counts);
        helper.setText(R.id.item_yhdetail_fengzhuang,"封装:" + item.fengzhuang);
        helper.setText(R.id.item_yhdetail_factory,"厂家:" + item.factory);
        helper.setText(R.id.item_yhdetail_description, "描述:" +item.description);
        helper.setText(R.id.item_yhdetail_note,"备注:" + item.notes);

    }
}
