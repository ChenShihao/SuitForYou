package com.cufe.suitforyou.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cufe.suitforyou.model.Area;
import com.cufe.suitforyou.model.City;
import com.cufe.suitforyou.model.Province;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Victor on 2016-09-12.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private Context context;

    private final static String DBName = "hat";

    private final static String createProvinceTable = "CREATE TABLE province (" +
            "  ID int(11) NOT NULL," +
            "  PROVINCE_ID varchar(6) DEFAULT NULL," +
            "  PROVINCE varchar(50) DEFAULT NULL" +
            ")";

    private final static String createCityTable = "CREATE TABLE city (" +
            "  ID int(11) NOT NULL," +
            "  CITY_ID varchar(6) DEFAULT NULL," +
            "  CITY varchar(50) DEFAULT NULL," +
            "  FATHER varchar(6) DEFAULT NULL" +
            ")";

    private final static String createAreaTable = "CREATE TABLE area (" +
            "  ID int(11) NOT NULL," +
            "  AREA_ID varchar(6) DEFAULT NULL," +
            "  AREA varchar(50) DEFAULT NULL," +
            "  FATHER varchar(6) DEFAULT NULL" +
            ") ";

    public MySQLiteHelper(Context context) {
        super(context, DBName, null, 1);
        this.context = context;
    }

    public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createProvinceTable);
        db.execSQL(createCityTable);
        db.execSQL(createAreaTable);

        try {
            AssetManager manager = context.getAssets();

            db.beginTransaction();
            InputStream provinceIS = manager.open("Province.json");
            String provinceJSON = readInputStream(provinceIS);
            Province[] provinces = new Gson().fromJson(provinceJSON, Province[].class);
            for (Province province : provinces) {
                ContentValues values = new ContentValues();
                values.put("ID", province.getID());
                values.put("PROVINCE_ID", province.getPROVINCE_ID());
                values.put("PROVINCE", province.getPROVINCE());

                db.insert("province", null, values);
            }

            InputStream cityIS = manager.open("City.json");
            String cityJSON = readInputStream(cityIS);
            City[] citys = new Gson().fromJson(cityJSON, City[].class);
            for (City city : citys) {
                ContentValues values = new ContentValues();
                values.put("ID", city.getID());
                values.put("CITY_ID", city.getCITY_ID());
                values.put("CITY", city.getCITY());
                values.put("FATHER", city.getFATHER());

                db.insert("city", null, values);
            }

            InputStream areaIS = manager.open("Area.json");
            String areaJSON = readInputStream(areaIS);
            Area[] areas = new Gson().fromJson(areaJSON, Area[].class);
            for (Area area : areas) {
                ContentValues values = new ContentValues();
                values.put("ID", area.getID());
                values.put("AREA_ID", area.getAREA_ID());
                values.put("AREA", area.getAREA());
                values.put("FATHER", area.getFATHER());

                db.insert("area", null, values);
            }
            db.setTransactionSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private String readInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}
