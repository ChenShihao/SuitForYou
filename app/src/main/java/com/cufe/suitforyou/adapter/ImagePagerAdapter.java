package com.cufe.suitforyou.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cufe.suitforyou.customclass.MyImageTag;
import com.cufe.suitforyou.fragment.ImageContainerFragment;

/**
 * Created by Victor on 2016-09-03.
 */
public class ImagePagerAdapter extends FragmentPagerAdapter {

    private MyImageTag[] urls;

    public ImagePagerAdapter(FragmentManager fm, MyImageTag[] urls) {
        super(fm);
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.length;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageContainerFragment.newInstance(urls[position]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position + 1 + " / " + urls.length);
    }


}
