package com.solution.tecno.androidanimations.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.activities.FirstActivity;
import com.solution.tecno.androidanimations.adapters.AccountAdapter;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;


public class AccountsFragment extends Fragment {

    Context ctx;
    Credentials cred;
    ViewDialog viewDialog;
    String user_id;
    View v;
    String base_url;

    AccountAdapter adapter;
    RecyclerView activity;
    List<JSONObject> l=new ArrayList<>();

    public AccountsFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static AccountsFragment newInstance() {
        AccountsFragment fragment = new AccountsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this.getContext();
        cred = new Credentials(ctx);
        viewDialog = new ViewDialog((FirstActivity)ctx);
        base_url = ctx.getResources().getString(R.string.base_url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_accounts, container, false);
        user_id=cred.getUserId();
        cred.getUserLoginStatus();
        activity=v.findViewById(R.id.recycler_view_accounts);
        activity.setLayoutManager(new LinearLayoutManager(ctx));
        adapter=new AccountAdapter(l);

        if(cred.getNetworkStatus().equals("1")){
            getAccounts(user_id);
        }else{
            getAccountsOffline();
        }
        activity.setAdapter(adapter);
        return v;
    }

    public void getAccounts(final String user_id) {
        viewDialog.showDialog();
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+Integer.parseInt(user_id);
        String url = base_url+"getUserAccounts.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cred.setJsonResponse(response);
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            l.clear();
                            for(int i=0;i<ja.size();i++){
                                JSONObject item=(JSONObject)ja.get(i);
                                l.add(item);
                            }
                            adapter.notifyDataSetChanged();
                            viewDialog.hideDialog(0);
                            //wait 3 seconds to hide success dialog
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
//                                    asd.hide();
                                    verifiedPhoneNumber();
                                }
                            }, 1500);   //3 seconds
                        } catch (Exception e) {
                            cred.registerError(e.getMessage(),user_id);
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx,e.getMessage(),1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cred.registerError(error.getMessage(),user_id);
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx, error.getMessage(),1);
                        // error
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

    public void getAccountsOffline() {
        System.out.println("offline");
        viewDialog.showDialog();

        JSONParser jp = new JSONParser();
        try {
            JSONArray ja=(JSONArray)jp.parse(cred.getJsonResponse());
            l.clear();
            for(int i=0;i<ja.size();i++){
                JSONObject item=(JSONObject)ja.get(i);
                l.add(item);
            }
            adapter.notifyDataSetChanged();
            viewDialog.hideDialog(0);
        } catch (Exception e) {
            cred.registerError(e.getMessage(),user_id);
            viewDialog.hideDialog(0);
            new Utils().createAlert(ctx, e.getMessage(),1);
        }
    }

    public void verifiedPhoneNumber(){
        if(cred.getPhoneNumber().equals("0")){
            viewDialog.hideDialog(0);
            new Utils().createAlert(ctx, "Actualiza tu número de celular para disfrutar todas las funciones",1);
        }else{
            viewDialog.hideDialog(0);
            verifiedEmail();
        }
    }

    public void verifiedEmail(){
        if(cred.getEmail().equals("0")){
            viewDialog.hideDialog(0);
            new Utils().createAlert(ctx, "Actualiza tu email para disfrutar de todas las funciones",2);
        }else{
            viewDialog.hideDialog(0);
        }
    }
}
