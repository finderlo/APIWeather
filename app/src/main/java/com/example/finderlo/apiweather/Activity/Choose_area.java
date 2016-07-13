package com.example.finderlo.apiweather.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.finderlo.apiweather.DB.DatabaseHandler;
import com.example.finderlo.apiweather.Model.County;
import com.example.finderlo.apiweather.Model.District;
import com.example.finderlo.apiweather.Model.Province;
import com.example.finderlo.apiweather.R;
import com.example.finderlo.apiweather.Util.APIWeather;
import com.example.finderlo.apiweather.Util.HttpRequestCallbackListener;
import com.example.finderlo.apiweather.Util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by finderlo on 16-7-12.
 */
public class Choose_area extends Activity {

    /**级别，可以用于判断用户选中的是哪一个级别*/
    public static final int LEVEL_PROVINCE = 0 ;
    public static final int LEVEL_DISTRICT = 1 ;
    public static final int LEVEL_COUNTY = 2 ;

    /**用户选中的省、市、县，和选中的级别*/
    private Province selectedProvince;
    private District selectedDistrict;
    private County selectedCounty;

    private int selectedLevel;

    /**界面需要的元素*/
    private ListView listView;
    private TextView titletext;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> adapter;
    private List<String> data_list = new ArrayList<String>();

    /**这是列表，通过数据库查询返回list定义*/
    private List<Province> provinceList;
    private List<District> districtList;
    private List<County> countyList;

    DatabaseHandler databaseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean flag = preferences.getBoolean("city_selected",false);
        if (flag){
            Intent intent = new Intent(Choose_area.this,WeatherInfoAty.class);
//            intent.putExtra("county_en",selectedCounty.getName_en());
            startActivity(intent);
            finish();
        }

        listView = (ListView) findViewById(R.id.list_view);
        titletext = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data_list);
        listView.setAdapter(adapter);

        databaseHandler = DatabaseHandler.getInstance(this);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectedLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
                    queryDistrict();
                }else if (selectedLevel == LEVEL_DISTRICT){
                    selectedDistrict = districtList.get(i);
                    queryCounty();
                }else if (selectedLevel == LEVEL_COUNTY){
                    selectedCounty = countyList.get(i);
                    Intent intent = new Intent(Choose_area.this,WeatherInfoAty.class);
                    intent.putExtra("county_en",selectedCounty.getName_en());
                    startActivity(intent);
                }
            }
        });
        queryProvince();
    }


    /**
     *查询全国所有的省，优先从数据库查询，否则从服务器查询
     */
    private void queryProvince() {
        provinceList = databaseHandler.loadProvinces();
        /**若list的大小不为空，则成功从数据库中读出了数据*/
        if (provinceList.size() > 0){
            data_list.clear();
            for (Province province:provinceList){
                data_list.add(province.getProvince_cn());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletext.setText("中国");
            selectedLevel = LEVEL_PROVINCE;
        }else {
            /**否者，列表为空，重新从服务器读取，然后保存到数据库中*/
            queryFromServer(null,"province");
        }
    }



    /**
     *查询全国所有的市，优先从数据库查询，否则从服务器查询
     */
    private void queryDistrict() {
        districtList = databaseHandler.loadDistrict(selectedProvince.getId());
        /**若list的大小不为空，则成功从数据库中读出了数据*/
        if (districtList.size() > 0){
            data_list.clear();
            for (District district:districtList){
                data_list.add(district.getDistrict_cn());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletext.setText(selectedProvince.getProvince_cn());
            selectedLevel = LEVEL_DISTRICT;

        }else {
            /**否者，列表为空，重新从服务器读取，然后保存到数据库中*/
            queryFromServer(selectedProvince,"district");
        }
    }

    /**
     *查询全国所有的县，优先从数据库查询，否则从服务器查询
     */
    private void queryCounty() {
        countyList = databaseHandler.loadCounty(selectedDistrict.getId());
        /**若list的大小不为空，则成功从数据库中读出了数据*/
        if (countyList.size() > 0){
            data_list.clear();
            for (County county:countyList){
                data_list.add(county.getName_cn());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletext.setText(selectedDistrict.getDistrict_cn());
            selectedLevel = LEVEL_COUNTY;

        }else {
            /**否者，列表为空，重新从服务器读取，然后保存到数据库中*/
            queryFromServer(selectedDistrict,"county");
        }
    }

    /**
     *从服务器查询数据，传入被选择的对象，如省份或者市区，然后将查询数据存储到数据库中，若存储成功，将重新加载数据到用户界面
     */
    private void queryFromServer(final Object selectedobject, final String type) {

        if ("province".equals(type)){
            String address = "http://www.weather.com.cn/data/list3/city.xml";
            boolean flag = false;
            final DatabaseHandler databaseHandler = DatabaseHandler.getInstance(Choose_area.this);
            Util.sendHttpRequestWithInBook(address, new HttpRequestCallbackListener() {
                @Override
                public void onFinish(String result) {
                    boolean isSucceed = false;
                    isSucceed = Util.handleProvincesRequest(databaseHandler,result);
                    if (isSucceed){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryProvince();
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("initProvince","发生了错误");
                }
            });
        }

        if ("district".equals(type)) {
            Province selectedProvince = (Province) selectedobject;
            APIWeather.sendHttpRuquestForDistrict_name(selectedProvince.getProvince_cn(), new HttpRequestCallbackListener() {
                @Override
                public void onFinish(String result) {
                    boolean isSuccess = Util.handleDistrictRequest(databaseHandler, result, (Province) selectedobject);
                    if (isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryDistrict();
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("123", "解析市级数据错误");
                }
            });
        }

        if ("county".equals(type)){
            District selectDistrict = (District) selectedobject;
            APIWeather.sendHttpRuquestForDistrict_name(selectDistrict.getDistrict_cn(), new HttpRequestCallbackListener() {
                @Override
                public void onFinish(String result) {
                    boolean isSuccess = Util.handleCountyRequest(databaseHandler,result,selectedDistrict);
                    if (isSuccess){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryCounty();
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("123","解析县级数据错误");
                }
            });

        }
    }



    /**
     *class End by finderlo
     */
}
