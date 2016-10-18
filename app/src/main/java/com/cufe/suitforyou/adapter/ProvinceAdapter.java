package com.cufe.suitforyou.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cufe.suitforyou.model.Province;
import com.cufe.suitforyou.utils.MyUtil;

/**
 * Created by Victor on 2016-09-13.
 */
public class ProvinceAdapter extends BaseAdapter {

    private Province[] provinces;

    public ProvinceAdapter(Province[] provinces) {
        this.provinces = provinces;
    }

    @Override
    public int getCount() {
        return provinces.length;
    }

    @Override
    public Object getItem(int position) {
        return provinces[position];
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
        textView.setText(provinces[position].getPROVINCE());
        return textView;
    }
}
