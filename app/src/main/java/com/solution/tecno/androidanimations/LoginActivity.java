package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alirezaahmadi.progressbutton.ProgressButtonComponent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LoginActivity extends AppCompatActivity {

    ProgressButtonComponent login_btn;
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
}


