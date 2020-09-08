package com.solution.tecno.androidanimations.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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

public class LoginActivity extends AppCompatActivity {

//    ProgressButtonComponent login_btn,register_btn;
    Button login_btn,register_btn;
    EditText et_user,et_psw;
    TextView forgot_password;
    Context ctx;
    ViewDialog viewDialog;
    Credentials cred;
    String base_url;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx=LoginActivity.this;
        cred=new Credentials(ctx);
        viewDialog = new ViewDialog(this);
        base_url = ctx.getResources().getString(R.string.base_url);

        et_user=findViewById(R.id.et_user);
        et_psw=findViewById(R.id.et_psw);
        forgot_password = findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, ForgotPasswordActivity.class);
                startActivity(i);
                LoginActivity.this.finish();
            }
        });
        login_btn = findViewById(R.id.btn_login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cred.getNetworkStatus().equals("1")){
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
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                login(username,psw);
                            }
                        }, 3000);
                    }
                }else{
                    new Utils().createAlert(ctx,"Red no disponible",1);
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
        viewDialog.showDialog();
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
                            String email=item.get("email").toString();
                            String user_photo=item.get("profile_photo").toString();
                            String login_status=item.get("login_status").toString();

                            cred.save_credentials(id,full_name,user_name,phone_number,email,user_photo,login_status);
                            viewDialog.hideDialog(1.5);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i=new Intent(ctx,FirstActivity.class);
                                    startActivity(i);
                                    LoginActivity.this.finish();
                                }
                            }, 1500);


                        } catch (Exception e) {
                            cred.registerError(e.getMessage(),user);
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx,e.getMessage(),1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cred.registerError(error.getMessage(),user);
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx,error.getMessage(),1);
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


