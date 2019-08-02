package com.dewcis.baraza;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dewcis.baraza.Utils.DataClient;

import java.util.HashMap;

/**
 * Created by Dennis Gichangi on 3/29/2018.
 * Update by Joseph Onalo
 */

public class MainDesk extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle ABT;
    String accessToken = null,AppType=null;
    Button toTest;
    LinearLayout Accounts,Loan,transfer,Commodity,CCacounts,Parking,Rent,land,market;
    public String AccountView,CommodityView,LoanView,TransfersView,CCacountsV,ParkingV,RentV,landV,marketV,phone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk);

        mDrawerLayout = findViewById(R.id.drawer_layout) ;

        Accounts=(LinearLayout)findViewById(R.id.AccountButton);
        Loan=(LinearLayout) findViewById(R.id.LoanButton);
        transfer=(LinearLayout)findViewById(R.id.TransferButton);
        Commodity=(LinearLayout)findViewById(R.id.CommodityButton);
        toTest = (Button) findViewById(R.id.TestActivity);
        toTest.setOnClickListener(this);

        CCacounts=(LinearLayout)findViewById(R.id.AccountCC);
        Parking=(LinearLayout)findViewById(R.id.parking);
        Rent=(LinearLayout)findViewById(R.id.Rent);
        land=(LinearLayout)findViewById(R.id.land);
        market=(LinearLayout)findViewById(R.id.market);



        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            accessToken = extras.getString("accessToken");
            AppType=extras.getString("Portal");
            phone=extras.getString("phone");
            System.out.println("BASE 2010 " + accessToken);
        }


        Toolbar myToolbar = findViewById(R.id.HomeToolBar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(AppType);
        getSupportActionBar().setHomeButtonEnabled(true);

        ABT = new ActionBarDrawerToggle(this, mDrawerLayout, myToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(ABT);
        ABT.syncState();

        NavigationView navigationView = findViewById(R.id.navigation);
        Menu menu = navigationView.getMenu();

        String rBody = DataClient.makeSecuredRequest(accessToken, "view=0:0", "menu", "{}");
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


        if(AppType.equals("Banking")){
            CCacounts.setVisibility(View.GONE);
            Parking.setVisibility(View.GONE);
            Rent.setVisibility(View.GONE);
            land.setVisibility(View.GONE);
            market.setVisibility(View.GONE);
            prepareDashboard(jBody);
        }
        else if(AppType.equals("E-Lipa")){
            prepareCCDash();
            Accounts.setVisibility(View.GONE);
            Loan.setVisibility(View.GONE);
            transfer.setVisibility(View.GONE);
            Commodity.setVisibility(View.GONE);
        }
        else{
            Accounts.setVisibility(View.GONE);
            Loan.setVisibility(View.GONE);
            transfer.setVisibility(View.GONE);
            Commodity.setVisibility(View.GONE);
            CCacounts.setVisibility(View.GONE);
            Parking.setVisibility(View.GONE);
            Rent.setVisibility(View.GONE);
            land.setVisibility(View.GONE);
            market.setVisibility(View.GONE);
        }

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


    public void openMenu(MenuItem menuItem) {
        String viewLink = menuItem.getItemId() + ":0";
        HashMap<String,String>Map=new HashMap<>();
        Map.put("accessToken",accessToken);
        Map.put("viewLink",viewLink);
        DataClient.StartIntent(this,Map);
    }

    public void prepareCCDash(){
        CCacounts.setOnClickListener(this);
        Parking.setOnClickListener(this);
        Rent.setOnClickListener(this);
        land.setOnClickListener(this);
        market.setOnClickListener(this);


        CCacountsV="117:0";
        ParkingV="265:0";
        RentV="270:0";
        landV="275:2";
        marketV="280:0";
    }


    public void prepareDashboard(JSONObject jBody)
    {

        Accounts.setOnClickListener(this);
        Loan.setOnClickListener(this);
        transfer.setOnClickListener(this);
        Commodity.setOnClickListener(this);
        try {
            JSONArray jMenu = jBody.getJSONArray("menu");
            for (int i = 0; i < jMenu.length(); i++) {
                JSONObject menuItem = jMenu.getJSONObject(i);
                if(menuItem.has("dashboard")){
                    String type=menuItem.getString("dashboard");
                    switch(type){
                        case "account":
                            AccountView=menuItem.getInt("key")+":0";
                            break;
                        case "loan":
                            LoanView=menuItem.getInt("key")+":0";
                            break;
                        case "commodities":
                            CommodityView = menuItem.getInt("key")+":0";
                            break;
                        case "transfers":
                            TransfersView=menuItem.getInt("key")+":0";
                            break;
                    }
                }
            }
        }
        catch (JSONException ex)
        {System.out.println("JSON Menu error " + ex);}
    }

    @Override
    public void onClick(View view) {
        HashMap<String,String>Map=new HashMap<>();
        Map.put("accessToken",accessToken);

        if(view.getId()==R.id.TestActivity){
            Intent testActivity = new Intent(this,TestActivity.class);
            startActivity(testActivity);
        }

        if(view.getId()==R.id.AccountButton){
            Map.put("viewLink",AccountView);
            Log.e("viewlink",AccountView);
            DataClient.StartIntent(this,Map);}

        if(view.getId()==R.id.LoanButton){
            Map.put("viewLink",LoanView);
            Log.e("viewlink",LoanView);
            DataClient.StartIntent(this,Map);}

        if(view.getId()==R.id.TransferButton){
            Map.put("viewLink",TransfersView);
            Log.e("viewlink",TransfersView);
            DataClient.StartIntent(this,Map);}

        if(view.getId()==R.id.CommodityButton){
            Map.put("viewLink",CommodityView);
            Log.e("viewlink",CommodityView);
            DataClient.StartIntent(this,Map);}

        if(view.getId()==R.id.AccountCC){
            Intent intent = new Intent(this,Payment.class);
            intent.putExtra("token",accessToken);
            intent.putExtra("phone",getIntent().getExtras().getString("phone"));
            startActivity(intent);}

        if(view.getId()==R.id.parking){
            Intent intent = new Intent(this,Parking.class);
            intent.putExtra("token",accessToken);
            startActivity(intent);
        }

        if(view.getId()==R.id.Rent){
            Intent intent = new Intent(this,Rent.class);
            intent.putExtra("token",accessToken);
            startActivity(intent);}

        if(view.getId()==R.id.land){
            Intent intent = new Intent(this,Land.class);
            intent.putExtra("token",accessToken);
            startActivity(intent);}

        if(view.getId()==R.id.market){
            Intent intent = new Intent(this,Market.class);
            intent.putExtra("token",accessToken);
            startActivity(intent);}
    }
}
