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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
    LinearLayout Accounts,Loan,transfer,Commodity;
    public String AccountView,CommodityView,LoanView,TransfersView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk);

        mDrawerLayout = findViewById(R.id.drawer_layout) ;

        Accounts=(LinearLayout)findViewById(R.id.AccountButton);
        Loan=(LinearLayout) findViewById(R.id.LoanButton);
        transfer=(LinearLayout)findViewById(R.id.TransferButton);
        Commodity=(LinearLayout)findViewById(R.id.CommodityButton);



        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            accessToken = extras.getString("accessToken");
            AppType=extras.getString("Portal");
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

        if(AppType.equals("Banking")){prepareDashboard(jBody);}
        else{
            Accounts.setVisibility(View.GONE);
            Loan.setVisibility(View.GONE);
            transfer.setVisibility(View.GONE);
            Commodity.setVisibility(View.GONE);
        }

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
            finish();
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

        if(view.getId()==R.id.AccountButton){
            Map.put("viewLink",AccountView);
            DataClient.StartIntent(this,Map);}

        if(view.getId()==R.id.LoanButton){
            Map.put("viewLink",LoanView);
            DataClient.StartIntent(this,Map);}

        if(view.getId()==R.id.TransferButton){
            Map.put("viewLink",TransfersView);
            DataClient.StartIntent(this,Map);}

        if(view.getId()==R.id.CommodityButton){
            Map.put("viewLink",CommodityView);
            DataClient.StartIntent(this,Map);}
    }
}
