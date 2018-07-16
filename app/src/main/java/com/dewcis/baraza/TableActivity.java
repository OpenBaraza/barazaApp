package com.dewcis.baraza;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.View.generateViewId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dewcis.baraza.Utils.DataClient;

/**
 * Created by Dennis Gichangi on 3/29/2018.
 * Update by Joseph Onalo
 */

public class TableActivity extends AppCompatActivity {

    String accessToken = null;
    String viewLink = null;
    Map<Integer, String> keyMap = null;
    List<Integer> actionList = null;
    Map<String, CheckBox> actionBoxes = null;

    JSONArray jGrid = null;
    JSONArray jViews = null;

    RelativeLayout tableRelativeView;
    TableLayout gTableLayout;
    Spinner actionSpinner;
    Button btnActions;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        tableRelativeView = findViewById(R.id.tableRelativeView);
        gTableLayout = findViewById(R.id.tableLayout);

        String viewName = "Table";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accessToken = extras.getString("accessToken");
            viewLink = extras.getString("viewLink");
            viewName = extras.getString("viewName");
            System.out.println("BASE 2010 " + accessToken);

            JSONObject jBody = DataClient.makeJSONRequest(accessToken, viewLink, "grid", "{}");

            // build the grid defination
            getGridDef(jBody);
        }

        // Place toolbar
        Toolbar myToolbar = findViewById(R.id.tableToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(viewName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton btnAddForm = findViewById(R.id.btNewForm);
        btnAddForm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addForm();
            }
        });

    }

    public void getGridDef(JSONObject jBody) {
        try {
            jGrid = jBody.getJSONArray("grid");

            // Add the action buttons if there are there
            if(jBody.has("actions")) makeActions(jBody);

            // Add the sub grids
            if(jBody.has("views")) jViews= jBody.getJSONArray("views");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Recreate the table
    public void refreshTable() {
        JSONObject jBody = DataClient.makeJSONRequest(accessToken, viewLink, "read", "{}");
        makeTable(jBody);
    }

    public void makeTable(JSONObject jBody) {
        gTableLayout.removeAllViews();

        try {
            tableTitle(jGrid);

            if(jBody.has("data")) {
                JSONArray jData = jBody.getJSONArray("data");
                tableData(jGrid, jData);
            }
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void tableTitle(JSONArray jGrid) {
        TableRow tbTitle = new TableRow(this);

        try {
            TextView thId = new TextView(this);
            thId.setText("#");
            thId.setPadding(4,2,4,2);
            tbTitle.addView(thId);

            for(int i = 0; i < jGrid.length(); i++) {
                JSONObject jTitle = jGrid.getJSONObject(i);
                TextView th = new TextView(this);
                th.setText(jTitle.getString("title"));
                th.setPadding(4,2,4,2);
                tbTitle.addView(th);
            }
        } catch(JSONException ex) {
            ex.printStackTrace();
        }

        gTableLayout.addView(tbTitle);
    }

    public void tableData(JSONArray jGrid, JSONArray jData) {
        keyMap = new HashMap<Integer, String>();
        actionBoxes = new HashMap<String, CheckBox>();

        try {
            for(int i = 0; i < jData.length(); i++) {
                TableRow tbRow = new TableRow(this);
                JSONObject jRow = jData.getJSONObject(i);

                CheckBox keyBox = new CheckBox(this);
                keyBox.setId(i);
                tbRow.addView(keyBox);
                actionBoxes.put(jRow.getString("keyfield"), keyBox);
                keyMap.put(i, jRow.getString("keyfield"));

                for (int j = 0; j < jGrid.length(); j++) {
                    JSONObject jHeader = jGrid.getJSONObject(j);
                    String fieldName = jHeader.getString("name");

                    TextView td = new TextView(this);
                    td.setText(jRow.getString(fieldName));
                    td.setPadding(4, 2, 4, 2);
                    tbRow.addView(td);
                }
                gTableLayout.addView(tbRow);
            }
        } catch(JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void addForm() {
        Intent formActivity = new Intent(this, FormActivity.class);
        formActivity.putExtra("accessToken", accessToken);
        formActivity.putExtra("viewLink", viewLink);
        startActivity(formActivity);
    }

    public void callAction() {
        Integer aid = actionList.get(actionSpinner.getSelectedItemPosition());
        JSONArray jActionIds = new JSONArray();

        try {
            for(String key : actionBoxes.keySet()){
                if(actionBoxes.get(key).isChecked()) {
                    JSONObject selectedAction = new JSONObject();
                    selectedAction.put("id", key);
                    jActionIds.put(selectedAction);
                }
            }

            // Make call only when values are selected
            if(jActionIds.length()>0) {
                String actionLink = viewLink + "&action=" + aid.toString();
                JSONObject jBody = DataClient.makeJSONRequest(accessToken, actionLink, "actions", jActionIds.toString());

                // Show the new data
                refreshTable();

                // Show result message
                String resultMsg = jBody.getString("ResultMsg");
                Toast.makeText(this, resultMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void makeActions(JSONObject jBody) {
        // Generate the action list table
        actionList = new ArrayList<Integer>();
        List<String> spinnerItems = new ArrayList<String>();
        try {
            JSONArray jActions = jBody.getJSONArray("actions");
            for(int i = 0; i < jActions.length(); i++) {
                JSONObject jAction = jActions.getJSONObject(i);
                actionList.add(jAction.getInt("aid"));
                spinnerItems.add(jAction.getString("action"));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        // Create the action spinner
        actionSpinner = new Spinner(this);
        actionSpinner.setId(generateViewId());
        int spinnerId = actionSpinner.getId();
        RelativeLayout.LayoutParams spinnerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        spinnerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        spinnerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        spinnerParams.setMargins(10,10,10,10);
        actionSpinner.setLayoutParams(spinnerParams);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerItems);
        actionSpinner.setAdapter(arrayAdapter);
        tableRelativeView.addView(actionSpinner);

        // Create the action button
        btnActions = new Button(this);
        btnActions.setText("Action");
        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        btnParams.addRule(RelativeLayout.RIGHT_OF, spinnerId);
        btnParams.setMargins(10,10,10,10);
        btnActions.setLayoutParams(btnParams);
        tableRelativeView.addView(btnActions);
        btnActions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                callAction();
            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("BASE 1010 - tool bar press " + item.getItemId());
        finish();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Show the new data
        refreshTable();
    }

}
