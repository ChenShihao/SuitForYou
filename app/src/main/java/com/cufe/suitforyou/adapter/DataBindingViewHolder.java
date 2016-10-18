package com.cufe.suitforyou.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Victor on 2016-09-14.
 */
public class DataBindingViewHolder extends RecyclerView.ViewHolder {

    private ViewDataBinding binding;

    public DataBindingViewHolder(View itemView) {
        super(itemView);
    }

    public ViewDataBinding getBinding() {
        return binding;
    }

    public void setBinding(ViewDataBinding binding) {
        this.binding = binding;
    }

}
