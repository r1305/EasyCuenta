package com.solution.tecno.androidanimations;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;


public class AccountsFragment extends Fragment {

    Context ctx;
    Credentials cred;
    String user_id;
    SwipeRefreshLayout swipe_refresh;
    View v;
    AwesomeProgressDialog apd;
    AwesomeSuccessDialog asd;
    AwesomeErrorDialog aed;
    AwesomeInfoDialog aid;
    AwesomeWarningDialog awd;
    FloatingActionButton add_new_account;
    String base_url="https://www.jadconsultores.com.pe/php_connection/app/bancos_resumen/";

    AccountAdapter adapter;
    RecyclerView activity;
    List<JSONObject> l=new ArrayList<>();

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

        //create info dialog
        aid=new AwesomeInfoDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Actualiza tu número de celular para disfrutar de todas las funciones")
                .setColoredCircle(R.color.dialogInfoBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning,R.color.white)
                .setPositiveButtonText("Ver mi perfil")
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        Fragment fr=ProfileFragment.newInstance();
                        fragmentTransaction.replace(R.id.container,fr);
                        fragmentTransaction.commit();
                    }
                })
                .setNegativeButtonText("Ahora no")
                .setNegativeButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        aid.hide();
                    }
                })
                .setCancelable(true);

        //create info dialog
        awd=new AwesomeWarningDialog(ctx)
                .setTitle(R.string.app_name)
                .setMessage("Actualiza tu número de celular para disfrutar de todas las funciones")
                .setColoredCircle(R.color.dialogWarningBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning,R.color.white)
                .setButtonText("Ver mi perfil")
                .setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        Fragment fr=ProfileFragment.newInstance();
                        fragmentTransaction.replace(R.id.container,fr);
                        fragmentTransaction.commit();
                    }
                })
                .setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_accounts, container, false);
        user_id=cred.getUserId();
        cred.getUserLoginStatus();
        // Inflate the layout for this fragment
        swipe_refresh = v.findViewById(R.id.swipe_refresh_account);
        add_new_account = v.findViewById(R.id.float_add_new_account);
        add_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View layout=LayoutInflater.from(ctx).inflate(R.layout.new_account_view,null);
                new MaterialStyledDialog.Builder(ctx)
                        .setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle("Nueva cuenta")
                        .setDescription("Añade una nueva cuenta para compartirla rápidamente")
                        .setPositiveText("Agregar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                EditText diag_et_bank=layout.findViewById(R.id.diag_et_bank);
                                EditText diag_et_number=layout.findViewById(R.id.diag_et_account);
                                EditText diag_et_user_name=layout.findViewById(R.id.diag_et_titular);
                                EditText diag_et_cci=layout.findViewById(R.id.diag_et_cci);
                                String bank = diag_et_bank.getText().toString();
                                String number = diag_et_number.getText().toString();
                                String user_name = diag_et_user_name.getText().toString();
                                String cci = diag_et_cci.getText().toString();
                                apd.setMessage("Guardando...");
                                apd.show();
                                addAccount(user_id,bank,number,user_name,cci);
                            }
                        })
                        .setNegativeText("Cancelar")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setCustomView(layout) // Old standard padding: .setCustomView(your_custom_view, 20, 20, 20, 0)
                        //.setCustomView(your_custom_view, 10, 20, 10, 20) // int left, int top, int right, int bottom
                        .show();
            }
        });
        activity=v.findViewById(R.id.recycler_view_accounts);
        activity.setLayoutManager(new LinearLayoutManager(ctx));
        adapter=new AccountAdapter(l);

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAccounts(user_id);
            }
        });

        getAccounts(user_id);
        activity.setAdapter(adapter);
        return v;
    }

    public void getAccounts(String user_id) {
        apd.setMessage("Cargando...");
        apd.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+Integer.parseInt(user_id);
        String url = base_url+"getUserAccounts.php"+params;
        System.out.println("*** url_accounts: "+url);
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
                            adapter.notifyDataSetChanged();
                            swipe_refresh.setRefreshing(false);

//                            asd.setMessage("Listo!");
//                            asd.show();
                            //wait 3 seconds to hide success dialog
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    apd.hide();
//                                    asd.hide();
                                    verifiedPhoneNumber();
                                }
                            }, 1500);   //3 seconds
                        } catch (Exception e) {
                            Log.d("***",e.toString());
                            swipe_refresh.setRefreshing(false);
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
                        swipe_refresh.setRefreshing(false);
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

    public void addAccount(final String user_id, String bank, String number,String user_name,String cci) {

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+Integer.parseInt(user_id)+"" +
                "&bank="+Uri.encode(bank)+
                "&account="+number+
                "&name="+user_name+
                "&cci="+cci;
        String url = base_url+"addAccount.php"+params;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true")){
                            apd.hide();
//                            asd.setMessage("Registro exitoso!\nDeslice hacia abajo para actualizar");
                            asd.setMessage("Registro exitoso!");
                            asd.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    asd.hide();
                                    getAccounts(user_id);
                                }
                            }, 1500);

                        }else{
                            apd.hide();
                            aed.setMessage("Ocurrió un error al registrar su cuenta!\nIntentelo nuevamente");
                            aed.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    aed.hide();
                                }
                            }, 1500);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        apd.hide();
                        aed.setMessage("Ocurrió un error al registrar su cuenta!\n"+error.getMessage());
                        aed.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                aed.hide();
                            }
                        }, 1500);
                    }
                }
        );
        queue.add(postRequest);
    }

    public void verifiedPhoneNumber(){
        asd.hide();
        if(cred.getPhoneNumber().equals("0")){
            awd.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    awd.hide();
                }
            },3500);
        }else{
            verifiedEamil();
        }
    }

    public void verifiedEamil(){
        asd.hide();
        if(cred.getEmail().equals("0")){
            awd.setMessage("Actualiza tu email para disfrutar de todas las funciones");
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
