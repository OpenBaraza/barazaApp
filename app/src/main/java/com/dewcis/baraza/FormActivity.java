package com.dewcis.baraza;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.dewcis.baraza.Utils.DataClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Dennis Gichangi on 3/29/2018.
 * Update by Joseph Onalo
 */

public class FormActivity extends AppCompatActivity {

    String accessToken = null;
    String viewLink = null, linkedValue =null,keyValue;

    TableLayout tableLayout;
    boolean newForm;
    Map<String, FormField> formFields = null;
    boolean location;
    String latitude,longtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        newForm=false;
        location=false;
        latitude=null;
        longtitude=null;

        tableLayout = findViewById(R.id.viewContainer);
        formFields = new HashMap<String, FormField>();

        String viewName = "Form";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accessToken = extras.getString("accessToken");

            //if the viewlink is already in the format 2:0:0 ,it means
            viewLink = extras.getString("viewLink");
            String[]segments=viewLink.split(":");
            if(segments.length<=2){
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

                    System.out.println("\nForm Activity jBody-----"+jBody.toString());

                    if(jBody.has("location")){
                        Intent pickContactIntent = new Intent(this,LocationActivity.class);
                        startActivityForResult(pickContactIntent, 1);

                    }
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
        tableLayout.removeAllViews();
        TableLayout.LayoutParams tableLayoutParams=new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        try {
            //System.out.println(jBody.toString());
            JSONArray jForm = jBody.getJSONArray("form");
            if(location){
                System.out.println("\nlongtitude\t"+longtitude+"\nlatitude\t"+latitude);
            }
            if(newForm){
                for (int i = 0; i < jForm.length(); i++) {
                    TableRow formRow = new TableRow(this);
                    formRow.setLayoutParams(tableLayoutParams);
                    JSONObject jField = jForm.getJSONObject(i);
                    String name=jField.getString("name");
                   // System.out.println("The FormField----"+jField.toString());
                    FormField formField = new FormField(jField, formRow, this);
                    formFields.put(jField.getString("name"), formField);
                    tableLayout.addView(formRow);
                }
            }
            else{
                JSONArray jData=jBody.getJSONArray("data");
                System.out.println("DAta\n"+jData.toString());
                for (int i = 0; i < jForm.length(); i++) {
                    TableRow fmRow = new TableRow(this);
                    JSONObject jField = jForm.getJSONObject(i);
                    String name=jField.getString("name");
                    String Data=getData(name,jData);
                    jField.put("data",Data);
                    FormField formField = new FormField(jField, fmRow, this);
                    formFields.put(jField.getString("name"), formField);
                    tableLayout.addView(fmRow);
                }
            }
        }
        catch (JSONException ex) { ex.printStackTrace(); }
    }
    public String getData(String key,JSONArray Data){
        String name=null;
        try {
            JSONObject jsonObject=Data.getJSONObject(0);
            if( jsonObject.has(key) ){ return jsonObject.getString(key); }
            else return null;
        }
        catch (JSONException e) { e.printStackTrace();}
        return  null;
    }

    public void saveForm() {
        if(DataClient.Connected(this)){
            try {
                JSONObject jValues = new JSONObject();
                for(String key : formFields.keySet()){
                    String fieldValue = formFields.get(key).getValue();
                    jValues.put(key, fieldValue);
                }
                if(location){
                    jValues.put("longitude",longtitude);
                    jValues.put("latitude",latitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> listAddresses =
                                geocoder.getFromLocation(Double.parseDouble(latitude),Double.parseDouble(longtitude), 1);
                        if(null!=listAddresses&&listAddresses.size()>0){
                            String _Location = listAddresses.get(0).getAddressLine(0);
                            jValues.put("location",_Location);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("\nJVALUES\n"+jValues.toString());

                JSONObject jResp = null;
                if(newForm && accessToken!=null){
                        String passLink = viewLink;
                        if(linkedValue != null) passLink += "&linkdata=" + linkedValue;
                        jResp = DataClient.makeJSONRequest(accessToken, passLink, "data", jValues.toString());
                }
                else if(!newForm && accessToken!=null){ jResp = DataClient.makeJSONRequest(accessToken, viewLink, "data", jValues.toString());}
                else {
                    String sResp = DataClient.makeUnsecuredRequest(viewLink, "udata", jValues.toString());
                    jResp = DataClient.getJObject(sResp);
                }
                // Show result message
                if(jResp != null){
                    if(jResp.has("ResultDesc")) {
                        String resultMsg = jResp.getString("ResultDesc");
                        System.out.println("\nFormActivity save response---\n"+resultMsg);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
                System.out.println("onActivityResult");
                longtitude=data.getStringExtra("longtitude");
                latitude=data.getStringExtra("latitude");
                System.out.println("onActivityResult  "+longtitude+" "+latitude);
                location=true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
