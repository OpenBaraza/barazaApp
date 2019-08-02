package com.dewcis.baraza;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.dewcis.baraza.R;
import com.dewcis.baraza.Adapters.PaymentsAdapter;
import com.dewcis.baraza.Utils.DataClient;
import com.dewcis.baraza.Utils.MakeViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Payment extends AppCompatActivity {

    Button button;
    TextView textView;
    TextView tvBalance, tvStatement;
    ListView listView;
    String token ,phone;
    ConstraintLayout constraintLayout;
    int [] images = {R.drawable.mpesa,R.drawable.airtel,R.drawable.equity};
    String title, instructions;
    String cur_balance, av_balance;
    JSONObject json;
    View spView;

    LinearLayout linearLayout;
    ActionBarDrawerToggle ABT;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rev_base);

        token = getIntent().getStringExtra("token");
        phone = getIntent().getStringExtra("phone");

        Toolbar myToolbar = findViewById(R.id.HomeToolBar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout) ;
        ABT = new ActionBarDrawerToggle(this, mDrawerLayout, myToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(ABT);
        ABT.syncState();




        NavigationView navigationView = findViewById(R.id.navigation);
        Menu menu = navigationView.getMenu();

        String rBody = DataClient.makeSecuredRequest(token, "view=0:0", "menu", "{}");
        JSONObject jBody = DataClient.getJObject(rBody);

        try {
            JSONArray jMenu = jBody.getJSONArray("menu");

            for (int i = 0; i < jMenu.length(); i++) {
                JSONObject menuItem = jMenu.getJSONObject(i);
                menu.add(0, menuItem.getInt("key"),Menu.NONE, menuItem.getString("name"));
            }
        }
        catch (JSONException ex) { System.out.println("JSON Menu error " + ex); }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                openMenu(menuItem);
                return true;
            }
        });


        constraintLayout = (ConstraintLayout)findViewById(R.id.mainLayout);
        linearLayout = (LinearLayout)findViewById(R.id.viewContainer);

        button = (Button)findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        listView = new ListView(this);
        tvBalance = new TextView(this);
        tvStatement = new TextView(this);
        spView = new View(this);


        String viewLink = "115:0&where=account_number='"+phone+"'";
        String balance = DataClient.makeSecuredRequest(token,viewLink,"read","{}");
                //DataClient.getForm(viewLink,token,"read");

        String statement = "115:0:0&where=transfer_account_no='"+phone+"'";
        final String statementJson = DataClient.makeSecuredRequest(token,statement,"read","{}");
                //.getForm(statement,token,"read");
        Log.e("baseStatement",statementJson);

        try {
            JSONObject jsonObject = new JSONObject(balance);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int j=0;j<jsonArray.length();j++){
                json = jsonArray.getJSONObject(j);
            }
            cur_balance = json.getString("current_balance");
            av_balance = json.getString("available_balance");

            instructions = "Current balance is: "+cur_balance+"\n\n"+ "Available balance: "+av_balance+"\n";

        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvBalance.setText(instructions);
        tvStatement.setText("View Statement");

        //Opens Statement activity
        tvStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Payment.this,Statement.class);
                intent.putExtra("statement",statementJson);
                startActivity(intent);
            }
        });

        linearLayout.addView(tvBalance);
        linearLayout.addView(tvStatement);
        //linearLayout.addView(spView);
        linearLayout.addView(listView);

        button.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);

        PaymentsAdapter paymentsAdapter = new PaymentsAdapter(getApplicationContext(),images);
        listView.setAdapter(paymentsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    PaymentDialog paymentDialog = new PaymentDialog();
                    title = "MPESA Instructions";
                    instructions ="1.\tGo to Safaricom SIM Tool Kit, select M-PESA menu, select Lipa na M-PESA\n" +
                            "2.\tSelect Pay Bill from the M-Pesa menu.\n" +
                            "3.\tEnter the Revenue Collection business number XXXXXX\n" +
                            "4.\tEnter your Registration Number as the account number e.g. 123456.\n" +
                            "5.\tEnter the amount you wish to pay \n" +
                            "6.\tEnter your M-Pesa PIN.\n" +
                            "7.\tConfirm that all details are correct and click send\n" +
                            "8.\tYou will receive a confirmation of the transaction via SMS";
                    Bundle bundle = new Bundle();
                    bundle.putString("title",title);
                    bundle.putString("instructions",instructions);
                    paymentDialog.setArguments(bundle);
                    paymentDialog.show(getSupportFragmentManager(),"Dialog");
                } else if (i==1){
                    PaymentDialog paymentDialog = new PaymentDialog();
                    instructions ="1.\tGo to Airtel Money Menu on your SIM Toolkit\n" +
                            "2.\tSelect on Make payments option\n" +
                            "3.\tChoose on Pay bill\n" +
                            "4.\tThen go to Revenue Collection option \n" +
                            "5.\tThen Enter the Amount to Pay in kshs i.e. 1000 \n" +
                            "6.\tClick Send option\n" +
                            "7.\tEnter your PIN\n" +
                            "8.\tEnter your Reference which is your Registration Number e.g. 012345 \n" +
                            "9.\tYou will receive a transaction Message showing your available balance with the transaction details.";
                    Bundle bundle = new Bundle();
                    bundle.putString("title",title);
                    bundle.putString("instructions",instructions);
                    paymentDialog.setArguments(bundle);
                    paymentDialog.show(getSupportFragmentManager(),"Dialog");
                }else if (i==2){
                    PaymentDialog paymentDialog = new PaymentDialog();
                    instructions ="1.\tSelect Eazzy Pay from your Equitel menu\n" +
                            "2.\tSelect Pay Bill from Eazzy Pay.\n" +
                            "3.\tChoose the bank account number to pay from e.g.0123456789102\n" +
                            "4.\tSelect Revenue Collection business number \n" +
                            "5.\tEnter your new Registration number e.g. 12345\n" +
                            "6.\tEnter the amount you wish to pay \n" +
                            "7.\tEnter your Equitel PIN and press OK\n" +
                            "8.\tConfirm that all details are correct by pressing 1 OK.\n" +
                            "9.\tYou will receive a text confirmation of the transaction from Equity bank.\n";
                    Bundle bundle = new Bundle();
                    bundle.putString("title",title);
                    bundle.putString("instructions",instructions);
                    paymentDialog.setArguments(bundle);
                    paymentDialog.show(getSupportFragmentManager(),"Dialog");
                } else if (i == 3) {
                    PaymentDialog paymentDialog = new PaymentDialog();
                    title = "Balance";
                    String account = phone;
                    String viewLink = "115:0&where=account_number='"+account+"'";
                    String balance = DataClient.makeSecuredRequest(token,viewLink,"read","{}");
                            //.getForm(viewLink,token,"read");
                    try {
                        JSONObject jsonObject = new JSONObject(balance);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int j=0;j<jsonArray.length();j++){
                            json = jsonArray.getJSONObject(j);
                        }
                        cur_balance = json.getString("current_balance");
                        av_balance = json.getString("available_balance");

                        instructions = "Current balance is: "+cur_balance+"\n\n"+ "Available balance: "+av_balance;
                        Bundle bundle = new Bundle();
                        bundle.putString("title",title);
                        bundle.putString("instructions",instructions);
                        paymentDialog.setArguments(bundle);
                        paymentDialog.show(getSupportFragmentManager(),"Dialog");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("base1010",balance);
                }


            }
        });
    }
    public void viewPositions(View view,int left,int top,int right, int bottom) {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams)view.getLayoutParams();
        layoutParams.setMargins(left,top,right,bottom);
        view.setLayoutParams(layoutParams);
    }

    public void openMenu(MenuItem menuItem) {
        String viewLink = menuItem.getItemId() + ":0";
        HashMap<String,String> Map=new HashMap<>();
        Map.put("accessToken",token);
        Map.put("viewLink",viewLink);
        DataClient.StartIntent(this,Map);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        ABT.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ABT.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater MI=getMenuInflater();
        MI.inflate(R.menu.my_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(ABT.onOptionsItemSelected(item)) return true;
        if(item.getItemId()==R.id.logout) {
            Intent I=new Intent(this,LoginActivity.class);
            startActivity(I);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
