package com.cufe.suitforyou.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.cufe.suitforyou.BR;

/**
 * Created by Victor on 2016-09-13.
 */
public class Address extends BaseObservable {

    private int addressId;
    private String province;
    private String city;
    private String area;
    private String addressInfo;
    private String receiverName;
    private String receiverPhone;

    @Bindable
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
        notifyPropertyChanged(BR.addressId);
    }

    @Bindable
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
        notifyPropertyChanged(BR.province);
    }

    @Bindable
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        notifyPropertyChanged(BR.city);
    }

    @Bindable
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
        notifyPropertyChanged(BR.area);
    }

    @Bindable
    public String getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
        notifyPropertyChanged(BR.addressInfo);
    }

    @Bindable
    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
        notifyPropertyChanged(BR.receiverName);
    }

    @Bindable
    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
        notifyPropertyChanged(BR.receiverPhone);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s %s", province, city, area, addressInfo, receiverName, receiverPhone);
    }
}
