package com.yiwu.kotlinx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.yiwu.kotlinx.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mViewBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main) as ActivityMainBinding

        init()
    }

    init {

    }


    private fun init() {
        mViewBinding!!.btnBook.setOnClickListener {
            startActivity(Intent(this, BookManagerActivity::class.java))
        }
        mViewBinding!!.btnBinderPool.setOnClickListener {
            startActivity(Intent(this, BinderPoolActivity::class.java))
        }
    }

}
