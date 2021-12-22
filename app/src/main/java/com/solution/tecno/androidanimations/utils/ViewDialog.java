package com.solution.tecno.androidanimations.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.solution.tecno.androidanimations.R;

public class ViewDialog {

    Activity activity;
    Dialog dialog;
    LottieAnimationView lottie;
    TextView message;
    View dialog_view;
    public ViewDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog(String msg) {
        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog_view = View.inflate(activity,R.layout.dialog_loading,null);
        lottie = dialog_view.findViewById(R.id.lottie_image);
        message = dialog_view.findViewById(R.id.message_text);
        if(msg.isEmpty())
            message.setVisibility(View.GONE);
        else {
            message.setVisibility(View.VISIBLE);
            message.setText(msg);
        }
        dialog.setContentView(dialog_view);
        dialog.show();
    }

    public void hideDialog(@NonNull double timer){
        new Handler().postDelayed(() -> {
            if(dialog!=null)
                dialog.dismiss();
        },Integer.parseInt(String.valueOf(timer*1000).split("\\.")[0]));
    }

    public void showSuccess(String msg)
    {
        if(msg.isEmpty())
            message.setVisibility(View.GONE);
        else {
            message.setVisibility(View.VISIBLE);
            message.setText(msg);
        }
        lottie.setAnimation(R.raw.lottie_success);
        lottie.playAnimation();
        hideDialog(3);
    }

    public void showFail(String msg)
    {
        if(msg.isEmpty())
            message.setVisibility(View.GONE);
        else {
            message.setVisibility(View.VISIBLE);
            message.setText(msg);
        }
        lottie.setAnimation(R.raw.lottie_fail);
        lottie.playAnimation();
        hideDialog(3);
    }

}