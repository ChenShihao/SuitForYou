package com.cufe.suitforyou.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.cufe.suitforyou.BR;

/**
 * Created by Victor on 2016-09-17.
 */
public class CommentPost extends BaseObservable {

    private int orderId;
    private int skuId;
    private String comment;
    private float score;

    public CommentPost(int orderId, int skuId, String comment, float score) {
        this.orderId = orderId;
        this.skuId = skuId;
        this.comment = comment;
        this.score = score;
    }

    @Bindable
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        notifyPropertyChanged(BR.orderId);
    }

    @Bindable
    public int getSkuId() {
        return skuId;
    }

    public void setSkuId(int skuId) {
        this.skuId = skuId;
        notifyPropertyChanged(BR.skuId);
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
}
