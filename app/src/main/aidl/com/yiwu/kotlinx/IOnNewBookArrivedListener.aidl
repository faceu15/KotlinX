// IOnNewBookArrivedListener.aidl
package com.yiwu.kotlinx;

// Declare any non-default types here with import statements
import com.yiwu.kotlinx.Book;
interface IOnNewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onNewBookArrived(in Book book);
}
