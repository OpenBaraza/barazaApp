package com.dewcis.baraza;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.StrictMode;
import android.os.Bundle;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.ProgressDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import  android.Manifest.*;

import com.dewcis.baraza.Utils.DataClient;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.READ_PHONE_STATE;

/**
 * Created by Dennis Gichangi on 3/29/2018.
 * Update by Joseph Onalo
 */

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnSignIn, btnRegister, btnLocation;
    Spinner Apps;

    //Preferences
    private static final String PREF = "preferences";
    private static final String PREF_NAME = "Username";
    private final String DefaultNameValue = "";
    private String EmailValue;
    private String PasswordValue;
    final int PERMISSION_READ_STATE=0;
    TelephonyManager tm ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tm = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);





        Apps=(Spinner)findViewById(R.id.Apps);
        String[] apps={"HR","Banking","Transport","Sacco","Chama","Property","Assets","E-Lipa","Spider","Rider"};
        //String[] apps={"E-Lipa"};
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

            btnLocation=findViewById(R.id.btnLocation);
            btnLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Location();
                }
            });
            btnLocation.setVisibility(View.GONE);

            btnRegister = findViewById(R.id.btnRegister);
            btnRegister.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    registerForm();
                }
            });
        }
        if (ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE}, PERMISSION_READ_STATE);
        }
        else{
            @SuppressLint("MissingPermission") String simID = tm.getSimSerialNumber();
            @SuppressLint("MissingPermission") String telNumber = tm.getLine1Number();
            @SuppressLint("MissingPermission") String IMEI = tm.getDeviceId();
            String result="\nSim id : "+simID+"\nphone ID : "+IMEI+"\nPhone number :"+telNumber;

           // Toast.makeText(this,result,Toast.LENGTH_LONG).show();
            Log.e("Phone Details",result);
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
        //loadPreferences();
    }

    public void Location(){
        Intent intent=new Intent(this,LocationActivity.class);
        startActivity(intent);
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
        else if(selected.equals("Assets")){url="https://portal.dewcis.com/assets/dataserver";}
        else if(selected.equals("Transport")){url="https://demo.dewcis.com/agency/dataserver";}
        else if(selected.equals("Chama")){url="https://demo.dewcis.com/chama/dataserver";}
        else if(selected.equals("Sacco")){url="https://demo.dewcis.com/impress-sacco/dataserver";}
        else if(selected.equals("Property")){url="https://demo.dewcis.com/property-portal/dataserver";}
        else if(selected.equals("E-Lipa")){url="https://demo.dewcis.com/elipa/dataserver";}
        else if(selected.equals("Spider")){url="https://demo.dewcis.com/spidernest//dataserver";}
        else if(selected.equals("Rider")){url="http://192.168.0.163:9090/rider/dataserver";}

        //http://demo.dewcis.com/revenuecollection/
        //Revenue Collection
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
            else if(jToken.has("errorMessage")){
                Toast.makeText(this,jToken.getString("errorMessage"),Toast.LENGTH_LONG).show();
            }
            else {
                int ResultCode = jToken.getInt("ResultCode");
                if (ResultCode == 0) {
                    String accessToken = jToken.getString("access_token");
                    Intent mainDesk = new Intent(this, MainDesk.class);
                    mainDesk.putExtra("accessToken", accessToken);
                    mainDesk.putExtra("Portal",selected);
                    mainDesk.putExtra("phone",email);
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
        String selected=Apps.getSelectedItem().toString();
        String url=null;
        if(selected.equals("HR")){url="https://demo.dewcis.com/hr/dataserver";}
        else if(selected.equals("Banking")){url="https://demo.dewcis.com/banking/dataserver";}
        else if(selected.equals("Transport")){url="https://demo.dewcis.com/agency/dataserver";}
        else if(selected.equals("Chama")){url="https://demo.dewcis.com/chama/dataserver";}
        else if(selected.equals("Sacco")){url="https://demo.dewcis.com/impress-sacco/dataserver";}
        else if(selected.equals("Property")){url="https://demo.dewcis.com/property-portal/dataserver";}
        else if(selected.equals("E-Lipa")){url="https://demo.dewcis.com/elipa/dataserver";}

        else if(selected.equals("Spider")){url="https://demo.dewcis.com/spidernest//dataserver";}
        saveServiceType(selected);
        DataClient DC=new DataClient(url);
        Intent formActivity = new Intent(this, FormActivity.class);
        formActivity.putExtra("viewLink", "1");
        startActivity(formActivity);
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
    public void onBackPressed() {
        this.finish();
        System.exit(0);
    }


    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_READ_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                    @SuppressLint("MissingPermission") String simID = tm.getSimSerialNumber();
                    @SuppressLint("MissingPermission") String telNumber = tm.getLine1Number();
                    @SuppressLint("MissingPermission") String IMEI = tm.getDeviceId();

                    String result="\nSim id : "+simID+"\nphone ID : "+IMEI+"\nPhone number :"+telNumber;

                    //Toast.makeText(this,result,Toast.LENGTH_LONG).show();
                    Log.e("Phone Details",result);
                }
                else { }
                break;
            }
        }
    }

}
