package com.example.finderlo.apiweather.Model;

/**
 * Created by finderlo on 16-7-12.
 */
public class District {
    String district_cn;
    int id;
    int province_id;

    public String getDistrict_cn() {
        return district_cn;
    }

    public void setDistrict_cn(String district_cn) {
        this.district_cn = district_cn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvince_id() {
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
    }
}
