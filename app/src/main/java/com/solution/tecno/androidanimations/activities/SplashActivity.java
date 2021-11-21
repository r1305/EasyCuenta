package com.solution.tecno.androidanimations.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Window;
import android.widget.TextView;
import com.solution.tecno.androidanimations.BuildConfig;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Preferences;
import com.solution.tecno.androidanimations.utils.ViewDialog;

public class SplashActivity extends AppCompatActivity {

    Context ctx;
    ViewDialog viewDialog;
    Class activity;
    TextView version_name;
    public static int MY_PERMISSIONS_REQUEST_ACCESS= 1;
    Credentials cred;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ctx= SplashActivity.this;
        cred = new Credentials(ctx);
        viewDialog = new ViewDialog(this);

        version_name=findViewById(R.id.tv_version);
        version_name.setText("V."+ BuildConfig.VERSION_NAME);
        version_name.setTextColor(Color.BLACK);
        version_name.setTypeface(version_name.getTypeface(),Typeface.BOLD);


        version_name=findViewById(R.id.tv_version);
        version_name.setText("V."+BuildConfig.VERSION_NAME);
        version_name.setTextColor(Color.BLACK);
        version_name.setTypeface(version_name.getTypeface(),Typeface.BOLD);

        cred.getNetworkStatus();

        checkPermissions();
    }

    public void redirect(Class activity_class){
        Intent i=new Intent(ctx,activity_class);
        startActivity(i);
        SplashActivity.this.finish();
    }

    public void validateSession(){
        viewDialog.showDialog();

        new Handler().postDelayed(() -> {
            String userId=cred.getData(Preferences.USER_ID);
            String login_status=cred.getLoginStatus();
            Dialog dialog;
            if(login_status.equals("0")){
                viewDialog.hideDialog(0);
                dialog = showLogoutDialog();
                new Handler().postDelayed(() -> {
                    dialog.dismiss();
                    cred.logout();
                }, 1500);
            }else{
                viewDialog.hideDialog(0);
                activity=userId.isEmpty()?LoginActivity.class: MainActivity.class;
                redirect(activity);
            }
        }, 3000);
    }

    private Dialog showLogoutDialog()
    {
        Dialog dialog  = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.show();

        return dialog;
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
                        &&
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                )
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE
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
