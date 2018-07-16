package com.dewcis.baraza;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.dewcis.baraza.Utils.DataClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dennis Gichangi on 3/29/2018.
 * Update by Joseph Onalo
 */

public class FormActivity extends AppCompatActivity {

    String accessToken = null;
    String viewLink = null;

    TableLayout viewContainer;
    Map<String, FormField> formFields = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        viewContainer = findViewById(R.id.viewContainer);
        formFields = new HashMap<String, FormField>();

        String viewName = "Form";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accessToken = extras.getString("accessToken");
            viewLink = extras.getString("viewLink") + ":0";
            //viewName = extras.getString("viewName");  // I need to get the form name
            System.out.println("BASE 2010 " + accessToken);

            if(accessToken == null) {
                String sBody = DataClient.makeUnsecuredRequest(viewLink, "uform", "{}");
                JSONObject jBody = DataClient.getJObject(sBody);
                makeForm(jBody);
            } else {
                JSONObject jBody = DataClient.makeJSONRequest(accessToken, viewLink, "form", "{}");
                makeForm(jBody);
            }
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
            for (int i = 0; i < jForm.length(); i++) {
                TableRow fmRow = new TableRow(this);
                JSONObject jField = jForm.getJSONObject(i);
                FormField formField = new FormField(jField, fmRow, this);
                formFields.put(jField.getString("name"), formField);

                viewContainer.addView(fmRow);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void saveForm() {
        try {
            JSONObject jValues = new JSONObject();
            for(String key : formFields.keySet()){
                String fieldValue = formFields.get(key).getValue();
                System.out.println("BASE 2020 : " + key + " = " + fieldValue);
                jValues.put(key, fieldValue);
            }

            JSONObject jResp = null;
            if(accessToken == null) {
                String sResp = DataClient.makeUnsecuredRequest(viewLink, "udata", jValues.toString());
                jResp = DataClient.getJObject(sResp);
            } else {
                jResp = DataClient.makeJSONRequest(accessToken, viewLink, "data", jValues.toString());
            }

            // Show result message
            if(jResp != null){
                if(jResp.has("ResultDesc")) {
                    String resultMsg = jResp.getString("ResultDesc");
                    Toast.makeText(this, resultMsg, Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        // Return to calling table
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
