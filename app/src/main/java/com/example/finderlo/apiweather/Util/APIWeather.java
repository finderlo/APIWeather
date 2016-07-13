package com.example.finderlo.apiweather.Util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by finderlo on 16-7-12.
 * 这个类使用了百度API，并进行了封装
 */
public class APIWeather {
    /**
     *这个方法限制传入的参数为一个汉字字符的省份名和一个网络请求回拨监听者
     * 当网络请求完成时，将获得的信息转换成string发送给监听者的onFinsh()方法
     * 当网络请求错误时发送到监听者的onError()方法
     */
    public static final void sendHttpRuquestForDistrict_name(final String privince_cn, final HttpRequestCallbackListener listener){


        new Thread(new Runnable() {
            @Override
            public void run() {
                String httpUrl = "http://apis.baidu.com/apistore/weatherservice/citylist";
                String privince_encode = toURLEncoded(privince_cn);
                String httpArg = "cityname="+privince_encode;
                String result = null;
                try {
                    result = request(httpUrl,httpArg);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e);
                }

                if (result != null ){
                    Log.e("1",result);
                    listener.onFinish(result);
                }

            }
        }).start();
    }

    public static final void sendHttpRuquestForweatherinfo(final String county_en, final HttpRequestCallbackListener listener){


        new Thread(new Runnable() {
            @Override
            public void run() {
                String httpUrl = "http://apis.baidu.com/apistore/weatherservice/weather";
                String httpArg = "citypinyin="+county_en;

                String result = null;
                try {
                    result = request(httpUrl,httpArg);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e);
                }

                if (result != null ){
                    Log.e("1",result);
                    listener.onFinish(result);
                }

            }
        }).start();
    }

    /**
     * @param
     *            :请求接口
     * @param httpArg
     *            :参数
     * @return 返回结果
     */
    private static String request( String httpUrl,final String httpArg) throws Exception{

        final String apikey = "718b0d3ff19002897a32926cbc5bbbc4";
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;


            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey",  apikey);
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();

        return result;
    }

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
     *class End by findelo
     */
}
