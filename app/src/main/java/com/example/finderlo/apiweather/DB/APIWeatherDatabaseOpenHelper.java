package com.example.finderlo.apiweather.DB;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by finderlo on 16-7-12.
 */
public class APIWeatherDatabaseOpenHelper extends SQLiteOpenHelper{

    /**
     * 建表语句
     * */
    private static final String CREATE_PROVINCE = "create table Province (" +
            "id integer primary key autoincrement," +
            "province_cn text)";
    private static final String CREATE_DISTRICT = "create table District (" +
            "id integer primary key autoincrement," +
            "district_cn text," +
            "province_id integer)";
    private static final String CREATE_COUNTY = "create table County (" +
            "id integer primary key autoincrement," +
            "name_cn text," +
            "name_en text," +
            "area_id integer," +
            "district_id integer," +
            "province_id integer)";


    public APIWeatherDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_DISTRICT);
        sqLiteDatabase.execSQL(CREATE_COUNTY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
