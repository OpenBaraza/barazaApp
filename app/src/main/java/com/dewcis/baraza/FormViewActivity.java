package com.dewcis.baraza;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dewcis.baraza.Utils.DataClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Dennis Gichangi on 3/29/2018.
 * Update by Joseph Onalo
 */

public class FormViewActivity extends AppCompatActivity {

    String accessToken = null;
    String viewLink = null;
    String linkValue = null;
    String keyField = null;
    TableLayout gTableLayout;

    JSONArray jFormView = null;
    JSONArray jViews = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_view);

        gTableLayout = findViewById(R.id.formViewLayout);

        String viewName = "Form View";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accessToken = extras.getString("accessToken");
            viewLink = extras.getString("viewLink");
            viewName = extras.getString("viewName");
            System.out.println("BASE 2010 " + accessToken);

            JSONObject jBody = DataClient.makeJSONRequest(accessToken, viewLink, "grid", "{}");
            System.out.println("BASE 2011 Form input " + jBody.toString());

            getGridDef(jBody);
        }

        // Place toolbar
        Toolbar myToolbar = findViewById(R.id.formViewToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(viewName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void EditDefinitions(JSONObject jBody){

    }

    public void getGridDef(JSONObject jBody) {
        try {
            jFormView = jBody.getJSONArray("grid");

            // Add the sub grids
            if(jBody.has("views")) jViews= jBody.getJSONArray("views");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    // Recreate the table
    public void refreshTable() {
        String myLink = viewLink;
        if(linkValue != null) myLink = viewLink + "&linkdata=" + linkValue;
        JSONObject jBody = DataClient.makeJSONRequest(accessToken, myLink, "read", "{}");
        makeTable(jBody);
    }

    public void makeTable(JSONObject jBody) {
        gTableLayout.removeAllViews();

        try {
            if(jBody.has("data")) {
                JSONArray jData = jBody.getJSONArray("data");
                tableData(jData);
            }
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void tableData(JSONArray jData) {

        try {
            for(int i = 0; i < jData.length(); i++) {
                JSONObject jRow = jData.getJSONObject(i);
                keyField = jRow.getString("keyfield");
                for (int j = 0; j < jFormView.length(); j++) {
                    JSONObject jHeader = jFormView.getJSONObject(j);
                    String fieldTitle = jHeader.getString("title");
                    String fieldName = jHeader.getString("name");

                    TableRow tbRow = new TableRow(this);
                    TextView th = new TextView(this);
                    th.setText(fieldTitle);
                    th.setPadding(2, 1, 2, 1);
                    tbRow.addView(th);

                    TextView td = new TextView(this);
                    td.setText(jRow.getString(fieldName));
                    td.setPadding(2, 1, 2, 1);
                    tbRow.addView(td);

                    gTableLayout.addView(tbRow);
                }
            }
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(jViews != null) {
            try {
                for(int i = 0; i < jViews.length(); i++) {
                    JSONObject JView = jViews.getJSONObject(i);
                    String viewName = JView.getString("name");
                    menu.add(0, i, Menu.NONE, viewName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        System.out.println("BASE 1030 - Making menu");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        System.out.println("BASE 1010 - tool bar press " + menuItem.getItemId());

        if (menuItem.getItemId() < jViews.length()) {
            if(keyField != null) openMenu(menuItem, keyField);
        } else {
            finish();
        }

        return true;
    }

    public void openMenu(MenuItem menuItem, String selectedValue) {
        String newLink = viewLink + ":" + menuItem.getItemId();
        HashMap<String,String> Map=new HashMap<>();

        Map.put("viewLink",newLink);
        Map.put("accessToken",accessToken);
        if (linkValue != null){
            Map.put("keyValue",selectedValue);
            Map.put("linkValue",linkValue);}
        DataClient.StartIntent(this,Map);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Show the new data
        refreshTable();
    }
}
