package com.dewcis.baraza;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.dewcis.baraza.Utils.DataClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Dennis Gichangi on 3/29/2018.
 * Update by Joseph Onalo
 */

public class FormActivity extends AppCompatActivity {

    String accessToken = null;
    String viewLink = null, linkedValue =null,keyValue;

    TableLayout viewContainer;
    boolean newForm;
    Map<String, FormField> formFields = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        newForm=false;

        viewContainer = findViewById(R.id.viewContainer);
        formFields = new HashMap<String, FormField>();

        String viewName = "Form";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accessToken = extras.getString("accessToken");

            viewLink = extras.getString("viewLink");
            String[]segments=viewLink.split(":");
            if(segments.length==2){
                viewLink+=":0"; newForm=true;
            }
            linkedValue = extras.getString("linkValue");
            keyValue=extras.getString("keyValue");
            //viewName = extras.getString("viewName");  // I need to get the form name
            System.out.println("BASE 2010 " + accessToken);
            if(DataClient.Connected(this)){

                if(accessToken == null) {
                    String sBody = DataClient.makeUnsecuredRequest(viewLink, "uform", "{}");
                    JSONObject jBody = DataClient.getJObject(sBody);
                    makeForm(jBody);
                } else {
                    if(linkedValue==null && keyValue!=null){viewLink+="&keydata="+keyValue;}
                    if(linkedValue!=null && keyValue!=null){viewLink+="&linkdata=" + linkedValue+"&keydata="+keyValue;}
                    if(!newForm) {viewLink+="&linkdata=" + linkedValue;}
                    System.out.println("Form Activity viewlink-----"+viewLink);
                    JSONObject jBody = DataClient.makeJSONRequest(accessToken, viewLink, "form", "{}");
                    makeForm(jBody);
                }
            }
            else Toast.makeText(this,"Poor or no connection",Toast.LENGTH_LONG).show();

        }

        // Place toolbar
        Toolbar myToolbar = findViewById(R.id.formToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(viewName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnSaveForm = findViewById(R.id.saveButton);
        btnSaveForm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveForm();
            }
        });
    }

    public void makeForm(JSONObject jBody) {
        viewContainer.removeAllViews();

        try {
            JSONArray jForm = jBody.getJSONArray("form");
            if(newForm){
                for (int i = 0; i < jForm.length(); i++) {
                    TableRow fmRow = new TableRow(this);
                    JSONObject jField = jForm.getJSONObject(i);
                    String name=jField.getString("name");
                    System.out.println("The FormField----"+jField.toString());
                    FormField formField = new FormField(jField, fmRow, this);
                    formFields.put(jField.getString("name"), formField);
                    viewContainer.addView(fmRow);
                }
            }
            else{
                JSONArray jData=jBody.getJSONArray("data");
                for (int i = 0; i < jForm.length(); i++) {
                    TableRow fmRow = new TableRow(this);
                    JSONObject jField = jForm.getJSONObject(i);
                    String name=jField.getString("name");
                    String Data=getData(name,jData);
                    jField.put("data",Data);
                    System.out.println("The FormField----"+jField.toString());
                    FormField formField = new FormField(jField, fmRow, this);
                    formFields.put(jField.getString("name"), formField);
                    viewContainer.addView(fmRow);
                }
            }


            System.out.println("Form object -------"+jForm.toString());
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    public String getData(String key,JSONArray Data){
        String name=null;
        try {
            JSONObject jsonObject=Data.getJSONObject(0);
            if(jsonObject.has(key)){
                return jsonObject.getString(key);
            }
            else return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public void saveForm() {
        if(DataClient.Connected(this)){
            try {
                JSONObject jValues = new JSONObject();
                for(String key : formFields.keySet()){
                    String fieldValue = formFields.get(key).getValue();
                    System.out.println("FormField output -------- " + key + " = " + fieldValue);
                    jValues.put(key, fieldValue);
                }


                JSONObject jResp = null;
                System.out.println("\nValues being sent from form--- "+jValues.toString());
                if(newForm && accessToken!=null){
                        String passLink = viewLink;
                        if(linkedValue != null) passLink += "&linkdata=" + linkedValue;
                        jResp = DataClient.makeJSONRequest(accessToken, passLink, "data", jValues.toString());
                        System.out.println("-------------------"+jResp);
                }else if(!newForm && accessToken!=null){
                        System.out.println("Form activity edit viewlink----------"+viewLink);
                        jResp = DataClient.makeJSONRequest(accessToken, viewLink, "data", jValues.toString());
                        //jResp = DataClient.getJObject(sResp);
                        System.out.println("-------------------"+jResp);
                    }
                else {
                String sResp = DataClient.makeUnsecuredRequest(viewLink, "udata", jValues.toString());
                jResp = DataClient.getJObject(sResp);
                }
                // Show result message
                if(jResp != null){
                    if(jResp.has("ResultDesc")) {
                        String resultMsg = jResp.getString("ResultDesc");
                        Toast.makeText(this, resultMsg, Toast.LENGTH_LONG).show();
                    }
                }
            }
            catch (JSONException ex) {ex.printStackTrace();}
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
