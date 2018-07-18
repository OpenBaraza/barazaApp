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

            getGridDef(jBody);
        }

        // Place toolbar
        Toolbar myToolbar = findViewById(R.id.formViewToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(viewName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        System.out.println("BASE 2030 " + menuItem.getItemId());

        String newLink = viewLink + ":" + menuItem.getItemId();
        String rBody = DataClient.makeSecuredRequest(accessToken, newLink, "view", "{}");
        JSONObject jBody = DataClient.getJObject(rBody);

        System.out.println("BASE 2040 " + selectedValue + " : " + rBody);

        try {
            int viewType = jBody.getInt("typeId");
            String viewName = jBody.getString("name");
            switch (viewType) {
                case 8:         // Form view
                    Intent formViewActivity = new Intent(this, FormViewActivity.class);
                    formViewActivity.putExtra("accessToken", accessToken);
                    formViewActivity.putExtra("viewLink", newLink);
                    formViewActivity.putExtra("viewName", viewName);
                    formViewActivity.putExtra("linkValue", selectedValue);
                    startActivity(formViewActivity);
                    break;
                case 9:         // Grid view
                    Intent tableActivity = new Intent(this, TableActivity.class);
                    tableActivity.putExtra("accessToken", accessToken);
                    tableActivity.putExtra("viewLink", newLink);
                    tableActivity.putExtra("viewName", viewName);
                    tableActivity.putExtra("linkValue", selectedValue);
                    startActivity(tableActivity);
                    break;
                case 10:        // HTML report view
                    Intent reportActivity = new Intent(this, ReportActivity.class);
                    reportActivity.putExtra("accessToken", accessToken);
                    reportActivity.putExtra("viewLink", newLink);
                    reportActivity.putExtra("viewName", viewName);
                    reportActivity.putExtra("linkValue", selectedValue);
                    startActivity(reportActivity);
                    break;
            }
        } catch (JSONException ex) {
            System.out.println("JSON Menu error " + ex);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Show the new data
        refreshTable();
    }
}
