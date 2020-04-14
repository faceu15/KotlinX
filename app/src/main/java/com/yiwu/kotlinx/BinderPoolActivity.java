package com.yiwu.kotlinx;

import android.os.IBinder;
import android.util.Log;

import com.yiwu.kotlinx.base.BaseActivity;
import com.yiwu.kotlinx.binder.BinderPool;
import com.yiwu.kotlinx.binder.ComputeImp;
import com.yiwu.kotlinx.binder.SecurityCenterImp;
import com.yiwu.kotlinx.databinding.ActivityBinderPoolBinding;

import static com.yiwu.kotlinx.binder.BinderPool.BINDER_COMPUTE;
import static com.yiwu.kotlinx.binder.BinderPool.BINDER_SECURITY_CENTER;

public class BinderPoolActivity extends BaseActivity<ActivityBinderPoolBinding> {

    private BinderPool mBinderPool;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_binder_pool;
    }

    @Override
    protected void init() {

        mViewDataBinding.btnStart.setOnClickListener(view -> new Thread(this::doWork).start());
    }

    private void doWork() {
        mBinderPool = BinderPool.getInstance(this);

        IBinder binder = mBinderPool.queryBinder(BINDER_SECURITY_CENTER);

        ISecurityCenter iSecurityCenter = SecurityCenterImp.asInterface(binder);

        String msg = "helloworld-安卓";
        Log.i("DDDD", "BinderPoolActivity[28]->init: " + msg);
        try {
            String password = iSecurityCenter.encrypt(msg);
            Log.i("DDDD", "encrypt: " + password);
            Log.i("DDDD", "decrypt:" + iSecurityCenter.decrypt(password));
        } catch (Exception e) {
            e.printStackTrace();
        }

        IBinder computeBinder = mBinderPool
                .queryBinder(BinderPool.BINDER_COMPUTE);
        ICompute mCompute = ComputeImp.asInterface(computeBinder);
        try {
            Log.i("DDDD", "3+5=" + mCompute.add(3, 5));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
