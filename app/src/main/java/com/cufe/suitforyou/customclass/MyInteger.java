package com.cufe.suitforyou.customclass;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.cufe.suitforyou.BR;

/**
 * Created by Victor on 2016-09-11.
 */
public class MyInteger extends BaseObservable {

    private Integer value;

    public MyInteger(Object value) {
        this.value = Integer.parseInt(String.valueOf(value));
    }

    @Bindable
    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
        notifyPropertyChanged(BR.value);
    }
}
