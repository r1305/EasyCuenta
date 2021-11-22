package com.solution.tecno.androidanimations.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.databinding.ActivityRegisterBinding;
import com.solution.tecno.androidanimations.model.Usuario;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Preferences;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    Context ctx;
    Credentials cred;
    ViewDialog viewDialog;
    Utils utils;
    int password_visible = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ctx = this;
        cred = new Credentials(ctx);
        utils = new Utils(ctx);
        viewDialog = new ViewDialog(this);

        binding.regBtnCancel.setOnClickListener(v -> {
            Intent i = new Intent(ctx,LoginActivity.class);
            startActivity(i);
            RegisterActivity.this.finish();
        });

        binding.regBtnRegister.setOnClickListener(v -> {
            String name = binding.diagEtName.getText().toString();
            String email = binding.diagEtEmail.getText().toString();
            String psw = binding.diagEtPassword.getText().toString();
            String phone = binding.diagEtPhone.getText().toString();

            if(name.isEmpty() && psw.isEmpty()){
                binding.diagEtName.setError("Complete el nombre");
                binding.diagEtName.requestFocus();
                binding.diagEtPassword.setError("Ingrese contraseña");
                binding.diagEtPassword.requestFocus();
            }

            if(name.isEmpty()){
                binding.diagEtName.setError("Complete el nombre");
                binding.diagEtName.requestFocus();
            }

            if(email.isEmpty()){
                binding.diagEtEmail.setError("Complete el correo");
                binding.diagEtEmail.requestFocus();
            }else if(!isValidEmail(email)){
                binding.diagEtEmail.setError("Ingrese un correo válido");
                binding.diagEtEmail.requestFocus();
            }

            if(phone.isEmpty()){
                binding.diagEtPhone.setError("Complete su celular");
                binding.diagEtPhone.requestFocus();
            }else if(phone.length()<9){
                binding.diagEtPhone.setError("Ingrese un número de celular válido");
                binding.diagEtPhone.requestFocus();
            }
            if(psw.isEmpty()){
                binding.diagEtPassword.setError("Ingrese contraseña");
                binding.diagEtPassword.requestFocus();
            }

            if(!name.isEmpty() &&
                    isValidPhone(phone) &&
                    !psw.isEmpty() &&
                    isValidEmail(email)
            ){
                register();
            }
        });

        binding.showHidePassword.setOnClickListener(v -> {
            if(password_visible==0){
                password_visible=1;
                binding.showHidePassword.setImageResource(R.drawable.icon_invisible_password);
                binding.diagEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                password_visible=0;
                binding.showHidePassword.setImageResource(R.drawable.icon_visible_password);
                binding.diagEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            binding.diagEtPassword.setSelection(binding.diagEtPassword.getText().length());
        });
    }


    public final static boolean isValidPhone(CharSequence target)
    {
        if(target==null)
            return false;

        if(target.toString().isEmpty())
            return false;


        if(target.length()<9)
            return false;


        return Patterns.PHONE.matcher(target).matches();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        if(target.toString().isEmpty())
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    void register(){
        viewDialog.showDialog("");
        Usuario conductor = new Usuario();
        FirebaseAuth auth = utils.initFirebaseAuth();
        auth.createUserWithEmailAndPassword(binding.diagEtEmail.getText().toString(),binding.diagEtPassword.getText().toString())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        viewDialog.showSuccess("¡Registro exitoso!");
                        FirebaseUser user = auth.getCurrentUser();
                        conductor.setKey(user.getUid());
                        conductor.setCelular(binding.diagEtPhone.getText().toString());
                        createUser(conductor);
                    }else{
                        viewDialog.showFail("¡Usuario ya registrado!");
                        viewDialog.hideDialog(3);
                    }
                });
    }

    void createUser(Usuario usuario){
        DatabaseReference database = utils.getDatabaseReference(Preferences.FIREBASE_USUARIOS);
        usuario.setNombre(binding.diagEtName.getText().toString());
        usuario.setCelular(binding.diagEtPhone.getText().toString());
        usuario.setCorreo(binding.diagEtEmail.getText().toString());
        usuario.setPassword(binding.diagEtPassword.getText().toString());
        database.child(usuario.getKey()).setValue(usuario).addOnSuccessListener(unused -> {
            cred.saveData(Preferences.LOGIN,"1");
            cred.saveData(Preferences.USER_ID,usuario.getKey());
            cred.saveData(Preferences.USER_EMAIL,usuario.getCorreo());
            cred.saveData(Preferences.USER_PHONE,usuario.getCelular());
            cred.saveData(Preferences.USER_NAME,usuario.getNombre());
            viewDialog.hideDialog(2);
            new Handler().postDelayed(() -> {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }, 2000);
        });

    }
}
