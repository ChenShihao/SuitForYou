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
import java.util.List;

/**
 * Created by Victor on 2016-09-07.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<DataBindingViewHolder> {

    private final int NORMAL_ITEM = 0;

    private final int FOOT_ITEM = 1;

    private int FLAG = 0;

    private ArrayList<SimpleItem> list = new ArrayList<>();

    private int searchNum = 10;

    private List newList;

    public SearchResultAdapter(ArrayList<SimpleItem> list) {
        this.newList = list;
        this.list.addAll(list);
    }

    public SearchResultAdapter(ArrayList<SimpleItem> list, int searchNum) {
        this.newList = list;
        this.searchNum = searchNum;
        this.list.addAll(list);
    }

    public void addItem(ArrayList<SimpleItem> items, int searchNum) {
        ArrayList<Integer> ids = new ArrayList<Integer>() {{
            for (SimpleItem item : list) {
                add(item.getClothesId());
            }
        }};
        ArrayList<SimpleItem> cloneItems = (ArrayList) items.clone();
        for (SimpleItem item : items) {
            if (ids.contains(item.getClothesId()))
                cloneItems.remove(item);
        }

        this.searchNum = searchNum;
        this.newList = items;
        this.list.addAll(cloneItems);
        notifyDataSetChanged();
    }

    public void clearItems() {
        FLAG = 0;
        this.list.clear();
        this.newList = null;
        notifyDataSetChanged();
    }

    @Override
    public DataBindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DataBindingViewHolder holder;
        ViewDataBinding binding = null;
        switch (viewType) {
            case NORMAL_ITEM:
                binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.customview_search_list_item, parent, false);
                break;
            case FOOT_ITEM:
                binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.customview_card_tips, parent, false);
                break;
        }
        holder = new DataBindingViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder holder, int position) {
        if (position < getItemCount() - 1) {
            holder.getBinding().setVariable(BR.searchListItem, list.get(position));
        } else {
            if (newList.size() < searchNum) {
                holder.getBinding().setVariable(BR.tipText, "后面没有啦...");
                holder.getBinding().setVariable(BR.tipTag, 0);
            } else {
                holder.getBinding().setVariable(BR.tipText, "上拉或点击加载更多");
                holder.getBinding().setVariable(BR.tipTag, 1);
            }
        }
        holder.getBinding().executePendingBindings();
        int delta = list.size() - newList.size();
        if (position >= delta && FLAG < getItemCount()) {
            FLAG = position + 1;
            AnimationUtil.RecyleViewItemEnterAnimHorizontal(holder.itemView, FLAG - delta);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() > 0 ? list.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == list.size())
            return FOOT_ITEM;
        else
            return NORMAL_ITEM;
    }

}
