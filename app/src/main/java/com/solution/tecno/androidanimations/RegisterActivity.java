package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

public class RegisterActivity extends AppCompatActivity {

    EditText reg_username,reg_psw,reg_full_name;
    Context ctx;
    ProgressButtonComponent reg_button,cancel_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ctx = this;

        reg_full_name = findViewById(R.id.diag_et_name);
        reg_username = findViewById(R.id.diag_et_user);
        reg_psw = findViewById(R.id.diag_et_password);

        reg_button = findViewById(R.id.reg_btn_register);
        cancel_button = findViewById(R.id.reg_btn_cancel);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx,LoginActivity.class);
                startActivity(i);
                RegisterActivity.this.finish();
            }
        });

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = reg_full_name.getText().toString();
                String username = reg_username.getText().toString();
                String psw = reg_psw.getText().toString();

                Toast.makeText(ctx, "click", Toast.LENGTH_SHORT).show();
                System.out.println("validation: "+(!name.isEmpty() && !username.isEmpty() && !psw.isEmpty()));

                if(name.isEmpty() && username.isEmpty() && psw.isEmpty()){
                    reg_full_name.setError("Complete el nombre");
                    reg_full_name.requestFocus();
                    reg_username.setError("Complete el usuario");
                    reg_username.requestFocus();
                    reg_psw.setError("Ingrese contraseña");
                    reg_psw.requestFocus();
                }
                if(name.isEmpty()){
                    reg_full_name.setError("Complete el nombre");
                    reg_full_name.requestFocus();
                }
                if(username.isEmpty()){
                    reg_username.setError("Complete el usuario");
                    reg_username.requestFocus();
                }
                if(psw.isEmpty()){
                    reg_psw.setError("Ingrese contraseña");
                    reg_psw.requestFocus();
                }

                if(!name.isEmpty() && !username.isEmpty() && !psw.isEmpty()){
                    Toast.makeText(ctx, "registro", Toast.LENGTH_SHORT).show();
                    register(username,psw,name);
                }
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
                            RegisterActivity.this.finish();

                        } catch (Exception e) {
                            Toast.makeText(ctx,"Usuario o Contraseña incorrecta", Toast.LENGTH_SHORT).show();
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
