package com.example.finderlo.apiweather.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.finderlo.apiweather.Model.Province;
import com.example.finderlo.apiweather.R;
import com.example.finderlo.apiweather.Util.APIWeather;
import com.example.finderlo.apiweather.Util.HttpRequestCallbackListener;
import com.example.finderlo.apiweather.Util.Util;

import java.util.Date;

/**
 * Created by finderlo on 16-7-12.
 */
public class WeatherInfoAty extends Activity implements View.OnClickListener {

    String selectedCounty_en;

    private TextView weather;
    private Button refreshWeather;
    private Button switchCity;
    private TextView title;

    final static String TAG = "WeatherInfoAty";
    final static int WEATHER_INFO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weatherinfo);
        weather = (TextView) findViewById(R.id.weather_info);
        title = (TextView) findViewById(R.id.title);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean flag = preferences.getBoolean("city_selected",false);
        Intent intent = getIntent();
        selectedCounty_en = intent.getStringExtra("county_en");
        if (selectedCounty_en!=null || !"".equals(selectedCounty_en)){
                queryWeatherInfo(selectedCounty_en);
        }else {queryWeatherInfo(null);}
//        if (flag){
//
//        }else {
////            Intent intent = getIntent();
////            selectedCounty_en = intent.getStringExtra("county_en");
////            weather = (TextView) findViewById(R.id.weather_info);
//            Log.e("123",selectedCounty_en);
//            queryWeatherInfo(selectedCounty_en);
//        }

        refreshWeather = (Button) findViewById(R.id.refreshWeather);
        switchCity = (Button) findViewById(R.id.switchCity);
        title = (TextView) findViewById(R.id.title_text);
        refreshWeather.setOnClickListener(this);
        switchCity.setOnClickListener(this);



    }

    private void queryWeatherInfo(String selectedCounty_en) {
        if (selectedCounty_en!= null &&!selectedCounty_en.equals("")){
            APIWeather.sendHttpRuquestForweatherinfo(selectedCounty_en, new HttpRequestCallbackListener() {
                @Override
                public void onFinish(String result) {
                    final String weather_info = Util.handleWeatherInfoResult(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weather.setText(weather_info);
                            Log.e("123",weather_info);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.e("123","ERROR");
                        }
                    });
                }
            });
        }else if (selectedCounty_en == null || selectedCounty_en.equals("")){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("city: "+preferences.getString("city","")+"\n");
            stringBuilder.append("date: "+preferences.getString("date","")+"\n");
            stringBuilder.append("time: "+preferences.getString("time","")+"\n");
            stringBuilder.append("weather: "+preferences.getString("weather","")+"\n");
            stringBuilder.append("temp: "+preferences.getString("temp","")+"度\n");
            stringBuilder.append("WD: "+preferences.getString("WD","")+"\n");
            stringBuilder.append("WS: "+preferences.getString("WS","")+"\n");
            stringBuilder.append("sunrise: "+preferences.getString("sunrise","")+"\n");
            stringBuilder.append("sunset: "+preferences.getString("sunset","")+"\n");
            stringBuilder.append("刷新时间："+preferences.getString("refresh_date",new Date().toString()));

            final String string = preferences.getString("city","city");

            final String weather_info = stringBuilder.toString();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weather.setText(weather_info);
                        title.setText(string);
                    Log.e("123",weather_info);
                }
            });

        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.switchCity:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("city_selected",false);
                editor.commit();
                startActivity(new Intent(WeatherInfoAty.this,Choose_area.class));
                break;

            case R.id.refreshWeather:
                String name_en = PreferenceManager.getDefaultSharedPreferences(this).getString("citycode","");
                if (!TextUtils.isEmpty(name_en)){
                    queryWeatherInfo(name_en);
                }

                break;

        }

    }
}
