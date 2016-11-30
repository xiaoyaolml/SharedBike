package com.leo.sharedbike;

import android.app.Application;

import cn.bmob.v3.Bmob;

public class App extends Application {
    private static final String APPID = "6297eb1b04065a2362eb40a5718cdfb7";

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, APPID);
    }
}
