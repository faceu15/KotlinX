package com.yiwu.kotlinx.base;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2018/10/24
 *     desc   :
 * </pre>
 */
public abstract class BaseActivity<T extends ViewDataBinding> extends FragmentActivity {

    protected Context mContext = null;
    protected T mViewDataBinding = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState = null;
        }
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mViewDataBinding = DataBindingUtil.setContentView(this, getContentViewId());
        mViewDataBinding.setLifecycleOwner(this);
        mContext = this;
        init();
    }

    public void post(Runnable runnable) {
        mViewDataBinding.getRoot().post(runnable);
    }

    /**
     * get content view id
     *
     * @return
     */
    @LayoutRes
    protected abstract int getContentViewId();

    /**
     * init data and view
     */
    protected abstract void init();

}
