package com.devup.android_shopping_mall.view.mypage

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.ActivityMypageEditBinding
import com.devup.android_shopping_mall.view.mypage.MoreFragmentViewModel
import org.koin.android.ext.android.inject

class MypageEditActivity : AppCompatActivity() {
    private val TAG = "MypageEditActivity"

    private val viewModel: MoreFragmentViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_mypage_edit)
        val binding : ActivityMypageEditBinding = DataBindingUtil.setContentView(this, R.layout.activity_mypage_edit)

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

}