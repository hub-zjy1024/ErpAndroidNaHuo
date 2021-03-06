package com.b1b.js.erpandroid_nahuo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.entity.FTPImgInfo;

import java.util.LinkedHashMap;
import java.util.List;

import utils.image.MyImageUtls;


/**
 Created by 张建宇 on 2017/3/2. */

public class ViewPicAdapter extends MyViewPicBaseAdapter<FTPImgInfo> {

    private LinkedHashMap<String, Bitmap> bitmaps = new LinkedHashMap<>();

    public ViewPicAdapter(List<FTPImgInfo> data, Context mContext, int itemViewId) {
        super(data, mContext, itemViewId);
    }

    @Override
    protected void initItems(View convertView, MyBasedHolder baseHolder) {
        PicHolder mHolder = (PicHolder) baseHolder;
        mHolder.iv = (ImageView) convertView.findViewById(R.id.item_viewpic_iv);
    }

    @Override
    protected void initHolder(FTPImgInfo currentData, MyBasedHolder baseHolder) {
        PicHolder mHolder = (PicHolder) baseHolder;
        String realPath = currentData.getImgPath();
        Bitmap bitmap;
        if (!bitmaps.containsKey(realPath)) {
            try {
                bitmap = MyImageUtls.getMySmallBitmap(realPath, 200, 200);
                bitmaps.put(realPath, bitmap);
                mHolder.iv.setImageBitmap(bitmap);
            } catch (OutOfMemoryError error) {
                error.printStackTrace();
            }
        } else {
            bitmap = bitmaps.get(realPath);
            mHolder.iv.setImageBitmap(bitmap);
        }
    }

    @Override
    protected MyBasedHolder getHolder() {
        return new PicHolder();
    }


    class PicHolder extends MyViewPicBaseAdapter.MyBasedHolder {
        ImageView iv;
    }
}
