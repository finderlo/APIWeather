package com.example.finderlo.apiweather.Util;

/**
 * Created by finderlo on 16-7-12.
 */
public interface HttpRequestCallbackListener {
    void onFinish(String result);
    void onError(Exception e);
}
