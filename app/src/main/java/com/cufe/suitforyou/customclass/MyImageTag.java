package com.cufe.suitforyou.customclass;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.cufe.suitforyou.BR;

/**
 * Created by Victor on 2016-09-09.
 */
public class MyImageTag extends BaseObservable {

    private String tag;

    private String url;

    @Bindable
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        notifyPropertyChanged(BR.url);
    }

    @Bindable
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        notifyPropertyChanged(BR.tag);
    }
}
