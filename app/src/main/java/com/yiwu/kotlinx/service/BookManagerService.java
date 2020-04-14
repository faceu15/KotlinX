package com.yiwu.kotlinx.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.yiwu.kotlinx.Book;
import com.yiwu.kotlinx.IBookManager;
import com.yiwu.kotlinx.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author:yiwu
 * @Date: Created in 11:31 2020/3/30
 * @Description:
 */
public class BookManagerService extends Service {
    private static final String TAG = BookManagerService.class.getSimpleName();

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);

    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            //方法运行在服务端的Binder线程池中
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            //方法运行在服务端的Binder线程池中
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            Log.i("DDDD", "BookManagerService[48]->registerListener: ");
            mListenerList.register(listener);
        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            Log.i("DDDD", "BookManagerService[56]->unRegisterListener: ");
            mListenerList.unregister(listener);
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book("Android", 75));
        new Thread(new ServiceWorker()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //TODO 加入权限验证
        int check = checkCallingOrSelfPermission("com.yiwu.kotlin.permission.ACCESS_BOOK_SERVICE");
        if(check == PackageManager.PERMISSION_DENIED){
            return null;
        }
        return mBinder;
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            if (listener != null) {
                listener.onNewBookArrived(book);
            }
        }
        mListenerList.finishBroadcast();
    }


    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Book book = new Book("第一行代码", 75);
                try {
                    onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
