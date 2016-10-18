package com.cufe.suitforyou.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cufe.suitforyou.BR;
import com.cufe.suitforyou.R;
import com.cufe.suitforyou.model.Order;
import com.cufe.suitforyou.utils.AnimationUtil;

import java.util.ArrayList;

/**
 * Created by Victor on 2016-09-10.
 */
public class OrderManageListAdapter extends RecyclerView.Adapter<DataBindingViewHolder> {

    private int FLAG = 0;

    private ArrayList<Order> list;

    public OrderManageListAdapter(ArrayList<Order> list) {
        this.list = list;
    }

    @Override
    public DataBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.customview_order_manage_list_item, parent, false);
        DataBindingViewHolder holder = new DataBindingViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder holder, int position) {
        Order order = list.get(position);
        holder.getBinding().setVariable(BR.order, order);
        holder.getBinding().executePendingBindings();
        if (FLAG <= position) {
            FLAG = position + 1;
            AnimationUtil.RecyleViewItemEnterAnimVertical(holder.itemView, position);
        }
        if (order != null && order.getStatus() != null) {
            TextView textView = (TextView) holder.itemView.findViewById(R.id.order_manage_status_change);
            ViewGroup group = (ViewGroup) textView.getParent();
            switch (order.getStatus()) {
                case "未支付":
                    textView.setText("去支付");
                    group.setVisibility(View.VISIBLE);
                    break;
                case "已签收":
                    textView.setText("确认收货");
                    group.setVisibility(View.VISIBLE);
                    break;
                case "确认收货":
                    textView.setText("去评价");
                    group.setVisibility(View.VISIBLE);
                    break;
                default:
                    group.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
