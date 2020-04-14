package com.yiwu.kotlinx.manualbinder;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import com.yiwu.kotlinx.Book;

import java.util.List;

/**
 * @Author:yiwu
 * @Date: Created in 15:42 2020/3/31
 * @Description:
 */
public interface IDogManage extends IInterface {
    static final String DESCRIPTOR = "com.yiwu.kotlinx.manualbinder.IDogManage";

    static final int TRANSACTION_getDogList = IBinder.FIRST_CALL_TRANSACTION + 1;
    static final int TRANSACTION_addDog = IBinder.FIRST_CALL_TRANSACTION + 2;

    public List<Book> getDogList() throws RemoteException;
    public void addDog(Book book) throws RemoteException;
}
