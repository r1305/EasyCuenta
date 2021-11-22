package com.solution.tecno.androidanimations.adapters;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.firebase.database.DatabaseReference;
import com.solution.tecno.androidanimations.fragments.AccountsFragment;
import com.solution.tecno.androidanimations.activities.MainActivity;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.model.Tarjeta;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Preferences;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>{

    List<Tarjeta> l;
    Context ctx;
    ViewDialog viewDialog;

    private String base_url="https://www.jadconsultores.com.pe/php_connection/app/bancos_resumen/";
    private EditText diag_et_bank,diag_et_moneda,diag_et_number,diag_et_user_name,diag_et_cci;
    private String bank_edit,account_edit,titular_edit,cci_edit;
    private AccountsFragment af=AccountsFragment.newInstance();
    private String user_id;
    private Credentials cred;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx=parent.getContext();
        cred = new Credentials(ctx);
        viewDialog = new ViewDialog((MainActivity)ctx);
        user_id = cred.getData(Preferences.USER_ID);

        return new AccountAdapter.ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_account,parent,false));
    }

    public AccountAdapter(List<Tarjeta> l) {
        this.l = l;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tarjeta tarjeta=l.get(position);
        final String titular = tarjeta.getTitular();
        final String bco = tarjeta.getBanco();
        final String cta = tarjeta.getCuenta();
        final String cci = tarjeta.getCci();
        final String moneda = tarjeta.getMoneda();
        final boolean fav = tarjeta.isFav();

        holder.titular.setText(titular);
        holder.banco.setText(bco + " "+ moneda);
        holder.cta.setText(cta);
        holder.cci.setText(cci);
        holder.fav.setFavorite(fav);

        holder.fav.setOnClickListener(v -> {
            if(cred.getNetworkStatus().equals("1")){
                tarjeta.setFav(!fav);
                System.out.println("fav: "+tarjeta.isFav());
                updateTarjeta(tarjeta);
            }else{
                new Utils().createAlert(ctx,"Red no disponible",1);
            }
        });

        if(cci.isEmpty()){
            holder.ic_copy_cci.setVisibility(View.GONE);
            holder.ic_share_cci.setVisibility(View.GONE);
        }else{
            holder.ic_copy_cci.setVisibility(View.VISIBLE);
            holder.ic_share_cci.setVisibility(View.VISIBLE);
        }
        holder.ic_copy_cta.setOnClickListener(view -> {
            ClipboardManager cm = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(cta);
            Toast.makeText(ctx, "Cuenta copiada", Toast.LENGTH_SHORT).show();
        });
        holder.ic_share_cta.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, bco+": "+cta);
            sendIntent.setType("text/plain");
            ctx.startActivity(sendIntent);
        });

        //CCI
        holder.ic_copy_cci.setOnClickListener(view -> {
            ClipboardManager cm = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(cci);
            Toast.makeText(ctx, "CCI copiado", Toast.LENGTH_SHORT).show();
        });
        holder.ic_share_cci.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, bco+": "+cci);
            sendIntent.setType("text/plain");
            ctx.startActivity(sendIntent);
        });

        holder.ic_delete_card.setOnClickListener(view -> {
            if(cred.getNetworkStatus().equals("1")){
                viewDialog.hideDialog(0);
                new AlertDialog.Builder(ctx)
                        .setTitle("Eliminar")
                        .setMessage("¿Estás seguro de eliminar esta cuenta?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            removeTarjeta(tarjeta);
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                        .show();
            }else{
                viewDialog.hideDialog(0);
                new Utils().createAlert(ctx,"Red no disponible",1);
            }
        });

        holder.cardView.setOnClickListener(v -> {
            if(cred.getNetworkStatus().equals("0")){
                viewDialog.hideDialog(0);
                new Utils().createAlert(ctx,"Red no disponible",1);
            }else{
                viewDialog.showDialog("");
                viewDialog.hideDialog(1.5);
                new Handler().postDelayed(() -> {
                    final View layout = LayoutInflater.from(ctx).inflate(R.layout.edit_account_view, null);
                    diag_et_bank = layout.findViewById(R.id.diag_et_bank_edit);
                    diag_et_moneda = layout.findViewById(R.id.diag_et_moneda_edit);
                    diag_et_number = layout.findViewById(R.id.diag_et_account_edit);
                    diag_et_user_name = layout.findViewById(R.id.diag_et_titular_edit);
                    diag_et_cci = layout.findViewById(R.id.diag_et_cci_edit);

                    diag_et_bank.setText(bco);
                    diag_et_moneda.setText(moneda);
                    diag_et_number.setText(cta);
                    diag_et_user_name.setText(titular);
                    diag_et_cci.setText(cci);

                    new AlertDialog.Builder(ctx)
                            .setTitle("Editar cuenta")
                            .setMessage("Edita tu cuenta para compartirla rápidamente")
                            .setPositiveButton("Guardar", (dialog, which) -> {
                                dialog.dismiss();
                                String diag_bank = diag_et_bank.getText().toString();
                                String diag_moneda = diag_et_moneda.getText().toString();
                                String diag_cuenta = diag_et_number.getText().toString();
                                String diag_titular = diag_et_user_name.getText().toString();
                                String diag_cci = diag_et_cci.getText().toString();

                                tarjeta.setMoneda(diag_moneda);
                                tarjeta.setCci(diag_cci);
                                tarjeta.setBanco(diag_bank);
                                tarjeta.setCuenta(diag_cuenta);
                                tarjeta.setTitular(diag_titular);

                                updateTarjeta(tarjeta);
                            })
                            .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                            .setView(layout)
                            .show();
                }, 1500);
            }
        });
    }

    void updateTarjeta(Tarjeta tarjeta)
    {
        DatabaseReference reference = new Utils(ctx).getDatabaseReference(Preferences.FIREBASE_TARJETAS);
        reference.child(tarjeta.getId()).setValue(tarjeta);
    }

    void removeTarjeta(Tarjeta tarjeta)
    {
        DatabaseReference reference = new Utils(ctx).getDatabaseReference(Preferences.FIREBASE_TARJETAS);
        reference.child(tarjeta.getId()).removeValue();
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

        TextView titular,cta,banco,cci;
        ImageView ic_share_cta,ic_share_cci,ic_copy_cta,ic_copy_cci,ic_edit_card,ic_delete_card;
        CardView cardView;
        MaterialFavoriteButton fav;

        private ViewHolder(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.item_card_view);
            banco=itemView.findViewById(R.id.item_et_bank);
            cta=itemView.findViewById(R.id.item_et_account);
            cci=itemView.findViewById(R.id.item_et_account_cci);
            titular=itemView.findViewById(R.id.item_et_titular);
            ic_share_cta=itemView.findViewById(R.id.ic_share_cta);
            ic_share_cci=itemView.findViewById(R.id.ic_share_cci);
            ic_copy_cta=itemView.findViewById(R.id.ic_copy_cta);
            ic_copy_cci=itemView.findViewById(R.id.ic_copy_cci);
            ic_delete_card=itemView.findViewById(R.id.ic_delete_card);
            fav = itemView.findViewById(R.id.item_fav);
        }
    }
}
