package com.devup.android_shopping_mall.view.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentLanguageSearchBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_language_search.*
import kotlinx.android.synthetic.main.fragment_language_search.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LanguageSearchFragment : Fragment() {
    private val TAG = "LanguageSearchFragment"

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
        //return inflater.inflate(R.layout.fragment_language_search, container, false)
        val binding: FragmentLanguageSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_language_search, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@LanguageSearchFragment
            viewModel.getJobTypeList()
            viewModel.check++
            viewModel._languagesSearchResult.value = null

            view.rvSearchResult.let {
                it.layoutManager = getFlexBoxLayoutManager()
                it.adapter = LanguageIdeAddAdapter("searchResult",
                    object : LanguageIdeAddAdapter.ItemClickListener {
                        override fun onItemClick(str: String) {
                            Log.d(TAG, "onItemClick,str: $str")
                            //languagesSearchResult 에서 제거, languages에 추가
                            viewModel.deleteResult("lang", "searchResult", str)
                            viewModel.addChoice("lang", str)
                        }
                    }
                )
            }

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



        Log.d(TAG, "onItemSelected,myWorkingAreaIndex: ${viewModel.myWorkingAreaIndex.value}")
        Log.d(TAG, "onItemSelected,check : ${viewModel.check}")




        view.btnSearch.setOnClickListener {
            //
            viewModel._languagesSearchResult.value = null
            val searchWord = etSearch.text.toString()
            viewModel.getLanguageIdeCategories("language",searchWord)
        }

        view.btnDirectInput.setOnClickListener {
            navController!!.navigate(R.id.action_languageSearchFragment_to_languageDirectFragment)
        }


        view.btnSelectComplete.setOnClickListener {
            navController!!.navigate(R.id.action_languageSearchFragment_to_mypageEditFragment)
        }

        observeToast()

        return view
    }

    private fun getFlexBoxLayoutManager(): FlexboxLayoutManager {
        return FlexboxLayoutManager(requireContext()).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }
    }

    private fun observeToast() {
        viewModel.toastMessage.observe(
            viewLifecycleOwner,
            Observer { message ->
                if (message.isNotBlank()) {
                    alertText(message)
                    viewModel._toastMessage.value = ""
                }
            }
        )
    }

    private fun makeToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun alertText(message: String) {
        var dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(message)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }
}