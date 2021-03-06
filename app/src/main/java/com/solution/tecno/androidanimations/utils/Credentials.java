package com.solution.tecno.androidanimations.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.solution.tecno.androidanimations.Firebase.Constants;
import com.solution.tecno.androidanimations.activities.LoginActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static android.content.Context.MODE_PRIVATE;

public class Credentials {
    public String user_id;
    public String phone_number;
    public String full_name;
    public String username;
    public String email;
    public String user_photo;
    public String login_status;
    public String json_response;
    public String network_status;
    private Context ctx;

    public Credentials(Context ctx) {
        this.ctx = ctx;
    }

    public String getUserId(){

        SharedPreferences sp1=ctx.getSharedPreferences("Login", MODE_PRIVATE);

        String user_id=sp1.getString("user_id", "0");
        this.user_id=user_id;
        return this.user_id;
    }

    public String getPhoneNumber(){

        SharedPreferences sp1=ctx.getSharedPreferences("Login", MODE_PRIVATE);

        String phone_number=sp1.getString("phone_number", "0");
        this.phone_number=phone_number;
        return this.phone_number;
    }

    public String getFullName(){

        SharedPreferences sp1=ctx.getSharedPreferences("Login", MODE_PRIVATE);

        String full_name=sp1.getString("full_name", "0");
        this.full_name=full_name;
        return this.full_name;
    }

    public String getUserName(){

        SharedPreferences sp1=ctx.getSharedPreferences("Login", MODE_PRIVATE);

        String username=sp1.getString("username", "0");
        this.username=username;
        return this.username;
    }

    public String getEmail(){

        SharedPreferences sp1=ctx.getSharedPreferences("Login", MODE_PRIVATE);

        String email=sp1.getString("email", "0");
        this.email=email;
        return this.email;
    }

    public String getUserPhoto(){
        SharedPreferences apl=ctx.getSharedPreferences("Login",MODE_PRIVATE);

        String user_photo=apl.getString("user_photo","0");
        this.user_photo=user_photo;
        return this.user_photo;
    }

    public String getLoginStatus(){
        SharedPreferences apl=ctx.getSharedPreferences("Login",MODE_PRIVATE);
        String login_status=apl.getString("login_status","0");
        this.login_status=login_status;
        return this.login_status;
    }

    public String getNetworkStatus(){
        isNetworkAvailable();
        SharedPreferences apl=ctx.getSharedPreferences("Login",MODE_PRIVATE);
        String network_status=apl.getString("network_status","0");
        this.network_status=network_status;
        return this.network_status;
    }

    public void setJsonResponse(String response){
        SharedPreferences sp=ctx.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("json_response",response);
        Ed.commit();
        this.json_response=response;
    }

    public String getJsonResponse(){
        SharedPreferences apl=ctx.getSharedPreferences("Login",MODE_PRIVATE);
        String json_response=apl.getString("json_response","0");
        this.json_response=json_response;
        return this.json_response;
    }

    public void logout(){

        SharedPreferences sp=ctx.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("user_id","0");
        Ed.putString("phone_number","0");
        Ed.putString("full_name","0");
        Ed.putString("username","0");
        Ed.putString("email","0");
        Ed.putString("login_status","0");
        Ed.commit();

        Intent i=new Intent(ctx, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, 0);
        ctx.startActivity(i);
    }

    public void save_credentials(String user_id,String full_name,String username,String phone_number,String email,String photo,String login_status){
        SharedPreferences sp=ctx.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putString("user_id",user_id);
        Ed.putString("full_name",full_name);
        Ed.putString("username",username);
        Ed.putString("phone_number",phone_number);
        Ed.putString("email",email);
        Ed.putString("user_photo",photo);
        Ed.putString("login_status",login_status);
        Ed.commit();
    }

    public void validateSession(){
        isNetworkAvailable();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(network_status.equals("1")){
                    getUserLoginStatus();
                    if(login_status.equals("0")){
                        new Utils().createAlert(ctx,"Su sesión ha caducado",1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                logout();
                            }
                        }, 1500);

                    }
                }
            }
        }, 3000);
    }

    private void isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        SharedPreferences sp=ctx.getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor Ed=sp.edit();

        if(activeNetworkInfo==null || !activeNetworkInfo.isConnected()){
            Ed.putString("network_status","0");
            this.network_status="0";
        }else{
            Ed.putString("network_status","1");
            this.network_status="1";
        }
        Ed.commit();
    }

    public void getUserLoginStatus() {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+getUserId();
        String url =  Constants.BASE_URL+"getLoginStatus.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            JSONObject item=(JSONObject)ja.get(0);
                            login_status=item.get("login_status").toString();
                            SharedPreferences sp=ctx.getSharedPreferences("Login", MODE_PRIVATE);
                            SharedPreferences.Editor Ed=sp.edit();
                            Ed.putString("login_status",login_status);
                            Ed.commit();
                            validateSession();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    public void registerError(final String error,final String user_id) {
        String base_url="https://www.jadconsultores.com.pe/php_connection/app/bancos_resumen/";
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+user_id+
                "&error="+ Uri.encode(error);
        String url = base_url+"registerError.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        registerError(error.toString(),user_id);
                    }
                }
        );
        queue.add(postRequest);
    }
}
