package com.solution.tecno.androidanimations.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.activities.MainActivity;
import com.solution.tecno.androidanimations.adapters.AccountAdapter;
import com.solution.tecno.androidanimations.model.Tarjeta;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Preferences;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountsFragment extends Fragment {

    Context ctx;
    Credentials cred;
    ViewDialog viewDialog;
    Utils utils;
    String user_id;
    View v;

    AccountAdapter adapter;
    RecyclerView activity;
    List<Tarjeta> l=new ArrayList<>();
    DatabaseReference reference;
    EditText et_search;
    ImageView icon_close;
    List<Tarjeta> tarjetas_offline;

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
        utils = new Utils(ctx);
        viewDialog = new ViewDialog((MainActivity)ctx);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_accounts, container, false);
        user_id=cred.getData(Preferences.USER_ID);
        cred.getLoginStatus();
        activity=v.findViewById(R.id.recycler_view_accounts);
        activity.setLayoutManager(new LinearLayoutManager(ctx));
        adapter=new AccountAdapter(l);
        viewDialog.showDialog();
        if(cred.getNetworkStatus().equals("1")){
            getAccounts();
        }else{
            tarjetas_offline = new Gson().fromJson(cred.getData(Preferences.TARJETAS_OFFLINE),new TypeToken<List<Tarjeta>>(){}.getType());
            l.addAll(tarjetas_offline);
            adapter.notifyDataSetChanged();
            viewDialog.hideDialog(0);
        }
        activity.setAdapter(adapter);
        et_search = v.findViewById(R.id.tv_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tarjetas_offline = new Gson().fromJson(cred.getData(Preferences.TARJETAS_OFFLINE),new TypeToken<List<Tarjeta>>(){}.getType());
                l.clear();
                l.addAll(tarjetas_offline);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Tarjeta> filter = new ArrayList<>();
                if(!s.toString().isEmpty()){
                    for(Tarjeta t : l)
                    {
                        if(
                            t.getTitular().toLowerCase(Locale.ROOT).contains(s.toString().toLowerCase(Locale.ROOT)) ||
                            t.getBanco().toLowerCase(Locale.ROOT).contains(s.toString().toLowerCase(Locale.ROOT)) ||
                            t.getMoneda().toLowerCase(Locale.ROOT).contains(s.toString().toLowerCase(Locale.ROOT))
                        ){
                            filter.add(t);
                        }
                    }
                    l.clear();
                    l.addAll(filter);
                }else{
                    l.clear();
                    l.addAll(tarjetas_offline);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        icon_close = v.findViewById(R.id.img_search);
        icon_close.setOnClickListener(v -> et_search.setText(""));
        return v;
    }

    void getAccounts() {
        reference = utils.getDatabaseReference(Preferences.FIREBASE_TARJETAS);
        reference.orderByChild("userId").equalTo(cred.getData(Preferences.USER_ID)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Tarjeta tarjeta = snapshot.getValue(Tarjeta.class);
                l.add(tarjeta);
                cred.saveData(Preferences.TARJETAS_OFFLINE, new Gson().toJson(l));
                adapter.notifyItemChanged(l.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                boolean find = false;
                int index = 0;
                while(!find){
                    if (l.get(index).getId().equals(snapshot.getKey())) {
                        find = true;
                        l.set(index, snapshot.getValue(Tarjeta.class));
                        adapter.notifyItemChanged(index);
                    }else{
                        index++;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                boolean find = false;
                int index = 0;
                while(!find){
                    if (l.get(index).getId().equals(snapshot.getKey())) {
                        find = true;
                        l.remove(index);
                        adapter.notifyItemChanged(index);
                    }else{
                        index++;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < l.size(); i++) {
                    if (l.get(i).getId().equals(snapshot.getKey())) {
                        l.remove(i);
                        adapter.notifyItemChanged(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewDialog.hideDialog(1.5);
    }
}
