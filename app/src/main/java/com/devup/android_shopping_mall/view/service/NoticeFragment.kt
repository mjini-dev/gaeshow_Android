package com.devup.android_shopping_mall.view.service

import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.data.services.model.ServicesPostsRequest
import com.devup.android_shopping_mall.databinding.FragmentNoticeBinding
import kotlinx.android.synthetic.main.fragment_notice.view.*
import kotlinx.android.synthetic.main.fragment_notice.view.btnSearch
import kotlinx.android.synthetic.main.fragment_notice.view.etSearch
import org.koin.android.ext.android.inject

class NoticeFragment : Fragment() {
    private val TAG = "NoticeFragment"

    private val viewModel: ServicesViewModel by inject()
    private val categoryId = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_notice, container, false)

        val binding: FragmentNoticeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notice, container, false)
        val view = binding.root

        binding.apply {
            vm = viewModel
            lifecycleOwner = this@NoticeFragment
            viewModel.loadServicesPosts(categoryId)
            view.rvNoticeList.let {
                it.adapter = ServicesAdapter(
                    categoryId,
                    object : ServicesAdapter.PostItemClickListener {
                        override fun onPostItemClick(position: Int) {
                            Log.d(TAG, "onPostItemClick,postId: $position")

                        }
                    }
                )
            }
        }

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


          view.rvNoticeList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = view.rvNoticeList.layoutManager

                // 다음 페이지가 있는 경우
                if (viewModel.isNextPage.value == true) {
                    val lastVisibleItem = (layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()

                    // 마지막으로 보여진 아이템 position 이
                    // 전체 아이템 개수보다 2개 모자란 경우, 데이터를 loadMore 한다
                    if (layoutManager.itemCount <= lastVisibleItem + 2) {
                        viewModel.loadMorePosts(categoryId)
                    }
                }
            }
        })

        return view
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        view?.etSearch?.text?.clear()
        viewModel.clearServicesPosts()
    }

    fun alertSearch(strId: Int) {
        var dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str) { _, _ ->
            view?.etSearch?.requestFocus()
        }
        dialog.show()
    }
    private fun hideSoftInput() {
        val main = activity as AppCompatActivity
        val imm: InputMethodManager = main.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.etSearch?.windowToken, 0);
    }

}