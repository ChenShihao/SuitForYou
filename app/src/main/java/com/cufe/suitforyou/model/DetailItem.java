package com.cufe.suitforyou.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.cufe.suitforyou.BR;

import java.io.Serializable;

/**
 * Created by Victor on 2016-09-08.
 */
public class DetailItem extends BaseObservable implements Serializable {

    private int id;
    private String title;
    private String desc;
    private double price;
    private int payNumber;
    private int commentNumber;
    private String status;
    private String environment;
    private String material;
    private String people;
    private String popularElement;
    private String style;
    private String brand;
    private String[] photos;
    private String[] classifyChain;
    private String[] detailPhotos;
    private SKU[] skus;

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
        notifyPropertyChanged(BR.desc);
    }

    @Bindable
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public int getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(int payNumber) {
        this.payNumber = payNumber;
        notifyPropertyChanged(BR.payNumber);
    }

    @Bindable
    public int getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
        notifyPropertyChanged(BR.commentNumber);
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
    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
        notifyPropertyChanged(BR.environment);
    }

    @Bindable
    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
        notifyPropertyChanged(BR.material);
    }

    @Bindable
    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
        notifyPropertyChanged(BR.people);
    }

    @Bindable
    public String getPopularElement() {
        return popularElement;
    }

    public void setPopularElement(String popularElement) {
        this.popularElement = popularElement;
        notifyPropertyChanged(BR.popularElement);
    }

    @Bindable
    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
        notifyPropertyChanged(BR.style);
    }

    @Bindable
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
        notifyPropertyChanged(BR.brand);
    }

    @Bindable
    public String[] getClassifyChain() {
        return classifyChain;
    }

    public void setClassifyChain(String[] classifyChain) {
        this.classifyChain = classifyChain;
        notifyPropertyChanged(BR.classifyChain);
    }

    @Bindable
    public String[] getDetailPhotos() {
        return detailPhotos;
    }

    public void setDetailPhotos(String[] detailPhotos) {
        this.detailPhotos = detailPhotos;
        notifyPropertyChanged(BR.detailPhotos);
    }

    @Bindable
    public SKU[] getSkus() {
        return skus;
    }

    public void setSkus(SKU[] skus) {
        this.skus = skus;
        notifyPropertyChanged(BR.skus);
    }

    @Bindable
    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
        notifyPropertyChanged(BR.photos);
    }
}
