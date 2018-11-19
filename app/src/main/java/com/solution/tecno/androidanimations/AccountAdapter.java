package com.solution.tecno.androidanimations;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.Random;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>{

    List<JSONObject> l;
    Context ctx;

    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;
    AwesomeWarningDialog awd;
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

        awd=new AwesomeWarningDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Actualiza tu número de celular para disfrutar de todas las funciones")
                .setColoredCircle(R.color.dialogWarningBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning,R.color.white)
                .setButtonText("Ver mi perfil")
                .setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        FragmentManager fm = af.getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        Fragment fr=ProfileFragment.newInstance();
                        fragmentTransaction.replace(R.id.container,fr);
                        fragmentTransaction.commit();
                    }
                })
                .setCancelable(false);

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
        final String cci = obj.get("cci").toString().trim().trim();
        final String id = obj.get("id").toString();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(200), rnd.nextInt(200), rnd.nextInt(200));
        holder.cardView.setCardBackgroundColor(currentColor);
        holder.titular.setText(titular);
        holder.banco.setText(bco);
        holder.cta.setText(cta);
        holder.cci.setText(cci);
        if(cci.equals("")){
            holder.ic_copy_cci.setVisibility(View.GONE);
            holder.ic_share_cci.setVisibility(View.GONE);
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
        holder.ic_edit_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apd.setMessage("Obteniendo datos...");
                apd.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        getAccountDetail(account_id);
                        final View layout=LayoutInflater.from(ctx).inflate(R.layout.edit_account_view,null);
                        diag_et_bank=layout.findViewById(R.id.diag_et_bank_edit);
                        diag_et_number=layout.findViewById(R.id.diag_et_account_edit);
                        diag_et_user_name=layout.findViewById(R.id.diag_et_titular_edit);
                        diag_et_cci=layout.findViewById(R.id.diag_et_cci_edit);

                        diag_et_bank.setText(bco);
                        diag_et_number.setText(cta);
                        diag_et_user_name.setText(titular);
                        diag_et_cci.setText(cci);

                        apd.hide();
                        asd.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                asd.hide();
                                new MaterialStyledDialog.Builder(ctx)
                                        .setStyle(Style.HEADER_WITH_TITLE)
                                        .setTitle("Editar cuenta")
                                        .setDescription("Edita tu cuenta para compartirla rápidamente")
                                        .setPositiveText("Guardar")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                                apd.setMessage("Guardando...");
                                                String bank = diag_et_bank.getText().toString();
                                                String number = diag_et_number.getText().toString();
                                                String user_name =diag_et_user_name.getText().toString();
                                                String cci = diag_et_cci.getText().toString();
                                                updateAccount(id,bank,number,user_name,cci);
                                            }
                                        })
                                        .setNegativeText("Cancelar")
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCustomView(layout)
                                        .show();
                            }
                        },1000);//1 sec
                    }
                }, 2500);
            }
        });

        holder.ic_delete_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount(id);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apd.setMessage("Obteniendo datos...");
                apd.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        getAccountDetail(account_id);
                        final View layout=LayoutInflater.from(ctx).inflate(R.layout.edit_account_view,null);
                        diag_et_bank=layout.findViewById(R.id.diag_et_bank_edit);
                        diag_et_number=layout.findViewById(R.id.diag_et_account_edit);
                        diag_et_user_name=layout.findViewById(R.id.diag_et_titular_edit);
                        diag_et_cci=layout.findViewById(R.id.diag_et_cci_edit);

                        diag_et_bank.setText(bco);
                        diag_et_number.setText(cta);
                        diag_et_user_name.setText(titular);
                        diag_et_cci.setText(cci);

                        apd.hide();
                        asd.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                asd.hide();
                                new MaterialStyledDialog.Builder(ctx)
                                        .setStyle(Style.HEADER_WITH_TITLE)
                                        .setTitle("Editar cuenta")
                                        .setDescription("Edita tu cuenta para compartirla rápidamente")
                                        .setPositiveText("Guardar")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                                apd.setMessage("Guardando...");
                                                String bank = diag_et_bank.getText().toString();
                                                String number = diag_et_number.getText().toString();
                                                String user_name =diag_et_user_name.getText().toString();
                                                String cci = diag_et_cci.getText().toString();
                                                updateAccount(id,bank,number,user_name,cci);
                                            }
                                        })
                                        .setNegativeText("Cancelar")
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCustomView(layout)
                                        .show();
                            }
                        },1000);//1 sec
                    }
                }, 2500);
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
            ic_edit_card=itemView.findViewById(R.id.ic_edit_card);
            ic_delete_card=itemView.findViewById(R.id.ic_delete_card);
        }
    }

    private void getAccounts(String user_id) {
        apd.setMessage("Cargando...");
        apd.show();
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
                            for(int i=0;i<ja.size();i++){
                                JSONObject item=(JSONObject)ja.get(i);
                                l.add(item);
                            }
                            notifyDataSetChanged();
                            apd.hide();
                            asd.show();
                            //wait 3 seconds to hide success dialog
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    asd.hide();
                                    verifiedPhoneNumber();
                                }
                            }, 1500);   //3 seconds
                        } catch (Exception e) {
                            Log.d("***",e.toString());
                            apd.hide();
                            aed.setMessage(e.getMessage());
                            aed.show();
                            //wait 3 seconds to hide success dialog
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    aed.hide();
                                }
                            }, 3000);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("***",error.toString());
                        apd.hide();
                        aed.setMessage(error.toString());
                        aed.show();
                        //wait 3 seconds to hide success dialog
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                aed.hide();
                            }
                        }, 3000);
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

    private void updateAccount(String id,String bank, String number, String user_name,String cci) {

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
                            apd.hide();
                            getAccounts(user_id);
                        } catch (Exception e) {
                            apd.hide();
                            aed.setMessage(e.getMessage());
                            aed.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    aed.hide();
                                }
                            }, 2500);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        apd.hide();
                        aed.setMessage(error.getMessage());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 2500);
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void getAccountDetail(final String id) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?account_id="+id;
        String url = base_url+"getAccountDetail.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ctx,response,Toast.LENGTH_LONG);
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            for(int i=0;i<ja.size();i++){
                                JSONObject item=(JSONObject)ja.get(i);
                                bank_edit=item.get("bank").toString();
                                account_edit=item.get("account_number").toString();
                                titular_edit=item.get("user_name").toString();
                                cci_edit=item.get("cci").toString();

                                final View layout=LayoutInflater.from(ctx).inflate(R.layout.edit_account_view,null);
                                diag_et_bank=layout.findViewById(R.id.diag_et_bank_edit);
                                diag_et_number=layout.findViewById(R.id.diag_et_account_edit);
                                diag_et_user_name=layout.findViewById(R.id.diag_et_titular_edit);
                                diag_et_cci=layout.findViewById(R.id.diag_et_cci_edit);

                                diag_et_bank.setText(bank_edit);
                                diag_et_number.setText(account_edit);
                                diag_et_user_name.setText(titular_edit);
                                diag_et_cci.setText(cci_edit);

                                apd.hide();
                                asd.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        asd.hide();
                                        new MaterialStyledDialog.Builder(ctx)
                                                .setStyle(Style.HEADER_WITH_TITLE)
                                                .setTitle("Editar cuenta")
                                                .setDescription("Edita tu cuenta para compartirla rápidamente")
                                                .setPositiveText("Guardar")
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                        apd.setMessage("Guardando...");
                                                        String bank = diag_et_bank.getText().toString();
                                                        String number = diag_et_number.getText().toString();
                                                        String user_name =diag_et_user_name.getText().toString();
                                                        String cci = diag_et_cci.getText().toString();
                                                        updateAccount(id,bank,number,user_name,cci);
                                                    }
                                                })
                                                .setNegativeText("Cancelar")
                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setCustomView(layout)
                                                .show();
                                    }
                                },1000);//1 sec
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
                            }, 25000);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        apd.hide();
                        aed.setMessage(error.toString());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 25000);
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
        apd.setMessage("Eliminando...");
        apd.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?id="+id;
        String url = base_url+"deleteAccount.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            apd.hide();
                            getAccounts(user_id);
                        } catch (Exception e) {
                            apd.hide();
                            aed.setMessage(e.getMessage());
                            aed.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    aed.hide();
                                }
                            }, 2500);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        apd.hide();
                        aed.setMessage(error.getMessage());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 2500);
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
        asd.hide();
        if(cred.getPhoneNumber().equals("0")){
            awd.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    awd.hide();
                }
            },3500);
        }
    }
}
