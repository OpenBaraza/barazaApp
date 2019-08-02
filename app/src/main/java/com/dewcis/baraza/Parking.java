package com.dewcis.baraza;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dewcis.baraza.R;
import com.dewcis.baraza.Adapters.VehicleAdapter;
import com.dewcis.baraza.Models.Vehicle;
import com.dewcis.baraza.Utils.DataClient;
import com.dewcis.baraza.Utils.MakeList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class Parking extends AppCompatActivity implements View.OnClickListener, ParkingDilaog.OnCompleteListener {

    FloatingActionButton fabAdd;
    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet;
    RelativeLayout relativeLayout;
    ListView listView;
    String token, vehicleList;
    Button button;
    TextView textView;
    String data;
    ArrayList<Vehicle> vehicleItems = new ArrayList<>();
    ActionBarDrawerToggle ABT;
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rev_base);

        Toolbar myToolbar = findViewById(R.id.HomeToolBar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Parking");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout) ;
        ABT = new ActionBarDrawerToggle(this, mDrawerLayout, myToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(ABT);
        ABT.syncState();

        token = getIntent().getStringExtra("token");
        Log.e("base4080",token);


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


        relativeLayout = new RelativeLayout(this);
        constraintLayout = (ConstraintLayout)findViewById(R.id.mainLayout);
        button = (Button)findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        button.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);

        fabAdd = new FloatingActionButton(this);
        listView = new ListView(this);
        fabAdd.setImageResource(R.drawable.ic_add_white_24dp);
        fabAdd.setId(View.generateViewId());
        constraintLayout.addView(relativeLayout);
        relativeLayout.addView(listView);
        relativeLayout.addView(fabAdd);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)fabAdd.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        fabAdd.setLayoutParams(params);
        //.getForm("265:0",token,"read");

        data = DataClient.makeSecuredRequest(token,"265:0","read","{}");
        try {
            refresh(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fabAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //Populate dialog
        ParkingDilaog parkingDilaog = new ParkingDilaog();
        Bundle bundle = new Bundle();
        bundle.putString("token",token);
        parkingDilaog.setArguments(bundle);
        parkingDilaog.show(getSupportFragmentManager(),"Diag");
    }

    @Override
    public void sendData(String data) {
        Log.e("base2025",data);
        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
        try {
            refresh(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void refresh(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        Log.e("VehicleList",jsonArray.toString());
        VehicleAdapter vehiclesAdapter = new VehicleAdapter(this, MakeList.makeArrayList(jsonArray),token);
        listView.setAdapter(vehiclesAdapter);
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
