package com.dewcis.baraza.Adapters;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dewcis.baraza.Models.Vehicle;
import com.dewcis.baraza.R;
import com.dewcis.baraza.Utils.DataClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * Created by Faith on 4/6/2018.
 */

public class VehicleAdapter extends BaseAdapter {

    Context context;
    ArrayList<Vehicle> vehicleItems = new ArrayList<>();
    String token;

    public VehicleAdapter(Context context,ArrayList<Vehicle> vehicleItems, String token){
        this.context = context;
        this.vehicleItems = vehicleItems;
        this.token = token;
    }

    @Override
    public int getCount() {
        return vehicleItems.size();
    }

    @Override
    public Object getItem(int i) {
        return vehicleItems.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.e("base5050","Start vAdapter");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.vehicle_list,null);

        final TextView Balance = (TextView)view.findViewById(R.id.tvBalance);
        final TextView Registration = (TextView)view.findViewById(R.id.tvRegistration);
        TextView payment    = (TextView)view.findViewById(R.id.tvPay);

        Registration.setText(vehicleItems.get(i).getRegNumber());
        Balance.setText("Balance is :"+vehicleItems.get(i).getBalance());
        final String id_number = vehicleItems.get(i).getVehicle_id();

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String registration = Registration.getText().toString();
                Log.e("base5050",registration);
                JSONArray jsonArray = new JSONArray();
                JSONObject json = new JSONObject();

                try{
                    json.put("id",id_number);
                    jsonArray.put(json);

                    //Pay parking for specific vehicle
                    String result = DataClient.makeSecuredRequest(token,"265:0&operation=0","operation","{}");
                            //postOpForm("265:0&operation=0",token,jsonArray.toString());
                    Log.e("base2030",result);

                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("ResultCode");

                    if (resultCode.equals("0")){
                        Toast.makeText(context,"Insufficient account balace. Top up and try again",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context,"Parking for "+registration+" paid successfully",Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    Log.e("JSONError",e.toString());
                }
            }
        });

        return view;
    }
}
