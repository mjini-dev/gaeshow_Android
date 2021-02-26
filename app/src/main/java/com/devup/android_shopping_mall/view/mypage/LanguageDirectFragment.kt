package com.devup.android_shopping_mall.view.mypage

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
import com.devup.android_shopping_mall.databinding.FragmentLanguageDirectBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_language_direct.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LanguageDirectFragment : Fragment() {
    private val TAG = "LanguageDirectFragment"

    private val viewModel: MoreFragmentViewModel by sharedViewModel()

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
        //return inflater.inflate(R.layout.fragment_language_direct, container, false)

        val binding: FragmentLanguageDirectBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_language_direct, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@LanguageDirectFragment

            view.rvDevelopmentLanguages.let {
                it.layoutManager = getFlexBoxLayoutManager()
                it.adapter = LanguageIdeAddAdapter("choice",
                    object : LanguageIdeAddAdapter.ItemClickListener {
                        override fun onItemClick(str: String) {
                            Log.d(TAG, "onItemClick,str: $str")
                            viewModel.deleteResult("lang", "choice", str)
                        }
                    }
                )
            }
        }

        view.btnRegister.setOnClickListener {
            //빈칸확인,
            //선택한 언어에 없는지 확인
            /*val str = view.etLanguageSelect.text.toString()
            viewModel.addChoice(str)*/

            if (view.etLanguageSelect.text.toString().isNotBlank()) {
                val str = view.etLanguageSelect.text.toString()

                if (viewModel.languages.value != null) {
                    if (str in viewModel.languages.value!!) {
                        alert(R.string.notice_already_registered_language_str)
                        view.etLanguageSelect.text.clear()

                    } else {
                        viewModel.addChoice("lang", str)
                        view.etLanguageSelect.text.clear()
                    }
                } else {
                    viewModel.addChoice("lang", str)
                    view.etLanguageSelect.text.clear()
                }
            } else if (view.etLanguageSelect.text.toString().trim() == "") {
                alert(R.string.notice_blank_str)
                return@setOnClickListener
            }

        }


        view.btnSearchInput.setOnClickListener {
            navController!!.navigate(R.id.action_languageDirectFragment_to_languageSearchFragment)
        }

        view.btnSelectComplete.setOnClickListener {
            navController!!.navigate(R.id.action_languageDirectFragment_to_mypageEditFragment)
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