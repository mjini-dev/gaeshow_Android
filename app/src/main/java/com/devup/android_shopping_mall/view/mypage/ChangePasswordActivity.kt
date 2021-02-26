package com.devup.android_shopping_mall.view.mypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.user.model.ChangePasswordRequest
import com.devup.android_shopping_mall.databinding.ActivityChangePasswordBinding
import com.devup.android_shopping_mall.util.PASSWORD_REG_EXP
import kotlinx.android.synthetic.main.activity_change_password.*
import org.koin.android.ext.android.inject

class ChangePasswordActivity : AppCompatActivity() {
    private val TAG: String = "ChangePasswordActivity"

    private val viewModel: MoreFragmentViewModel by inject()

    val passwordRegexp = PASSWORD_REG_EXP.toRegex()

    var isPasswordRegexp = false
    var isPasswordCheck = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_change_password)
        val binding: ActivityChangePasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@ChangePasswordActivity
        }

        //-----새 비밀번호 정규식 확인
        etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etNewPassword.text.toString().matches(passwordRegexp)) {
                    tvPasswordInfo.setTextColor(
                        ContextCompat.getColor(
                            this@ChangePasswordActivity,
                            R.color.colorBlack
                        )
                    )
                    isPasswordRegexp = true
                } else if (!etNewPassword.text.toString().matches(passwordRegexp)) {
                    tvPasswordInfo.setTextColor(
                        ContextCompat.getColor(
                            this@ChangePasswordActivity,
                            R.color.colorRed
                        )
                    )
                    isPasswordRegexp = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        //----- 비밀번호 확인
        etPasswordCheck.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (etPasswordCheck.text.toString() == etNewPassword.text.toString()) {
                    tvPasswordCheckInfo.visibility = View.GONE
                    isPasswordCheck = true

                } else if (etPasswordCheck.text.toString() != etNewPassword.text.toString()) {
                    tvPasswordCheckInfo.setTextColor(
                        ContextCompat.getColor(
                            this@ChangePasswordActivity,
                            R.color.colorRed
                        )
                    )
                    tvPasswordCheckInfo.visibility = View.VISIBLE
                    isPasswordCheck = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //비밀번호 변경
        btnChange.setOnClickListener {
            val currentPassword: String = etCurrentPassword.text.toString()
            val newPassword: String = etNewPassword.text.toString()

            if (currentPassword.isBlank()) {
                alert(R.string.notice_password_str)
                view.scrollTo(0, 0)  //스크롤 이동
                etCurrentPassword.requestFocus() //포커스이동
                return@setOnClickListener
            }

            if (newPassword.isBlank()) {
                alert(R.string.notice_password_str)
                view.scrollTo(0, 0)  //스크롤 이동
                etNewPassword.requestFocus() //포커스이동
                return@setOnClickListener
            }

            if (!isPasswordRegexp) {
                alert(R.string.notice_passwordRegexp_str)
                view.scrollTo(0, 0)  //스크롤 이동
                etNewPassword.requestFocus() //포커스이동
                return@setOnClickListener
            }

            if (!isPasswordCheck) {
                alert(R.string.notice_passwordCheck_str)
                view.scrollTo(0, 0)  //스크롤 이동
                etPasswordCheck.requestFocus() //포커스이동
                return@setOnClickListener
            }


            val request = ChangePasswordRequest("email", currentPassword, newPassword)

            viewModel.changPassword(request)

        }

        observeAlert()


        //변경완료하면
        //finish()
    }

    fun alert(strId: Int) {
        var dialog = AlertDialog.Builder(this@ChangePasswordActivity)
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }

    private fun alertText(message: String) {
        var dialog = AlertDialog.Builder(this@ChangePasswordActivity)
        dialog.setMessage(message)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }

    private fun observeAlert() {
        viewModel.alertMessage.observe(this, Observer { message ->
            if (message.isNotBlank()) {
                when (message) {
                    "비밀번호 변경이 완료되었습니다" -> {
                        var dialog = AlertDialog.Builder(this@ChangePasswordActivity)
                        dialog.setMessage(message)
                        dialog.setPositiveButton(R.string.ok_str) { dialog, which ->
                            finish()
                        }
                        dialog.show()
                    }

                    else -> {
                        alertText(message)
                        viewModel._alertMessage.value = ""
                    }
                }

                //finish()
            }
        }
        )
    }
}