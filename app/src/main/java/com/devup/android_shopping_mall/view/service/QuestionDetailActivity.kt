package com.devup.android_shopping_mall.view.service

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.ActivityQuestionDetailBinding
import kotlinx.android.synthetic.main.activity_customer_service.*
import kotlinx.android.synthetic.main.activity_customer_service.btnCloseToolbar
import kotlinx.android.synthetic.main.activity_question_detail.*
import kotlinx.android.synthetic.main.fragment_board_details.view.*
import org.koin.android.ext.android.inject

class QuestionDetailActivity : AppCompatActivity() {
    private val TAG = "QuestionDetailActivity"

    private val viewModel: ServicesViewModel by inject()

    private var resource_id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_question_detail)

        if (intent.hasExtra("resource_id")) {
            resource_id = intent.getIntExtra("resource_id",0)
        }

        val binding : ActivityQuestionDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_question_detail )
        binding.apply {
            Log.d(TAG, "onCreate,resource_id: $resource_id")
            vm = viewModel
            lifecycleOwner = this@QuestionDetailActivity
            viewModel.loadServicesPostDetails(resource_id)
            rvCommentList.let {
                it.adapter = ServicesCommentAdapter()
            }
        }

        btnCloseToolbar.setOnClickListener {
            finish()
        }

        observeContent()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume, resource_id: $resource_id")
        viewModel.loadServicesComments(resource_id)
        //포커스 이동
    }

    private fun observeContent() {
        viewModel.isLoadClear.observe(this, Observer { load ->
            if (load) {
                //웹뷰에 링크넣기
                val tvContent : WebView? = tvContent
                Log.d(TAG, "observeContent,viewModel.postDetails.value?.content.toString(): ${viewModel.postDetails.value?.content.toString()}")
                viewModel.postDetails.value?.content.toString().let {
                    tvContent?.loadData(it,"text/html; charset=utf-8", "UTF-8")
                }
                if (tvContent != null) {
                    tvContent.webViewClient = HelloWebViewClient()
                }

            }
        }
        )
    }
    private class HelloWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            view.loadUrl(url!!)
            return true
        }
    }
}