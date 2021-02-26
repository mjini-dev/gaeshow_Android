package com.devup.android_shopping_mall.view.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentIdeSearchBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_ide_search.*
import kotlinx.android.synthetic.main.fragment_ide_search.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class IdeSearchFragment : Fragment() {
    private val TAG = "IdeSearchFragment"

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
        //return inflater.inflate(R.layout.fragment_ide_search, container, false)
        val binding: FragmentIdeSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ide_search, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@IdeSearchFragment
            viewModel.getJobTypeList()
            viewModel.check++
            viewModel._idesSearchResult.value = null

            view.rvSearchResult.let {
                it.layoutManager = getFlexBoxLayoutManager()
                it.adapter = LanguageIdeAddAdapter("searchResult",
                    object : LanguageIdeAddAdapter.ItemClickListener {
                        override fun onItemClick(str: String) {
                            Log.d(TAG, "onItemClick,str: $str")
                            //languagesSearchResult 에서 제거, languages에 추가
                            viewModel.deleteResult("ide", "searchResult", str)
                            viewModel.addChoice("ide", str)
                        }
                    }
                )
            }

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



        view.btnSearch.setOnClickListener {
            //
            viewModel._idesSearchResult.value = null
            val searchWord = etSearch.text.toString()
            viewModel.getLanguageIdeCategories("ide",searchWord)
        }

        view.btnDirectInput.setOnClickListener {
            navController!!.navigate(R.id.action_ideSearchFragment_to_ideDirectFragment)
        }


        view.btnSelectComplete.setOnClickListener {
            navController!!.navigate(R.id.action_ideSearchFragment_to_mypageEditFragment)
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