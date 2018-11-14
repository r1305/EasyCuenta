package com.solution.tecno.androidanimations.Firebase;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.solution.tecno.androidanimations.Credentials;

/**
 * Created by Julian on 25/02/2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    String user_id;
    String base_url="https://www.jadconsultores.com.pe/php_connection/app/bancos_resumen/";
    //this method will be called
    //when the token is generated
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
    }

    public void onTokenRefresh2(Context ctx) {
        super.onTokenRefresh();
        user_id=new Credentials(ctx).getUserId();

        //now we will have the token
        final String token = FirebaseInstanceId.getInstance().getToken();

        //for now we are displaying the token in the log
        //copy it as this method is called only when the new token is generated
        //and usually new token is only generated when the app is reinstalled or the data is cleared
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?id="+user_id+"&fcm="+token;
        String url = base_url+"updateFCM.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        // Access the RequestQueue through your singleton class.
        queue.add(postRequest);
    }
}
