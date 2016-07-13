package com.example.finderlo.apiweather.Model;

/**
 * Created by finderlo on 16-7-12.
 */
public class County {

    int id;

    //地区中文名
    String name_cn;
    //地区拼音
    String name_en;
    //地区代号
    int area_id;

    //地区所属市级的市级在数据表中id
    int district_id;
    //地区所属市级的省级在数据表中id
    int province_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_cn() {
        return name_cn;
    }

    public void setName_cn(String name_cn) {
        this.name_cn = name_cn;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public int getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(int district_id) {
        this.district_id = district_id;
    }

    public int getProvince_id() {
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
    }
}
