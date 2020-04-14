package com.yiwu.kotlinx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.yiwu.kotlinx.databinding.ActivityBookManagerBinding;
import com.yiwu.kotlinx.service.BookManagerService;

import java.util.List;

public class BookManagerActivity extends AppCompatActivity {

    private static final String TAG = BookManagerActivity.class.getSimpleName();

    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private ActivityBookManagerBinding mViewBinding;

    private final Handler mHandle = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.i("DDDD", "BookManagerActivity[32]->handleMessage: " + msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private IBookManager mRemoteBookManager;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mRemoteBookManager = IBookManager.Stub.asInterface(service);
            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mRemoteBookManager = null;
            Log.e(TAG, "binder died.");
        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mRemoteBookManager != null) {
                mRemoteBookManager.asBinder().unlinkToDeath(this, 0);
                mRemoteBookManager = null;
                //重新绑定远程service
                Intent intent = new Intent(BookManagerActivity.this, BookManagerService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_book_manager);
        initView();
        initData();
    }

    private void initView() {
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mViewBinding.btnRegister.setOnClickListener(view -> initData());
        mViewBinding.btnStop.setOnClickListener(view -> unRegister());
    }

    private void initData() {
        if (mRemoteBookManager == null) {
            return;
        }
        List<Book> list = null;
        try {
            Book book1 = new Book("Andorid开发艺术", 102);
            mRemoteBookManager.addBook(book1);
            //UI线程，不要做长时间的耗时操作
            List<Book> bookList = mRemoteBookManager.getBookList();
            Log.i("DDDD", "BookManagerActivity[34]->: " + bookList.get(bookList.size() - 1).bookName);

            mRemoteBookManager.registerListener(mNewBookListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private IOnNewBookArrivedListener mNewBookListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            mHandle.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, book).sendToTarget();
        }
    };

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        unRegister();
        super.onDestroy();
    }

    private void unRegister() {
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                Log.i("DDDD", "BookManagerActivity[103]->unRegister: ");
                mRemoteBookManager.unRegisterListener(mNewBookListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


}
