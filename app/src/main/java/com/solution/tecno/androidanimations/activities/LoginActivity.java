package com.solution.tecno.androidanimations.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.databinding.ActivityLoginBinding;
import com.solution.tecno.androidanimations.model.Usuario;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Preferences;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;

public class LoginActivity extends AppCompatActivity {

    Context ctx;
    ViewDialog viewDialog;
    Credentials cred;
    Utils utils;
    ActivityLoginBinding binding;
    int password_visible = 0;

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
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ctx=LoginActivity.this;
        cred=new Credentials(ctx);
        viewDialog = new ViewDialog(this);
        utils = new Utils(ctx);

        binding.forgotPassword.setOnClickListener(v -> {
            if(binding.etUser.getText().toString().isEmpty())
            {
                viewDialog.showDialog("");
                viewDialog.showFail("Debes ingresar tu correo primero");
                viewDialog.hideDialog(3);
            }else{
                viewDialog.showDialog("Restableciendo contraseña...");
                resetPassword();
            }

        });
        binding.btnLogin.setOnClickListener(v -> {
            if(cred.getNetworkStatus().equals("1")){
                final String username=binding.etUser.getText().toString();
                final String psw=binding.etPsw.getText().toString();

                if(username.isEmpty() && psw.isEmpty()){
                    binding.etUser.setError("Complete el usuario");
                    binding.etUser.requestFocus();
                    binding.etPsw.setError("Ingrese contraseña");
                    binding.etPsw.requestFocus();
                    return;
                }
                if(username.isEmpty()){
                    binding.etUser.setError("Complete el usuario");
                    binding.etUser.requestFocus();
                    return;
                }
                if(!isValidEmail(username))
                {
                    binding.etUser.setError("Ingrese un correo válido");
                    binding.etUser.requestFocus();
                    return;
                }
                if(psw.isEmpty()){
                    binding.etPsw.setError("Ingrese contraseña");
                    binding.etPsw.requestFocus();
                    return;
                }

                viewDialog.showDialog("");
                new Handler().postDelayed(this::login,3000);
            }else{
                new Utils().createAlert(ctx,"Red no disponible",1);
            }
        });

        binding.btnRegister.setOnClickListener(v -> {
            Intent i = new Intent(ctx,RegisterActivity.class);
            startActivity(i);
            LoginActivity.this.finish();
        });

        binding.showHidePassword.setOnClickListener(v -> {
            if(password_visible==0){
                password_visible=1;
                binding.showHidePassword.setImageResource(R.drawable.icon_invisible_password);
                binding.etPsw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                password_visible=0;
                binding.showHidePassword.setImageResource(R.drawable.icon_visible_password);
                binding.etPsw.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            binding.etPsw.setSelection(binding.etPsw.getText().length());
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    void login(){
        FirebaseAuth auth = utils.initFirebaseAuth();
        auth.signInWithEmailAndPassword(binding.etUser.getText().toString(),binding.etPsw.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        viewDialog.showSuccess("");
                        LoginActivity.this.getUserDetails(binding.etUser.getText().toString());
                        new Handler().postDelayed(() -> {
                            cred.saveData(Preferences.LOGIN, "1");
                            viewDialog.hideDialog(0);
                            LoginActivity.this.startActivity(new Intent(ctx, MainActivity.class));
                            LoginActivity.this.finish();
                        }, 3000);
                    } else {
                        viewDialog.showFail("Credenciales incorrectas");
                        viewDialog.hideDialog(3);
                    }
                });
    }

    void resetPassword()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = binding.etUser.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("es");
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                utils.showNotification(ctx,"Actualización de contraseña","Se ha enviado un correo para restablecer la contraseña");
                viewDialog.showSuccess("");
                viewDialog.hideDialog(3);
            }else{
                utils.showNotification(ctx,"Actualización de contraseña","No se encontró usuario");
                viewDialog.showFail("NO se encontró tu usuario");
            }
        });
    }

    void getUserDetails(final String email)
    {
        DatabaseReference reference = utils.getDatabaseReference(Preferences.FIREBASE_USUARIOS);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                usuario.setKey(snapshot.getKey());
                if(usuario.getCorreo().equals(email)){
                    cred.saveData(Preferences.USER_ID,usuario.getKey());
                    cred.saveData(Preferences.USER_NAME,usuario.getNombre());
                    cred.saveData(Preferences.USER_PHONE,usuario.getCelular());
                    cred.saveData(Preferences.USER_EMAIL,usuario.getCorreo());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}


