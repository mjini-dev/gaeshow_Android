package com.devup.android_shopping_mall.view.join

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentSignupIdeDirectBinding
import com.devup.android_shopping_mall.view.mypage.LanguageIdeAddAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_signup_ide_direct.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignupIdeDirectFragment : Fragment() {
    private val TAG = "SignupIdeDirectFragment"

    private val viewModel: SignupViewModel by sharedViewModel()

    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentSignupIdeDirectBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_ide_direct, container, false)
        val view = binding.root

        binding.apply {
            vm = viewModel
            lifecycleOwner = this@SignupIdeDirectFragment

            view.rvSelectedIde.let {
                it.layoutManager = getFlexBoxLayoutManager()
                it.adapter = LanguageIdeAddAdapter("choice",
                    object : LanguageIdeAddAdapter.ItemClickListener {
                        override fun onItemClick(str: String) {
                            Log.d(TAG, "onItemClick,str: $str")
                            viewModel.deleteResult("ide", "choice", str)
                        }
                    }
                )
            }
        }

        view.btnRegister.setOnClickListener {
            //빈칸확인,
            //선택한 언어에 없는지 확인

            if (view.etIdeSelect.text.toString().isNotBlank()) {
                val str = view.etIdeSelect.text.toString()

                if (viewModel.ides.value != null) {
                    if (str in viewModel.ides.value!!) {
                        alert(R.string.notice_already_registered_ide_str)
                        view.etIdeSelect.text.clear()

                    } else {
                        viewModel.addChoice("ide", str)
                        view.etIdeSelect.text.clear()
                    }
                } else {
                    viewModel.addChoice("ide", str)
                    view.etIdeSelect.text.clear()
                }

            } else if (view.etIdeSelect.text.toString().trim() == "") {
                alert(R.string.notice_blank_str)
                return@setOnClickListener
            }

        }


        view.btnSearchInput.setOnClickListener {
            navController!!.navigate(R.id.action_signupIdeDirectFragment_to_signupIdeSearchFragment)
        }

        view.btnSelectComplete.setOnClickListener {
            navController!!.navigate(R.id.action_signupIdeDirectFragment_to_signupStep02Fragment)
        }

        return view
    }

    fun alert(strId: Int) {
        var dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }


    private fun getFlexBoxLayoutManager(): FlexboxLayoutManager {
        return FlexboxLayoutManager(requireContext()).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }
    }

}