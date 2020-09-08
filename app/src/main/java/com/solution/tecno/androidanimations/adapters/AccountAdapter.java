package com.solution.tecno.androidanimations.adapters;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.solution.tecno.androidanimations.fragments.AccountsFragment;
import com.solution.tecno.androidanimations.activities.FirstActivity;
import com.solution.tecno.androidanimations.R;
import com.solution.tecno.androidanimations.utils.Credentials;
import com.solution.tecno.androidanimations.utils.Utils;
import com.solution.tecno.androidanimations.utils.ViewDialog;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.Random;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>{

    List<JSONObject> l;
    Context ctx;
    ViewDialog viewDialog;

    private String base_url="https://www.jadconsultores.com.pe/php_connection/app/bancos_resumen/";
    private EditText diag_et_bank,diag_et_number,diag_et_user_name,diag_et_cci;
    private String bank_edit,account_edit,titular_edit,cci_edit;
    private AccountsFragment af=AccountsFragment.newInstance();
    private String user_id;
    private Credentials cred;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx=parent.getContext();
        cred = new Credentials(ctx);
        viewDialog = new ViewDialog((FirstActivity)ctx);
        user_id = cred.getUserId();

        return new AccountAdapter.ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_account,parent,false));
    }

    public AccountAdapter(List<JSONObject> l) {
        this.l = l;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject obj=l.get(position);
        final String titular = obj.get("user_name").toString().trim();
        final String bco = obj.get("bank").toString().trim();
        final String cta = obj.get("account_number").toString().trim();
        final String cci = obj.get("cci").toString().trim();
        final String id = obj.get("id").toString();
        final String fav = obj.get("fav").toString();

        holder.titular.setText(titular);
        holder.banco.setText(bco);
        holder.cta.setText(cta);
        holder.cci.setText(cci);
        if(fav.equals("0")){
            holder.fav.setFavorite(false);
        }else{
            holder.fav.setFavorite(true);
        }

        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cred.getNetworkStatus().equals("1")){
                    if(fav.equals("0")){
                        updateFavorite(id,1);
                    }else{
                        updateFavorite(id,0);
                    }
                }else{
                    new Utils().createAlert(ctx,"Red no disponible",1);
                }
            }
        });

        if(cci.isEmpty()){
            holder.ic_copy_cci.setVisibility(View.GONE);
            holder.ic_share_cci.setVisibility(View.GONE);
        }else{
            holder.ic_copy_cci.setVisibility(View.VISIBLE);
            holder.ic_share_cci.setVisibility(View.VISIBLE);
        }
        holder.ic_copy_cta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(cta);
                Toast.makeText(ctx, "Cuenta copiada", Toast.LENGTH_SHORT).show();
            }
        });
        holder.ic_share_cta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, bco+": "+cta);
                sendIntent.setType("text/plain");
                ctx.startActivity(sendIntent);
            }
        });

        //CCI
        holder.ic_copy_cci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(cci);
                Toast.makeText(ctx, "CCI copiado", Toast.LENGTH_SHORT).show();
            }
        });
        holder.ic_share_cci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, bco+": "+cci);
                sendIntent.setType("text/plain");
                ctx.startActivity(sendIntent);
            }
        });

        holder.ic_delete_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cred.getNetworkStatus().equals("1")){
                    viewDialog.hideDialog(0);
                    new AlertDialog.Builder(ctx)
                            .setTitle("Eliminar")
                            .setMessage("¿Estás seguro de eliminar esta cuenta?")
                            .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteAccount(id);
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }else{
                    viewDialog.hideDialog(0);
                    new Utils().createAlert(ctx,"Red no disponible",1);
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cred.getNetworkStatus().equals("0")){
                    viewDialog.hideDialog(0);
                    new Utils().createAlert(ctx,"Red no disponible",1);
                }else{
                    viewDialog.showDialog();
                    viewDialog.hideDialog(1.5);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final View layout=LayoutInflater.from(ctx).inflate(R.layout.edit_account_view,null);
                            diag_et_bank=layout.findViewById(R.id.diag_et_bank_edit);
                            diag_et_number=layout.findViewById(R.id.diag_et_account_edit);
                            diag_et_user_name=layout.findViewById(R.id.diag_et_titular_edit);
                            diag_et_cci=layout.findViewById(R.id.diag_et_cci_edit);

                            diag_et_bank.setText(bco);
                            diag_et_number.setText(cta);
                            diag_et_user_name.setText(titular);
                            diag_et_cci.setText(cci);

                            new AlertDialog.Builder(ctx)
                                    .setTitle("Editar cuenta")
                                    .setMessage("Edita tu cuenta para compartirla rápidamente")
                                    .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            String bank = diag_et_bank.getText().toString();
                                            String number = diag_et_number.getText().toString();
                                            String user_name =diag_et_user_name.getText().toString();
                                            String cci = diag_et_cci.getText().toString();
                                            updateAccount(id,bank,number,user_name,cci);
                                        }
                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setView(layout)
                                    .show();
                        }
                    }, 1500);
                }
            }
        });
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

    private void getAccounts(final String user_id) {
        viewDialog.showDialog();
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+Integer.parseInt(user_id);
        String url = base_url+"getUserAccounts.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            l.clear();
                            notifyDataSetChanged();
                            for(int i=0;i<ja.size();i++){
                                JSONObject item=(JSONObject)ja.get(i);
                                l.add(item);
                            }
                            notifyDataSetChanged();
                            viewDialog.hideDialog(0);
                            //wait 3 seconds to hide success dialog
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    verifiedPhoneNumber();
                                }
                            }, 1500);   //3 seconds

                        } catch (Exception e) {
                            cred.registerError(e.toString(),user_id);
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx,"No se pudo obtener la información",1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cred.registerError(error.toString(),user_id);
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx,"No se pudo obtener la información",1);
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void updateAccount(String id,String bank, String number, String user_name,String cci)
    {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?id="+id+
                "&bank="+Uri.encode(bank)+
                "&account="+number+
                "&name="+Uri.parse(user_name)+
                "&cci="+cci.trim();
        String url = base_url+"updateAccount.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            getAccounts(user_id);
                        } catch (Exception e) {
                            cred.registerError(e.getMessage(),user_id);
                            new Utils().createAlert(ctx,"Error al actualizar",1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        cred.registerError(error.getMessage(),user_id);
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx,"Error al actualizar",1);
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void updateFavorite(String id,int fav) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?id="+id+"&fav="+fav;
        String url = base_url+"updateFavorite.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            viewDialog.hideDialog(0);
                            getAccounts(user_id);
                        } catch (Exception e) {
                            cred.registerError(e.getMessage(),user_id);
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx,"Error al actualizar",1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        cred.registerError(error.getMessage(),user_id);
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx,"Error al actualizar",1);
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    public void deleteAccount(String id) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?id="+id;
        String url = base_url+"deleteAccount.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            viewDialog.hideDialog(0);
                            getAccounts(user_id);
                        } catch (Exception e) {
                            cred.registerError(e.getMessage(),user_id);
                            viewDialog.hideDialog(0);
                            new Utils().createAlert(ctx,"Ocurrió un error al eliminar",1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        cred.registerError(error.getMessage(),user_id);
                        viewDialog.hideDialog(0);
                        new Utils().createAlert(ctx,"Ocurrió un error al eliminar",1);
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void verifiedPhoneNumber(){
        viewDialog.hideDialog(0);
        if(cred.getPhoneNumber().equals("0")){
            new Utils().createAlert(ctx,"Actualiza tu número de celular para disfrutar de todas las funciones",1);
        }else{
            viewDialog.hideDialog(0);
        }
    }
}
