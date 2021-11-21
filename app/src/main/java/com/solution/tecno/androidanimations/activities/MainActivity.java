package com.solution.tecno.androidanimations.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.solution.tecno.androidanimations.Firebase.Constants;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.databinding.ActivityMainBinding;
import com.solution.tecno.androidanimations.fragments.AccountsFragment;
import com.solution.tecno.androidanimations.fragments.ProfileFragment;
import com.solution.tecno.androidanimations.model.Tarjeta;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Preferences;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    Context ctx;
    ViewDialog viewDialog;
    Utils utils;
    Toolbar toolbar;
    DrawerLayout drawer;
    String user_id,full_name,email;
    Credentials cred;
    TextView header_name,header_username;
    CircleImageView header_photo;
    ActivityMainBinding binding;
    Tarjeta tarjeta;
    AlertDialog alertDialog;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ctx= MainActivity.this;
        cred = new Credentials(ctx);
        utils = new Utils(ctx);
        viewDialog = new ViewDialog(this);

        user_id=cred.getData(Preferences.USER_ID);
        full_name=cred.getData(Preferences.USER_NAME);
        email=cred.getData(Preferences.USER_EMAIL);

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

        toolbar.setNavigationOnClickListener(v -> {
            if(drawer.isDrawerOpen(Gravity.START)){
                drawer.closeDrawer(Gravity.START);
                toolbar.setNavigationIcon(R.drawable.ic_menu_white);
            }else{
                drawer.openDrawer(Gravity.START);
                toolbar.setNavigationIcon(R.drawable.ic_close_drawer);
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        header_name = headerView.findViewById(R.id.nav_header_name);
        header_username = headerView.findViewById(R.id.nav_header_username);
        header_photo = headerView.findViewById(R.id.nav_header_photo);
        header_name.setText(full_name);
        Picasso.get()
                .load(Constants.BASE_PHOTO)
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
        int id = item.getItemId();
        switch (id){
            case R.id.action_log_out:
                new Utils().createAlert(ctx,"Cerrando Sesión",1);
                new Handler().postDelayed(() -> {
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
                }
                else{
                    viewDialog.showDialog();
                    final View layout= LayoutInflater.from(ctx).inflate(R.layout.new_account_view,null);
                    alertDialog = new AlertDialog.Builder(ctx)
                            .setTitle("Nueva cuenta")
                            .setMessage("Añade una nueva cuenta para compartirla rápidamente")
                            .setPositiveButton("Agregar", (dialog, which) -> {
                                dialog.dismiss();
                                EditText diag_et_banco = layout.findViewById(R.id.diag_et_banco);
                                EditText diag_et_moneda = layout.findViewById(R.id.diag_et_moneda);
                                EditText diag_et_number=layout.findViewById(R.id.diag_et_account);
                                EditText diag_et_user_name=layout.findViewById(R.id.diag_et_titular);
                                EditText diag_et_cci=layout.findViewById(R.id.diag_et_cci);

                                String banco = diag_et_banco.getText().toString();
                                String moneda = diag_et_moneda.getText().toString();
                                String cuenta = diag_et_number.getText().toString();
                                String titular = diag_et_user_name.getText().toString();
                                String cci = diag_et_cci.getText().toString();

                                tarjeta = new Tarjeta();
                                tarjeta.setBanco(banco);
                                tarjeta.setMoneda(moneda);
                                tarjeta.setCuenta(cuenta);
                                tarjeta.setTitular(titular);
                                tarjeta.setCci(cci);
                                tarjeta.setUserId(cred.getData(Preferences.USER_ID));
                                createAccount(tarjeta);
                            })
                            .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                            .setView(layout)
                            .show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void createAccount(Tarjeta tarjeta)
    {
        alertDialog.dismiss();
        DatabaseReference reference = utils.getDatabaseReference(Preferences.FIREBASE_TARJETAS);
        String key = reference.push().getKey();
        tarjeta.setId(key);
        reference.child(key).setValue(tarjeta).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                viewDialog.hideDialog(0);
                if(task.isSuccessful()){
                    Toast.makeText(ctx, "Cuenta agregada", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ctx, "Cuenta agregada", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

}
