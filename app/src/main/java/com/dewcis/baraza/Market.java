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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dewcis.baraza.R;
import com.dewcis.baraza.Adapters.LandAdapter;
import com.dewcis.baraza.Adapters.ShopAdapter;
import com.dewcis.baraza.Models.RentModel;
import com.dewcis.baraza.Models.ShopModel;
import com.dewcis.baraza.Utils.DataClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Market extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    RelativeLayout relativeLayout;
    ListView listView;
    String token;
    Button button;
    TextView textView;
    String data;
    ActionBarDrawerToggle ABT;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rev_base);

        Toolbar myToolbar = findViewById(R.id.HomeToolBar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Market");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout) ;
        ABT = new ActionBarDrawerToggle(this, mDrawerLayout, myToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(ABT);
        ABT.syncState();


        relativeLayout = new RelativeLayout(this);
        constraintLayout = (ConstraintLayout)findViewById(R.id.mainLayout);
        button = (Button)findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        button.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);

        listView = new ListView(this);

        constraintLayout.addView(relativeLayout);
        relativeLayout.addView(listView);

        token = getIntent().getStringExtra("token");
        Log.e("base1020token",token);

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


        //data = DataClient.getForm("280:0",token,"read");

        //Log.e("base10200",data);

        try {
            JSONObject jsonObject = DataClient.makeJSONRequest(token,"280:0","read","{}");
            Log.e("base10200",jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            Log.e("RentList",jsonArray.toString());
            makeArrayList(jsonArray);

            ShopAdapter shopAdapter = new ShopAdapter(this,makeArrayList(jsonArray),token);
            listView.setAdapter(shopAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<ShopModel> makeArrayList(JSONArray jsonArray) throws JSONException {
        ArrayList<ShopModel> arrayList = new ArrayList<>();

        for (int i=0;i<jsonArray.length();i++){
            try {
                Log.e("base2020",jsonArray.getJSONObject(i).toString());

                arrayList.add(getJSON(jsonArray.getJSONObject(i)));
            }catch (JSONException e){
                e.toString();
            }
        }


        return arrayList;
    }

    public ShopModel getJSON(JSONObject json) throws JSONException {
        Log.e("JSONlista",json.toString());
        ShopModel shopModel = new ShopModel();
        shopModel.setNumber(json.getString("shop_number"));
        shopModel.setLocation(json.getString("shop_location_name"));
        shopModel.setSize(json.getString("shop_size_name"));

        return shopModel;
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
