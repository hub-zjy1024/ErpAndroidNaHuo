package com.b1b.js.erpandroid_nahuo.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.b1b.js.erpandroid_nahuo.R;

/**
 Created by 张建宇 on 2018/9/6. */
public class ZjyNotifyManager {
    private Context mContext;
    private String group;

    public ZjyNotifyManager(Context mContext, String group) {
        this.mContext = mContext;
        this.group = mContext.getPackageName();
    }

    public void sendNotif(String title, String content) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(mContext);
        notifyBuilder.setContentText(content);
        notifyBuilder.setContentTitle(title);
        notifyBuilder.setPriority(Notification.PRIORITY_HIGH);
        notifyBuilder.setSmallIcon(R.drawable.menu_icon_nahuo);
        Bitmap lagerIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.menu_icon_nahuo);
        notifyBuilder.setLargeIcon(lagerIcon);
        notifyBuilder.setGroup(group);
        NotificationManager manger = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manger != null) {
            manger.notify(getID(), notifyBuilder.build());
        }
    }

    private int getID() {
        int num = (int) (Math.random() * 1000 * 1000);
        return num;
    }
}
