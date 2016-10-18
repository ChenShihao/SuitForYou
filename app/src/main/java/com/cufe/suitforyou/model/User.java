package com.cufe.suitforyou.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;

import com.cufe.suitforyou.BR;

/**
 * Created by Victor on 2016-09-01.
 */

public class User extends BaseObservable {

    private String name;
    private String birthday;
    private String phone;
    private String nickname;
    private String userDesc;
    private String sex;
    private Bitmap photo;
    private String email;

    public User() {
    }

    public User(String name, String birthday, String phone, String nickname, String userDesc, String sex, String email, Bitmap photo) {
        this.name = name;
        this.birthday = birthday;
        this.phone = phone;
        this.nickname = nickname;
        this.userDesc = userDesc;
        this.sex = sex;
        this.email = email;
        this.photo = photo;
    }

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public String getBirthday() {
        return birthday;
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    @Bindable
    public String getNickname() {
        return nickname;
    }

    @Bindable
    public String getUserDesc() {
        return userDesc;
    }

    @Bindable
    public String getSex() {
        return sex;
    }

    @Bindable
    public Bitmap getPhoto() {
        return photo;
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
        notifyPropertyChanged(BR.birthday);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        notifyPropertyChanged(BR.nickname);
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
        notifyPropertyChanged(BR.userDesc);
    }

    public void setSex(String sex) {
        this.sex = sex;
        notifyPropertyChanged(BR.sex);
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
        notifyPropertyChanged(BR.photo);
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }
}
