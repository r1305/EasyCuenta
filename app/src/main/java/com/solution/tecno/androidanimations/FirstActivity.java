package com.solution.tecno.androidanimations;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        main_table = findViewById(R.id.main_table);
        TableRow tr_head = new TableRow(this);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView label_bank_header = new TextView(this);
        label_bank_header.setText("Banco");
        label_bank_header.setTextColor(Color.WHITE);
        label_bank_header.setPadding(5, 5, 5, 5);
        label_bank_header.setGravity(Gravity.CENTER);
        tr_head.addView(label_bank_header);// add the column to the table row here

        TextView label_account_header = new TextView(this);
        label_account_header.setText("Num. Cuenta"); // set the text for the header
        label_account_header.setTextColor(Color.WHITE); // set the color
        label_account_header.setPadding(5, 5, 5, 5); // set the padding (if required)
        label_account_header.setGravity(Gravity.CENTER);
        tr_head.addView(label_account_header); // add the column to the table row here

        main_table.addView(tr_head, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        getAccounts();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstActivity.this.overridePendingTransition(R.anim.fadeout,R.anim.fadein);
                Snackbar.make(view, "Pronto nuevas funcionalidades", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void addNewTableRow(int id,String bank,String account){

        Integer count= main_table.getChildCount();
        // Create the table row
        final TableRow tr = new TableRow(this);
        if(count%2!=0) tr.setBackgroundColor(Color.BLACK);
        tr.setId(id);
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        //Create two columns to add as table data
        //create column bank
        TextView labelBank = new TextView(this);
        labelBank.setText(bank.toUpperCase());
        labelBank.setPadding(50, 0, 0, 0);
        labelBank.setHeight(100);
        labelBank.setGravity(Gravity.LEFT);
        if(count%2!=0) labelBank.setTextColor(Color.WHITE); else labelBank.setTextColor(Color.BLUE);
        tr.addView(labelBank);

        //create column account
        TextView labelAccount = new TextView(this);
        labelAccount.setText(account.toString());
        labelAccount.setPadding(2, 0, 5, 0);
        labelAccount.setHeight(100);
        labelAccount.setGravity(Gravity.CENTER);
        if(count%2!=0) labelAccount.setTextColor(Color.WHITE); else labelAccount.setTextColor(Color.BLUE);
        tr.addView(labelAccount);

        //do row clickeable
        tr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView tv_bank = (TextView)tr.getChildAt(0);
                TextView tv_account = (TextView)tr.getChildAt(1);
                String bank=tv_bank.getText().toString();
                String account=tv_account.getText().toString();
                ClipboardManager cm = (ClipboardManager)FirstActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(account);
                Toast.makeText(FirstActivity.this, "Cuenta copiada", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

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
            getAccounts();
            return true;
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

    public void getAccounts() {
        cleanTable(main_table);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://taimu.pe/php_connection/select_accounts.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParser jp = new JSONParser();
                        try {
                            JSONArray ja=(JSONArray)jp.parse(response);
                            for(int i=0;i<ja.size();i++){
                                JSONObject item=(JSONObject)ja.get(i);
                                int id=Integer.parseInt(item.get("id").toString());
                                String bank=item.get("bank").toString();
                                String account=item.get("account").toString();
                                addNewTableRow(id,bank,account);
                            }
                        } catch (Exception e) {
                            Toast.makeText(FirstActivity.this,"Intente luego", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        Toast.makeText(FirstActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(postRequest);
    }
}
