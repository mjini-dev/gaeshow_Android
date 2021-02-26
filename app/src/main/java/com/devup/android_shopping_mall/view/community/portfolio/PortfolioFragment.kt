package com.devup.android_shopping_mall.view.community.portfolio

import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentPortfolioBinding
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter
import com.devup.android_shopping_mall.view.community.viewmodel.BoardViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_portfolio.view.*
import org.koin.android.ext.android.inject

class PortfolioListFragment : Fragment() {
    private val TAG = "PortfolioListFragment"

    private val viewModel: BoardViewModel by inject()
    private val categoryId = 8
    lateinit var title: String

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

        val binding: FragmentPortfolioBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_portfolio, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@PortfolioListFragment
            viewModel.loadPosts(categoryId)
            view.rvPortfolioList.adapter = ListAdapter(
                categoryId,
                object : ListAdapter.PostItemClickListener {
                    override fun onPostItemClick(postId: Int) {
                        viewModel.openPostDetail(postId)
                    }
                })
        }

        val main = activity as AppCompatActivity
        val actionBar: ActionBar = main.supportActionBar!!
        main.tvTitleToolbar.text = getText(R.string.navi_community_str)
        actionBar.setDisplayHomeAsUpEnabled(false)
        main.imageBtnDrawer.visibility = View.VISIBLE
        main.btnBackToolbar.visibility = View.GONE
        main.btnCloseToolbar.visibility = View.GONE
        main.imageBtnDrawer.setOnClickListener { main.drawerLayout.openDrawer(GravityCompat.START) }

        main.imageBtnBell.isEnabled = true
        main.imageBtnBell.setOnClickListener {
            navController.navigate(R.id.action_navigationPortfolio_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }

        //네비게이션 드로어 타이틀로  프래그먼트 타이틀 변경
        val navigationView: NavigationView = main.findViewById(R.id.navigationView)
        val menu: Menu = navigationView.menu
        title = menu.findItem(R.id.navigationPortfolio).title.toString()
        view.tvPortfolio.text = title

        //상세보기 이동
        observeStartDetailActivity()

        //글쓰기
        view.btnWrite.setOnClickListener {
            observeIsExistAuthor()
        }

        //무한스크롤
        view.rvPortfolioList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = view.rvPortfolioList.layoutManager

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
//-----onCreateView종료

    fun observeStartDetailActivity() {
        viewModel.openDetailActivity.observe(viewLifecycleOwner, Observer { viewFragment ->
            if (viewFragment) {
                val postId = viewModel.postId.value
                if (postId == null) {
                    Log.e(TAG, "viewFragmwnt of observeStartDetailActivity")
                } else {
                    navigateAction(postId)
                }
            }
        })
    }

    fun navigateAction(postId: Int) {
        val bundle = bundleOf("resource_id" to postId, "category_id" to categoryId, "title" to title)
        navController!!.navigate(R.id.action_navigationPortfolio_to_boardViewFragment, bundle)
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
                navController!!.navigate(R.id.action_navigationPortfolio_to_portfolioWriteFragment, bundle)
            } else {
                alert(R.string.notice_login_str)
            }
        })
    }

}