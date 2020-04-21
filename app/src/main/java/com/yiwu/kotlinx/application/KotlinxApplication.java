package com.yiwu.kotlinx.application;

import android.app.Application;

import com.yiwu.kotlinx.skin.CustomSDCardLoader;

import skin.support.SkinCompatManager;
import skin.support.app.SkinAppCompatViewInflater;

/**
 * @Author:yiwu
 * @Date: Created in 9:13 2020/4/17
 * @Description:
 */
public class KotlinxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //皮肤框架初始化
        SkinCompatManager.withoutActivity(this)
                .addInflater(new SkinAppCompatViewInflater())
                .addStrategy(new CustomSDCardLoader())
                .setSkinStatusBarColorEnable(true)
                .setSkinWindowBackgroundEnable(true)
                .loadSkin();
    }
}
