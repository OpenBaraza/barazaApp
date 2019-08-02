package com.dewcis.baraza.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dewcis.baraza.R;

/**
 * Created by Faith on 4/10/2018.
 */

public class PaymentsAdapter extends BaseAdapter {

    Context context;
    int [] images;

    public PaymentsAdapter(Context context,int [] images ){
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return images.length;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.payment_list,null);

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        imageView.setImageResource(images[i]);

        return view;
    }
}
