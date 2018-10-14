package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    Context ctx;
    Class activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx=MainActivity.this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String user_id=new Credentials(ctx).getUserId();
                activity=(user_id.equals("0") || user_id==null)?LoginActivity.class:FirstActivity.class;
                redirect(activity);
            }
        }, 3000);
    }

    public void redirect(Class activity_class){
        Intent i=new Intent(this,activity_class);
        startActivity(i);
    }
}
