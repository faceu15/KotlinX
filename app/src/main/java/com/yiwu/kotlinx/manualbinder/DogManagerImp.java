package com.yiwu.kotlinx.manualbinder;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yiwu.kotlinx.Book;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author:yiwu
 * @Date: Created in 16:08 2020/3/31
 * @Description:
 */
public class DogManagerImp extends Binder implements IDogManage {

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    public DogManagerImp() {
        this.attachInterface(this, DESCRIPTOR);
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTOR);
                return true;
            case TRANSACTION_getDogList:
                data.enforceInterface(DESCRIPTOR);
                List<Book> result = this.getDogList();
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;
            case TRANSACTION_addDog:
                data.enforceInterface(DESCRIPTOR);
                Book book;
                if (data.readInt() != 0) {
                    book = Book.CREATOR.createFromParcel(reply);
                } else {
                    book = null;
                }
                this.addDog(book);
                reply.writeNoException();
                return true;
        }
        return super.onTransact(code, data, reply, flags);
    }

    @Override
    public List<Book> getDogList() throws RemoteException {
        //TODO 获取数据
        return mBookList;
    }

    @Override
    public void addDog(Book book) throws RemoteException {
        //TODO
        mBookList.add(book);
    }

    public static IDogManage asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
        if (iin != null && iin instanceof IDogManage) {
            return (IDogManage) iin;
        }
        return new DogManagerImp.Proxy(obj);
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    private static class Proxy implements IDogManage {
        private IBinder mRemote;

        public Proxy(IBinder binder) {
            this.mRemote = binder;
        }

        @Override
        public List<Book> getDogList() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            List<Book> result;
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                mRemote.transact(TRANSACTION_getDogList, data, reply, 0);
                reply.readException();
                result = reply.createTypedArrayList(Book.CREATOR);

            } finally {
                data.recycle();
                reply.recycle();
            }
            return result;
        }

        @Override
        public void addDog(Book book) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                data.writeInterfaceToken(DESCRIPTOR);
                if (book != null) {
                    data.writeInt(1);
                    book.writeToParcel(data, 0);
                } else {
                    data.writeInt(0);
                }
                mRemote.transact(TRANSACTION_addDog,data,reply,0);
                reply.readException();

            } finally {
                data.recycle();
                reply.recycle();
            }

        }

        public String getInterfaceDescriptor() {
            return DESCRIPTOR;
        }

        @Override
        public IBinder asBinder() {
            return mRemote;
        }
    }

}
