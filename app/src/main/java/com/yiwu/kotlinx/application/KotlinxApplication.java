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

    private static KotlinxApplication mInstance = null;

    public static KotlinxApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //皮肤框架初始化
        SkinCompatManager.withoutActivity(this)
                .addInflater(new SkinAppCompatViewInflater())
                .addStrategy(new CustomSDCardLoader())
                .loadSkin();
    }
}
