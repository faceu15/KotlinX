package com.yiwu.kotlinx.skin;

import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

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

public class SkinActivity extends BaseActivity<ActivitySkinBinding> implements View.OnClickListener {


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
        mViewDataBinding.btnChangeSkin.setOnClickListener(this);
        mViewDataBinding.btnAssets.setOnClickListener(this);
        mViewDataBinding.btnSdcard.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_skin:
                Log.i("DDDD", "SkinActivity[35]->skinName=: " + SkinPreference.getInstance().getSkinName());
                if (SkinPreference.getInstance().getSkinName().equals("night_skin")) {
                    SkinCompatManager.getInstance().restoreDefaultTheme();
                } else {
                    SkinCompatManager.getInstance().loadSkin("night_skin", SkinCompatManager.SKIN_LOADER_STRATEGY_PREFIX_BUILD_IN);
                }
                break;
            case R.id.btn_assets:
                Log.i("DDDD", "SkinActivity[46]->Assets skinName: " + SkinPreference.getInstance().getSkinName());
                if (SkinPreference.getInstance().getSkinName().equals("night.skin")) {
                    SkinCompatManager.getInstance().restoreDefaultTheme();
                } else {
                    SkinCompatManager.getInstance().loadSkin("night.skin", null,
                            SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
                }
                break;
            case R.id.btn_sdcard:
                if (SkinPreference.getInstance().getSkinName().equals("night.skin")) {
                    SkinCompatManager.getInstance().restoreDefaultTheme();
                } else {
                    SkinCompatManager.getInstance().loadSkin("night.skin", null,
                            CustomSDCardLoader.SKIN_LOADER_STRATEGY_SDCARD);
                }
                break;
        }
    }
}
