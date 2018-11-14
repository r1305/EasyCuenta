package com.solution.tecno.androidanimations;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.jackandphantom.circularprogressbar.CircleProgressbar;

public class MainActivity extends AppCompatActivity {

    Context ctx;
    Class activity;
    TextView version_name;
    public static int MY_PERMISSIONS_REQUEST_ACCESS= 1;
    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;
    AwesomeInfoDialog aid;
    Credentials cred;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx=MainActivity.this;
        cred = new Credentials(ctx);

        //create progress dialog
        apd=new AwesomeProgressDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Cargando")
                .setColoredCircle(R.color.dialogInfoBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(false);

        //create success dialog
        asd=new AwesomeSuccessDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Listo!")
                .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(false);

        //create error dialog
        aed=new AwesomeErrorDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Ocurrió un error")
                .setColoredCircle(R.color.dialogErrorBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_error,R.color.white)
                .setCancelable(false);

        //create info dialog
        aid=new AwesomeInfoDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Inicie sesión nuevamente por favor")
                .setColoredCircle(R.color.dialogInfoBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_info,R.color.white)
                .setCancelable(false);


        version_name=findViewById(R.id.tv_version);
        version_name.setText("V."+BuildConfig.VERSION_NAME);
        version_name.setTextColor(Color.BLACK);
        version_name.setTypeface(version_name.getTypeface(),Typeface.BOLD);

        checkPermissions();
    }

    public void redirect(Class activity_class){
        Intent i=new Intent(ctx,activity_class);
        startActivity(i);
        this.finish();
    }

    public void validateSession(){
        CircleProgressbar circleProgressbar = findViewById(R.id.main_progress_bar);
        circleProgressbar.setForegroundProgressColor(Color.RED);
        circleProgressbar.setBackgroundProgressWidth(15);
        circleProgressbar.setForegroundProgressWidth(20);
        circleProgressbar.enabledTouch(false);
        circleProgressbar.setRoundedCorner(true);
        circleProgressbar.setClockwise(true);
        circleProgressbar.setMaxProgress(100);
        int animationDuration = 8000; // 2500ms = 2,5s
        circleProgressbar.setProgressWithAnimation(-100, animationDuration); // Default duration = 1500ms

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String user_id=cred.getUserId();
                String full_name=cred.getFullName();
                if(full_name.equals("0")){
                    aid.setMessage("Inicie sesión nuevamente por favor");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            aid.hide();
                            cred.logout();
                        }
                    }, 1500);

                }else{
                    activity=(user_id.equals("0") || user_id==null)?LoginActivity.class:FirstActivity.class;
                    redirect(activity);
                }
            }
        }, 3000);
    }

    private void checkPermissions(){
        if (
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED
                )
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS
                    },
                    MY_PERMISSIONS_REQUEST_ACCESS);
        }else{
            validateSession();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case 1 :
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    validateSession();
                } else {
                    checkPermissions();
                }
            break;
        }
    }


}
