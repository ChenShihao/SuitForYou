package com.cufe.suitforyou.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.cufe.suitforyou.BR;

/**
 * Created by Victor on 2016-09-16.
 */
public class Comment extends BaseObservable {

    private String userNickName;
    private String comment;
    private float score;
    private CartItem sku;

    @Bindable
    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
        notifyPropertyChanged(BR.userNickName);
    }

    @Bindable
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
        notifyPropertyChanged(BR.comment);
    }

    @Bindable
    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
        notifyPropertyChanged(BR.score);
    }

    @Bindable
    public CartItem getSku() {
        return sku;
    }

    public void setSku(CartItem sku) {
        this.sku = sku;
        notifyPropertyChanged(BR.sku);
    }
}
