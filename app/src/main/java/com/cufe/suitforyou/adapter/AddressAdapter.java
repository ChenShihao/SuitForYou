package com.cufe.suitforyou.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cufe.suitforyou.model.Address;
import com.cufe.suitforyou.utils.MyUtil;

/**
 * Created by Victor on 2016-09-13.
 */
public class AddressAdapter extends BaseAdapter {

    private Address[] addresses;

    public AddressAdapter(Address[] addresses) {
        this.addresses = addresses;
    }

    @Override
    public int getCount() {
        return addresses != null && addresses.length > 0 ? addresses.length + 1 : 1;
    }

    @Override
    public Object getItem(int position) {
        return position == getCount() - 1 ? null : addresses[position];
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
        textView.setText(position == getCount() - 1 ? "新增收货信息" : getItem(position).toString());
        return textView;
    }
}
