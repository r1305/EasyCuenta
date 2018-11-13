package com.solution.tecno.androidanimations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{

    List<String> l =new ArrayList<>();
    Context ctx;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx=parent.getContext();
        return new ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_contact,parent,false));
    }

    public ContactAdapter(List<String> list) {
        this.l = list;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String obj=l.get(position);
        String[] item=obj.split("-");
        holder.name.setText(item[0]);
        holder.phone.setText(item[1]);
    }

    @Override
    public int getItemCount() {
        if(l==null){
            return 0;
        }else {
            return l.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,phone;

        private ViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.contact_name);
            phone=itemView.findViewById(R.id.contact_phone);
        }
    }
}
