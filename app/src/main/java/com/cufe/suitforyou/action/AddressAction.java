package com.cufe.suitforyou.action;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cufe.suitforyou.model.Area;
import com.cufe.suitforyou.model.City;
import com.cufe.suitforyou.model.Province;
import com.cufe.suitforyou.db.DBManager;

import java.util.ArrayList;

/**
 * Created by Victor on 2016-09-12.
 */
public class AddressAction {

    private SQLiteDatabase mDB;

    public AddressAction(Context context) {
        mDB = DBManager.getInstance(context).getmDB();
    }

    public Province[] queryProvince() {
        ArrayList<Province> list = new ArrayList<>();
        Cursor cursor = mDB.query("province", new String[]{"PROVINCE_ID", "PROVINCE"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Province province = new Province();
            province.setPROVINCE(cursor.getString(cursor.getColumnIndex("PROVINCE")));
            province.setPROVINCE_ID(cursor.getString(cursor.getColumnIndex("PROVINCE_ID")));
            list.add(province);
        }
        cursor.close();
        Province[] provinces = new Province[list.size()];
        list.toArray(provinces);
        return provinces;
    }

    public City[] queryCity(String PROVINCE_ID) {
        ArrayList<City> list = new ArrayList<>();
        Cursor cursor = mDB.query("city", new String[]{"CITY_ID", "CITY"}, "FATHER=?", new String[]{PROVINCE_ID}, null, null, null);
        while (cursor.moveToNext()) {
            City city = new City();
            city.setCITY(cursor.getString(cursor.getColumnIndex("CITY")));
            city.setCITY_ID(cursor.getString(cursor.getColumnIndex("CITY_ID")));
            list.add(city);
        }
        cursor.close();
        City[] cities = new City[list.size()];
        list.toArray(cities);
        return cities;
    }

    public Area[] queryArea(String CITY_ID) {
        ArrayList<Area> list = new ArrayList<>();
        Cursor cursor = mDB.query("area", new String[]{"AREA_ID", "AREA"}, "FATHER=?", new String[]{CITY_ID}, null, null, null);
        while (cursor.moveToNext()) {
            Area area = new Area();
            area.setAREA(cursor.getString(cursor.getColumnIndex("AREA")));
            area.setAREA_ID(cursor.getString(cursor.getColumnIndex("AREA_ID")));
            list.add(area);
        }
        cursor.close();
        Area[] areas = new Area[list.size()];
        list.toArray(areas);
        return areas;
    }

}
