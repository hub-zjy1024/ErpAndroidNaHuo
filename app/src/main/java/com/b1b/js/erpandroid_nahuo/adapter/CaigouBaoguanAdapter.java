package com.b1b.js.erpandroid_nahuo.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.entity.BaoguanDetail;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zhy.utils.CommonAdapter;
import zhy.utils.ViewHolder;

/**
 Created by 张建宇 on 2018/7/11. */
public class CaigouBaoguanAdapter extends CommonAdapter<BaoguanDetail> {
    private double rate;
    private int pos;
    private Map<Integer, MChangeListner> map = new HashMap<>();

    public CaigouBaoguanAdapter(Context context, List<BaoguanDetail> mDatas, double rate) {
        super(context, mDatas, R.layout.item_baoguan_detail);
        this.rate = rate;
    }

    static class MChangeListner implements TextWatcher {
        TextView tvHeji;

        public MChangeListner(TextView tvHeji, BaoguanDetail tempBd) {
            this.tvHeji = tvHeji;
            this.tempBd = tempBd;
        }

        public MChangeListner(BaoguanDetail tempBd) {
            this.tempBd = tempBd;
        }

        private BaoguanDetail tempBd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                tempBd.setCost(s.toString());
                tvHeji.setText(getShowTotal(s.toString(), tempBd));
            }
        }
        public String getShowTotal(String usPrice, BaoguanDetail tempBd) {
            double dollar = Double.parseDouble(usPrice);
            int counts = Integer.parseInt(tempBd.counts);
            BigDecimal bPrice = new BigDecimal(dollar * counts).setScale(4, BigDecimal.ROUND_HALF_UP);
            String result = "合计:" + bPrice + "$";
            return result;
        }
    }



    @Override
    public void convert(ViewHolder helper, BaoguanDetail item) {
        TextView tvID = helper.getView(R.id.item_baoguan_tv_id);
        TextView partNo = helper.getView(R.id.item_baoguan_tv_partno);
        TextView tvCounts = helper.getView(R.id.item_baoguan_tv_counts);
        TextView tvPrice = helper.getView(R.id.item_baoguan_tv_sigleprice);
        TextView tvHeji = helper.getView(R.id.item_baoguan_tv_dollar_now);
        tvPrice.setText(item.getCostRMB());
        Log.e("zjy", "CaigouBaoguanAdapter->convert(): Rmb==" + item.getCostRMB());
        tvCounts.setText(item.counts);
        partNo.setText(item.parno);
        tvID.setText("" + helper.getPosition());
        EditText edDollar = helper.getView(R.id.item_baoguan_ed_dollar);

        View convertView = helper.getConvertView();
        MChangeListner listner = (MChangeListner) convertView.getTag(R.string.tag_edit);
        if (listner == null) {
            listner = new MChangeListner(tvHeji, item);
            convertView.setTag(R.string.tag_edit, listner);
        } else {
            edDollar.removeTextChangedListener(listner);
            listner.tempBd = item;
            listner.tvHeji = tvHeji;
        }
        tvHeji.setText(listner.getShowTotal(item.getCost(), item));
        edDollar.setText(item.getCost());
        edDollar.addTextChangedListener(listner);
        //        edDollar.setOnTouchListener(new View.OnTouchListener() {
        //
        //            public boolean onTouch(View view, MotionEvent event) {
        //
        //                if (event.getAction() == MotionEvent.ACTION_UP) {
        //                    pos = helper.getPosition();
        //                }
        //                return false;
        //            }
        //        });
        //
        //        edDollar.addTextChangedListener(new TextWatcher() {
        //            @Override
        //            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
        //            }
        //
        //            @Override
        //            public void onTextChanged(CharSequence s, int start, int before, int count) {
        //
        //            }
        //
        //            @Override
        //            public void afterTextChanged(Editable s) {
        //                Log.e("zjy", "CaigouBaoguanAdapter->afterTextChanged(): ==" + s.toString());
        //                //                if (pos == helper.getPosition()) {
        //                //                    item.setCost(edDollar.getText().toString());
        //                //                }
        //                //                pos = helper.getPosition();
        //                if (pos >= 0 && pos == helper.getPosition()) {
        //                    item.setCost(s.toString());
        //                }
        //            }
        //        });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
