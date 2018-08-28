package com.dewcis.baraza;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.StrictMode;
import android.os.Bundle;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.ProgressDialog;
import android.view.View;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dewcis.baraza.Utils.DataClient;
import com.dewcis.baraza.Utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dennis Gichangi on 3/29/2018.
 * Update by Joseph Onalo
 */

public class LoginActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    EditText etEmail, etPassword;
    Button btnSignIn, btnRegister;
    ProgressDialog progressDialog;
    Spinner Apps;

    //Preferences
    private static final String PREF = "preferences";
    private static final String PREF_NAME = "Username";
    private final String DefaultNameValue = "";
    private String EmailValue;
    private String PasswordValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Apps=(Spinner)findViewById(R.id.Apps);
        String[] apps={"HR","Banking","Transport"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, apps);
        Apps.setAdapter(adapter);
        setSavedService();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);

            btnSignIn = findViewById(R.id.btnSignIn);
            btnSignIn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    signIn();
                }
            });

            btnRegister = findViewById(R.id.btnRegister);
            btnRegister.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    registerForm();
                }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }

    private void savePreferences(){
        SharedPreferences settings = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        //Edit and commit
        EmailValue = etEmail.getText().toString();

        editor.putString(PREF, EmailValue);
        editor.commit();
    }

    private void loadPreferences(){
        SharedPreferences settings = getSharedPreferences(PREF, Context.MODE_PRIVATE);

        //Get values
        EmailValue = settings.getString(PREF, DefaultNameValue);
        etEmail.setText(EmailValue);
    }

    public boolean IsConnected() {
        ConnectivityManager CM= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NI= CM.getActiveNetworkInfo();
        if(NI!=null && NI.isConnected()) {
            return true;
        } else {
            Toast.makeText(this,"No network detected ,Please check your network settings",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void signIn() {
        String selected=Apps.getSelectedItem().toString();
        String url=null;
        if(selected.equals("HR")){url="https://demo.dewcis.com/hr/dataserver";}
        else if(selected.equals("Banking")){url="https://demo.dewcis.com/banking/dataserver";}
        else if(selected.equals("Transport")){url="https://demo.dewcis.com/agency/dataserver";}
        saveServiceType(selected);
        DataClient DC=new DataClient(url);
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        try {
            JSONObject jToken = DataClient.authenticate(email, password,this);
            if(jToken == null) {
                String message = "Check on network connection";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
            else {
                int ResultCode = jToken.getInt("ResultCode");
                if (ResultCode == 0) {
                    String accessToken = jToken.getString("access_token");
                    Intent mainDesk = new Intent(this, MainDesk.class);
                    mainDesk.putExtra("accessToken", accessToken);
                    mainDesk.putExtra("Portal",selected);
                    startActivity(mainDesk);
                } else if (ResultCode == 1) {
                    String message = jToken.getString("ResultDesc");
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException ex) {
            System.out.println("JSON Error " + ex);
        }
    }

    public void registerForm() {
        Intent formActivity = new Intent(this, FormActivity.class);
        formActivity.putExtra("viewLink", "1");
        startActivity(formActivity);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected=Apps.getSelectedItem().toString();
        String url=null;
        if(selected.equals("HR")){url="https://demo.dewcis.com/hr/dataserver";}
        else if(selected.equals("Banking")){url="https://demo.dewcis.com/banking/dataserver";}
        else if(selected.equals("Transport")){url="https://demo.dewcis.com/agency/dataserver";}

        DataClient DC=new DataClient(selected);


    }
    public void saveServiceType(String selection){
        SharedPreferences servicePreferences=getSharedPreferences("Services",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=servicePreferences.edit();
        editor.putString("Service",selection);
        editor.apply();
    }
    public void setSavedService(){
        SharedPreferences servicePreferences=getSharedPreferences("Services",Context.MODE_PRIVATE);
        String service=servicePreferences.getString("Service","N/A");
        int i=0;
        if(!service.equals("N/A")){
            ArrayAdapter spinAdapter= (ArrayAdapter) Apps.getAdapter();
            i=spinAdapter.getPosition(service);
            Apps.setSelection(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
