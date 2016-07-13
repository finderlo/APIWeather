package com.example.finderlo.apiweather.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.finderlo.apiweather.Model.County;
import com.example.finderlo.apiweather.Model.District;
import com.example.finderlo.apiweather.Model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by finderlo on 16-7-12.
 */
public class DatabaseHandler {

    /**
     *数据库名称
     */
    public static final String DB_NAME = "api_weather";

    private static DatabaseHandler databaseHandler;

    private SQLiteDatabase database;

    final int VERSION = 1;

    /**
     *构造方法私有化，将子类转换成单例模式
     */
    private DatabaseHandler(Context context){
        APIWeatherDatabaseOpenHelper databaseOpenHelper = new APIWeatherDatabaseOpenHelper(context,DB_NAME,null,VERSION);
        database = databaseOpenHelper.getWritableDatabase();
    }

    /**
     *获取实例DatabaseHandler
     */
    public synchronized static DatabaseHandler getInstance(Context context){
        if (databaseHandler == null){
            databaseHandler = new DatabaseHandler(context);
        }
        return databaseHandler;
    }

    /**
     *从数据库读取全国所有的省份信息
     */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        /*定义一个cusor对象来指针指向Province表*/
        Cursor cursor = database.query("Province",null,null,null,null,null,null,null);
        /*当指针不为空的时候来进行do-while循环，将cusor中的值来传入到list中*/
        if (cursor.moveToFirst()){
            do {
                Province province = new Province();
                /**setID方法中，传入id。使用了cursor.getInt()方法来获取一个Int值。
                 * getInt()方法传入表中的列名。使用了getColunmIndex()来获取列明。
                 * 这个方法直接传入列名就可以*/
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvince_cn(cursor.getString(cursor.getColumnIndex("province_cn")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return list;
    }

    /**
     *将Province实例化存储到数据库
     */
    public void saveProvince(Province province){
        if (province != null){
            ContentValues values = new ContentValues();     //使用了contentvalues对象来作为穿入数据库的对象
            values.put("province_cn",province.getProvince_cn());
            /**
             *insert方法，有三个传入参数：
             *第一个是穿入的表名，第二个是某些列没有传值的列的默认值，一般传入null即可，
             * 第三个是传入的值对象（contentvalues）
             */
            database.insert("Province",null,values);
        }
    }

    /**
     *将District实例存储到数据库中
     */
    public void saveDistrict(District district){
        if (district != null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("district_cn",district.getDistrict_cn());
            contentValues.put("province_id",district.getProvince_id());
            database.insert("District",null,contentValues);
        }
    }

    /**
     *读取某个省份下的所以城市信息
     * @param province_id
     */
    public List<District> loadDistrict(int province_id){
        List<District> list = new ArrayList<District>();
        /**查询表中的第三个第四个参数分别是要选择的条件查询，条件参数部分用？代替
         * 第四个参数用来写具体的参数，为string[]类型，其他类型需要转换*/
        Cursor cursor = database.query("District",null,"province_id = ?",new String[]{String.valueOf(province_id)},null,null,null);
        if (cursor.moveToFirst()){
            do {
                District district = new District();
                district.setId(cursor.getInt(cursor.getColumnIndex("id")));
                district.setDistrict_cn(cursor.getString(cursor.getColumnIndex("district_cn")));
                district.setProvince_id(province_id);
                list.add(district);
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return list;
    }

    /**
     *将County实例存储到数据库中
     */
    public void saveCounty(County county){
        if (county != null){
            ContentValues values = new ContentValues();
            values.put("name_cn",county.getName_cn());
            values.put("name_en",county.getName_en());
            values.put("area_id",county.getArea_id());
            values.put("district_id",county.getDistrict_id());
            values.put("province_id",county.getProvince_id());
            database.insert("County",null,values);
        }
    }

    /**
     *根据传入的City_id来返回所有县级城市，返回对象为list
     * @param
     */
    public List<County> loadCounty(int district_id){
        List<County> list = new ArrayList<County>();
        Cursor cursor = database.query("County",null,"district_id = ? ",new String[]{String.valueOf(district_id)},null,null,null);
        if (cursor.moveToFirst()){
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setName_cn(cursor.getString(cursor.getColumnIndex("name_cn")));
                county.setName_en(cursor.getString(cursor.getColumnIndex("name_en")));
                county.setArea_id(cursor.getInt(cursor.getColumnIndex("area_id")));
                county.setDistrict_id(district_id);
                county.setProvince_id(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(county);
            }while (cursor.moveToNext());
        }

        if (cursor != null){
            cursor.close();
        }
        return list;

    }
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
        Cursor cursor = database.query(table,columns,selection,selectionArgs,groupBy,having,orderBy);
        return cursor;
    }



    /**
     *class End by findelo
     */
}
