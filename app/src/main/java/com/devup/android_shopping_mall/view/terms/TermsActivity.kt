package com.devup.android_shopping_mall.view.terms

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.ActivityTermsBinding
import com.devup.android_shopping_mall.view.join.SignupStep01Activity
import com.devup.android_shopping_mall.view.mypage.MypageEditActivity
import kotlinx.android.synthetic.main.activity_terms.*
import org.koin.android.ext.android.inject

class TermsActivity : AppCompatActivity() {
    private val TAG: String = "TermsActivity"

    private val viewModel: TermsViewModel by inject()

    var type:String = " "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_terms)

        val binding: ActivityTermsBinding = DataBindingUtil.setContentView(this,R.layout.activity_terms)
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@TermsActivity
        }

        if (intent.hasExtra("type")) {
            type = intent.extras?.get("type") as String
        }

        btnSignup.setOnClickListener {

            viewModel.completeTermsAgree(type)
            /*when(type) {
                "kakao" -> {
                    //저장된 카카오유저정보(카카오유저정보 가져오기)로 회원가입 진행
                    //내정보 수정으로 이동
                    Log.d(TAG, "onCreate: 카카오다")

                }

                else ->{
                    val intent = Intent(this, SignupStep01Activity::class.java)
                    startActivity(intent)
                    finish()
                }
            }*/

        }


        observeStartStep01Activity()
        observeMypageEditActivity()
        observeDialog()
        observeLoadTerms()
    }

    fun alert(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_str)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun observeDialog() {
        viewModel.errorDialogMessage.observe(
            this,
            Observer { message ->
                if (message.isNotBlank()) {
                    alert(message)
                }
            }
        )
    }

    private fun observeLoadTerms() {
        viewModel.isLoadClear.observe(this,Observer { load ->
                if (load) {
                    //웹뷰에 링크넣기
                    val serviceWebView : WebView = findViewById(R.id.tvTermsService)
                    viewModel.terms.value?.service?.let { serviceWebView.loadUrl(it) }

                    val privacyWebView : WebView = findViewById(R.id.tvTermsPrivacy)
                    viewModel.terms.value?.privacy?.let { privacyWebView.loadUrl(it) }

                    val additionalWebView : WebView = findViewById(R.id.tvTermsAdditional)
                    viewModel.terms.value?.additional?.let { additionalWebView.loadUrl(it) }

                    val advertisingWebView : WebView = findViewById(R.id.tvTermsAdvertising)
                    viewModel.terms.value?.advertising?.let { advertisingWebView.loadUrl(it) }

                }
            }
        )
    }

    private fun observeStartStep01Activity() {
        viewModel.openStep01Activity.observe(
            this,
            Observer { startActivity ->
                if (startActivity) {
                    val intent = Intent(this, SignupStep01Activity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        )
    }
    private fun observeMypageEditActivity() {
        viewModel.openMypageEditActivity.observe(
            this,
            Observer { startActivity ->
                if (startActivity) {
                    val intent = Intent(this, MypageEditActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        )
    }
}