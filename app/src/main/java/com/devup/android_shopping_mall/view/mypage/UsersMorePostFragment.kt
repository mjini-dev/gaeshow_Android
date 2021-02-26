package com.devup.android_shopping_mall.view.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
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
import com.devup.android_shopping_mall.databinding.FragmentUsersMorePostBinding
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_users_more_post.view.*
import org.koin.android.ext.android.inject


class UsersMorePostFragment : Fragment() {

    private val TAG: String = "UsersMorePostFragment"

    private val viewModel: MypageViewModel by inject()

    lateinit var navController: NavController

    var userId = 0
    var categoryId = 0
    var profileNickname = " "
    var type = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt("user_id")
            categoryId = it.getInt("category_id")
            profileNickname = it.getString("profile_nickname").toString()
            type = it.getString("type").toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_users_more_post, container, false)
        val binding: FragmentUsersMorePostBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_users_more_post, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@UsersMorePostFragment
            viewModel.getUserInfo(userId)
            viewModel.page = 0
            viewModel.loadPosts(categoryId, type ,userId)
            when (categoryId) {
                5 -> {
                    view.rvWorkspaceList.let {
                        it.adapter = ListAdapter(5, object : ListAdapter.PostItemClickListener {
                            override fun onPostItemClick(postId: Int) {
                                viewModel.openPostDetail(categoryId, postId)
                            }
                        })
                    }
                }

                1 -> {
                    view.rvBoardList.let {
                        it.adapter = ListAdapter(1, object : ListAdapter.PostItemClickListener {
                            override fun onPostItemClick(postId: Int) {
                                viewModel.openPostDetail(categoryId, postId)
                            }
                        })
                    }
                }
            }

        }

        val main = activity as AppCompatActivity
        val actionBar: ActionBar = main.supportActionBar!!
        when (type) {
            "unitary" -> main.tvTitleToolbar.text = "${profileNickname}님의 페이지 "
            "bookmark" -> main.tvTitleToolbar.text = "북마크 관리"
        }
        //main.tvTitleToolbar.text = "${profileNickname}님의 페이지 "
        actionBar.setDisplayHomeAsUpEnabled(false)
        main.imageBtnDrawer.visibility = View.GONE
        main.btnBackToolbar.visibility = View.GONE
        main.btnCloseToolbar.visibility = View.GONE

        //무한스크롤
        when (categoryId) {
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
                                viewModel.loadMorePosts(categoryId,type, userId)
                            }
                        }
                    }
                })
            }
            1 -> {
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
                                viewModel.loadMorePosts(categoryId,type, userId)
                            }
                        }
                    }
                })
            }
        }



        observeStartDetailActivity()

        return view
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
                navController.navigate(R.id.action_usersMorePostFragment_to_workspaceViewFragment, bundle)
                Log.d(TAG, "bundle Of resource_id $bundle")
            }
            1->{
                navController.navigate(R.id.action_usersMorePostFragment_to_boardViewFragment, bundle)
                Log.d(TAG, "bundle Of resource_id $bundle")
            }
        }


    }


}