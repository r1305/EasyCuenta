package com.solution.tecno.androidanimations.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.solution.tecno.androidanimations.Firebase.Constants;
import com.solution.tecno.androidanimations.activities.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

public class Credentials {
    public String login_status;
    public String network_status;
    private Context ctx;

    public Credentials(Context ctx) {
        this.ctx = ctx;
    }

    public String getLoginStatus(){
        SharedPreferences apl=ctx.getSharedPreferences("Login",MODE_PRIVATE);
        String login_status=apl.getString(Preferences.LOGIN,"0");
        this.login_status=login_status;
        return this.login_status;
    }
    
    public String getData(String key)
    {
        SharedPreferences apl=ctx.getSharedPreferences("Login",MODE_PRIVATE);
        return apl.getString(key,"");
    }

    public String getNetworkStatus(){
        isNetworkAvailable();
        SharedPreferences apl=ctx.getSharedPreferences("Login",MODE_PRIVATE);
        String network_status=apl.getString(Preferences.NETWORK_STATUS,"0");
        this.network_status=network_status;
        return this.network_status;
    }

    public void logout(){
        SharedPreferences sp=ctx.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString(Preferences.USER_ID,"0");
        Ed.putString(Preferences.USER_PHONE,"0");
        Ed.putString(Preferences.USER_NAME,"0");
        Ed.putString(Preferences.USER_EMAIL,"0");
        Ed.putString(Preferences.LOGIN,"0");
        Ed.commit();

        Intent i=new Intent(ctx, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, 0);
        ctx.startActivity(i);
    }
    
    public void saveData(String key,String value)
    {
        SharedPreferences sp=ctx.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString(key,value);
        Ed.commit();
    }


    private void isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        SharedPreferences sp=ctx.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();

        if(activeNetworkInfo==null || !activeNetworkInfo.isConnected()){
            Ed.putString(Preferences.NETWORK_STATUS,"0");
            this.network_status="0";
        }else{
            Ed.putString(Preferences.NETWORK_STATUS,"1");
            this.network_status="1";
        }
        Ed.commit();
    }
}
