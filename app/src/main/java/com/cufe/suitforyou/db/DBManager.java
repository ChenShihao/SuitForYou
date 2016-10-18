package com.cufe.suitforyou.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Victor on 2016-09-12.
 */
public class DBManager {

    private static DBManager mInstance;

    private SQLiteDatabase mDB;

    private DBManager(Context context) {
        MySQLiteHelper helper = new MySQLiteHelper(context);
        mDB = helper.getWritableDatabase();
    }

    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null)
                    mInstance = new DBManager(context);
            }
        }
        return mInstance;
    }

    public SQLiteDatabase getmDB() {
        return mDB;
    }
}
