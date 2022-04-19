package com.yiwu.kotlinx

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *@ Author:yiwu
 *@ Date: Created in 18:13 2020/6/15
 *@ Description:
 */
class MainViewModel : ViewModel() {

    val mValue = MutableLiveData<String>()

}