// IBookManager.aidl
package com.yiwu.kotlinx;

// Declare any non-default types here with import statements
import com.yiwu.kotlinx.Book;
import com.yiwu.kotlinx.IOnNewBookArrivedListener;
interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

     List<Book> getBookList();

     void addBook(in Book book);

     void registerListener(IOnNewBookArrivedListener listener);

     void unRegisterListener(IOnNewBookArrivedListener listener);
}
