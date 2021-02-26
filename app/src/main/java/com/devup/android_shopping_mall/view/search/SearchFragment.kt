package com.devup.android_shopping_mall.view.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentSearchBinding
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_search.view.rvBoardList
import kotlinx.android.synthetic.main.fragment_search.view.rvWorkspaceList
import kotlinx.android.synthetic.main.fragment_users_more_post.view.*
import org.koin.android.ext.android.inject

class SearchFragment : Fragment() {
    private val TAG = "SearchFragment"
    private val viewModel: SearchViewModel by inject()


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
        val binding: FragmentSearchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@SearchFragment
            when (viewModel.categoryId.value) {
                5 -> {
                    view.rvWorkspaceList.let {
                        it.adapter = ListAdapter(5, object : ListAdapter.PostItemClickListener {
                            override fun onPostItemClick(postId: Int) {
                                viewModel.openPostDetail(5, postId)
                            }
                        })
                    }
                }

                else -> {
                    view.rvBoardList.let {
                        it.adapter = ListAdapter(viewModel.categoryId.value!!, object : ListAdapter.PostItemClickListener {
                            override fun onPostItemClick(postId: Int) {
                                viewModel.openPostDetail(viewModel.categoryId.value!!, postId)
                            }
                        })
                    }
                }
            }

        }

        val main = activity as AppCompatActivity
        val actionBar: ActionBar = main.supportActionBar!!
        //actionBar.title = "ddddddddddd"
        main.tvTitleToolbar.text = getText(R.string.navi_search_str)
        actionBar.setDisplayHomeAsUpEnabled(false)
//----- 커뮤니티 -네비게이션 드로어 버튼 숨기기
        main.imageBtnDrawer.visibility = View.GONE
        main.btnBackToolbar.visibility = View.GONE
        main.btnCloseToolbar.visibility = View.GONE

        main.imageBtnBell.isEnabled = true
        main.imageBtnBell.setOnClickListener {
            navController.navigate(R.id.action_navigationSearch_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }


        //게시판 선택
        view.spCommunityFilte.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                val categoryName = view.spCommunityFilte.selectedItem.toString()
                when (categoryName) {
                    "전체보기" -> viewModel._categoryId.value = 1
                    "업무 얘기 공유" -> viewModel._categoryId.value = 2
                    "자기 작업물 자랑" -> viewModel._categoryId.value = 8
                    "회사 욕 하기" -> viewModel._categoryId.value = 3
                    "프리랜서 팁 공유" -> viewModel._categoryId.value = 4
                    "워크스페이스 공유" -> viewModel._categoryId.value = 5
                }
            }
        }

        //searchType
        view.spSearchFilte.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                when (position) {
                    0 -> viewModel._searchType.value = "content"
                    1 -> viewModel._searchType.value = "nickname"
                    2 -> viewModel._searchType.value = "tag"
                }
            }
        }

        view.btnSearch.setOnClickListener {
            if (!view.etSearch.text.toString().isBlank()) {
                val searchWord = view.etSearch.text.toString()
                viewModel._searchWord.value = searchWord

                viewModel.clearPosts()
                viewModel.page = 0
                viewModel.loadPosts(viewModel.categoryId.value!!, viewModel.searchType.value.toString(), searchWord)
                hideSoftInput()
            } else {
                alertSearch(R.string.search_hint_str)
            }
        }


        //무한스크롤
        when (viewModel.categoryId.value) {
            5 -> {
                view.rvWorkspaceList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = view.rvWorkspaceList.layoutManager
                        // 다음 페이지가 있는 경우
                        if (viewModel.isNextPage.value == true) {
                            val lastVisibleItem = (layoutManager as GridLayoutManager)
                                .findLastCompletelyVisibleItemPosition()

                            // 마지막으로 보여진 아이템 position 이
                            // 전체 아이템 개수보다 2개 모자란 경우, 데이터를 loadMore 한다
                            if (layoutManager.itemCount <= lastVisibleItem + 4) {
                                viewModel.loadMorePosts(viewModel.categoryId.value!!, viewModel.searchType.value.toString(), viewModel.searchWord.value.toString())
                            }
                        }
                    }
                })
            }
            else -> {
                view.rvBoardList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = view.rvBoardList.layoutManager
                        // 다음 페이지가 있는 경우
                        if (viewModel.isNextPage.value == true) {
                            val lastVisibleItem = (layoutManager as LinearLayoutManager)
                                .findLastCompletelyVisibleItemPosition()

                            // 마지막으로 보여진 아이템 position 이
                            // 전체 아이템 개수보다 2개 모자란 경우, 데이터를 loadMore 한다
                            if (layoutManager.itemCount <= lastVisibleItem + 4) {
                                viewModel.loadMorePosts(viewModel.categoryId.value!!, viewModel.searchType.value.toString(), viewModel.searchWord.value.toString())
                            }
                        }
                    }
                })
            }
        }


        observeIsWorkspace()
        observeStartDetailActivity()

        return view
    }
//-----onCreateView종료

    private fun observeIsWorkspace() {
        viewModel.isWorkspacePage.observe(viewLifecycleOwner, Observer { isWorkspace ->
            if (isWorkspace) {
                view?.rvWorkspaceList!!.visibility = View.VISIBLE
                view?.rvBoardList!!.visibility = View.GONE

                view?.rvWorkspaceList!!.apply {
                    layoutManager = GridLayoutManager(context,2)
                    adapter = ListAdapter(5, object : ListAdapter.PostItemClickListener {
                        override fun onPostItemClick(postId: Int) {
                            viewModel.openPostDetail(5, postId)
                        }
                    })
                }
                (view?.rvWorkspaceList!!.adapter as? ListAdapter)?.run {
                    viewModel.workspacePosts.value?.let { updateItems(it) }
                    notifyDataSetChanged()
                }


            } else {
                view?.rvWorkspaceList!!.visibility = View.GONE
                view?.rvBoardList!!.visibility = View.VISIBLE
            }
        })
    }

    private fun alertSearch(strId: Int) {
        val dialog = AlertDialog.Builder(requireContext())
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

    fun observeStartDetailActivity() {
        viewModel.openDetailActivity.observe(viewLifecycleOwner, Observer { viewFragment ->
            if (viewFragment) {
                val postId = viewModel.postId.value
                val categoryId = viewModel.categoryId.value!!
                if (postId == null ) {
                    Log.e(TAG, "postId is null observeStartDetailActivity")
                } else {
                    navigateAction(categoryId, postId)
                }
            }
        })
    }

    fun navigateAction(categoryId: Int, postId: Int) {
        val bundle = bundleOf("resource_id" to postId, "category_id" to categoryId)
        when (categoryId) {
            5->{
                navController.navigate(R.id.action_navigationSearch_to_workspaceViewFragment, bundle)
                Log.d(TAG, "bundle Of resource_id $bundle")
            }
            else->{
                navController.navigate(R.id.action_navigationSearch_to_boardViewFragment, bundle)
                Log.d(TAG, "bundle Of resource_id $bundle")
            }
        }
    }

}