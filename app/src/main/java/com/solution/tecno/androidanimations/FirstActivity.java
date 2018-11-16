package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.solution.tecno.androidanimations.Firebase.MyFirebaseInstanceIdService;

public class FirstActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    Context ctx;
    Toolbar toolbar;
    DrawerLayout drawer;
    String user_id,full_name,user_name;
    String base_url="https://www.jadconsultores.com.pe/php_connection/app/bancos_resumen/";
    Credentials cred;
    TextView header_name,header_username;

    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ctx=FirstActivity.this;
        cred = new Credentials(ctx);
        MyFirebaseInstanceIdService serv=new MyFirebaseInstanceIdService();
        serv.onTokenRefresh2(ctx);
        user_id=cred.getUserId();
        full_name=cred.getFullName();
        user_name=cred.getUserName();

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
        header_name.setText(full_name);
        header_username.setText(user_name);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fr=AccountsFragment.newInstance();
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
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_log_out){
            new Credentials(ctx).logout();
        }

        if(id == R.id.action_add_new_account){
            final View layout=LayoutInflater.from(ctx).inflate(R.layout.new_account_view,null);
            new MaterialStyledDialog.Builder(this)
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .setTitle("Nueva cuenta")
                    .setDescription("Añade una nueva cuenta para compartirla rápidamente")
                    .setPositiveText("Agregar")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            EditText diag_et_bank=layout.findViewById(R.id.diag_et_bank);
                            EditText diag_et_number=layout.findViewById(R.id.diag_et_account);
                            EditText diag_et_user_name=layout.findViewById(R.id.diag_et_titular);
                            EditText diag_et_cci=layout.findViewById(R.id.diag_et_cci);
                            String bank = diag_et_bank.getText().toString();
                            String number = diag_et_number.getText().toString();
                            String user_name = diag_et_user_name.getText().toString();
                            String cci = diag_et_cci.getText().toString();
                            apd.setMessage("Guardando...");
                            apd.show();
                            addAccount(user_id,bank,number,user_name,cci);
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

        return super.onOptionsItemSelected(item);
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
            case R.id.menu_contacts:
                fr=ContactFragment.newInstance();
                fragmentTransaction.replace(R.id.container,fr);
            break;
            case R.id.menu_profile:
                fr=ProfileFragment.newInstance();
                fragmentTransaction.replace(R.id.container,fr);
            break;
            default:
                fr=AccountsFragment.newInstance();
                fragmentTransaction.replace(R.id.container,fr);
            break;
        }
        drawer.closeDrawer(Gravity.START);
        fragmentTransaction.commit();
        return true;
    }

    public void addAccount(final String user_id, String bank, String number,String user_name,String cci) {

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
                            apd.hide();
                            asd.setMessage("Registro exitoso!\nDeslice hacia abajo para actualizar");
                            asd.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    asd.hide();
                                }
                            }, 1500);

                        }else{
                            apd.hide();
                            aed.setMessage("Ocurrió un error al registrar su cuenta!\nIntentelo nuevamente");
                            aed.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    aed.hide();
                                }
                            }, 1500);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        apd.hide();
                        aed.setMessage("Ocurrió un error al registrar su cuenta!\n"+error.getMessage());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 1500);
                    }
                }
        );
        queue.add(postRequest);
    }

    public void updateHeader(String name,String username) {
        header_name.setText(name); //str OR whatvever you need to set.
        header_username.setText(username); //str OR whatvever you need to set.
    }

}
