package com.cufe.suitforyou.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cufe.suitforyou.BR;
import com.cufe.suitforyou.R;
import com.cufe.suitforyou.model.CartItem;
import com.cufe.suitforyou.utils.AnimationUtil;

import java.util.ArrayList;

/**
 * Created by Victor on 2016-09-10.
 */
public class AddCommentListAdapter extends RecyclerView.Adapter<DataBindingViewHolder> {

    private int FLAG = 0;

    private ArrayList<CartItem> list;

    public AddCommentListAdapter(ArrayList<CartItem> list) {
        this.list = list;
    }

    @Override
    public DataBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.customview_add_comment_list_item, parent, true);
        DataBindingViewHolder holder = new DataBindingViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder holder, int position) {
        holder.getBinding().setVariable(BR.addCommentCartItem, list.get(position));
        holder.getBinding().setVariable(BR.addCommentItemPosition, position);
        holder.getBinding().executePendingBindings();
        if (FLAG <= position) {
            FLAG = position + 1;
            AnimationUtil.RecyleViewItemEnterAnimVertical(holder.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
