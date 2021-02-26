package com.devup.android_shopping_mall.view.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentNotificationsBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NotificationsFragment : Fragment() {
    private val TAG = "NotificationsFragment"
    private val viewModel: MainViewModel by sharedViewModel()
    lateinit var navController: NavController

    var uniqueId = 0
    var categoryId:Int? = 0

//    lateinit var title: String
    val title: String =  " " //카테고리 조회후,  categoryId에 맞는 타이틀 보낸다.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_notifications, container, false)
        val binding: FragmentNotificationsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)
        val view = binding.root

        binding.apply {
            vm = viewModel
            lifecycleOwner = this@NotificationsFragment
            viewModel.getAccessToken()
            //viewModel.loadNotifications()
            view.rvList.let {
                it.adapter = NotificationAdapter(

                    object : NotificationAdapter.PostItemClickListener {
                        override fun onPostItemClick(position: Int) {
                            Log.d(TAG, "onPostItemClick,postId: $position")
                            //알림 수정 - 읽음 처리 하기

                            viewModel.modifyNotifications(viewModel.notifications.value?.get(position)!!.id)

                            uniqueId = viewModel.notifications.value?.get(position)?.unique_id ?: 0
                            categoryId = viewModel.notifications.value?.get(position)?.post_category_id ?: 0

                            viewModel.openPostDetail()

                        }
                    }
                )
            }
        }


        view.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = view.rvList.layoutManager

                if (viewModel.isNextPage.value == true) {
                    val lastVisibleItem = (layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()

                    if (layoutManager.itemCount <= lastVisibleItem + 2) {
                        viewModel.loadMoreNotifications()
                    }
                }
            }
        })


        val main = activity as AppCompatActivity
        val actionBar: ActionBar = main.supportActionBar!!
        main.tvTitleToolbar.text = getText(R.string.notification_str)
        //ActionBar 뒤로가기버튼 비활성화
        actionBar.setDisplayHomeAsUpEnabled(false)
//----- 커뮤니티 -네비게이션 드로어 open 버튼
        main.imageBtnDrawer.visibility = View.GONE
        main.btnBackToolbar.visibility = View.GONE
        main.btnCloseToolbar.visibility = View.GONE
        main.imageBtnDrawer.setOnClickListener { main.drawerLayout.openDrawer(GravityCompat.START) }

        main.imageBtnBell.isEnabled = false




        observeOpenDetail()

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume ")
        viewModel.getAccessToken()
        observeIsExistAuthor()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        viewModel.clearNotifications()
    }

    fun observeIsExistAuthor() {
        viewModel.isExistAuthor.observe(viewLifecycleOwner, Observer { isExistAuthor ->
            if (isExistAuthor) {
                viewModel.loadNotifications()
            } else {
                //alert(R.string.notice_login_str)
            }
        })
    }

    private fun observeOpenDetail() {
        viewModel.openDetail.observe(viewLifecycleOwner, Observer { viewFragment ->
            if (viewFragment) {
                if (categoryId != 0) {
                    navigateAction()
                }
            }
        })
    }

    fun navigateAction() {
        val bundle = bundleOf("resource_id" to uniqueId, "category_id" to categoryId, "title" to title)
        when (categoryId) {
            5-> {
                navController!!.navigate(R.id.action_notificationsFragment_to_workspaceViewFragment, bundle)
            }
            else -> navController!!.navigate(R.id.action_notificationsFragment_to_boardViewFragment, bundle)
        }
        /*val bundle = bundleOf("resource_id" to uniqueId, "category_id" to categoryId, "title" to title)
        navController!!.navigate(R.id.action_notificationsFragment_to_boardViewFragment, bundle)*/
    }


}