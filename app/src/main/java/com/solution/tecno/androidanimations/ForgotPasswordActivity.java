package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.alirezaahmadi.progressbutton.ProgressButtonComponent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
                }else if(number.isEmpty()){
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
                }else if(number.length()<9){
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
                }else{
                    apd.setMessage("Validando...");
                    apd.show();
                    resetPassword(email,number);
                }
            }
        });
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void resetPassword(final String email, String phone) {

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?email="+email+
                "&phone="+phone;
        String url = base_url+"resetPassword.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try{
                            JSONArray ja=(JSONArray)jp.parse(response);
                            JSONObject item=(JSONObject)ja.get(0);
                            String find=item.get("encontrado").toString();
                            if(find.equals("0")){
                                apd.hide();
                                aed.setMessage("No se pudo encontrar el usuario ");
                                aed.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        aed.hide();
                                    }
                                }, 1500);

                            }else{
                                final String find_id=item.get("id").toString();
                                final String find_email=item.get("email").toString();
                                apd.hide();
                                asd.setMessage("Hemos enviado un email al correo registrado!");
                                asd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        asd.hide();
                                        updatePassword(find_id,find_email);
                                        Intent i=new Intent(ctx,LoginActivity.class);
                                        startActivity(i);
                                        ForgotPasswordActivity.this.finish();
                                    }
                                }, 1500);
                            }
                        }catch (Exception e){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        apd.hide();
                        aed.setMessage("Ocurrió un error al registrar su cuenta!\n"+error.getMessage());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 1500);
                    }
                }
        );
        queue.add(postRequest);
    }

    public void updatePassword(final String id,final String email) {

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?id="+id+"&email="+email;
        String url = base_url+"sendMail.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try{
                            JSONArray ja=(JSONArray)jp.parse(response);
                            JSONObject item=(JSONObject)ja.get(0);
                            String find=item.get("encontrado").toString();
                            if(find.equals("0")){
                                apd.hide();
                                aed.setMessage("No se pudo encontrar el usuario ");
                                aed.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        aed.hide();
                                    }
                                }, 1500);

                            }else{
                                apd.hide();
                                asd.setMessage("Hemos enviado un email al correo registrado!");
                                asd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        asd.hide();
                                    }
                                }, 1500);
                            }
                        }catch (Exception e){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        apd.hide();
                        aed.setMessage("Ocurrió un error al registrar su cuenta!\n"+error.getMessage());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 1500);
                    }
                }
        );
        queue.add(postRequest);
    }
}
