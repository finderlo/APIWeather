package com.example.finderlo.apiweather.Util;

import android.app.Application;
import android.content.Context;

/**
 * Created by finderlo on 16-7-13.
 * 全局获取context的类
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {

        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}
