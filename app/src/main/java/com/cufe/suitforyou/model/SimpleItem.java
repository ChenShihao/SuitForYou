package com.cufe.suitforyou.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.cufe.suitforyou.BR;


/**
 * Created by Victor on 2016-09-05.
 */
public class SimpleItem extends BaseObservable {

    private int clothesId;
    private String title;
    private double price;
    private int payNumber;
    private double commentGreat;
    private String photo;

    @Bindable
    public double getPrice() {
        return price;
    }

    @Bindable
    public int getClothesId() {
        return clothesId;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    @Bindable
    public int getPayNumber() {
        return payNumber;
    }

    @Bindable
    public double getCommentGreat() {
        return commentGreat;
    }

    @Bindable
    public String getPhoto() {
        return photo;
    }

    public void setClothesId(int clothesId) {
        this.clothesId = clothesId;
        notifyPropertyChanged(BR.clothesId);
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    public void setPrice(double price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    public void setPayNumber(int payNumber) {
        this.payNumber = payNumber;
        notifyPropertyChanged(BR.payNumber);
    }

    public void setCommentGreat(double commentGreat) {
        this.commentGreat = commentGreat;
        notifyPropertyChanged(BR.commentGreat);
    }

    public void setPhoto(String photo) {
        this.photo = photo;
        notifyPropertyChanged(BR.photo);
    }
}
