package com.dewcis.baraza;


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
    TableLayout gTableLayout;

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

            JSONObject jBody = DataClient.makeJSONRequest(accessToken, viewLink, "read", "{}");

            makeTable(jBody);
        }

        // Place toolbar
        Toolbar myToolbar = findViewById(R.id.formViewToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(viewName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void makeTable(JSONObject jBody) {
        gTableLayout.removeAllViews();

        try {
            JSONArray jGrid = jBody.getJSONArray("grid");

            if(jBody.has("data")) {
                JSONArray jData = jBody.getJSONArray("data");
                tableData(jGrid, jData);
            }
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void tableData(JSONArray jGrid, JSONArray jData) {

        try {
            for(int i = 0; i < jData.length(); i++) {
                JSONObject jRow = jData.getJSONObject(i);
                for (int j = 0; j < jGrid.length(); j++) {
                    JSONObject jHeader = jGrid.getJSONObject(j);
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
        MenuInflater MI = getMenuInflater();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
