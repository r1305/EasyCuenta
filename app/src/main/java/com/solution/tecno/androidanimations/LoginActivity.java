package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alirezaahmadi.progressbutton.ProgressButtonComponent;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LoginActivity extends AppCompatActivity {

    ProgressButtonComponent login_btn,register_btn;
    EditText et_user,et_psw;
    TextView forgot_password;
    Context ctx;
    Credentials cred;
    String base_url="https://www.jadconsultores.com.pe/php_connection/app/bancos_resumen/";
    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx=LoginActivity.this;
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

        et_user=findViewById(R.id.et_user);
        et_psw=findViewById(R.id.et_psw);
        forgot_password = findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx,ForgotPasswordActivity.class);
                startActivity(i);
                LoginActivity.this.finish();
            }
        });
        login_btn = findViewById(R.id.btn_login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username=et_user.getText().toString();
                final String psw=et_psw.getText().toString();

                if(username.isEmpty() && psw.isEmpty()){
                    et_user.setError("Complete el usuario");
                    et_user.requestFocus();
                    et_psw.setError("Ingrese contraseña");
                    et_psw.requestFocus();
                    return;
                }
                if(username.isEmpty()){
                    et_user.setError("Complete el usuario");
                    et_user.requestFocus();
                    return;
                }
                if(psw.isEmpty()){
                    et_psw.setError("Ingrese contraseña");
                    et_psw.requestFocus();
                    return;
                }

                if(!username.isEmpty() && !psw.isEmpty()){
                    apd.show();
                    login_btn.setInProgress(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            login_btn.setInProgress(false);
                            login(username,psw);
                        }
                    }, 3000);
                }
            }
        });

        register_btn = findViewById(R.id.btn_register);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx,RegisterActivity.class);
                startActivity(i);
                LoginActivity.this.finish();
            }
        });
    }

    public void login(final String user, String psw) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?username="+user+"&psw="+psw;
        String url = base_url+"login.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            JSONObject item=(JSONObject)ja.get(0);
                            String id=item.get("id").toString();
                            String full_name=item.get("full_name").toString();
                            String user_name=item.get("username").toString();
                            String phone_number=item.get("phone_number").toString();

                            cred.save_credentials(id,full_name,user_name,phone_number);
                            apd.hide();
                            asd.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    asd.hide();
                                    Intent i=new Intent(ctx,FirstActivity.class);
                                    startActivity(i);
                                    LoginActivity.this.finish();
                                }
                            }, 1500);


                        } catch (Exception e) {
                            apd.hide();
                            aed.setMessage(e.getMessage());
                            aed.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    aed.hide();
                                }
                            }, 2000);
                            System.out.println(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apd.hide();
                        aed.setMessage(error.toString());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 2000);
                        // error
                        Log.d("Error.Response", error.toString());
                        apd.hide();
                        aed.setMessage(error.toString());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 2000);
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}


