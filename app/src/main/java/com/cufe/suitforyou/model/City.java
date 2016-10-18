package com.cufe.suitforyou.model;

/**
 * Created by Victor on 2016-09-12.
 */
public class City {

    private int ID;
    private String CITY_ID;
    private String CITY;
    private String FATHER;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCITY_ID() {
        return CITY_ID;
    }

    public void setCITY_ID(String CITY_ID) {
        this.CITY_ID = CITY_ID;
    }

    public String getCITY() {
        return CITY;
    }

    public void setCITY(String CITY) {
        this.CITY = CITY;
    }

    public String getFATHER() {
        return FATHER;
    }

    public void setFATHER(String FATHER) {
        this.FATHER = FATHER;
    }
}
