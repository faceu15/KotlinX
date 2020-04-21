package com.yiwu.kotlinx.skin;

import android.content.Context;
import android.util.Log;

import java.io.File;

import skin.support.load.SkinSDCardLoader;
import skin.support.utils.SkinFileUtils;

/**
 * @Author:yiwu
 * @Date: Created in 14:47 2020/4/20
 * @Description:
 */
public class CustomSDCardLoader extends SkinSDCardLoader {

    public static final int SKIN_LOADER_STRATEGY_SDCARD = 1;

    @Override
    protected String getSkinPath(Context context, String skinName) {
        String absolutePath = new File(SkinFileUtils.getSkinDir(context), skinName).getAbsolutePath();
        Log.i("DDDD", "CustomSDCardLoader[22]->getSkinPath: "+absolutePath);
        return absolutePath;
    }

    @Override
    public int getType() {
        return SKIN_LOADER_STRATEGY_SDCARD;
    }
}
