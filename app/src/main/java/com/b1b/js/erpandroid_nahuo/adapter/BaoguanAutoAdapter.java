package com.b1b.js.erpandroid_nahuo.adapter;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.entity.CaigouBaoguanInfo;

import java.util.ArrayList;
import java.util.List;

import zhy.utils.CommonAdapter;
import zhy.utils.ViewHolder;

/**
 Created by 张建宇 on 2018/7/16. */
public class BaoguanAutoAdapter extends CommonAdapter<CaigouBaoguanInfo> implements Filterable, ListAdapter {
    public BaoguanAutoAdapter(Context context, List<CaigouBaoguanInfo> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    public BaoguanAutoAdapter(Context context, List<CaigouBaoguanInfo> mDatas) {
        this(context, mDatas, R.layout.item_comons_tv);
    }

    @Override
    public void convert(ViewHolder helper, CaigouBaoguanInfo item) {
        helper.setText(R.id.item_common_tv, item.getName());
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    Filter mFilter;
    private List<CaigouBaoguanInfo> mOriginalValues;

    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            final FilterResults results = new FilterResults();
            if (mOriginalValues == null) {
                synchronized (this) {
                    mOriginalValues = new ArrayList<>(mDatas);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                final ArrayList<CaigouBaoguanInfo> list;
                synchronized (this) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                final ArrayList<CaigouBaoguanInfo> values;
                synchronized (this) {
                    values = new ArrayList<>(mOriginalValues);
                }
                final int count = values.size();
                final ArrayList<CaigouBaoguanInfo> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final CaigouBaoguanInfo value = values.get(i);
                    final String valueText = value.toString().toLowerCase();
                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
