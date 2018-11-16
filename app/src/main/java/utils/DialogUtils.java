package utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.b1b.js.erpandroid_nahuo.R;

/**
 Created by 张建宇 on 2017/8/1. */

public class DialogUtils {
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

    public static void safeShowDialog(Context mContext, Dialog dialog) {
        boolean finishing = ((Activity) mContext).isFinishing();
        if (!finishing) {
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        }
    }

    public static AlertDialog createAlertDialog(Context mContext, String msg) {

        return createAlertDialog(mContext, "提示", msg, null, null, null,
                null, true);
    }

    public static void cancelDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
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
