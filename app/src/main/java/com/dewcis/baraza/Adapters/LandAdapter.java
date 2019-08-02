package com.dewcis.baraza.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dewcis.baraza.Models.LandModel;
import com.dewcis.baraza.R;
import com.dewcis.baraza.Land;

import java.util.ArrayList;

/**
 * Created by Faith on 4/20/2018.
 */

public class LandAdapter extends BaseAdapter {

    Context context;
    ArrayList<LandModel> landList;
    String token;

    public LandAdapter(Context context, ArrayList<LandModel> landList, String token) {
        this.context = context;
        this.landList = landList;
        this.token = token;
    }

    @Override
    public int getCount() {
        return landList.size();
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

        tvNumber.setText("Plot number :"+landList.get(i).getPlotNumber());
        tvLocation.setText("Location :"+landList.get(i).getPlotLocation());
        tvSize.setText("Size :"+landList.get(i).getPlotSize());

        return view;
    }
}
