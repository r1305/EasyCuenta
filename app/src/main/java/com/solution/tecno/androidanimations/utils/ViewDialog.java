package com.solution.tecno.androidanimations.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Window;

import androidx.annotation.NonNull;

import com.solution.tecno.androidanimations.R;

public class ViewDialog {

    Activity activity;
    Dialog dialog;
    //..we need the context else we can not create the dialog so get context in constructor
    public ViewDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {

        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.show();
    }

    //..also create a method which will hide the dialog when some work is done
    public void hideDialog(@NonNull double timer){
        System.out.println("loader "+(timer==5?"edit":"otros"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(dialog!=null)
                    dialog.dismiss();
            }
        },Integer.parseInt(String.valueOf(timer*1000).split("\\.")[0]));
    }

}