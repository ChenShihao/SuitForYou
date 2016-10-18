package com.cufe.suitforyou.model;

/**
 * Created by Victor on 2016-09-12.
 */
public class Area {

    private int ID;
    private String AREA_ID;
    private String AREA;
    private String FATHER;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getAREA_ID() {
        return AREA_ID;
    }

    public void setAREA_ID(String AREA_ID) {
        this.AREA_ID = AREA_ID;
    }

    public String getAREA() {
        return AREA;
    }

    public void setAREA(String AREA) {
        this.AREA = AREA;
    }

    public String getFATHER() {
        return FATHER;
    }

    public void setFATHER(String FATHER) {
        this.FATHER = FATHER;
    }
}
