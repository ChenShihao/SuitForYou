package com.cufe.suitforyou.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cufe.suitforyou.model.Comment;
import com.cufe.suitforyou.model.DetailItem;
import com.cufe.suitforyou.fragment.PageCommentFragment;
import com.cufe.suitforyou.fragment.PageDetailFragment;

import java.util.ArrayList;

/**
 * Created by Victor on 2016-09-08.
 */
public class DetailPageAdapter extends FragmentPagerAdapter {

    private String[] title;

    private DetailItem item;

    private ArrayList<Comment> comments;

    public DetailPageAdapter(FragmentManager fm, DetailItem item, ArrayList<Comment> comments) {
        super(fm);
        this.item = item;
        this.comments = comments;
        this.title = new String[]{"详情", comments == null ? "评价" : "评价（" + comments.size() + "）"};
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PageDetailFragment.newInstance(item);
            case 1:
                return PageCommentFragment.newInstance(comments);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
