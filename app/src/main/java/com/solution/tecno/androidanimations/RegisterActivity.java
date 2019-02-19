package com.solution.tecno.androidanimations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static java.security.AccessController.getContext;

public class RegisterActivity extends AppCompatActivity {

    EditText reg_username,reg_psw,reg_full_name,reg_phone,reg_email;
    Context ctx;
    Credentials cred;
//    ProgressButtonComponent reg_button,cancel_button;
    Button reg_button,cancel_button;
    String base_url="https://www.jadconsultores.com.pe/php_connection/app/bancos_resumen/";
    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ctx = this;
        cred = new Credentials(ctx);

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

        reg_full_name = findViewById(R.id.diag_et_name);
        reg_username = findViewById(R.id.diag_et_user);
        reg_psw = findViewById(R.id.diag_et_password);
        reg_phone = findViewById(R.id.diag_et_phone);
        reg_email = findViewById(R.id.diag_et_email);

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
                String email = reg_email.getText().toString();
                String psw = reg_psw.getText().toString();
                String phone = reg_phone.getText().toString();

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

                if(email.isEmpty()){
                    reg_email.setError("Complete el correo");
                    reg_email.requestFocus();
                }else if(!isValidEmail(email)){
                    reg_email.setError("Ingrese un correo válido");
                    reg_email.requestFocus();
                }

                if(phone.isEmpty()){
                    reg_phone.setError("Complete su celular");
                    reg_phone.requestFocus();
                }else if(phone.length()<9){
                    reg_phone.setError("Ingrese un número de celular válido");
                    reg_phone.requestFocus();
                }
                if(psw.isEmpty()){
                    reg_psw.setError("Ingrese contraseña");
                    reg_psw.requestFocus();
                }

                if(!name.isEmpty() && !username.isEmpty() &&
                        (!phone.isEmpty() && phone.length()>=9) &&
                        !psw.isEmpty() && !email.isEmpty() && isValidEmail(email)){
                    apd.setMessage("Registrando...");
                    apd.show();
                    validatePhone(username,psw,name,phone,email);
                }
            }
        });
    }

    public void validatePhone(final String user,final String psw,final String name,final String phone,final String email) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?phone="+Uri.encode(phone);
        String url = base_url+"findPhoneNumber.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            JSONObject item=(JSONObject)ja.get(0);
                            int encontrado=Integer.parseInt(item.get("encontrado").toString());
                            if(encontrado==0){
                                register(user,psw,name,phone,email);
                            }else{
                                apd.hide();
                                aed.setMessage("# Celular ya registrado");
                                aed.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        aed.hide();
                                        reg_phone.setError("Número ya registrado");
                                        reg_phone.requestFocus();
                                    }
                                }, 3000);
                            }
                        } catch (Exception e) {
                            apd.hide();
                            aed.setMessage("Ocurrió un error al registrar\n"+e.getMessage());
                            aed.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    aed.hide();
                                }
                            }, 3000);
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

    public void register(final String user,final String psw,String name,String phone,String email) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?username="+user+"&psw="+psw+"&name="+ Uri.encode(name)+
                "&phone="+Uri.encode(phone)+"&email="+email;
        String url = base_url+"registerUser.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(response.equals("true")){
                                apd.hide();
                                asd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        asd.hide();
                                        login(user,psw);
                                    }
                                }, 3000);

                            }else{
                                apd.hide();
                                aed.setMessage("Ocurrió un error");
                                aed.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        aed.hide();
                                    }
                                }, 3000);
                            }
                        } catch (Exception e) {
                            apd.hide();
                            aed.setMessage(e.getMessage());
                            aed.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    aed.hide();
                                }
                            }, 3000);
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

    public void login(String user,String psw) {
        apd.setMessage("Ingresando...");
        apd.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String params="?username="+user+"&psw="+psw;
        String url = base_url+"login.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            JSONObject item=(JSONObject)ja.get(0);
                            String id=item.get("id").toString();
                            String full_name=item.get("full_name").toString();
                            String user_name=item.get("username").toString();
                            String phone_number=item.get("phone_number").toString();
                            String email=item.get("email").toString();
                            String photo=item.get("profile_photo").toString();
                            cred.save_credentials(id,full_name,user_name,phone_number,email,photo,"1");
                            apd.hide();
                            asd.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    asd.hide();
                                    Intent i=new Intent(ctx,FirstActivity.class);
                                    startActivity(i);
                                    RegisterActivity.this.finish();
                                }
                            }, 3000);
                        } catch (Exception e) {
                            apd.hide();
                            aed.setMessage("Ocurrió un error al registrar\n"+e.getMessage());
                            aed.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    aed.hide();
                                }
                            }, 3000);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        apd.hide();
                        aed.setMessage("Ocurrió un error al registrar\n"+error.toString());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 3000);
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(postRequest);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
