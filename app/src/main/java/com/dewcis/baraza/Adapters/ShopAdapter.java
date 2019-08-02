package com.dewcis.baraza.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dewcis.baraza.Models.ShopModel;
import com.dewcis.baraza.R;

import java.util.ArrayList;

/**
 * Created by Faith on 4/20/2018.
 */

public class ShopAdapter extends BaseAdapter {
    Context context;
    ArrayList<ShopModel> arrayList;
    String token;

    public ShopAdapter(Context context, ArrayList<ShopModel> arrayList, String token) {
        this.context = context;
        this.arrayList = arrayList;
        this.token = token;
    }

    @Override
    public int getCount() {
        return arrayList.size();
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.land_list,null);

        TextView tvNumber = (TextView)view.findViewById(R.id.tvPlotNumber);
        TextView tvLocation = (TextView)view.findViewById(R.id.tvLocation);
        TextView tvSize= (TextView)view.findViewById(R.id.tvSize);

        tvNumber.setText("Shop Number :"+arrayList.get(i).getNumber());
        tvLocation.setText("Shop Location :"+arrayList.get(i).getLocation());
        tvSize.setText("Shop Size :"+arrayList.get(i).getSize());


        return view;
    }
}
