package com.cufe.suitforyou.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cufe.suitforyou.BR;
import com.cufe.suitforyou.R;
import com.cufe.suitforyou.model.CartItem;
import com.cufe.suitforyou.customclass.MyInteger;
import com.cufe.suitforyou.utils.AnimationUtil;

import java.util.ArrayList;

/**
 * Created by Victor on 2016-09-10.
 */
public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartListViewHolder> {

    private int FLAG = 0;

    private ArrayList<CartItem> list;

    private ArrayList<MyInteger> positionList;

    public CartListAdapter(final ArrayList<CartItem> list) {
        this.list = list;
        this.positionList = new ArrayList<MyInteger>() {{
            for (int index = 0; index < list.size(); index++) {
                add(new MyInteger(index));
            }
        }};
    }

    class CartListViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public CartListViewHolder(View itemView) {
            super(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }
    }

    @Override
    public CartListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.customview_cart_list_item, parent, false);
        CartListViewHolder holder = new CartListViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(CartListViewHolder holder, int position) {
        holder.getBinding().setVariable(BR.cartItem, list.get(position));
        holder.getBinding().setVariable(BR.position, positionList.get(position));
        holder.getBinding().executePendingBindings();
        if (FLAG < list.size()) {
            FLAG = position + 1;
            AnimationUtil.RecyleViewItemEnterAnimHorizontal(holder.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        for (int index = position; index < positionList.size(); index++) {
            int value = positionList.get(index).getValue();
            positionList.get(index).setValue(--value);
        }
    }
}
