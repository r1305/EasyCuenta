package com.solution.tecno.androidanimations.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

    public void resetPassword(final String email, String phone)
    {
        viewDialog.showDialog();
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
                                viewDialog.hideDialog(0);
                                new Utils().createAlert(ctx,"No se pudo encontrar el usuario",1);
                            }else{
                                final String find_id=item.get("id").toString();
                                final String find_email=item.get("email").toString();
                                viewDialog.hideDialog(0);
                                new Utils().createAlert(ctx,"Hemos enviado un email al correo registrado!",2);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        updatePassword(find_id,find_email);
                                        Intent i=new Intent(ctx,LoginActivity.class);
                                        startActivity(i);
                                        ForgotPasswordActivity.this.finish();
                                    }
                                }, 1500);
                            }
                        }catch (Exception e){
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx,e.getMessage(),1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx,error.getMessage(),1);
                    }
                }
        );
        queue.add(postRequest);
    }

    public void updatePassword(final String id,final String email)
    {
        viewDialog.showDialog();
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
                                viewDialog.hideDialog(0);
                                new Utils().createAlert(ctx,"No se pudo encontrar el usuario ",1);
                            }else{
                                viewDialog.hideDialog(0);
                                new Utils().createAlert(ctx,"Hemos enviado un email al correo registrado!",1);
                            }
                        }catch (Exception e){
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx,e.getMessage(),1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx,error.getMessage(),1);
                    }
                }
        );
        queue.add(postRequest);
    }
}
