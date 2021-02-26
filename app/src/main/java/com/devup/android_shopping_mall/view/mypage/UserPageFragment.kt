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
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentUserPageBinding
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user_page.view.*
import org.koin.android.ext.android.inject

class UserPageFragment : Fragment() {
    private val TAG: String = "UserPageFragment"

    private val viewModel: MypageViewModel by inject()

    lateinit var navController: NavController

    var userId = 0
    var profileNickname = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt("user_id")
            profileNickname = it.getString("profile_nickname").toString()
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
        //return inflater.inflate(R.layout.fragment_user_page, container, false)
        val binding: FragmentUserPageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_page, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@UserPageFragment
            Log.d(TAG, "onCreate,user_id : $userId ")
            viewModel.getUserInfo(userId)
            viewModel.loadPosts(5, "unitary", userId)

            view.rvWorkspaceList.let {
                it.adapter = ListAdapter(5, object : ListAdapter.PostItemClickListener {
                    override fun onPostItemClick(postId: Int) {
                        viewModel.openPostDetail(5, postId)
                    }
                })
            }
            view.rvBoardList.let {
                it.adapter = ListAdapter(1, object : ListAdapter.PostItemClickListener {
                    override fun onPostItemClick(postId: Int) {
                        viewModel.openPostDetail(1, postId)
                    }
                })
            }
        }


        val main = activity as AppCompatActivity
        val actionBar: ActionBar = main.supportActionBar!!
        main.tvTitleToolbar.text = "${profileNickname}님의 페이지 "
        actionBar.setDisplayHomeAsUpEnabled(false)
        main.imageBtnDrawer.visibility = View.GONE
        main.btnBackToolbar.visibility = View.GONE
        main.btnCloseToolbar.visibility = View.GONE

        //main.imageBtnBell.visibility= View.GONE
        main.imageBtnBell.isEnabled = true
        main.imageBtnBell.setOnClickListener {
            navController.navigate(R.id.action_userPageFragment_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }


        view.btnMoreWorkspace.setOnClickListener {
            val bundle = bundleOf("user_id" to userId, "profile_nickname" to profileNickname, "category_id" to 5, "type" to "unitary")
            navController!!.navigate(R.id.action_userPageFragment_to_usersMorePostFragment, bundle)
        }

        view.btnMorePost.setOnClickListener {
            val bundle = bundleOf("user_id" to userId, "profile_nickname" to profileNickname, "category_id" to 1, "type" to "unitary")
            navController!!.navigate(R.id.action_userPageFragment_to_usersMorePostFragment, bundle)
        }

        observeLoadWorkspaceFinish()
        observeStartDetailActivity()
        return view
    }

    override fun onPause() {
        super.onPause()
        viewModel.page = 0
        viewModel.clearPosts()
    }

    //워크스페이스 로드 후 일반 게시글 목록 로드
    private fun observeLoadWorkspaceFinish() {
        viewModel.loadWorkspaceFinish.observe(viewLifecycleOwner, Observer { finish ->
            if (finish) {
                viewModel.page = 0
                viewModel.loadPosts(1, "unitary", userId)
                viewModel._loadWorkspaceFinish.value = false
            }
        })
    }

    fun observeStartDetailActivity() {
        viewModel.openDetailActivity.observe(viewLifecycleOwner, Observer { viewFragment ->
            if (viewFragment) {
                val postId = viewModel.postId.value
                val categoryId = viewModel.categoryId.value!!
                if (postId == null) {
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
            5 -> {
                navController.navigate(R.id.action_userPageFragment_to_workspaceViewFragment, bundle)
                Log.d(TAG, "bundle Of resource_id $bundle")
            }
            1 -> {
                navController.navigate(R.id.action_userPageFragment_to_boardViewFragment, bundle)
                Log.d(TAG, "bundle Of resource_id $bundle")
            }
        }


    }

}