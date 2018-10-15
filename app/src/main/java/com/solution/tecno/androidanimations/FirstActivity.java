package com.solution.tecno.androidanimations;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FirstActivity extends AppCompatActivity {

    TableLayout main_table;
    ImageView wsp_icon;
    Context ctx;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ctx=FirstActivity.this;
        new Credentials(ctx);
        user_id=new Credentials(ctx).getUserId();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wsp_icon=findViewById(R.id.wsp_icon);
        wsp_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(ctx,"com.whatsapp");
            }
        });
        main_table = findViewById(R.id.main_table);
        TableRow tr_head = new TableRow(ctx);
        tr_head.setBackgroundColor(Color.parseColor("#fcdd9e"));
        tr_head.setGravity(Gravity.LEFT);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView label_bank_header = new TextView(ctx);
        label_bank_header.setText("Banco");
        label_bank_header.setTextColor(Color.BLACK);
        label_bank_header.setPadding(5, 5, 5, 5);
        label_bank_header.setHeight(100);
        label_bank_header.setGravity(Gravity.CENTER);
        label_bank_header.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
        tr_head.addView(label_bank_header);// add the column to the table row here

        TextView label_account_header = new TextView(this);
        label_account_header.setText("Num. Cuenta"); // set the text for the header
        label_account_header.setTextColor(Color.BLACK); // set the color
        label_account_header.setPadding(5, 5, 5, 5); // set the padding (if required)
        label_bank_header.setHeight(100);
        label_account_header.setGravity(Gravity.CENTER);
        label_account_header.setTypeface(Typeface.SANS_SERIF,Typeface.BOLD);
        tr_head.addView(label_account_header); // add the column to the table row here

        main_table.addView(tr_head, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        getAccounts(user_id);
    }

    public void addNewTableRow(int id,String bank,String account){

        Integer count= main_table.getChildCount();
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
        ImageButton copy_icon = new ImageButton(this);
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

        // finally add this to the table row
        main_table.addView(tr, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getAccounts(user_id);
            return true;
        }

        if(id == R.id.action_log_out){
            new Credentials(ctx).logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }

    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void getAccounts(String user_id) {
        cleanTable(main_table);

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String params="?user_id="+Integer.parseInt(user_id);
        String url = "http://taimu.pe/php_connection/app_bancos/getUserAccounts.php"+params;

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
                                int id=Integer.parseInt(item.get("id").toString());
                                String bank=item.get("bank").toString();
                                String account=item.get("account_number").toString();
                                addNewTableRow(id,bank,account);
                            }
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
