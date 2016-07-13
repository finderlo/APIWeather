package com.example.finderlo.apiweather.Util;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.finderlo.apiweather.DB.DatabaseHandler;
import com.example.finderlo.apiweather.Model.County;
import com.example.finderlo.apiweather.Model.District;
import com.example.finderlo.apiweather.Model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by finderlo on 16-7-12.
 *
 */
public class Util {

    private static final String TAG = "Util";

    /**
     *这个类可以将汉字字符转换为encode编码
     *传入参数为string
     */
    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.e(TAG,"toURLEncoded error:转换代码为空值");
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.e(TAG,"toURLEncoded error:"+paramString);
        }

        return "";
    }
    /**
     *这个类可以将encode编码解码为汉字
     */
    public static String toURLDecoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.e(TAG,"toURLDecoded error:转换代码为空值");
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLDecoder.decode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.e(TAG,"toURLDecoded error:"+paramString);
        }

        return "";
    }


    /**
     *传入的数据是省份级的数据，这个方法的作用是解析省级的数据，然后将其保存到数据库中
     * 没有使用百度的API，使用了另一种方法获取全国的省份
     */
    public static boolean handleProvincesRequest(DatabaseHandler myWeatherDB, String request){
        if (request!=null){
            String[] provinces = request.split(",");
            if (provinces != null && provinces.length >0){
                for (String p:provinces){
                    /**解析从服务器返回的数据，使用了正则表达式*/
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvince_cn(array[1]);
                    /**将解析的数据封装成实体类，使用数据库操作的封装类进行保存*/
                    myWeatherDB.saveProvince(province);
                }
            }
            return true;
        }
        return false;
    }

    /**
     *传入的数据是市级的数据，这个方法的作用是解析市级的数据，然后将其保存到数据库中
     */
    public static boolean handleDistrictRequest(DatabaseHandler databaseHandler, String result,Province selectedProvince) {
        if (result!=null){
            JSONArray jsonObjs = null;
            try {
                jsonObjs = new JSONObject(result).getJSONArray("retData");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < jsonObjs.length() ; i++){


                JSONObject jsonObj = null;
                try {
                    jsonObj = (JSONObject)jsonObjs.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String temp = null;
                try {
                    temp = jsonObj.getString("district_cn");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String[] temps = new String[]{temp};

                District district = new District();
                district.setProvince_id(selectedProvince.getId());
                district.setDistrict_cn(temp);

                Cursor cursor = databaseHandler.query("District",null,"district_cn = ?",temps,null,null,null);
                if (cursor.moveToNext()){
                    cursor.close();
                    continue;
                }else {
                    databaseHandler.saveDistrict(district);
                }


            }
            return true;
        }
        return false;
    }

    /**
     *传入的数据是县级的数据，这个方法的作用是解析县级的数据，然后将其保存到数据库中
     */
    public static boolean handleCountyRequest(DatabaseHandler databaseHandler, String result,District selectedDistrict) {
        if (result!=null){
            JSONArray jsonObjs = null;
            try {
                jsonObjs = new JSONObject(result).getJSONArray("retData");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < jsonObjs.length() ; i++){


                JSONObject jsonObj = null;
                try {
                    jsonObj = (JSONObject)jsonObjs.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String temp = null;
                try {
                    temp = jsonObj.getString("district_cn");
                    County county = new County();
                    county.setProvince_id(selectedDistrict.getProvince_id());
                    county.setDistrict_id(selectedDistrict.getId());
                    county.setName_cn(jsonObj.getString("name_cn"));
                    county.setName_en(jsonObj.getString("name_en"));
                    county.setArea_id(jsonObj.getInt("area_id"));
                    databaseHandler.saveCounty(county);

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
            return true;
        }
        return false;
    }


    /**
     * {
     "errNum": 0,
     "errMsg": "success",
     "retData": {
        "city": "北京",
        "pinyin": "beijing",
        "citycode": "101010100",
        "date": "16-07-13",
        "time": "08:00",
        "postCode": "100000",
        "longitude": 116.391,
        "latitude": 39.904,
        "altitude": "33",
        "weather": "晴",
        "temp": "35",
        "l_tmp": "23",
        "h_tmp": "35",
        "WD": "无持续风向",
        "WS": "微风(<10m/h)",
        "sunrise": "04:57",
        "sunset": "19:43"
        }
     }
     */
    private static void saveWeatherInfo(JSONObject jsonObject){
        /**
         *将获得的天气信息存储到sharePreference文件中
         */
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
        editor.putBoolean("city_selected",true);
        /**将当前的时间放入文件中*/
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        editor.putString("refresh_date",str);

        try {
            editor.putString("pinyin",jsonObject.getString("pinyin"));
            editor.putString("city",jsonObject.getString("city"));
            editor.putString("citycode",jsonObject.getString("citycode"));
            editor.putString("date",jsonObject.getString("date"));
            editor.putString("time",jsonObject.getString("time"));
            editor.putString("postCode",jsonObject.getString("postCode"));
            editor.putString("longitude",jsonObject.getString("longitude"));
            editor.putString("latitude",jsonObject.getString("latitude"));
            editor.putString("altitude",jsonObject.getString("altitude"));
            editor.putString("weather",jsonObject.getString("weather"));
            editor.putString("temp",jsonObject.getString("temp"));
            editor.putString("l_tmp",jsonObject.getString("l_tmp"));
            editor.putString("WD",jsonObject.getString("WD"));
            editor.putString("WS",jsonObject.getString("WS"));
            editor.putString("sunrise",jsonObject.getString("sunrise"));
            editor.putString("sunset",jsonObject.getString("sunset"));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG+".saveWeatherInfo","出现异常");
        }

    }
    /**
     *传入的数据是县级的天气数据，这个方法的作用是解析县级的数据，然后将其保存到，文件是默认的包名为名陈的shareProference文件中
     */
    public static String handleWeatherInfoResult(String result) {
        if (result!=null){
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(result).getJSONObject("retData");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonObj==null){
                return "JSON是空";
            }
            StringBuilder stringBuilder = new StringBuilder();
            try {
                stringBuilder.append("city: "+jsonObj.getString("city")+"\n");
                stringBuilder.append("date: "+jsonObj.getString("date")+"\n");
                stringBuilder.append("time: "+jsonObj.getString("time")+"\n");
                stringBuilder.append("weather: "+jsonObj.getString("weather")+"\n");
                stringBuilder.append("temp: "+jsonObj.getString("temp")+"度\n");
                stringBuilder.append("WD: "+jsonObj.getString("WD")+"\n");
                stringBuilder.append("WS: "+jsonObj.getString("WS")+"\n");
                stringBuilder.append("sunrise: "+jsonObj.getString("sunrise")+"\n");
                stringBuilder.append("sunset: "+jsonObj.getString("sunset")+"\n");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            saveWeatherInfo(jsonObj);

            return stringBuilder.toString();


        }
        return "没有读取成功";

    }


    /**
     *发送获取数据网络请求封装代码，传入参数是HTTP协议地址，和返回值监听器接口，接口实现错误返回和正确返回的逻辑
     *正确返回为服务器返回的数据，string类型
     */
    public static void sendHttpRequestWithInBook(final String address,final HttpRequestCallbackListener listener){

        final String TAG = "HttpUtil.sendHttpRequest";
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    Log.d(TAG,"open connection");

                    connection.setRequestMethod("GET");
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    Log.d(TAG,"init bufferedreader");

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine())!=null){
                        stringBuilder.append(line);
                    }

                    if (listener != null){
                        listener.onFinish(stringBuilder.toString());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e(TAG,"解释URL失败");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"io异常");
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }


}
