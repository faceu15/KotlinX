package com.yiwu.kotlinx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.yiwu.kotlinx.databinding.ActivityMainBinding
import com.yiwu.kotlinx.network.RequestActivity
import com.yiwu.kotlinx.skin.SkinActivity
import skin.support.SkinCompatManager

class MainActivity : AppCompatActivity() {

    private var mViewBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main) as ActivityMainBinding

        init()
    }

    private fun init() {
        SkinCompatManager.getInstance()
            .loadSkin("", SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS)

        mViewBinding!!.btnBook.setOnClickListener {
            startActivity(Intent(this, BookManagerActivity::class.java))
        }
        mViewBinding!!.btnBinderPool.setOnClickListener {
            startActivity(Intent(this, BinderPoolActivity::class.java))
        }
        mViewBinding!!.btnSkinActivity.setOnClickListener {
            startActivity(Intent(this, SkinActivity::class.java))
        }

        mViewBinding!!.btnRequestActivity.setOnClickListener {
            startActivity(Intent(this, RequestActivity::class.java))
        }
    }

}
