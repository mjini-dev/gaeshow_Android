package com.devup.android_shopping_mall.view.community.board

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
import com.devup.android_shopping_mall.databinding.FragmentBoardBinding
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter
import com.devup.android_shopping_mall.view.community.viewmodel.BoardViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_board.view.*
import org.koin.android.ext.android.inject

class BoardFragment : Fragment() {
    private val TAG = "BoardFragment"

    private val viewModel: BoardViewModel by inject()
    private val categoryId = 2
    lateinit var title: String

    //프래그먼트 화면전환에 필요한 컨트롤러
    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //뷰를 참조하여 내비게이션을 인스턴스화 한다
        //내비게이션 그래프에 대한 참조를 가질 컨트롤러
        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val view = inflater.inflate(R.layout.fragment_board, container, false)
        val binding: FragmentBoardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_board, container, false)
        val view = binding.root
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@BoardFragment
            viewModel.loadPosts(categoryId)
            view.rvBoardList.let {
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
            navController.navigate(R.id.action_navigationBoard_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }

        val navigationView: NavigationView = main.findViewById(R.id.navigationView)
        val menu: Menu = navigationView.menu
        title = menu.findItem(R.id.navigationBoard).title.toString()
        view.tvBoard.text = title


        observeStartDetailActivity()
        //observeIsExistAuthor()

        view.btnWrite.setOnClickListener {
            observeIsExistAuthor()
        }

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
        navController.navigate(R.id.action_navigationBoard_to_boardViewFragment, bundle)
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
                navController.navigate(R.id.action_navigationBoard_to_boardWriteFragment, bundle)
            } else {
                alert(R.string.notice_login_str)
            }
        })
    }

}