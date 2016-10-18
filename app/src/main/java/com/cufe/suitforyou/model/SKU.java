package com.cufe.suitforyou.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.cufe.suitforyou.BR;

import java.io.Serializable;

/**
 * Created by Victor on 2016-09-08.
 */
public class SKU extends BaseObservable implements Serializable {

    private int id;
    private int stock;
    private String color;
    private String size;
    private String pattern;

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
        notifyPropertyChanged(BR.stock);
    }

    @Bindable
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyPropertyChanged(BR.color);
    }

    @Bindable
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
        notifyPropertyChanged(BR.size);
    }

    @Bindable
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        notifyPropertyChanged(BR.pattern);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s码", pattern, color, size);
    }
}
