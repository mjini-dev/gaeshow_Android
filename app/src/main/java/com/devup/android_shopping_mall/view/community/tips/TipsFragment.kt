package com.devup.android_shopping_mall.view.community.tips

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentTipsBinding
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter
import com.devup.android_shopping_mall.view.community.viewmodel.BoardViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tips.view.*
import org.koin.android.ext.android.inject

class TipsFragment : Fragment() {

    private val TAG = "BoardFragment"

    private val viewModel: BoardViewModel by inject()
    private val categoryId = 4
    lateinit var title: String

    //프래그먼트 화면전환에 필요한 컨트롤러
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

        val binding: FragmentTipsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tips, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@TipsFragment
            viewModel.loadPosts(categoryId)
            view.rvTipsList.let {
                it.adapter = ListAdapter(
                    categoryId,
                    object : ListAdapter.PostItemClickListener {
                        override fun onPostItemClick(postId: Int) {
                            viewModel.openPostDetail(postId)
                        }
                    })
            }
        }

        val main = activity as AppCompatActivity
        val actionBar: ActionBar = main.supportActionBar!!
        main.tvTitleToolbar.text = getText(R.string.navi_community_str)
        //ActionBar 뒤로가기버튼 비활성화
        actionBar.setDisplayHomeAsUpEnabled(false)
//----- 커뮤니티 -네비게이션 드로어 open 버튼
        main.imageBtnDrawer.visibility = View.VISIBLE
        main.btnBackToolbar.visibility = View.GONE
        main.btnCloseToolbar.visibility = View.GONE
        main.imageBtnDrawer.setOnClickListener { main.drawerLayout.openDrawer(GravityCompat.START) }

        main.imageBtnBell.isEnabled = true
        main.imageBtnBell.setOnClickListener {
            navController.navigate(R.id.action_navigationTips_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }


        val navigationView: NavigationView = main.findViewById(R.id.navigationView)
        val menu: Menu = navigationView.menu
        title = menu.findItem(R.id.navigationTips).title.toString()
        view.tvTips.text = title


        observeStartDetailActivity()

        view.btnWrite.setOnClickListener {
            observeIsExistAuthor()
        }

        view.rvTipsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = view.rvTipsList.layoutManager

                // 다음 페이지가 있는 경우
                if (viewModel.isNextPage.value == true) {
                    val lastVisibleItem = (layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()

                    if (layoutManager.itemCount <= lastVisibleItem + 2) {
                        viewModel.loadMorePosts(categoryId)
                    }
                }
            }
        })

        return view
    }
//-----onCreateView종료

    fun observeStartDetailActivity() {
        viewModel.openDetailActivity.observe(viewLifecycleOwner, Observer { viewFragment ->
            if (viewFragment) {
                val postId = viewModel.postId.value
                if (postId == null) {
                    Log.e(TAG, "postId is null observeStartDetailActivity")
                } else {
                    navigateAction(postId)
                }
            }
        })
    }

    fun navigateAction(postId: Int) {
        val bundle = bundleOf("resource_id" to postId, "category_id" to categoryId, "title" to title)
        navController.navigate(R.id.action_navigationTips_to_boardViewFragment, bundle)
        Log.d(TAG, "bundle Of resource_id $bundle")
    }

    fun alert(strId: Int) {
        var dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }

    fun observeIsExistAuthor() {
        viewModel.isExistAuthor.observe(viewLifecycleOwner, Observer { isExistAuthor ->
            if (isExistAuthor) {
                val bundle = bundleOf("category_id" to categoryId, "title" to title)
                navController.navigate(R.id.action_navigationTips_to_boardWriteFragment, bundle)
            } else {
                alert(R.string.notice_login_str)
            }
        })
    }


}