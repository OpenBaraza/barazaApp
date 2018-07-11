package com.dewcis.baraza.Utils;

import android.util.Base64;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
    //public static String url = "http://192.168.122.1:9090/hr/dataserver";
    //public static String url = "https://demo.dewcis.com/hr/dataserver";
    public static String url = "https://demo.dewcis.com/banking/dataserver";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

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
            System.out.println(resp);
        } catch(IOException ex) {
            System.out.println("IO Error : " + ex);
        }

        return resp;
    }

    //Method to get token
    public static JSONObject authenticate(String appKey, String appPass) {
        JSONObject jToken = null;
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
            OkHttpClient okHttpClient = new OkHttpClient();
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
            System.out.println("BASE 1010 : " + rBody);

            jToken = new JSONObject(rBody);
        }catch (IOException ex){
            System.out.println("IO Error " + ex);
        } catch (JSONException ex) {
            System.out.println("JSON Error " + ex);
        }

        return jToken;
    }

    //make secured request data like grid, Form request and saving
    public static String makeSecuredRequest(String auth, String viewLink, String action, String json) {
        String resp = null;
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(JSON, json);

            Request request = new Request.Builder()
                    .url(url + "?view=" + viewLink)
                    .post(requestBody)
                    .addHeader("action", action)
                    .addHeader("authorization", auth)
                    .addHeader("content-type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            resp=response.body().string();
            System.out.println(resp);
        } catch(IOException ex) {
            System.out.println("IO Error : " + ex);
        }

        return resp;
    }

    public static JSONObject getJObject(String rBody) {
        JSONObject jBody = null;
        try {
            jBody = new JSONObject(rBody);
        } catch (JSONException ex) {
            System.out.println("JSON Error " + ex);
        }
        return jBody;
    }

    public static JSONObject makeJSONRequest(String auth, String viewLink, String action, String json) {
        String rBody = makeSecuredRequest(auth, viewLink, action, json);
        JSONObject jBody = null;
        try {
            jBody = new JSONObject(rBody);
        } catch (JSONException ex) {
            System.out.println("JSON Error " + ex);
        }
        return jBody;
    }
}
