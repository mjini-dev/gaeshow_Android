package com.devup.android_shopping_mall.view.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.services.model.ServicesPostsRequest
import com.devup.android_shopping_mall.databinding.FragmentQuestionListBinding
import com.devup.android_shopping_mall.view.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_question_list.view.*
import org.koin.android.ext.android.inject

class QuestionListFragment : Fragment() {
    private val TAG = "QuestionListFragment"

    private val viewModel: ServicesViewModel by inject()
    private val categoryId = 3


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_question_list, container, false)
        Log.d(TAG, "onCreateView")

        val binding: FragmentQuestionListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_list, container, false)
        val view = binding.root

        binding.apply {
            vm = viewModel
            lifecycleOwner = this@QuestionListFragment
            view.rvList.let {
                it.adapter = ServicesAdapter(
                    categoryId,
                    object : ServicesAdapter.PostItemClickListener {
                        override fun onPostItemClick(postId: Int) {
                            Log.d(TAG, "onPostItemClick,postId: $postId")
                            viewModel.openPostDetail(postId)
                        }
                    }
                )
            }
        }

        viewModel.getAccessToken()

        view.btnSearch.setOnClickListener {
            if (!view.etSearch.text.toString().isNullOrBlank()) {
                val searchWord = view.etSearch.text.toString()
                val request = ServicesPostsRequest(categoryId,1,10,"content",searchWord)
                viewModel.loadServicesPostsSearch(categoryId,request)
                hideSoftInput()
            } else {
                alertSearch(R.string.search_hint_str)
            }
        }

        view.btnWrite.setOnClickListener {
            val intent = Intent(requireContext(), QuestionWriteActivity::class.java)
            intent.putExtra("categoryId",categoryId)
            Log.d(TAG, "onCreateView,categoryId: $categoryId")
            startActivity(intent)
        }

        view.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = view.rvList.layoutManager

                if (viewModel.isNextPage.value == true) {
                    val lastVisibleItem = (layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()

                    if (layoutManager.itemCount <= lastVisibleItem + 2) {
                        viewModel.loadMorePosts(categoryId)
                    }
                }
            }
        })

        observeStartDetailActivity()
        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume ")
        //viewModel.getAccessToken()
        observeIsExistAuthor()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        view?.etSearch?.text?.clear()
        viewModel.clearServicesPosts()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    fun observeIsExistAuthor() {
        viewModel.isExistAuthor.observe(viewLifecycleOwner, Observer { isExistAuthor ->
            if (isExistAuthor) {
                viewModel.loadServicesPosts(categoryId)
            } else {
                alert(R.string.notice_login_str)
            }
        })
    }

    fun alert(strId: Int) {
        var dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(strId)
        /*dialog.setPositiveButton(R.string.ok_str, DialogInterface.OnClickListener { dialogInterface, i ->
        })*/
        dialog.setPositiveButton(R.string.ok_str) { _, _ ->
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        dialog.show()
    }

    private fun alertSearch(strId: Int) {
        var dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str) { _, _ ->
            view?.etSearch?.requestFocus()
        }
        dialog.show()
    }

    fun observeStartDetailActivity() {
        viewModel.openDetailActivity.observe(viewLifecycleOwner, Observer { viewActivity ->
            if (viewActivity) {
                val postId = viewModel.postId.value
                if (postId == null) {
                    Log.e(TAG, "postId is null observeStartDetailActivity")
                } else {
                    openDetailActivity(postId)
                }
            }
        })
    }

    fun openDetailActivity(postId: Int) {
        val intent = Intent(view?.context, QuestionDetailActivity::class.java)
        intent.putExtra("resource_id",postId)
        startActivity(intent)
    }

    private fun hideSoftInput() {
        val main = activity as AppCompatActivity
        val imm: InputMethodManager = main.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.etSearch?.windowToken, 0);
    }
}