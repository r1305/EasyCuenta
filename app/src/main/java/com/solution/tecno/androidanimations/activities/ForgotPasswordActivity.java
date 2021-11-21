package com.solution.tecno.androidanimations.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText forgot_email,forgot_number;
    ViewDialog viewDialog;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ctx, LoginActivity.class);
        startActivity(i);
        ForgotPasswordActivity.this.finish();
    }

//    ProgressButtonComponent btn_forgot_reset,btn_forgot_cancel;
    Button btn_forgot_reset,btn_forgot_cancel;
    Context ctx;
    Credentials cred;
    String base_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ctx=ForgotPasswordActivity.this;
        cred=new Credentials(ctx);
        base_url = ctx.getResources().getString(R.string.base_url);
        forgot_email = findViewById(R.id.forgot_et_email);
        forgot_number = findViewById(R.id.forgot_et_phone);
        btn_forgot_reset = findViewById(R.id.btn_forgot_reset);
        btn_forgot_cancel = findViewById(R.id.btn_forgot_cancel);

        btn_forgot_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx,LoginActivity.class);
                startActivity(i);
                ForgotPasswordActivity.this.finish();
            }
        });
        btn_forgot_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = forgot_email.getText().toString();
                String number = forgot_number.getText().toString();
                if(!isValidEmail(email)){
                    new Utils().createAlert(ctx,"Ingrese un correo válido",1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            forgot_email.setError("Ingrese un correo válido");
                            forgot_email.requestFocus();
                        }
                    }, 1500);
                    return;
                }else if(number.isEmpty()){
                    new Utils().createAlert(ctx,"Ingrese su número de celular",1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            forgot_number.setError("Ingrese un número de celular");
                            forgot_number.requestFocus();
                        }
                    }, 1500);
                    return;
                }else if(number.length()<9){
                    new Utils().createAlert(ctx,"Ingrese un número válido",1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            forgot_number.setError("Ingrese un número de celular válido");
                            forgot_number.requestFocus();
                        }
                    }, 1500);
                    return;
                }else{
                }
            }
        });
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
