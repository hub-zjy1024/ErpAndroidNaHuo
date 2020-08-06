package com.b1b.js.erpandroid_nahuo.myview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;

import java.text.NumberFormat;

/**
 * Created by 张建宇 on 2020/5/27.
 */

public class ProgressDialogV2 extends AlertDialog {
    ProgressBar mProgress;
    int mSecondaryProgressVal;
    int mProgressVal;
    int mMax;
    int mIncrementBy;
    int mProgressStyle = STYLE_HORIZONTAL;
    int mIncrementSecondaryBy;
    boolean mIndeterminate;
    boolean mHasStarted;
    private Drawable mIndeterminateDrawable;
    private Drawable mProgressDrawable;
    private TextView mProgressPercent;
    private String mProgressNumberFormat = "%1d/%2d";
    private CharSequence mMessage;

    private NumberFormat mProgressPercentFormat;

    private TextView mProgressNumber;
    private TextView mMessageView;
    public static final int STYLE_HORIZONTAL = 0;
    public static final int MSG_AUTO_UPDATE = 0;
    public static final int increaseMin = 1;
    public static final int maxTime = 5 * 1000;
    public static final int updateRate = 100;
    public static final int maxCout = 80;

    private Handler mViewUpdateHandler;

    private Context mContext;

    public ProgressDialogV2(@NonNull Context context) {
        this(context, 0);
    }

    public ProgressDialogV2(@NonNull Context context, int themeResId) {
        this(context, false, null);

    }

    public ProgressDialogV2(@NonNull Context context, boolean cancelable,
                            @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
        initFormats();
    }

    private void initFormats() {
        mProgressNumberFormat = "%1d/%2d";
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHasStarted = true;

        final LayoutInflater inflater = LayoutInflater.from(mContext);
     /*   TypedArray a = mContext.obtainStyledAttributes(null,
                com.android.internal.R.styleable.AlertDialog,
                com.android.internal.R.attr.alertDialogStyle, 0);
    */
        if (mProgressStyle == STYLE_HORIZONTAL) {

            /* Use a separate handler to update the text views as they
             * must be updated on the same thread that created them.
             */
            View view = inflater.inflate(R.layout.dialog_base_progressv2, null);
            //                    com.android.internal.R.styleable
            //                    .AlertDialog_horizontalProgressLayout,
            //                    R.layout.alert_dialog_progress), null);
            mProgress = (ProgressBar) view.findViewById(R.id.dialog_base_progress);
            mProgressNumber = (TextView) view.findViewById(R.id.dialog_base_tv_progress_number);
            mProgressPercent = (TextView) view.findViewById(R.id.dialog_base_tv_percent);
            setView(view);

            mViewUpdateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case MSG_AUTO_UPDATE:
                            int data = maxCout / maxTime / updateRate;
                            if (data == 0) {
                                data = 1;
                            }
                            int realData = getProgress() + data;
                            if (realData > maxCout) {
                                return;
                            }
                            setProgress(realData);
                            sendEmptyMessageDelayed(MSG_AUTO_UPDATE, updateRate);
                            break;
                        default:
                            /* Update the number and percent */
                            int progress = mProgress.getProgress();
                            int max = mProgress.getMax();
                            if (mProgressNumberFormat != null) {
                                String format = mProgressNumberFormat;
                                mProgressNumber.setText(String.format(format, progress, max));
                            } else {
                                mProgressNumber.setText("");
                            }
                            if (mProgressPercentFormat != null) {
                                double percent = (double) progress / (double) max;
                                //                        SpannableString tmp = new SpannableString(String.format
                                //                        (mProgressPercentFormat , percent));
                                SpannableString tmp =
                                        new SpannableString(mProgressPercentFormat.format(percent));
                                tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                                        0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                mProgressPercent.setText(tmp);
                            } else {
                                mProgressPercent.setText("");
                            }
                            break;
                    }
                }
            };

        } else {
            //            View view = inflater.inflate(a.getResourceId(
            //                    com.android.internal.R.styleable.AlertDialog_progressLayout,
            //                    R.layout.progress_dialog), null);
            //            mProgress = (ProgressBar) view.findViewById(R.id.progress);
            //            mMessageView = (TextView) view.findViewById(R.id.message);
            View view = inflater.inflate(R.layout.dialog_base_progressv_no_progress, null);
            mProgress = view.findViewById(R.id.dialog_base_progress);
            mMessageView = (TextView) view.findViewById(R.id.dialog_base_tv_msg);
            mProgress.setMax(mMax);
            setView(view);
        }
        //        a.recycle();
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
        if (mSecondaryProgressVal > 0) {
            setSecondaryProgress(mSecondaryProgressVal);
        }
        if (mIncrementBy > 0) {
            incrementProgressBy(mIncrementBy);
        }
        if (mIncrementSecondaryBy > 0) {
            incrementSecondaryProgressBy(mIncrementSecondaryBy);
        }
        if (mProgressDrawable != null) {
            setProgressDrawable(mProgressDrawable);
        }
        if (mIndeterminateDrawable != null) {
            setIndeterminateDrawable(mIndeterminateDrawable);
        }
        if (mMessage != null) {
            setMessage(mMessage);
        }
        setIndeterminate(mIndeterminate);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
    }

    private void incrementProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementBy += diff;
        }
    }

    @Override
    public void setMessage(CharSequence message) {
        if (mProgress != null) {
            if (mProgressStyle == STYLE_HORIZONTAL) {
                super.setMessage(message);
            } else {
                mMessageView.setText(message);
            }
        } else {
            mMessage = message;
        }
    }

    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    public void setProgressStyle(int style) {
        mProgressStyle = style;
    }

    public void setProgressNumberFormat(String format) {
        mProgressNumberFormat = format;
        onProgressChanged();
    }

    public void setProgressPercentFormat(NumberFormat format) {
        mProgressPercentFormat = format;
        onProgressChanged();
    }

    public void incrementSecondaryProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementSecondaryProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementSecondaryBy += diff;
        }
    }

    public void setProgressDrawable(Drawable mProgressDrawable) {
        this.mProgressDrawable = mProgressDrawable;

    }

    public void setIndeterminateDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setIndeterminateDrawable(d);
        } else {
            mIndeterminateDrawable = d;
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
    }

    public void setSecondaryProgress(int secondaryProgress) {
        if (mProgress != null) {
            mProgress.setSecondaryProgress(secondaryProgress);
        } else {
            mSecondaryProgressVal = secondaryProgress;
        }
    }

    public int getProgress() {
        if (mProgress != null) {
            return mProgress.getProgress();
        }
        return mProgressVal;

    }

    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    private void onProgressChanged() {
        if (mProgressStyle == STYLE_HORIZONTAL) {
            if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
                mViewUpdateHandler.sendEmptyMessage(0);
            }
        }
    }

    @Override
    public void show() {
        super.show();
        if (mViewUpdateHandler != null) {
            mViewUpdateHandler.sendEmptyMessage(MSG_AUTO_UPDATE);
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        if (mViewUpdateHandler != null) {
            mViewUpdateHandler.removeMessages(MSG_AUTO_UPDATE);
        }
    }
}
