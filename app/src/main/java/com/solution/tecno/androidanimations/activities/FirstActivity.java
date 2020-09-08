package com.solution.tecno.androidanimations.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.solution.tecno.androidanimations.fragments.AccountsFragment;
import com.solution.tecno.androidanimations.Firebase.Constants;
import com.solution.tecno.androidanimations.fragments.ProfileFragment;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirstActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    Context ctx;
    ViewDialog viewDialog;
    Toolbar toolbar;
    DrawerLayout drawer;
    String user_id,full_name,user_name,user_photo;
    String base_url= Constants.BASE_URL;
    Credentials cred;
    TextView header_name,header_username;
    CircleImageView header_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ctx=FirstActivity.this;
        cred = new Credentials(ctx);
        viewDialog = new ViewDialog(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("fcm_instance", "getInstanceId failed", task.getException());
                    return;
                }
                String token = task.getResult().getToken();
                String msg = token;
                Log.e("fcm_token", msg);
                new Utils(ctx).updateToken(msg);
            }
        });
        user_id=cred.getUserId();
        full_name=cred.getFullName();
        user_name=cred.getUserName();
        user_photo=cred.getUserPhoto();
        if(user_photo==""){
            user_photo=Constants.BASE_PHOTO;
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                toolbar.setNavigationIcon(R.drawable.ic_close_drawer);
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                toolbar.setNavigationIcon(R.drawable.ic_menu_white);
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                if(drawer.isDrawerOpen(Gravity.START)){
                    drawer.closeDrawer(Gravity.START);
                    toolbar.setNavigationIcon(R.drawable.ic_menu_white);
                }else{
                    drawer.openDrawer(Gravity.START);
                    toolbar.setNavigationIcon(R.drawable.ic_close_drawer);
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        header_name = headerView.findViewById(R.id.nav_header_name);
        header_username = headerView.findViewById(R.id.nav_header_username);
        header_photo = headerView.findViewById(R.id.nav_header_photo);
        header_name.setText(full_name);
        header_username.setText(user_name);
        Picasso.get()
                .load(user_photo)
                .resize(200, 200)
                .centerCrop()
                .into(header_photo);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fr= AccountsFragment.newInstance();
        fragmentTransaction.replace(R.id.container,fr);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifpest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_log_out:
                new Utils().createAlert(ctx,"Cerrando Sesión",1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        logout(user_id,"0");
                    }
                }, 1500);
                break;
            case R.id.action_refresh_accounts:
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Fragment fr= AccountsFragment.newInstance();
                fragmentTransaction.replace(R.id.container,fr);
                fragmentTransaction.commit();
                break;
            case R.id.action_new_account:
                if(cred.getNetworkStatus().equals("0")){
                    new Utils().createAlert(ctx,"Red no disponible",1);
                }else{
                    final View layout= LayoutInflater.from(ctx).inflate(R.layout.new_account_view,null);
                    new AlertDialog.Builder(ctx)
                            .setTitle("Nueva cuenta")
                            .setMessage("Añade una nueva cuenta para compartirla rápidamente")
                            .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    EditText diag_et_bank=layout.findViewById(R.id.diag_et_bank);
                                    EditText diag_et_number=layout.findViewById(R.id.diag_et_account);
                                    EditText diag_et_user_name=layout.findViewById(R.id.diag_et_titular);
                                    EditText diag_et_cci=layout.findViewById(R.id.diag_et_cci);
                                    String bank = diag_et_bank.getText().toString();
                                    String number = diag_et_number.getText().toString();
                                    String user_name = diag_et_user_name.getText().toString();
                                    String cci = diag_et_cci.getText().toString();
                                    addAccount(user_id,bank,number,user_name,cci);
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setView(layout) // Old standard padding: .setCustomView(your_custom_view, 20, 20, 20, 0)
                            //.setCustomView(your_custom_view, 10, 20, 10, 20) // int left, int top, int right, int bottom
                            .show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addAccount(final String user_id, String bank, String number,String user_name,String cci) {
        viewDialog.showDialog();
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+Integer.parseInt(user_id)+"" +
                "&bank="+Uri.encode(bank)+
                "&account="+number+
                "&name="+user_name+
                "&cci="+cci;
        String url = base_url+"addAccount.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true")){
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx, "!Registro exitoso!",2);

                        }else{
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx, "Ocurrió un error al registrar su cuenta!\nIntentelo nuevamente",2);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        cred.registerError(error.getMessage(),user_id);
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx, error.getMessage(),1);
                    }
                }
        );
        queue.add(postRequest);
    }

    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }




    @SuppressLint("WrongConstant")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fr;

        switch (id){
            case R.id.menu_accounts:
                fr=AccountsFragment.newInstance();
                fragmentTransaction.replace(R.id.container,fr);
            break;
            case R.id.menu_profile:
                if(cred.getNetworkStatus().equals("1")){
                    fr= ProfileFragment.newInstance();
                    fragmentTransaction.replace(R.id.container,fr);
                }else{
                    new Utils().createAlert(ctx,"Red no disponible",1);
                }

            break;
        }
        drawer.closeDrawer(Gravity.START);
        fragmentTransaction.commit();
        return true;
    }

    public void updateHeader(String name,String username,String photo) {
        header_name.setText(name); //str OR whatvever you need to set.
        header_username.setText(username); //str OR whatvever you need to set.
        Picasso.get()
                .load(photo)
                .resize(200, 200)
                .centerCrop()
                .into(header_photo);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void logout(final String user_id, String status) {
        viewDialog.showDialog();
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+user_id+"&status="+status;
        String url = base_url+"login.php"+params;
        System.out.println("*** logout: "+url);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            viewDialog.hideDialog(1.5);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    cred.logout();
                                }
                            }, 1500);


                        } catch (Exception e) {
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx,e.getMessage(),1);
                            System.out.println(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx,error.getMessage(),1);
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

}
