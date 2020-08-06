package com.b1b.js.erpandroid_nahuo.versioncompact;

import android.Manifest;

import com.b1b.js.erpandroid_nahuo.activity.base.BaseMActivity;
import com.b1b.js.erpandroid_nahuo.utils.callback.IBoolCallback;

/**
 * Created by 张建宇 on 2020/8/4.
 */

public class Api23Checker {
    BaseMActivity mActivity;

    static final String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION
            , Manifest.permission.CAMERA
    };

    public Api23Checker(BaseMActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void checkPermission(IBoolCallback mCallback) {
        mActivity.usePermission(permissions, mCallback);
    }

}
