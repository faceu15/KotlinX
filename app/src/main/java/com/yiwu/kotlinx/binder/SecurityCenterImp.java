package com.yiwu.kotlinx.binder;

import android.os.RemoteException;

import com.yiwu.kotlinx.ISecurityCenter;

/**
 * @Author:yiwu
 * @Date: Created in 10:19 2020/3/31
 * @Description:
 */
public class SecurityCenterImp  extends ISecurityCenter.Stub{

    private static final char SECRET_CODE = '@';

    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^= SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
