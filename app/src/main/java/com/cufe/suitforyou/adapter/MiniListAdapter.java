package com.cufe.suitforyou.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cufe.suitforyou.BR;
import com.cufe.suitforyou.R;
import com.cufe.suitforyou.model.SimpleItem;
import com.cufe.suitforyou.utils.AnimationUtil;

import java.util.ArrayList;

/**
 * Created by Victor on 2016-09-14.
 */
public class MiniListAdapter extends RecyclerView.Adapter<DataBindingViewHolder> {

    private int FLAG = 0;
    private ArrayList<SimpleItem> list = new ArrayList<>();

    public MiniListAdapter(ArrayList<SimpleItem> list) {
        this.list = list;
    }

    @Override
    public DataBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.customview_list_item_mini, parent, false);
        DataBindingViewHolder viewHolder = new DataBindingViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder holder, int position) {
        holder.getBinding().setVariable(BR.miniItem, list.get(position));
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
