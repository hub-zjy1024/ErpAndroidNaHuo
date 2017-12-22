package com.b1b.js.erpandroid_nahuo.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.entity.YanhuoInfo;

import java.util.List;

import utils.StringFormatUtils;

/**
 * Created by 张建宇 on 2017/8/25.
 */

public class YanhuoAdapter extends MyBaseAdapter<YanhuoInfo> {

    public YanhuoAdapter(List<YanhuoInfo> data, Context mContext, int itemViewId) {
        super(data, mContext, itemViewId);
    }

    @Override
    protected void findChildViews(View convertView, MyBasedHolder baseHolder) {
        CaigouYanhuoHoloder holder = (CaigouYanhuoHoloder) baseHolder;
        holder.tv =(TextView) convertView.findViewById(R.id
                .activity_caigouyanhuo_item_tv);
        holder.tvCaigouPlace =(TextView) convertView.findViewById(R.id
                .activity_caigouyanhuo_item_caigou);
        holder.tvState =(TextView) convertView.findViewById(R.id
                .activity_caigouyanhuo_item_state);
        holder.tvCompany =(TextView) convertView.findViewById(R.id
                .activity_caigouyanhuo_item_company);
        holder.tvDept =(TextView) convertView.findViewById(R.id
                .activity_caigouyanhuo_item_dept);
        holder.tvUName =(TextView) convertView.findViewById(R.id
                .activity_caigouyanhuo_item_uid);
        holder.tvMore = (TextView) convertView.findViewById(R.id
                .activity_caigouyanhuo_item_alert);
        holder.tvpartnos = (TextView) convertView.findViewById(R.id
                .activity_caigouyanhuo_item_partnos);
    }

    @Override
    protected void onBindData(YanhuoInfo currentData, MyBasedHolder baseHolder) {
        CaigouYanhuoHoloder holder = (CaigouYanhuoHoloder) baseHolder;
        holder.tv.setText("pid=" + currentData.getPid() + "  " + currentData.getPidDate());
        holder.tvCaigouPlace.setText(StringFormatUtils.getStringAtLen("采购地:" + currentData.getCaigouAddress()));
        holder.tvDept.setText(StringFormatUtils.getStringAtLen("部门:" + currentData.getDeptName()));
        holder.tvCompany.setText("公司:" + currentData.getCompany());
        holder.tvUName.setText("员工:" +currentData.getSaleMan());
        holder.tvState.setText("状态:" +currentData.getPidState());
        holder.tvMore.setText("长按查看更多信息……");
        String partnos = currentData.detail;
        if (partnos.equals("")) {
            holder.tvpartnos.setText(partnos);
        } else {
            String[] ps = partnos.split("\\$");
            if (ps.length == 2) {
                holder.tvpartnos.setText("型号:" + ps[0]);
            } else {
                MyApp.myLogger.writeBug("YanhuoAdapter:" + partnos);
            }
        }
    }

    @Override
    protected MyBasedHolder getCustomHolder() {
        return new CaigouYanhuoHoloder();
    }

    public class CaigouYanhuoHoloder extends MyBasedHolder{
        TextView tv;
        TextView tvCaigouPlace;
        TextView tvState;
        TextView tvDept;
        TextView tvUName;
        TextView tvCompany;
        TextView tvMore;
        TextView tvpartnos;
    }
}
