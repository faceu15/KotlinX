package com.yiwu.kotlinx.skin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author:yiwu
 * @Date: Created in 11:17 2020/4/16
 * @Description:
 */
public class SkinInflaterFactory implements LayoutInflater.Factory {

    @Nullable
    @Override
    public View onCreateView(@NonNull String s, @NonNull Context context, @NonNull AttributeSet attributeSet) {
//        boolean isSkinEnable = attributeSet.getAttributeBooleanValue()

        return null;
    }
}
