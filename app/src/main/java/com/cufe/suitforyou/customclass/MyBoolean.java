package com.cufe.suitforyou.customclass;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.cufe.suitforyou.BR;

/**
 * Created by Victor on 2016-09-08.
 */
public class MyBoolean extends BaseObservable {

    private boolean bool;

    public MyBoolean(boolean bool) {
        this.bool = bool;
    }

    @Bindable
    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
        notifyPropertyChanged(BR.bool);
    }
}
