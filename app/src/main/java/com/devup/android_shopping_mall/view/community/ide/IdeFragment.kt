package com.devup.android_shopping_mall.view.community.ide

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
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
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentIdeBinding
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter
import com.devup.android_shopping_mall.view.community.viewmodel.BoardViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_board.view.*
import kotlinx.android.synthetic.main.fragment_ide.view.*
import org.koin.android.ext.android.inject

class IdeFragment : Fragment() {
    private val TAG = "IdeFragment"

    private val viewModel: BoardViewModel by inject()

    var categoryId: Int = 7
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
        val binding: FragmentIdeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ide, container, false)
        val view = binding.root

        binding.apply {
            vm = viewModel
            lifecycleOwner = this@IdeFragment
            viewModel.getLanguageList()
            view.rvIdeLanguageList.let {
                it.adapter = ListAdapter(categoryId,
                object  : ListAdapter.PostItemClickListener {
                    override fun onPostItemClick(postId: Int) {
                        Log.d(TAG, "onPostItemClick, postId: $postId")
                        val type = viewModel.type.value
                        Log.d(TAG, "onPostItemClick,type: $type")
                        viewModel.openPostDetail(postId)
                    }
                })
            }

        }

        //viewModel.getIdeList()


        val main = activity as AppCompatActivity
        val actionBar: ActionBar = main.supportActionBar!!

        val navigationView: NavigationView = main.findViewById(R.id.navigationView)
        val menu: Menu = navigationView.menu

        title = menu.findItem(R.id.navigationIde).title.toString()
        main.tvTitleToolbar.text = title
        //ActionBar 뒤로가기버튼 비활성화
        actionBar.setDisplayHomeAsUpEnabled(false)
//----- 커뮤니티 -네비게이션 드로어 open 버튼
        main.imageBtnDrawer.visibility = View.VISIBLE
        main.btnBackToolbar.visibility = View.GONE
        main.imageBtnDrawer.setOnClickListener { main.drawerLayout.openDrawer(GravityCompat.START) }

        main.imageBtnBell.isEnabled = true
        main.imageBtnBell.setOnClickListener {
            navController.navigate(R.id.action_navigationIde_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }


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
        val type = viewModel.type.value
        val name = viewModel.idesLanguages.value?.get(postId-1)?.en_name
        val bundle = bundleOf("resource_id" to postId, "type" to type, "name" to name)
        navController.navigate(R.id.action_navigation_ide_to_ideRecommendViewFragment, bundle)
        Log.d(TAG, "bundle Of resource_id $bundle")
    }



}