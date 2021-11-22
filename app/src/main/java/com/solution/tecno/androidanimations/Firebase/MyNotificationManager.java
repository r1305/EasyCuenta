package com.solution.tecno.androidanimations.Firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.activities.LoginActivity;
import com.solution.tecno.androidanimations.activities.MainActivity;

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

    public void displayNotification(String title,String body) {
        Random random=new Random();
        int notificationId=random.nextInt();
        // Start without a delay
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {0, 500, 250, 500, 250,500, 250};

        Intent intent = null;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            intent = new Intent(mCtx, MainActivity.class);
        }else{
            intent = new Intent(mCtx, LoginActivity.class);
        }


        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nm=(NotificationManager)mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap icon = BitmapFactory.decodeResource(mCtx.getResources(),
                R.drawable.ic_bank_app);
        Notification notification = new NotificationCompat.Builder(mCtx, "1")
                .setSmallIcon(R.drawable.ic_bank_app)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setVibrate(pattern)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        if (nm != null) {
            nm.notify(notificationId, notification);
        }
    }
}
