package com.b1b.js.erpandroid_nahuo.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.b1b.js.erpandroid_nahuo.R;
import com.b1b.js.erpandroid_nahuo.activity.NahuoListAcitivity;
import com.b1b.js.erpandroid_nahuo.application.MyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;

public class NahuoPushService extends Service {
    private Context mContext = this;
    private long time = System.currentTimeMillis();
    private int counts = 0;

    public NahuoPushService() {
    }

    private static boolean start = false;

    public static void setStart(boolean start) {
        NahuoPushService.start = start;
    }

    @Override
    public IBinder onBind(Intent intent) { /* TODO: Return the communication channel to the service.*/
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; true; i++) {
                    Date date = new Date();
                    int hours = date.getHours();
                    Log.e("zjy", "NahuoPushService->run(): HourS==" + hours);
                    if ((hours >= 10 && hours <= 11) || (hours >= 14 && hours <= 15)) {
                        try {
                            String nahuoList = NahuoListAcitivity.getNahuoList(MyApp.id, "", "1");
                            Log.e("zjy", "NahuoPushService->run(): nahuoList==" + nahuoList);
                            JSONObject object = new JSONObject(nahuoList);
                            JSONArray jarray = object.getJSONArray("表"); int len = jarray.length();
                            if ("{\"表\":] }".equals(nahuoList))
                                Log.e("zjy", "NahuoPushService->run(): no data==");
                            else {
                                String contentText = "您有" + len + "条拿货消息需要处理";
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

                                Intent notifyIntent = new Intent(mContext, NahuoListAcitivity.class);

                                String infos = contentText;
                                notifyIntent.putExtra("content", infos);
                                notifyIntent.putExtra("uid", MyApp.id);
                                PendingIntent pIntent = PendingIntent.getActivity(mContext, 200,

                                        notifyIntent, PendingIntent
                                                .FLAG_UPDATE_CURRENT); builder.setContentIntent(pIntent);
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R

                                        .mipmap.appicon); builder.setLargeIcon(bitmap);
                                builder.setSmallIcon(R.drawable.bg_btn_review);

                                builder.setContentTitle("拿货消息");
                                builder.setContentText(infos);
                                builder.setAutoCancel(true);
                                builder.setSound(RingtoneManager
                                        .getDefaultUri(RingtoneManager
                                                .TYPE_NOTIFICATION));
                                builder.setLights(Color.RED, 1000, 1000);
                                builder.setTicker(infos);
                                NotificationManager notifyManager = (NotificationManager)
                                        getSystemService
                                                (NOTIFICATION_SERVICE);
                                notifyManager.notify(1, builder.build());
                                counts++;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    try {
                        Thread.sleep(10 * 60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }
}
