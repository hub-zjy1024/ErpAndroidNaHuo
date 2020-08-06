package utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import com.b1b.js.erpandroid_nahuo.myview.ProgressDialogV2;
import com.b1b.js.erpandroid_nahuo.myview.ScrollViewHasMaxHeight;

import java.util.concurrent.atomic.AtomicInteger;

/**
 Created by 张建宇 on 2017/8/1. */

public class DialogUtils {
    private SparseArray<Dialog> mPds = new SparseArray<>();
    private static AtomicInteger mit = new AtomicInteger(0);
    private Context mContext;

    public DialogUtils(Context mContext) {
        this.mContext = mContext;
    }

    public static void cancelDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
    }

    public int showAlertWithId(String msg) {
        Dialog proDialog = getDialog(mContext).showCommMsgDialog(msg);
        int andIncrement = mit.getAndIncrement();
        mPds.put(andIncrement, proDialog);
        return andIncrement;
    }

    public Dialog getDialogById(int id) {
        return mPds.get(id);
    }

    /**
     * 不可取消的Dialog，只能通过回调按键
     * @param msg
     * @param listener
     * @return
     */
    public int showMsgDialogWithCallback(String msg, DialogInterface.OnClickListener listener) {
        Dialog proDialog =
                getDialog(mContext).setBtn1L(listener).setBtn1("确认").setMsg(msg).create();
        proDialog.setCancelable(false);
        int andIncrement = mit.getAndIncrement();
        mPds.put(andIncrement, proDialog);
        return andIncrement;
    }

    public void cancelAll() {
        for (int i = 0; i < mPds.size(); i++) {
            int i1 = mPds.keyAt(i);
            cancelDialogById(i1);
        }
        mPds.clear();
    }

    public int showProgressWithID(String msg) {
        return showProgressWithID("请稍后", msg);
    }

    public int showProgressWithID(String title, String msg) {

        //        ProgressDialog proDialog = new ProgressDialog(mContext);
        ProgressDialogV2 proDialog = new ProgressDialogV2(mContext);
        proDialog.setTitle(title );
        proDialog.setMessage(msg);
        proDialog.setCancelable(true);
        try {
            proDialog.show();
        } catch (Exception e) {
            MyApp.myLogger.writeError(e, "showProgressWithID  err");
            Log.e("zjy", getClass() + "->showProgressWithID(): no Window==" + msg);
        }
        int andIncrement = mit.getAndIncrement();
        mPds.put(andIncrement, proDialog);
        return andIncrement;
    }

    public void cancelDialogById(int pdId) {
        Dialog proDialog = mPds.get(pdId);
        if (proDialog != null) {
            try {
                proDialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
                MyApp.myLogger.writeError(e, "showProgressWithID  err");
            }
            mPds.remove(pdId);
        }
    }

    public static Builder getDialog(Context mContext) {
        return new Builder(mContext);
    }

    public static class Builder {
        private Context mContext;
        private String msg;
        private String btn1;
        private String btn2;
        private DialogInterface.OnClickListener btn1L;
        private DialogInterface.OnClickListener btn2L;
        private View itemView;
        private boolean cancelAble = true;
        AlertDialog alertDialog;

        private float tvHeightRate = 0.6f;
        float maxHeight;
        public Builder(Context mContext) {
            this.mContext = mContext;
            Resources resources = mContext.getResources();
            alertDialog = new AlertDialog.Builder(mContext).create();
            Window window = alertDialog.getWindow();
            if(window!=null){
                //                window.setWindowAnimations(R.style.dialog_anim);
                window.setBackgroundDrawable(resources.getDrawable(R.drawable.material_dialog_bg));
            }
            View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_common_contentview, null,
                    false);
            itemView = mView;
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int heightPixels = displayMetrics.heightPixels;
            maxHeight= tvHeightRate * heightPixels;
        }

        public Builder setBtn1(String msg) {
            this.btn1 = msg;
            return this;
        }

        public Builder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder setCancelable(boolean cancelAble) {
            this.btn1 = msg;
            return this;
        }

        public Builder setBtn2(String msg) {
            this.btn2 = msg;
            return this;
        }

        public Dialog showCommMsgDialog(String msg) {
            Dialog dialog =
                    setBtn1("确认").setMsg(msg).create();
            return dialog;
        }
        public Dialog create() {
            View mView = itemView;
            final TextView mContent = (TextView) mView.findViewById(R.id.dialog_cm_tv);
            Button btnLeft = (Button) mView.findViewById(R.id.dialog_cm_btn_left);
            btnLeft.setText(btn1);
            Button btnRight = (Button) mView.findViewById(R.id.dialog_cm_btn_right);
            ScrollViewHasMaxHeight sroller = (ScrollViewHasMaxHeight) mView.findViewById(R.id.dialog_cm_scrollview);
            sroller.setMaxHeight((int) maxHeight);
            btnRight.setText(btn2);
            LinearLayout btnContainers = (LinearLayout) mView.findViewById(R.id.dialog_cm_btns);
            TextView tvDivider2 = (TextView) mView.findViewById(R.id.dialog_cm_divider2);
            if (btn2 == null) {
                tvDivider2.setVisibility(View.GONE);
                btnRight.setVisibility(View.GONE);
            }
            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                    if (btn1L != null) {
                        btn1L.onClick(alertDialog, 1);
                    }
                }
            });
            alertDialog.setCancelable(cancelAble);
            btnRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                    if (btn2L != null) {
                        btn2L.onClick(alertDialog, 2);
                    }
                }
            });
            mContent.setText(msg);
            mContent.setTextColor(Color.BLACK);
            alertDialog.show();
            alertDialog.setContentView(mView);
            return alertDialog;
        }

        public Builder setBtn1L(DialogInterface.OnClickListener btn1L) {

            this.btn1L = btn1L;
            return this;
        }

        public Builder setBtn2L(DialogInterface.OnClickListener btn2L) {
            this.btn2L = btn2L;
            return this;
        }
    }


    public static void safeShowDialog(Context mContext, Dialog dialog) {
        boolean finishing = ((Activity) mContext).isFinishing();
        if (!finishing) {
            dialog.show();
        }
    }


    public void setWindowAlpha(Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 1.0f;
        activity.getWindow().setAttributes(lp);
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static Dialog getSpAlert(Context mContext, String msg, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(true);
        return builder.create();
    }

    public static Dialog getSpAlert(Context mContext, String msg, String title, Dialog.OnClickListener ll,
                                    String lStr,
                                    DialogInterface
                                            .OnClickListener rl, String rStr) {
        AlertDialog dialog = (AlertDialog) getSpAlert(mContext, msg, title);
        if (lStr != null && !lStr.equals("")) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, lStr, ll);
        }
        if (rStr != null && !rStr.equals("")) {
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, rStr, rl);
        }
        return dialog;
    }

    private Snackbar initSnackbar(Activity mContext, View anchorView, String m, int snackHeight) {
        Snackbar finalSnackbar = Snackbar.make(anchorView, m, Snackbar.LENGTH_INDEFINITE);
        View view = finalSnackbar.getView();
        Snackbar.SnackbarLayout parent = (Snackbar.SnackbarLayout) view;
        float sHeight = mContext.getResources().getDisplayMetrics().heightPixels;
        float density = mContext.getResources().getDisplayMetrics().density;
        float fontSize = 6 * density;
        int height = (int) (sHeight * 1 / 8);
        parent.setMinimumHeight(height);
        int colorBg = mContext.getResources().getColor(R.color.button_light_bg);
        parent.setBackgroundColor(colorBg);
        fontSize = 18;
        TextView tv = (TextView) parent.getChildAt(0);
        tv.setTextColor(Color.GREEN);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        Button btn = (Button) parent.getChildAt(1);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        btn.setTextColor(Color.WHITE);
        finalSnackbar.setActionTextColor(Color.parseColor("#ffffff"));
        finalSnackbar.setAction("返回", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).finish();
            }
        });
        return finalSnackbar;
    }


    public static AlertDialog createAlertDialog(Context mContext, String msg) {

        return createAlertDialog(mContext, "提示", msg, null, null, null,
                null, true);
    }


    public static AlertDialog createAlertDialog(Context mContext, String title, String
            msg, String
                                                        leftBtn, DialogInterface
                                                        .OnClickListener lListener, String rightBtn,
                                                DialogInterface.OnClickListener
                                                        rListener, boolean canCancel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setMessage(msg);
        if (leftBtn != null) {
            builder.setNegativeButton(leftBtn, lListener);
        }
        if (rightBtn != null) {
            builder.setPositiveButton(rightBtn, rListener);
        }
        builder.setCancelable(canCancel);
        return builder.create();
    }
}
