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
import com.dewcis.baraza.Utils.DataClient;
import com.dewcis.baraza.Adapters.LandAdapter;
import com.dewcis.baraza.Models.LandModel;
import com.dewcis.baraza.Models.RentModel;
import com.dewcis.baraza.Models.Vehicle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Land extends AppCompatActivity {

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
        getSupportActionBar().setTitle("Land");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = findViewById(R.id.drawer_layout) ;

        token = getIntent().getStringExtra("token");
        Log.e("base4070",token);


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

        relativeLayout = new RelativeLayout(this);
        constraintLayout = (ConstraintLayout)findViewById(R.id.mainLayout);
        button = (Button)findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        button.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);

        listView = new ListView(this);

        constraintLayout.addView(relativeLayout);
        relativeLayout.addView(listView);

        //data = DataClient.getForm("275:2",token,"read");
        data = DataClient.makeSecuredRequest(token,"275:2","read","{}");
        Log.e("base1010",data);

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            Log.e("RentList",jsonArray.toString());
            makeArrayList(jsonArray);

            LandAdapter landAdapter = new LandAdapter(this,makeArrayList(jsonArray),token);
            listView.setAdapter(landAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<LandModel> makeArrayList(JSONArray jsonArray) throws JSONException {

        ArrayList<LandModel> arrayList = new ArrayList<>();
        for (int i = 0;i<jsonArray.length();i++){
            arrayList.add(getJSON(jsonArray.getJSONObject(i)));
        }

        return arrayList;
    }

    public LandModel getJSON(JSONObject jsonObject) throws JSONException {
        LandModel landModel = new LandModel();
        landModel.setPlotNumber(jsonObject.getString("plot_number"));
        landModel.setPlotLocation(jsonObject.getString("plot_location_name"));
        landModel.setPlotSize(jsonObject.getString("plot_size"));

        return landModel;
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
