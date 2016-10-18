package com.cufe.suitforyou.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.cufe.suitforyou.BR;

/**
 * Created by Victor on 2016-09-15.
 */
public class Order extends BaseObservable {

    private int oid;
    private String createTime;
    private String status;
    private Address address;
    private float totalPrice;
    private CartItem[] skus;

    @Bindable
    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
        notifyPropertyChanged(BR.oid);
    }

    @Bindable
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }

    @Bindable
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
        notifyPropertyChanged(BR.totalPrice);
    }

    @Bindable
    public CartItem[] getSkus() {
        return skus;
    }

    public void setSkus(CartItem[] skus) {
        this.skus = skus;
        notifyPropertyChanged(BR.skus);
    }
}
