package com.bitsgoa.macclub.news.plus.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Android on 16-01-2016.
 */
public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> arrayListString = new ArrayList<String>();

    public CustomAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayListString = arrayList;

    }

    @Override
    public int getCount() {
        return arrayListString.size();
    }

    @Override
    public String getItem(int position) {
        return arrayListString.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_adapter_layout, null);
            TextView title = (TextView) view.findViewById(R.id.customadaptertextview);
            title.setText(arrayListString.get(position));
        }
        return view;
    }
}
