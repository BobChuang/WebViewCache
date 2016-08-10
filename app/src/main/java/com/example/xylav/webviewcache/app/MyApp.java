package com.example.xylav.webviewcache.app;

import android.app.Application;

import com.example.xylav.webviewcache.util.ConfigUtil;

/**
 * Created by zhuguohui on 2016/6/8.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ConfigUtil.init(this,getCacheDir());
    }
}
