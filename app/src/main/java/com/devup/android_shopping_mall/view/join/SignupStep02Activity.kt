package com.devup.android_shopping_mall.view.join

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.ActivitySignupStep02Binding

class  SignupStep02Activity : AppCompatActivity() {
    private val TAG = "SignupStep02Activity"
    private val viewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_signup_step02)
        val binding : ActivitySignupStep02Binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_step02)


    }
//-----onCreate종료


}