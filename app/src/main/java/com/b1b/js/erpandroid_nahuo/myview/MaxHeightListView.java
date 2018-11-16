package com.b1b.js.erpandroid_nahuo.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.b1b.js.erpandroid_nahuo.R;

/**
 Created by 张建宇 on 2018/7/16. */
public class MaxHeightListView extends ListView {
    private int maxHeight = 0;
    public MaxHeightListView(Context context) {
        this(context, null, 0);
    }

    public MaxHeightListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxHeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightListView);
        maxHeight = typedArray.getDimensionPixelSize(R.styleable.MaxHeightListView_maxHeight, 250);
        Log.e("zjy", "MaxHeightListView->MaxHeightListView(): ==" + maxHeight);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            ListAdapter adapter = getAdapter();
            int count = 0;
            int itemHeight = 0;
            if (adapter != null) {
                count = adapter.getCount();
                for(int i=0;i<count;i++) {
                    View firstChild = adapter.getView(i, null, this);
                    firstChild.measure(0, 0);
                    int measuredHeight = firstChild.getMeasuredHeight();
                    itemHeight = measuredHeight;
                    Log.e("zjy", "MaxHeightListView->onMeasure(): MeasuredHeight==" + measuredHeight);
                    Log.e("zjy", "MaxHeightListView->onMeasure(): MeasuredWidth==" + firstChild.getMeasuredWidth());
                }
            }
            int pxHeight =maxHeight;
            if (count >= 3) {
                heightSize = pxHeight;
            } else {
                heightSize = itemHeight * count;
            }
            setMeasuredDimension(widthSize, heightSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
