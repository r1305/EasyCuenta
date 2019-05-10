package com.solution.tecno.androidanimations;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;

import in.shadowfax.proswipebutton.ProSwipeButton;

import static android.app.Activity.RESULT_OK;

public class ContactFragment extends Fragment {

    Context ctx;
    String user_id,full_name;
    Credentials cred;
    ImageButton contact;
    EditText contact_name,contact_phone,contact_msg;
    ProSwipeButton swipe_push;
    Button invite_button;
    int user_find_id=0;
    String user_fcm="";
    View v;

    String base_url="https://www.jadconsultores.com.pe/php_connection/app/bancos_resumen/";

    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;
    AwesomeInfoDialog aid;

    static final int REQUEST_CODE=1;

    public ContactFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this.getContext();
        cred = new Credentials(ctx);
        user_id = cred.getUserId();
        full_name = cred.getFullName();

        //create progress dialog
        apd = new AwesomeProgressDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Cargando")
                .setColoredCircle(R.color.dialogInfoBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(false);

        //create success dialog
        asd = new AwesomeSuccessDialog(ctx)
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

        //create info dialog
        aid=new AwesomeInfoDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Contacto no registrado")
                .setColoredCircle(R.color.dialogInfoBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning,R.color.white)
                .setCancelable(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_contact, container, false);
        contact = v.findViewById(R.id.btn_show_contacts);
        contact_name = v.findViewById(R.id.contact_et_name);
        contact_phone = v.findViewById(R.id.contact_et_phone);
        contact_msg = v.findViewById(R.id.contact_et_message);
        contact_msg.setVisibility(View.GONE);
        swipe_push = v.findViewById(R.id.contact_send_push);
        swipe_push.setVisibility(View.GONE);
        invite_button = v.findViewById(R.id.btn_invite_contact);

        invite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = contact_phone.getText().toString();
                if(phone.equals("")){
                    aed.setMessage("Debe elegir un contacto primero");
                    aed.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            aed.hide();
                            contact.setBackgroundColor(Color.RED);
                            contact_name.setError("Seleccione un contacto");
                        }
                    },2000);
                }

                if(!phone.isEmpty()){
                    contact.setBackgroundColor(Color.WHITE);
                    String msg2 ="Descarga Easy Cuenta y comparte tus cuentas de forma rapida y sencilla.\n"+
                            "Entra aqui: https://goo.gl/6pCzFf y empieza a compartir";
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, msg2);
                    sendIntent.setType("text/plain");
                    ctx.startActivity(sendIntent);
                }
            }
        });

        contact_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        swipe_push.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                String msg = contact_msg.getText().toString();
                msg = msg.trim();
                if(msg.isEmpty()){
                    aed.setMessage("Debe ingresar un mensaje al destinatario");
                    aed.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipe_push.showResultIcon(false);
                            aed.hide();
                            contact_msg.setError("Ingrese un mensaje");
                            contact_msg.requestFocus();
                        }
                    },2000);
                }else{
                    apd.setMessage("Enviando...");
                    apd.show();
                    sendPush(user_find_id,user_fcm,msg);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = ctx.getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);
                number = number.replace("+51","");
                number = number.replace(" ","");
                number = number.replace("-","");
                number = number.replace("(01)","");

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);

                contact_name.setText(name);
                contact_phone.setText(number);
                contact.setBackgroundColor(Color.WHITE);
                apd.setMessage("Buscando usuario");
                apd.show();
                findByPhone(number);
            }
        }
    }

    public void findByPhone(final String phone) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?phone="+Uri.encode(phone);
        String url = base_url+"getUserByPhone.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            JSONObject item=(JSONObject)ja.get(0);
                            int encontrado=Integer.parseInt(item.get("id").toString());
                            String fcm = item.get("fcm").toString();
                            if(encontrado>0){
                                user_find_id=encontrado;
                                user_fcm = fcm;
                                apd.hide();
                                asd.setMessage("Contacto encontrado");
                                asd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        contact_msg.setVisibility(View.VISIBLE);
                                        invite_button.setVisibility(View.GONE);
                                        swipe_push.setVisibility(View.VISIBLE);
                                        asd.hide();
                                    }
                                },2000);

                            }else{
                                apd.hide();
                                aid.setMessage("Contacto aún no registrado");
                                aid.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        contact_msg.setVisibility(View.GONE);
                                        swipe_push.setVisibility(View.GONE);
                                        invite_button.setVisibility(View.VISIBLE);
                                        aid.hide();
                                    }
                                },2000);

                            }
                        } catch (Exception e) {
                            apd.hide();
                            aid.setMessage("Contacto aún no registrado");
                            aid.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    contact_msg.setVisibility(View.GONE);
                                    swipe_push.setVisibility(View.GONE);
                                    invite_button.setVisibility(View.VISIBLE);
                                    aid.hide();
                                }
                            }, 2000);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        cred.registerError(error.getMessage(),user_id);
                        apd.hide();
                        aed.setMessage("Ocurrió un error al buscar la información");
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 3000);
                    }
                }
        );
        queue.add(postRequest);
    }

    public void sendPush(final int user_id,final String fcm,final String msg) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user="+user_id+"&fcm="+fcm+"&name="+Uri.encode(cred.getFullName())+"&msg="+msg;
        String url = base_url+"sendPushNotification.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int success = Integer.parseInt(response);
                        if(success>0){
                            apd.hide();
                            asd.setMessage("Notificación enviada");
                            asd.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    swipe_push.showResultIcon(true);
                                    asd.hide();
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                    Fragment fr=AccountsFragment.newInstance();
                                    fragmentTransaction.replace(R.id.container,fr);
                                    fragmentTransaction.commit();
                                }
                            },2000);
                        }else{
                            apd.hide();
                            aed.setMessage("Notificación NO enviada");
                            aed.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    swipe_push.showResultIcon(false);
                                    aed.hide();
                                }
                            },2000);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        apd.hide();
                        aed.setMessage(error.toString());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                                swipe_push.showResultIcon(false);
                            }
                        },2000);
                    }
                }
        );
        queue.add(postRequest);
    }
}
