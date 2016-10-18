package com.cufe.suitforyou.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cufe.suitforyou.model.Area;
import com.cufe.suitforyou.utils.MyUtil;

/**
 * Created by Victor on 2016-09-13.
 */
public class AreaAdapter extends BaseAdapter {

    private Area[] areas;

    public AreaAdapter(Area[] areas) {
        this.areas = areas;
    }

    @Override
    public int getCount() {
        return areas != null && areas.length > 0 ? areas.length : 1;
    }

    @Override
    public Object getItem(int position) {
        return areas != null && areas.length > 0 ? areas[position] : null;
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
        textView.setText(areas != null && areas.length > 0 ? areas[position].getAREA() : "");
        return textView;
    }
}
