package com.dewcis.baraza;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
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

public class TableActivity extends AppCompatActivity{

    boolean mapOpened;

    String accessToken = null;
    String viewLink = null;
    String linkValue = null;
    Map<Integer, String> keyMap = null;
    List<Integer> actionList = null;
    Map<String, CheckBox> actionBoxes = null;

    JSONArray jGrid = null;
    JSONArray jViews = null;
    RelativeLayout tableRelativeView;
    TableLayout gTableLayout;
    Spinner actionSpinner;
    Button btnActions;
    FloatingActionButton floatingActionButton;
    Resources resources;
    TableRow.LayoutParams CellParams;
    CoordinatorLayout coordinatorLayout;
    Context context;
    boolean selectable;
    JSONObject jBody,locationObject=null;
    JSONArray jData;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapOpened=false;
        jBody=null;
        jData=null;
        locationObject=new JSONObject();
        setContentView(R.layout.activity_table);
        context=this;
        selectable=true;

        resources=this.getResources();
        tableRelativeView = findViewById(R.id.tableRelativeView);
        gTableLayout = findViewById(R.id.tableLayout);
        CellParams=new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT,1);
        CellParams.setMargins(0,0,0,3);

        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.TableCoordinatorLayout);


        String viewName = "Table";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accessToken = extras.getString("accessToken");
            viewLink = extras.getString("viewLink");
            viewName = extras.getString("viewName");
            linkValue = extras.getString("linkValue");
            /*if(linkValue==null){
                String key=extras.getString("keyValue");
                if(key!=null){linkValue=key;}
            }*/
            jBody = DataClient.makeJSONRequest(accessToken, viewLink, "grid", "{}");
            System.out.println("Table Activity viewlink-----"+viewLink);
            System.out.println("Table Activity response-----"+jBody.toString());
            // build the grid defination
            getGridDef(jBody);
        }

        Toolbar myToolbar = findViewById(R.id.tableToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(viewName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    public void getGridDef(JSONObject jBody) {
        try {
            jGrid = jBody.getJSONArray("grid");

            // Add the action buttons if there are there
            if(jBody.has("actions")) makeActions(jBody);

            // Add the sub grids
            if(jBody.has("views")) {
                boolean hasForm = false;
                jViews = jBody.getJSONArray("views");
                for(int j = 0; j < jViews.length(); j++) {
                    JSONObject jView = jViews.getJSONObject(j);
                    if(jView.getInt("typeId") == 7) hasForm = true;
                }
                if(hasForm) {CreateFormButton();}
            }
            if(!jBody.has("actions") && jBody.getJSONArray("views").length()<=0){selectable=false;}

        } catch (JSONException e) {e.printStackTrace();}
    }

    public void CreateFormButton() {
        floatingActionButton=new FloatingActionButton(this);
        floatingActionButton.setImageResource(R.drawable.ic_action_plusb);
        CoordinatorLayout.LayoutParams FabParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        FabParams.gravity=Gravity.END|Gravity.BOTTOM|Gravity.RIGHT;
        floatingActionButton.setLayoutParams(FabParams);
        floatingActionButton.setId(R.id.ActionFabID);
        coordinatorLayout.addView(floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataClient.Connected(context)){addForm();}
                else Toast.makeText(context,"Poor or no connection",Toast.LENGTH_LONG).show();}
        });
    }

    // Recreate the table
    public void refreshTable() {
        String myLink = viewLink;
        if(linkValue != null) myLink = viewLink + "&linkdata=" + linkValue;
        jBody = DataClient.makeJSONRequest(accessToken, myLink, "read", "{}");
        //System.out.println("\nTable activity link value---------"+linkValue+"\nTable activity data viewlink--------"+myLink+"\nTable Activity-------"+jBody);
        makeTable(jBody);
    }

    public void makeTable(JSONObject jBody) {
        gTableLayout.removeAllViews();

        try {
            tableTitle();
            if(jBody.has("data")) {
                jData = jBody.getJSONArray("data");
                System.out.println("\nTable Activity data\n"+jData.toString());
                tableData(jData);
            }
        }
        catch(JSONException ex) {ex.printStackTrace();}
    }

    public void tableTitle() {
        TableRow tbTitle = new TableRow(this);
        TableLayout.LayoutParams rowParams=new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tbTitle.setLayoutParams(rowParams);
        try {
            if(selectable){
                TextView thId = new TextView(this);
                thId.setText("#");
                thId.setLayoutParams(CellParams);
                thId.setGravity(Gravity.CENTER);
                tbTitle.addView(thId);
            }

            for(int i = 0; i < jGrid.length(); i++) {
                JSONObject jTitle = jGrid.getJSONObject(i);
                TextView th = new TextView(this);
                th.setTypeface(null,Typeface.BOLD);
                th.setTextColor(resources.getColor(R.color.black));
                ColumnShader(i,th);
                th.setText(jTitle.getString("title"));
                th.setLayoutParams(CellParams);
                th.setGravity(Gravity.CENTER);
                tbTitle.addView(th);
            }
        }
        catch(JSONException ex) {ex.printStackTrace();}
        gTableLayout.addView(tbTitle);
    }


    public void tableData(JSONArray jData) {
        //keyMap stores the key and corresponding json object
        keyMap = new HashMap<Integer, String>();
        actionBoxes = new HashMap<String, CheckBox>();

        try {
            boolean mapPresent=false;
            String tempLatitude=null,tempLongitude=null;

            for(int i = 0; i < jData.length(); i++) {
                TableRow tbRow = new TableRow(this);
                TableLayout.LayoutParams rowParams=new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                tbRow.setLayoutParams(rowParams);
                JSONObject jRow = jData.getJSONObject(i);
                if(jRow.has("existing_latitude") && jRow.getString("existing_latitude").length()>1){
                    mapPresent=true;
                    tempLatitude=jRow.getString("existing_latitude");
                }
                if(jRow.has("existing_longitude") && jRow.getString("existing_longitude").length()>1){
                    mapPresent=true;
                    tempLongitude=jRow.getString("existing_longitude");
                }
                if(selectable){
                    CheckBox keyBox = new CheckBox(this);
                    keyBox.setId(i);
                    keyBox.setLayoutParams(CellParams);
                    keyBox.setGravity(Gravity.CENTER);
                    tbRow.addView(keyBox);
                    actionBoxes.put(jRow.getString("keyfield"), keyBox);
                }
                keyMap.put(i, jRow.getString("keyfield"));

                for (int j = 0; j < jGrid.length(); j++) {
                    TableRow.LayoutParams cellParams=new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    cellParams.setMargins(0,0,0,3);
                    JSONObject jHeader = jGrid.getJSONObject(j);
                    String fieldName = jHeader.getString("name");
                    TextView td = new TextView(this);
                    ColumnShader(j,td);
                    td.setText(jRow.getString(fieldName));
                    td.setGravity(Gravity.CENTER);
                    td.setLayoutParams(cellParams);
                    //setPadding(td);
                    tbRow.addView(td);
                }
                gTableLayout.addView(tbRow);
            }
            if(mapPresent && !mapOpened){
                mapOpened=true;
                Uri gmmIntentUri = Uri.parse("geo:"+tempLatitude+","+tempLongitude+"");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        }
        catch(JSONException ex) {ex.printStackTrace();}
    }

    public void ColumnShader(int ColNumber, TextView cell) {
        if(ColNumber==0 || ((ColNumber%2)==0)) cell.setBackground(resources.getDrawable(R.drawable.shade_1));
        else cell.setBackground(resources.getDrawable(R.drawable.shade_2));
    }

    public void addForm() {
        Intent formActivity = new Intent(this, FormActivity.class);
        formActivity.putExtra("accessToken", accessToken);
        formActivity.putExtra("viewLink", viewLink);
        formActivity.putExtra("LinkedValue", linkValue);
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
                refreshTable();
                String resultMsg = jBody.getString("ResultMsg");
                Toast.makeText(this, resultMsg, Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException ex) {ex.printStackTrace();}
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
        }
        catch (JSONException ex) {ex.printStackTrace();}

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
            }
            catch (JSONException e) {e.printStackTrace();}
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        System.out.println("BASE 1010 - tool bar press " + menuItem.getItemId());

        if (menuItem.getItemId() < jViews.length()) {
            String selectedValue = getSelectedValue();
            if(selectedValue != null) openMenu(menuItem, selectedValue);
        }
        else {finish();}

        return true;
    }

    public void openMenu(MenuItem menuItem, String selectedValue) {
        System.out.println("BASE 2030 " + menuItem.getItemId());
        String newLink = viewLink + ":" + menuItem.getItemId();
        HashMap<String,String>Map=new HashMap<>();
        Map.put("accessToken",accessToken);
        Map.put("viewLink",newLink);
        if(linkValue!=null) { Map.put("linkValue",linkValue);}
        Map.put("selectedValue",selectedValue);
        //String selected = keyMap.get(selectedValue);
        System.out.println("\nSelected value\n"+menuItem.getItemId());
        DataClient.StartIntent(this,Map);
    }


    private String getSelectedValue() {
        if(actionBoxes!=null && !actionBoxes.isEmpty()){
            for (String key : actionBoxes.keySet()) {if (actionBoxes.get(key).isChecked()) return key;}
        }
        Toast.makeText(this,"Sorry but you cannot use this feature without making a selection",Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Show the new data
        refreshTable();
    }

}
