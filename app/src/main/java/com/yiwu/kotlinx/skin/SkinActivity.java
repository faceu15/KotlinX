package com.yiwu.kotlinx.skin;

import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.SkinAppCompatDelegateImpl;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yiwu.kotlinx.Manifest;
import com.yiwu.kotlinx.R;
import com.yiwu.kotlinx.base.BaseActivity;
import com.yiwu.kotlinx.databinding.ActivitySkinBinding;

import skin.support.SkinCompatManager;
import skin.support.utils.SkinPreference;

public class SkinActivity extends BaseActivity<ActivitySkinBinding> {


    @Override
    protected int getContentViewId() {
        return R.layout.activity_skin;
    }

    @Override
    protected void init() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        mViewDataBinding.btnChangeSkin.setOnClickListener(v -> {
            Log.i("DDDD", "SkinActivity[35]->skinName=: "+SkinPreference.getInstance().getSkinName());
            if (SkinPreference.getInstance().getSkinName().equals("night_skin")) {
                SkinCompatManager.getInstance().restoreDefaultTheme();
            } else {
                Log.i("DDDD", "SkinActivity[40]->init: 应用内换肤");
                SkinCompatManager.getInstance().loadSkin("night_skin", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
            }
        });

        mViewDataBinding.btnAssets.setOnClickListener(v -> {
            if (SkinPreference.getInstance().getSkinName().equals("night")) {
                SkinCompatManager.getInstance().restoreDefaultTheme();
            } else {
                SkinCompatManager.getInstance().loadSkin("night.skin", new SkinCompatManager.SkinLoaderListener() {
                    @Override
                    public void onStart() {
                        Log.i("DDDD", "SkinActivity[42]->onStart: ");
                    }

                    @Override
                    public void onSuccess() {
                        Log.i("DDDD", "SkinActivity[47]->onSuccess: ");
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        Log.i("DDDD", "SkinActivity[52]->onFailed: " + errMsg.toString());
                    }
                }, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
            }
        });

        mViewDataBinding.btnSdcard.setOnClickListener(v -> {
            if (SkinPreference.getInstance().getSkinName().equals("night")) {
                SkinCompatManager.getInstance().restoreDefaultTheme();
            } else {
                SkinCompatManager.getInstance().loadSkin("night.skin", new SkinCompatManager.SkinLoaderListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess() {
                        Log.i("DDDD", "SkinActivity[77]->onSuccess: " + SkinPreference.getInstance().getSkinName());
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        Log.i("DDDD", "SkinActivity[52]->onFailed: " + errMsg.toString());
                    }
                }, CustomSDCardLoader.SKIN_LOADER_STRATEGY_SDCARD);
            }
        });


    }


    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return SkinAppCompatDelegateImpl.get(this, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            Log.i("DDDD", "SkinActivity[87]->onRequestPermissionsResult: 已获取权限");
        }
    }
}
