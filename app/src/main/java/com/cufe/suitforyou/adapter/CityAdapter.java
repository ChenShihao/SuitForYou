package com.cufe.suitforyou.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cufe.suitforyou.model.City;
import com.cufe.suitforyou.utils.MyUtil;

/**
 * Created by Victor on 2016-09-13.
 */
public class CityAdapter extends BaseAdapter {

    private City[] cities;

    public CityAdapter(City[] cities) {
        this.cities = cities;
    }

    @Override
    public int getCount() {
        return cities != null && cities.length > 0 ? cities.length : 1;
    }

    @Override
    public Object getItem(int position) {
        return cities != null && cities.length > 0 ? cities[position] : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(parent.getContext());
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        int padding = MyUtil.dpToPx(parent.getContext(), 12);
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(cities != null && cities.length > 0 ? cities[position].getCITY() : "");
        return textView;
    }
}
