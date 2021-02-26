package com.devup.android_shopping_mall.view.community.workspace

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentWorkspaceListBinding
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter
import com.devup.android_shopping_mall.view.community.viewmodel.BoardViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_workspace_list.view.*
import kotlinx.android.synthetic.main.fragment_workspace_list.view.btnWrite
import org.koin.android.ext.android.inject

class WorkspaceListFragment : Fragment() {
    private val TAG = "WorkspaceListFragment"
    private val viewModel: BoardViewModel by inject()

    private val categoryId = 5

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
        // Inflate the layout for this fragment
        //val view = inflater.inflate(R.layout.fragment_workspace_list, null)

        val binding: FragmentWorkspaceListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_workspace_list, container, false)
        val view = binding.root

        binding.apply {
            vm = viewModel
            lifecycleOwner = this@WorkspaceListFragment
            viewModel.loadPosts(categoryId)
            view.rvWorkspaceList.let {
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
            navController.navigate(R.id.action_navigationWorkspace_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }

        val navigationView: NavigationView = main.findViewById(R.id.navigationView)
        val menu: Menu = navigationView.menu
        val title = menu.findItem(R.id.navigationWorkspace).title.toString()
        view.tvWorkspace.text = title
        
        view.btnWrite.setOnClickListener {
        }


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
                        viewModel.loadMorePosts(categoryId)
                    }
                }
            }
        })

        observeStartDetailActivity()

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
        val bundle = bundleOf("resource_id" to postId, "category_id" to categoryId)
        navController.navigate(R.id.action_navigationWorkspace_to_workspaceViewFragment, bundle)
        Log.d(TAG, "bundle Of resource_id $bundle")
    }

}