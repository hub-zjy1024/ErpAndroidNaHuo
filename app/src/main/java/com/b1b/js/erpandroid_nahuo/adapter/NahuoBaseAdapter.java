package com.b1b.js.erpandroid_nahuo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by 张建宇 on 2017/10/17.
 */

public abstract class NahuoBaseAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected int itemLaoutID;
    protected List<T> data;

    public NahuoBaseAdapter(Context mContext, int itemLaoutID, List<T> data) {
        this.mContext = mContext;
        this.itemLaoutID = itemLaoutID;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        convert(viewHolder, getItem(position));
        return viewHolder.getConvertView();
    }

    public abstract void convert(ViewHolder helper, T item);

    private ViewHolder getViewHolder(int position, View convertView,
                                     ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, itemLaoutID,
                position);
    }

}
