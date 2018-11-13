package com.solution.tecno.androidanimations;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ContactFragment extends Fragment {

    Context ctx;
    String user_id;
    View v;
    RecyclerView activity;
    ContactAdapter adapter;
    List<String> l=new ArrayList<>();

    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_contact, container, false);
        activity=v.findViewById(R.id.recycler_view_contact);
        activity.setLayoutManager(new LinearLayoutManager(ctx));
        adapter=new ContactAdapter(l);
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

        apd.show();
        getAllContacts();
        activity.setAdapter(adapter);
        return v;
    }

    public void getAllContacts(){
        Cursor phones = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        List<String> contacs=new ArrayList<String>();
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber=phoneNumber.replace("#","");
            phoneNumber=phoneNumber.replace("+51","");
            phoneNumber=phoneNumber.replace("-"," ");
            phoneNumber=phoneNumber.replace("(01)","");

            name = name.replace("-"," ");

            contacs.add(name+"-"+phoneNumber);
        }
        phones.close();
        l.clear();
        for(int i=0;i<contacs.size();i++){
            String[] obj = contacs.get(i).split("-");
            if(obj[1].length()>=9)
                l.add(contacs.get(i));
        }
        apd.hide();
        asd.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                asd.hide();
            }
        }, 3000);
    }
}
