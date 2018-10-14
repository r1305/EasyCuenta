package com.solution.tecno.androidanimations;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class Credentials {
    public String user_id;
    private Context ctx;

    public Credentials(Context ctx) {
        this.ctx = ctx;
    }

    public Credentials(String user_id) {
        this.user_id = user_id;
    }

    public Credentials() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public String getUserId(){

        SharedPreferences sp1=ctx.getSharedPreferences("Login", MODE_PRIVATE);

        String user_id=sp1.getString("user_id", "0");
        this.user_id=user_id;
        return this.user_id;
    }

    public void logout(){

        SharedPreferences sp=ctx.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("user_id","0");
        Ed.commit();

        Intent i=new Intent(ctx,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, 0);
        ctx.startActivity(i);

    }
}
