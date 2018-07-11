package com.dewcis.baraza;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dewcis.baraza.Utils.DataClient;

/**
 * Created by Dennis Gichangi on 3/29/2018.
 * Update by Joseph Onalo
 */

public class MainDesk extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle ABT;
    String accessToken = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk);

        mDrawerLayout = findViewById(R.id.drawer_layout) ;

        Toolbar myToolbar = findViewById(R.id.HomeToolBar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ABT = new ActionBarDrawerToggle(this, mDrawerLayout, myToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(ABT);
        ABT.syncState();

        NavigationView navigationView = findViewById(R.id.navigation);
        Menu menu = navigationView.getMenu();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            accessToken = extras.getString("accessToken");
            System.out.println("BASE 2010 " + accessToken);
        }

        String rBody = DataClient.makeSecuredRequest(accessToken, "view=0:0", "menu", "{}");
        JSONObject jBody = DataClient.getJObject(rBody);
        try {
            JSONArray jMenu = jBody.getJSONArray("menu");
            for (int i = 0; i < jMenu.length(); i++) {
                JSONObject menuItem = jMenu.getJSONObject(i);
                menu.add(0, menuItem.getInt("key"),Menu.NONE, menuItem.getString("name"));
            }
        } catch (JSONException ex) {
            System.out.println("JSON Menu error " + ex);
        }

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
        if(ABT.onOptionsItemSelected(item)) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openMenu(MenuItem menuItem) {
        System.out.println("BASE 2030 " + menuItem.getItemId());

        String viewLink = menuItem.getItemId() + ":0";
        String rBody = DataClient.makeSecuredRequest(accessToken, viewLink, "view", "{}");
        JSONObject jBody = DataClient.getJObject(rBody);

        System.out.println("BASE 2040 " + rBody);

        try {
            int viewType = jBody.getInt("typeId");
            switch (viewType) {
                case 8:         // Form view
                    Intent formViewActivity = new Intent(this, FormViewActivity.class);
                    formViewActivity.putExtra("accessToken", accessToken);
                    formViewActivity.putExtra("viewLink", viewLink);
                    startActivity(formViewActivity);
                    break;
                case 9:         // Grid view
                    Intent tableActivity = new Intent(this, TableActivity.class);
                    tableActivity.putExtra("accessToken", accessToken);
                    tableActivity.putExtra("viewLink", viewLink);
                    startActivity(tableActivity);
                    break;
                case 10:        // HTML report view
                    Intent reportActivity = new Intent(this, ReportActivity.class);
                    reportActivity.putExtra("accessToken", accessToken);
                    reportActivity.putExtra("viewLink", viewLink);
                    startActivity(reportActivity);
                    break;
            }
        } catch (JSONException ex) {
            System.out.println("JSON Menu error " + ex);
        }
    }

}
