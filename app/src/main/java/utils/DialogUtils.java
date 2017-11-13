package utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by 张建宇 on 2017/8/1.
 */

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
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, lStr, ll);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, rStr, rl);
        return dialog;
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
