package com.solution.tecno.androidanimations;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;

import java.util.ArrayList;

import in.shadowfax.proswipebutton.ProSwipeButton;

import static android.app.Activity.RESULT_OK;

public class ContactFragment extends Fragment {

    Context ctx;
    String user_id,full_name;
    Credentials cred;
    ImageButton contact;
    EditText contact_name,contact_phone,contact_msg;
    ProSwipeButton swipe_sms,swipe_push;
    View v;

    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;

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
        swipe_sms = v.findViewById(R.id.contact_send_sms);
        swipe_push = v.findViewById(R.id.contact_send_push);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

        swipe_sms.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                String phone = contact_phone.getText().toString();
                String msg = contact_msg.getText().toString();
                String msg2 ="Descarga Easy Cuenta y comparte tus cuentas de forma rapida y sencilla.\n"+
                        "Entra aqui: https://goo.gl/6pCzFf y empieza a compartir";
                apd.setMessage("Enviando...");
                sendSMS2(phone,msg);
                sendSMS(phone,msg);
            }
        });

        return v;
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public void sendSMS2(String phoneNumber,String message) {
        SmsManager smsManager = SmsManager.getDefault();


        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        int messageCount = parts.size();

        Log.i("Message Count", "Message Count: " + messageCount);

        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

        PendingIntent sentPI = PendingIntent.getBroadcast(ctx, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(ctx, 0, new Intent(DELIVERED), 0);

        for (int j = 0; j < messageCount; j++) {
            sentIntents.add(sentPI);
            deliveryIntents.add(deliveredPI);
        }

        // ---when the SMS has been sent---
        ctx.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        swipe_sms.showResultIcon(true);
                        apd.hide();
                        asd.setMessage("Enviado");
                        asd.show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                asd.hide();
                            }
                        }, 1500);
                    break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        swipe_sms.showResultIcon(false);
                        apd.hide();
                        aed.setMessage("No Enviado");
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                aed.hide();
                            }
                        }, 1500);
                    break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        swipe_sms.showResultIcon(false);
                        apd.hide();
                        aed.setMessage("Sin Servicio");
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                aed.hide();
                            }
                        }, 1500);
                    break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        swipe_sms.showResultIcon(false);
                        apd.hide();
                        aed.setMessage("Sin Servicio");
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                aed.hide();
                            }
                        }, 1500);
                    break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        swipe_sms.showResultIcon(false);
                        apd.hide();
                        aed.setMessage("Sin Servicio");
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                aed.hide();
                            }
                        }, 1500);
                    break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        ctx.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {

                    case Activity.RESULT_OK:
                        aed.setMessage("SMS Entregado");
                        asd.show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                asd.hide();
                            }
                        }, 1500);
                    break;
                    case Activity.RESULT_CANCELED:
                        swipe_sms.showResultIcon(false);
                        aed.setMessage("No Entregado");
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                aed.hide();
                            }
                        }, 1500);
                    break;
                }
            }
        }, new IntentFilter(DELIVERED));
        smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        /* sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents); */
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
            }
        }
    }
}
