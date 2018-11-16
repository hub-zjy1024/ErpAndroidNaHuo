package com.b1b.js.erpandroid_nahuo.adapter;

import android.content.Context;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.entity.BaoguanDetail;

import java.util.List;

/**
 Created by 张建宇 on 2018/7/25. */
public class YHDetailAdapter2 extends NahuoBaseAdapter<BaoguanDetail> {

    public YHDetailAdapter2(Context mContext, int itemLaoutID, List<BaoguanDetail> data) {
        super(mContext, itemLaoutID, data);
    }

    @Override
    public void convert(ViewHolder helper, BaoguanDetail item) {
        //        helper.setText(R.id.item_yhdetail_tv, item.toString());
        helper.setText(R.id.item_yhdetail_tv, "型号:" + item.parno);
        helper.setText(R.id.item_yhdetail_pihao, "批号:" + item.pihao);
        helper.setText(R.id.item_yhdetail_counts, "数量:" + item.counts);
        helper.setText(R.id.item_yhdetail_fengzhuang, "封装:" + item.fengzhuang);
        helper.setText(R.id.item_yhdetail_factory, "厂家:" + item.factory);
        helper.setText(R.id.item_yhdetail_description, "描述:" + item.description);
        helper.setText(R.id.item_yhdetail_inprice, "进价:" + item.getCostRMB());
        helper.setText(R.id.item_yhdetail_area, "产地:" + item.getArea());

    }
}
