package com.solution.tecno.androidanimations;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.jackandphantom.circularprogressbar.CircleProgressbar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class AccountsFragment extends Fragment {

    TableLayout data_table;
    Context ctx;
    String user_id;
    EditText diag_et_bank,diag_et_number,diag_et_user_name;

    String bank_edit,account_edit,titular_edit;

    MaterialStyledDialog msd;

    public AccountsFragment() {
        // Required empty public constructor
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_accounts, container, false);
        user_id=new Credentials(ctx).getUserId();
        // Inflate the layout for this fragment
        data_table = v.findViewById(R.id.data_table);

        final View layout_loader=LayoutInflater.from(ctx).inflate(R.layout.floating_loader,null);
        msd=new MaterialStyledDialog.Builder(ctx).
                setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("Cargando...")
                .setCustomView(layout_loader)
                .build();

        CircleProgressbar circleProgressbar = layout_loader.findViewById(R.id.first_progress_bar);
        circleProgressbar.setForegroundProgressColor(Color.RED);
        circleProgressbar.setBackgroundProgressWidth(15);
        circleProgressbar.setForegroundProgressWidth(20);
        circleProgressbar.enabledTouch(false);
        circleProgressbar.setRoundedCorner(true);
        circleProgressbar.setClockwise(true);
        circleProgressbar.setMaxProgress(100);
        int animationDuration = 8000; // 2500ms = 2,5s
        circleProgressbar.setProgressWithAnimation(-100, animationDuration); // Default duration = 1500ms

//        showLoader(1,"Cargando...");
        getAccounts(user_id);
        return v;
    }

    public void getAccounts(String user_id) {
        cleanTable(data_table);

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+Integer.parseInt(user_id);
        String url = "http://taimu.pe/php_connection/app_bancos/getUserAccounts.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            System.out.println(1);
                            JSONArray ja=(JSONArray)jp.parse(response);
                            for(int i=0;i<ja.size();i++){
                                JSONObject item=(JSONObject)ja.get(i);
                                int id=Integer.parseInt(item.get("id").toString());
                                String bank=item.get("bank").toString();
                                String account=item.get("account_number").toString();
                                addNewTableRow(id,bank,account);
                            }
                            System.out.println(2);
                            showLoader(0,"");
                        } catch (Exception e) {
                            showLoader(0,"");
                            Toast.makeText(ctx,"Intente luego", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        showLoader(0,"");
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(ctx, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest);
    }

    private void cleanTable(TableLayout table) {
        int childCount = table.getChildCount();
        table.removeViews(0, childCount);
    }

    public void addNewTableRow(int id,String bank,String account){

        Integer count= data_table.getChildCount();
        // Create the table row
        final TableRow tr = new TableRow(ctx);
        if(count%2!=0) tr.setBackgroundColor(Color.GRAY);
        tr.setId(id);
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        //Create two columns to add as table data
        //create column bank
        TextView labelBank = new TextView(ctx);
        labelBank.setText(bank.toUpperCase());
        labelBank.setPadding(50, 0, 0, 0);
        labelBank.setHeight(100);
        labelBank.setGravity(Gravity.LEFT);
        if(count%2!=0) labelBank.setTextColor(Color.WHITE); else labelBank.setTextColor(Color.BLACK);
        tr.addView(labelBank);

        //create column account
        TextView labelAccount = new TextView(ctx);
        labelAccount.setText(account);
        labelAccount.setPadding(2, 0, 5, 0);
        labelAccount.setHeight(100);
        labelAccount.setGravity(Gravity.CENTER);
        if(count%2!=0) labelAccount.setTextColor(Color.WHITE); else labelAccount.setTextColor(Color.BLACK);
        tr.addView(labelAccount);

        //create share bank+account icon
        ImageButton share_icon = new ImageButton(ctx);
        share_icon.setImageResource(R.drawable.baseline_share_black_24dp);
        share_icon.setPadding(0, 20, 35, 0);
        share_icon.setClickable(true);
        share_icon.setBackgroundColor(Color.parseColor("#00000000"));
        share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_bank = (TextView)tr.getChildAt(0);
                TextView tv_account = (TextView)tr.getChildAt(1);
                String bank=tv_bank.getText().toString();
                String account=tv_account.getText().toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, bank+": "+account);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        tr.addView(share_icon);

        //create copy_account icon
        ImageButton copy_icon = new ImageButton(ctx);
        copy_icon.setImageResource(R.drawable.baseline_file_copy_black_24dp);
        copy_icon.setPadding(0, 20, 35, 0);
        copy_icon.setClickable(true);
        copy_icon.setBackgroundColor(Color.parseColor("#00000000"));
        copy_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_bank = (TextView)tr.getChildAt(0);
                TextView tv_account = (TextView)tr.getChildAt(1);
                String bank=tv_bank.getText().toString();
                String account=tv_account.getText().toString();
                ClipboardManager cm = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(account);
                Toast.makeText(ctx, "Cuenta copiada", Toast.LENGTH_SHORT).show();
            }
        });
        tr.addView(copy_icon);

        //create edit_row icon
        ImageButton edit_row = new ImageButton(ctx);
        edit_row.setImageResource(R.drawable.ic_edit_row);
        edit_row.setPadding(0, 20, 35, 0);
        edit_row.setClickable(true);
        edit_row.setBackgroundColor(Color.parseColor("#00000000"));
        edit_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int account_id=tr.getId();
                showLoader(1,"Obteniendo datos...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getAccountDetail(account_id);
                    }
                }, 3000);

            }
        });
        tr.addView(edit_row);

        //create edit_row icon
        ImageButton delete_row = new ImageButton(ctx);
        delete_row.setImageResource(R.drawable.ic_delete);
        delete_row.setPadding(0, 20, 35, 0);
        delete_row.setClickable(true);
        delete_row.setBackgroundColor(Color.parseColor("#00000000"));
        delete_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_bank = (TextView)tr.getChildAt(0);
                String bank=tv_bank.getText().toString();

                new MaterialStyledDialog.Builder(ctx)
                        .setStyle(Style.HEADER_WITH_TITLE)
                        .setTitle("Eliminar cuenta")
                        .setDescription("¿Está seguro de eliminar la cuenta: "+bank+"?")
                        .setPositiveText("Eliminar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                int id=tr.getId();
                                deleteAccount(id);
                            }
                        })
                        .setNegativeText("Cancelar")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        tr.addView(delete_row);

        // finally add this to the table row
        data_table.addView(tr, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
    }

    public void deleteAccount(int id) {
        cleanTable(data_table);

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?id="+id;
        String url = "http://taimu.pe/php_connection/app_bancos/deleteAccount.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            getAccounts(user_id);
                        } catch (Exception e) {
                            Toast.makeText(ctx,"Intente luego", Toast.LENGTH_SHORT).show();
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

    public void getAccountDetail(final int id) {
//        cleanTable(data_table);
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?account_id="+id;
        String url = "http://taimu.pe/php_connection/app_bancos/getAccountDetail.php"+params;
        System.out.println(url);

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
                                System.out.println(item);
                                bank_edit=item.get("bank").toString();
                                account_edit=item.get("account_number").toString();
                                titular_edit=item.get("user_name").toString();

                                final View layout=LayoutInflater.from(ctx).inflate(R.layout.edit_account_view,null);
                                diag_et_bank=layout.findViewById(R.id.diag_et_bank_edit);
                                diag_et_number=layout.findViewById(R.id.diag_et_account_edit);
                                diag_et_user_name=layout.findViewById(R.id.diag_et_titular_edit);

                                diag_et_bank.setText(bank_edit);
                                diag_et_number.setText(account_edit);
                                diag_et_user_name.setText(titular_edit);

                                new MaterialStyledDialog.Builder(ctx)
                                        .setStyle(Style.HEADER_WITH_TITLE)
                                        .setTitle("Editar cuenta")
                                        .setDescription("Edita tu cuenta para compartirla rápidamente")
                                        .setPositiveText("Guardar")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                                String bank = diag_et_bank.getText().toString();
                                                String number = diag_et_number.getText().toString();
                                                String user_name =diag_et_user_name.getText().toString();
                                                updateAccount(id,bank,number,user_name);
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
                                showLoader(0,"");
                            }
                        } catch (Exception e) {
                            showLoader(0,"");
                            Toast.makeText(ctx,"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        showLoader(0,"");
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(ctx, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest);
    }

    public void showLoader(int visibility,String message){
        // create floating loader
        msd.setTitle(message);
        System.out.println("visibility: "+visibility);
        if(visibility==1){
            System.out.println("if: "+visibility);
            msd.show();
        }else{
            System.out.println("else: "+visibility);
            msd.hide();
            msd.dismiss();
            msd.cancel();
        }
    }

    public void addAccount(final String user_id, String bank, String number,String user_name) {
        cleanTable(data_table);

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+Integer.parseInt(user_id)+"&bank="+Uri.encode(bank)+"&account="+number+"&name="+user_name;
        String url = "http://taimu.pe/php_connection/app_bancos/addAccount.php"+params;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ctx,response,Toast.LENGTH_LONG);
                        try {
                            getAccounts(user_id);
                        } catch (Exception e) {
                            Toast.makeText(ctx,"Intente luego", Toast.LENGTH_SHORT).show();
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

    public void updateAccount(int id,String bank, String number, String user_name) {
        cleanTable(data_table);

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?id="+id+"&bank="+Uri.encode(bank)+"&account="+number+"&name="+Uri.parse(user_name);
        String url = "http://taimu.pe/php_connection/app_bancos/updateAccount.php"+params;
        System.out.println(url);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            getAccounts(user_id);
                        } catch (Exception e) {
                            Toast.makeText(ctx,"Intente luego", Toast.LENGTH_SHORT).show();
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

}
