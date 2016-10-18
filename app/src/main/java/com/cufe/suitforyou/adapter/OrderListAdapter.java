package com.cufe.suitforyou.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cufe.suitforyou.BR;
import com.cufe.suitforyou.R;
import com.cufe.suitforyou.model.CartItem;

/**
 * Created by Victor on 2016-09-10.
 */
public class OrderListAdapter extends RecyclerView.Adapter<DataBindingViewHolder> {

    private CartItem[] list;

    public OrderListAdapter(CartItem[] list) {
        this.list = list;
    }

    @Override
    public DataBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.customview_order_list_item, parent, false);
        DataBindingViewHolder holder = new DataBindingViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder holder, int position) {
        holder.getBinding().setVariable(BR.orderItem, list[position]);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

}
