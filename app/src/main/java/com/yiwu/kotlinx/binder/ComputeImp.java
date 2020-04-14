package com.yiwu.kotlinx.binder;

import android.os.RemoteException;

import com.yiwu.kotlinx.ICompute;

/**
 * @Author:yiwu
 * @Date: Created in 10:32 2020/3/31
 * @Description:
 */
public class ComputeImp extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
