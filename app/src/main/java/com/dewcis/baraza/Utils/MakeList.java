package com.dewcis.baraza.Utils;

import android.util.Log;

import com.dewcis.baraza.Models.RentModel;
import com.dewcis.baraza.Models.Vehicle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Faith on 4/9/2018.
 */

public class MakeList {

    public static ArrayList<Vehicle> makeArrayList(JSONArray jsonArray) throws JSONException {
        ArrayList<Vehicle> arrayList = new ArrayList<>();

        for (int i=0;i<jsonArray.length();i++){
            try {
                arrayList.add(getJSON(jsonArray.getJSONObject(i)));

            }catch (JSONException e){
                e.toString();
            }
        }

        Log.e("CarList",arrayList.toString());

        return arrayList;
    }

    public static Vehicle getJSON(JSONObject json) throws JSONException {
        Vehicle vehicle = new Vehicle();
        vehicle.setRegNumber(json.getString("reg_number"));
        vehicle.setVehicle_id(json.getString("keyfield"));

        return vehicle;
    }


}
