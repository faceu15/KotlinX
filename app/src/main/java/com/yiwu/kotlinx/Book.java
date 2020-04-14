package com.yiwu.kotlinx;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * @Author:yiwu
 * @Date: Created in 8:49 2020/3/30
 * @Description:
 */
public class Book implements Parcelable {

    String bookName;
    int bookPrice;

    public Book(String bookName, int bookPrice) {
        this.bookName = bookName;
        this.bookPrice = bookPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bookName);
        dest.writeInt(this.bookPrice);
    }

    protected Book(Parcel in) {
        this.bookName = in.readString();
        this.bookPrice = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return bookName + ":" + bookPrice;
    }
}
