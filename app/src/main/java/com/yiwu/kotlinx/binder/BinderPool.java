package com.yiwu.kotlinx.binder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.yiwu.kotlinx.IBinderPool;
import com.yiwu.kotlinx.service.BinderPoolService;

import java.util.concurrent.CountDownLatch;

/**
 * @Author:yiwu
 * @Date: Created in 11:14 2020/3/31
 * @Description:
 */
public class BinderPool {
    public static final int BINDER_NONE = -1;
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY_CENTER = 1;

    private Context mContext;
    private static volatile BinderPool sInstance;

    private IBinderPool mBinderPool;
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    private BinderPool(Context context) {
        this.mContext = context;
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPool.class) {
                if (sInstance == null) {
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    private synchronized void connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent service = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(service, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        Log.i("DDDD", "BinderPool[52]->connectBinderPoolService: ");
        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.i("DDDD", "BinderPool[63]->onServiceConnected: ");
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("DDDD", "BinderPool[75]->onServiceDisconnected: ");
        }
    };

    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        if (mBinderPool != null) {
            try {
                binder = mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.i("DDDD", "BinderPool[86]->binderDied: ");
            mBinderPool.asBinder().unlinkToDeath(this,0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };


    public static class BinderPoolImp extends IBinderPool.Stub {

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_SECURITY_CENTER: {
                    Log.i("DDDD", "BinderPoolImp[109]->queryBinder: Security Center");
                    binder = new SecurityCenterImp();
                    break;
                }
                case BINDER_COMPUTE: {
                    Log.i("DDDD", "BinderPoolImp[114]->queryBinder: Compute");
                    binder = new ComputeImp();
                    break;
                }
                default:
                    break;
            }
            return binder;
        }
    }
}
