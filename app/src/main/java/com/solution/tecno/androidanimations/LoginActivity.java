package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alirezaahmadi.progressbutton.ProgressButtonComponent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LoginActivity extends AppCompatActivity {

    ProgressButtonComponent login_btn,register_btn;
    EditText et_user,et_psw;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx=LoginActivity.this;
        et_user=findViewById(R.id.et_user);
        et_psw=findViewById(R.id.et_psw);
        login_btn = findViewById(R.id.btn_login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_btn.setInProgress(true);
                final String username=et_user.getText().toString();
                final String psw=et_psw.getText().toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        login_btn.setInProgress(false);
                        login(username,psw);
                    }
                }, 3000);
            }
        });

        register_btn = findViewById(R.id.btn_register);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View layout=LayoutInflater.from(ctx).inflate(R.layout.new_user_view,null);


                new MaterialStyledDialog.Builder(ctx)
                        .setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle("Nuevo Usuario")
                        .setDescription("Registrate y disfruta de tus cuentas en un solo lado")
                        .setPositiveText("Registrar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                EditText diag_et_name=layout.findViewById(R.id.diag_et_name);
                                EditText diag_et_user=layout.findViewById(R.id.diag_et_user);
                                EditText diag_et_psw=layout.findViewById(R.id.diag_et_password);

                                String name = diag_et_name.getText().toString();
                                String username = diag_et_user.getText().toString();
                                String psw = diag_et_psw.getText().toString();

                                Toast.makeText(ctx,name+" - "+username+" - "+psw,Toast.LENGTH_SHORT).show();
                                register(username,psw,name);

                                dialog.dismiss();
                            }
                        })
                        .setNegativeText("Cancelar")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setCustomView(layout) // Old standard padding: .setCustomView(your_custom_view, 20, 20, 20, 0)
                        //.setCustomView(your_custom_view, 10, 20, 10, 20) // int left, int top, int right, int bottom
                        .show();
            }
        });
    }

    public void login(String user,String psw) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String params="?username="+user+"&psw="+psw;
        String url = "http://taimu.pe/php_connection/app_bancos/login.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONParser jp = new JSONParser();

                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            JSONObject item=(JSONObject)ja.get(0);
                            String id=item.get("id").toString();

                            save_credentials(id);

                            Intent i=new Intent(ctx,FirstActivity.class);
                            startActivity(i);

                        } catch (Exception e) {
                            Toast.makeText(ctx,"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(ctx, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest);
    }

    public void save_credentials(String user_id){
        SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("user_id",user_id);
        Ed.commit();
    }

    public void register(final String user,final String psw,String name) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?username="+user+"&psw="+psw+"&name="+ Uri.encode(name);
        String url = "http://taimu.pe/php_connection/app_bancos/registerUser.php"+params;
        Log.d("*********",url);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(response.equals("true")){
                                login(user,psw);
                            }else{
                                Toast.makeText(ctx,"Ocurrió un error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ctx,"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(ctx, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest);
    }
}


