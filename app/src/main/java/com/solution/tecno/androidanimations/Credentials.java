package com.solution.tecno.androidanimations;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Credentials {
    public String user_id;
    public String phone_number;
    public String full_name;
    public String username;
    private Context ctx;

    public Credentials(Context ctx) {
        this.ctx = ctx;
    }

    public String getUserId(){

        SharedPreferences sp1=ctx.getSharedPreferences("Login", MODE_PRIVATE);

        String user_id=sp1.getString("user_id", "0");
        this.user_id=user_id;
        return this.user_id;
    }

    public String getPhoneNumber(){

        SharedPreferences sp1=ctx.getSharedPreferences("Login", MODE_PRIVATE);

        String phone_number=sp1.getString("phone_number", "0");
        this.phone_number=phone_number;
        return this.phone_number;
    }

    public String getFullName(){

        SharedPreferences sp1=ctx.getSharedPreferences("Login", MODE_PRIVATE);

        String full_name=sp1.getString("full_name", "0");
        this.full_name=full_name;
        return this.full_name;
    }

    public String getUserName(){

        SharedPreferences sp1=ctx.getSharedPreferences("Login", MODE_PRIVATE);

        String username=sp1.getString("username", "0");
        this.username=username;
        return this.username;
    }

    public void logout(){

        SharedPreferences sp=ctx.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("user_id","0");
        Ed.putString("phone_number","0");
        Ed.putString("full_name","0");
        Ed.putString("username","0");
        Ed.commit();

        Intent i=new Intent(ctx,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, 0);
        ctx.startActivity(i);
    }

    public void save_credentials(String user_id,String full_name,String username,String phone_number){
        SharedPreferences sp=ctx.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("user_id",user_id);
        Ed.putString("full_name",full_name);
        Ed.putString("username",username);
        Ed.putString("phone_number",phone_number);
        Ed.commit();
    }
}
