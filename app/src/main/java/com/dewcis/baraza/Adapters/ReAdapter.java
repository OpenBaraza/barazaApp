package com.dewcis.baraza.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dewcis.baraza.Models.RentModel;
import com.dewcis.baraza.Models.Vehicle;
import com.dewcis.baraza.R;

import java.util.ArrayList;

/**
 * Created by Faith on 4/18/2018.
 */

public class ReAdapter extends BaseAdapter {

    Context context;
    ArrayList<RentModel> rentItems = new ArrayList<>();
    String token;

    public ReAdapter(Context context, ArrayList<RentModel> rentItems, String token) {
        this.context = context;
        this.rentItems = rentItems;
        this.token = token;
    }

    @Override
    public int getCount() {
        return rentItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.e("base5057","Start vAdapter");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.house_list,null);

        TextView location = (TextView)view.findViewById(R.id.tvLocation);
        TextView type = (TextView)view.findViewById(R.id.tvType);
        TextView amount = (TextView)view.findViewById(R.id.tvBalance);
        TextView pay = (TextView)view.findViewById(R.id.tvPay);

        location.setText("Location : "+rentItems.get(i).getLocation());
        type.setText("Type : "+rentItems.get(i).getType());
        amount.setText("Amount : "+rentItems.get(i).getBalance());

        return view;
    }
}
