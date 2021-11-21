package com.solution.tecno.androidanimations.Firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.solution.tecno.androidanimations.activities.MainActivity;
import com.solution.tecno.androidanimations.R;

import java.util.Random;

public class MyNotificationManager {

    private Context mCtx;
    private static MyNotificationManager mInstance;

    private MyNotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;
    }

    public void displayNotification(String body, String title) {
        Random random=new Random();
        int notificationId=random.nextInt();
        // Start without a delay
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {0, 500, 250, 500, 250,500, 250};

        Intent intent = new Intent(mCtx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(mCtx);
        mBuilder.setSmallIcon(R.drawable.ic_bank_app);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(body);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setChannelId(Constants.CHANNEL_ID);
        mBuilder.setAutoCancel(true);

        NotificationManager nm=(NotificationManager)mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setVibrate(pattern);

        if (nm != null) {
            nm.notify(notificationId, mBuilder.build());
        }
    }
}
