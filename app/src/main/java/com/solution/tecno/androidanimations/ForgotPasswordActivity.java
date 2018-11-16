package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.alirezaahmadi.progressbutton.ProgressButtonComponent;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText forgot_email,forgot_number;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ctx,LoginActivity.class);
        startActivity(i);
        ForgotPasswordActivity.this.finish();
    }

    ProgressButtonComponent btn_forgot_reset,btn_forgot_cancel;
    Context ctx;
    Credentials cred;
    String base_url="https://www.jadconsultores.com.pe/php_connection/app/bancos_resumen/";
    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ctx=ForgotPasswordActivity.this;
        cred=new Credentials(ctx);
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
                    aed.setMessage("Ingrese un correo válido");
                    aed.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            aed.hide();
                            forgot_email.setError("Ingrese un correo válido");
                            forgot_email.requestFocus();
                        }
                    }, 2500);
                    return;
                }

                if(!number.isEmpty()){
                    aed.setMessage("Ingrese su número de celular");
                    aed.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            aed.hide();
                            forgot_number.setError("Ingrese un número de celular");
                            forgot_number.requestFocus();
                        }
                    }, 2500);
                    return;
                }

                if(number.length()<9){
                    aed.setMessage("Ingrese un número válido");
                    aed.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            aed.hide();
                            forgot_number.setError("Ingrese un número de celular válido");
                            forgot_number.requestFocus();
                        }
                    }, 2500);
                    return;
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
