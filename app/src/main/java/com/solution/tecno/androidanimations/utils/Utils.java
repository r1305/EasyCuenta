package com.solution.tecno.androidanimations.utils;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.solution.tecno.androidanimations.Firebase.MyNotificationManager;
import com.solution.tecno.androidanimations.R;

import java.util.Locale;
import java.util.Random;

public class Utils {

    private static final String[] ALPHA_NUMERIC_STRING = {"a","A","b","B","c","C","d","D","e","E","f","F","g","G","h","H","i","I","j","J","k","K","l","L","m","M","n","N","o","O","p","P","q","Q","r","R","s","S","t","T","u","U","v","V","w","W","x","X","y","Y","z","Z","0","1","2","3","4","5","6","7","8","9"};
    //Spanish, PeruLocale locale = new Locale("es", "pe"); //Spanish, Peru
    Locale locale = new Locale("es", "pe");
    private String url ="";
    private Context ctx;
    private Credentials cred;

    public Utils(Context ctx) {
        this.ctx = ctx;
        cred = new Credentials(ctx);
    }

    public Utils() {
    }

    public String generateToken(int size) {
        StringBuilder token = new StringBuilder();
        for(int i=0;i<size;i++){
            Random rand = new Random();
            int character = rand.nextInt(ALPHA_NUMERIC_STRING.length);
            token.append(ALPHA_NUMERIC_STRING[character]);
        }
        return token.toString();
    }

    public void createAlert(Context ctx, String error,int type){
        AlertDialog.Builder builder =  new AlertDialog.Builder(ctx);
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_alert, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        TextView et_title = dialogView.findViewById(R.id.et_title);
        String title = "";
        if(type == 1)
            title = "¡Error!";
        else if(type == 2)
            title = "¡Éxito!";
        else if(type == 3)
            title = "¡Alerta!";

        TextView et_message = dialogView.findViewById(R.id.et_message);

        et_title.setText(title);
        et_message.setText(error);
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        }, 1500);
    }

    public String toEngDate(String esp_date)
    {
        String eng_date = "";
        String[] ar_fec_nac = esp_date.split("/");
        eng_date = ar_fec_nac[2]+"-"+ar_fec_nac[1]+"-"+ar_fec_nac[0];
        return eng_date;
    }

    public FirebaseAuth initFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    public String objectToJson(Object obj){
        return new Gson().toJson(obj);
    }

    public DatabaseReference getDatabaseReference(String database)
    {
        FirebaseDatabase databaseReference = FirebaseDatabase.getInstance(ctx.getString(R.string.firebase_database));
        return databaseReference.getReference(database);
    }

    public void showNotification(Context ctx,String title, String body)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("1", "1",importance);
            mChannel.setDescription("1");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{0, 500, 250, 500, 250,500, 250});
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
            MyNotificationManager.getInstance(ctx).displayNotification(title,body);
        }else{
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx)
                    .setSmallIcon(R.drawable.ic_bank_app)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setVibrate(new long[]{0, 500, 250, 500, 250,500, 250})
                    .setPriority(importance)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            NotificationManager notificationManager =
                    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}
