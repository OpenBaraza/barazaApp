package com.dewcis.baraza.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.widget.Toast;

import com.dewcis.baraza.FormActivity;
import com.dewcis.baraza.FormViewActivity;
import com.dewcis.baraza.ReportActivity;
import com.dewcis.baraza.TableActivity;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Faith Mandela on 3/29/2018.
 * Update by Dennis Gichangi and Joseph Onalo
 */

public class DataClient {
    //public static String url = "http://192.168.122.1:9090/banking/dataserver";
   // public static String url = "https://demo.dewcis.com/hr/dataserver";
    //public static String url = "https://demo.dewcis.com/banking/dataserver";
     public static String url;
     public DataClient(String url){
         this.url=url;
     }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public  static boolean Connected(Context context){
        boolean connected=false;
        ConnectivityManager CM= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=CM.getActiveNetworkInfo();
        if(info!=null && info.isConnected()){return  true;}
        return  false;
    }

    //make unsecured request data like uForm request and saving
    public static String makeUnsecuredRequest( String viewLink, String action, String json) {
        String resp = null;
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json);

            Request request = new Request.Builder()
                    .url(url + "?view=" + viewLink)
                    .post(body)
                    .addHeader("action", action)
                    .addHeader("content-type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();

            resp = response.body().string();
        } catch(IOException ex) {
            System.out.println("IO Error : " + ex);
        }

        return resp;
    }

    //Method to get token
    public static JSONObject authenticate(String appKey, String appPass,Context context) {
        JSONObject jToken = null;
        JSONObject errorMessage=new JSONObject();
        byte[] user;
        byte[] pass;
        try {
            user = appKey.getBytes("UTF-8");
            pass = appPass.getBytes("UTF-8");
        }catch(UnsupportedEncodingException ex) {
            System.out.println("Encoding Error " + ex);
            return jToken;
        }

        String authUser = Base64.encodeToString(user,Base64.URL_SAFE|Base64.NO_PADDING|Base64.NO_WRAP);
        String authPass = Base64.encodeToString(pass, Base64.URL_SAFE|Base64.NO_PADDING|Base64.NO_WRAP);
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS);
            OkHttpClient okHttpClient = builder.build();

            okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .addHeader("action", "authorization")
                .addHeader("authuser", authUser)
                .addHeader("authpass", authPass)
                .addHeader("cache-control", "no-cache")
                .build();
            okhttp3.Response response = okHttpClient.newCall(request).execute();
            String rBody = response.body().string();
            System.out.println("response Body\n"+rBody);

            jToken = new JSONObject(rBody);
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("IO Error (timeout)" + e);
            try {
                errorMessage.put("errorMessage","Sorry ,we couldn't connect to the server, please check your connection");
                return  errorMessage;
            }
            catch (JSONException ep) {ep.printStackTrace();}

        }
        catch (IOException ex){System.out.println("IO Error " + ex);}
        catch (JSONException ex ){
            System.out.println("JSON Error " + ex);
            try {
                errorMessage.put("errorMessage","Sorry ,the server is currently down");
                return  errorMessage;
            }
            catch (JSONException e) {e.printStackTrace();}
        }

        return jToken;
    }

    //make secured request data like grid, Form request and saving
    public static String makeSecuredRequest(String auth, String viewLink, String action, String json) {
        String resp = null;
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(JSON, json);
            System.out.println("\n URL---"+url+"\naction---"+action);
            Request request = new Request.Builder()
                    .url(url + "?view=" + viewLink)
                    .post(requestBody)
                    .addHeader("action", action)
                    .addHeader("authorization", auth)
                    .addHeader("content-type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            resp=response.body().string();
        } catch(IOException ex) {System.out.println("IO Error : " + ex);}

        return resp;
    }

    public static JSONObject getJObject(String rBody) {
        JSONObject jBody = null;
        try {jBody = new JSONObject(rBody);}
            catch (JSONException ex) {System.out.println("JSON Error " + ex);}
        return jBody;
    }

    public static JSONObject makeJSONRequest(String auth, String viewLink, String action, String json) {
        String rBody = makeSecuredRequest(auth, viewLink, action, json);
        JSONObject jBody = null;
        try {jBody = new JSONObject(rBody);}
        catch (JSONException ex) {System.out.println("JSON Error " + ex);}
        return jBody;
    }

    public static void StartIntent(Context context, HashMap<String,String> Map){
        Intent intent=null;
        String accessToken=Map.get("accessToken");
        String viewLink=Map.get("viewLink");
        String rBody = DataClient.makeSecuredRequest(accessToken, viewLink, "view", "{}");
        if(rBody!=null){
            try {
                JSONObject jBody = DataClient.getJObject(rBody);
                int viewType = jBody.getInt("typeId");
                System.out.println("\nview type\n"+viewType);
                String viewName = jBody.getString("name");
                switch (viewType) {
                    case 8:         // Form view
                        intent = new Intent(context, FormViewActivity.class);
                        if(Map.containsKey("selectedValue")){intent.putExtra("linkValue",Map.get("selectedValue"));}
                        break;
                    case 9:         // Grid view
                        intent = new Intent(context, TableActivity.class);
                        if(Map.containsKey("selectedValue")){intent.putExtra("linkValue",Map.get("selectedValue"));}
                        break;
                    case 10:        // HTML report view
                        intent = new Intent(context, ReportActivity.class);
                        if(Map.containsKey("selectedValue")){intent.putExtra("linkValue",Map.get("selectedValue"));}
                        break;
                    case 7:         // Edit view
                        intent = new Intent(context, FormActivity.class);
                        if(Map.containsKey("linkValue")){intent.putExtra("linkValue",Map.get("linkValue"));}
                        if(Map.containsKey("selectedValue")){intent.putExtra("keyValue",Map.get("selectedValue"));}
                        intent.putExtra("Edit","true");
                        break;
                }
                intent.putExtra("accessToken", accessToken);
                intent.putExtra("viewLink", viewLink);
                intent.putExtra("viewName", viewName);

                context.startActivity(intent);
            }
            catch (JSONException ex)  { Toast.makeText(context,"Sorry ,format error has occured",Toast.LENGTH_LONG).show();}
            catch (NullPointerException ex)  { Toast.makeText(context,"Sorry ,this feature is currently unavailbale",Toast.LENGTH_LONG).show();}
        }
        else Toast.makeText(context,"Poor or no network connection",Toast.LENGTH_LONG).show();
    }
}
