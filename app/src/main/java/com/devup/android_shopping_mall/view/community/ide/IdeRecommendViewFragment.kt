package com.devup.android_shopping_mall.view.community.ide

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
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
import com.devup.android_shopping_mall.databinding.FragmentIdeRecommendViewBinding
import com.devup.android_shopping_mall.view.comment.BoardIdeCommentAdapter
import com.devup.android_shopping_mall.view.comment.CommentModifyActivity
import com.devup.android_shopping_mall.view.community.viewmodel.BoardDetailsViewModel
import com.devup.android_shopping_mall.view.login.LoginActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_ide_recommend_view.view.*
import org.koin.android.ext.android.inject

class IdeRecommendViewFragment : Fragment() {
    private val TAG = "IdeRecommendFragment"

    private val viewModel: BoardDetailsViewModel by inject()

    private lateinit var callback: OnBackPressedCallback

    lateinit var title: String
    private var uniqueId: Int = 0
    lateinit var type: String
    lateinit var name: String

    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uniqueId = it.getInt("resource_id")
            type = it.getString("type", "")
            name = it.getString("name", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentIdeRecommendViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ide_recommend_view, container, false)
        val view = binding.root

        binding.apply {
            vm = viewModel
            lifecycleOwner = this@IdeRecommendViewFragment
            view.tvLanguageIde.text = name
            view.tvName.text = name

            viewModel.loadRecommendDetails(type, uniqueId)
            view.rvRatingList.let {
                it.adapter = BoardIdeCommentAdapter(
                    object : BoardIdeCommentAdapter.PostItemClickListener {
                        override fun onPostItemClick(postId: Int) {
                            //observeIsExistAuthor(postId)
                        }

                        //댓글수정
                        override fun onModifyClick(position: Int) {
                            val commentId = viewModel.comments.value?.get(position)?.comment_id as Int
                            val rate =  viewModel.comments.value?.get(position)?.average_score
                            val advantage = viewModel.comments.value?.get(position)?.advantage_content
                            val disadvantage = viewModel.comments.value?.get(position)?.disadvantage_content

                            //modifyComment(uniqueId, comment_id, content)

                            observeIsExistAuthor(uniqueId, type, commentId, rate, advantage, disadvantage)

                        }

                        //댓글 삭제
                        override fun onDeleteClick(comment_id: Int) {
                            viewModel.deletePostRecommendsComment(comment_id, type, uniqueId)
                        }

                        //유저 페이지 이동
                        override fun onUserClick(position: Int) {
                            /*val userId = viewModel.postDetails.value!!.user_id
                            val profileNickname = viewModel.postDetails.value!!.profile_nickname*/
                            val userId = viewModel.comments.value?.get(position)?.user_id
                            val profileNickname = viewModel.comments.value?.get(position)?.profile_nickname

                            val bundle = bundleOf("user_id" to userId, "profile_nickname" to profileNickname)
                            navController.navigate(R.id.action_ideRecommendViewFragment_to_userPageFragment, bundle)
                        }
                    })
            }
        }

        viewModel._isTypeLanguage.value = type == "language"
        if (type == "language") {
            viewModel.getLanguageList()
        } else if (type == "ide") {
            viewModel.getIdeList()
            view.radioBtnIde.isChecked = true
        }


        /*      val spAdapter = ArrayAdapter<String>( requireContext(),R.layout.support_simple_spinner_dropdown_item)
              view.spLanguageIde.adapter = spAdapter
              view.spLanguageIde.isSelected = false
              view.spLanguageIde.setSelection(uniqueId,true)
      */
        var check = 0
        view.spLanguageIde.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                if (check++ > 0) {
                    Log.d(TAG, "onItemSelected,position: $position")
                    view.tvLanguageIde.text = viewModel.spinnerItem.value?.get(position) ?: ""
                    view.tvName.text = viewModel.spinnerItem.value?.get(position) ?: ""
                    viewModel.loadRecommendDetailsSpinner(viewModel.type.value.toString(), position + 1)

                }
                check++
            }
        }


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
        main.btnBackToolbar.visibility = View.VISIBLE
        main.btnBackToolbar.setOnClickListener { navController.navigate(R.id.action_ideRecommendViewFragment_to_navigationIde) }
        main.imageBtnDrawer.setOnClickListener { main.drawerLayout.openDrawer(GravityCompat.START) }

        main.imageBtnBell.isEnabled = false

        view.btnGoLogin.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        //평가하기
        view.btnRating.setOnClickListener {
            observeIsExistAuthor(uniqueId, type, 0, null, null, null)
        }

        //댓글 무한 스크롤
        view.rvRatingList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = view.rvRatingList.layoutManager

                // 다음 페이지가 있는 경우
                if (viewModel.isNextPage.value == true) {
                    val lastVisibleItem = (layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()

                    // 마지막으로 보여진 아이템 position 이
                    // 전체 아이템 개수보다 2개 모자란 경우, 데이터를 loadMore 한다
                    if (layoutManager.itemCount <= lastVisibleItem + 2) {
                        viewModel.loadMoreRecommendComments(type, uniqueId)
                    }
                }
            }
        })

        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigate(R.id.action_ideRecommendViewFragment_to_navigationIde)
                Log.d(TAG, "OnBackPressed")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        //viewModel.loadPostDetails(resource_id)
        viewModel.loadRecommendComments(type, uniqueId)
        viewModel.getAccessToken()
        //포커스 이동
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearComments()
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    private fun observeIsExistAuthor(uniqueId: Int, type: String, commentId: Int, rate: Int?, advantage: String?, disadvantage: String?) {
        viewModel.isExistAuthor.observe(viewLifecycleOwner, Observer { isExistAuthor ->
            if (isExistAuthor) {
                val bundle = bundleOf("uniqueId" to uniqueId, "type" to type, "commentId" to commentId, "rate" to rate, "advantage" to advantage, "disadvantage" to disadvantage)
                navController.navigate(R.id.action_ideRecommendViewFragment_to_ideRecommendWriteFragment, bundle)
                Log.d(TAG, "observeIsExistAuthor: $bundle")
            } else {
                alert(R.string.notice_login_str)
                Log.d(TAG, "observeIsExistAuthor: 여기안와?")
            }
        })
    }

    fun alert(strId: Int) {
        var dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }

    fun modifyComment(resource_id: Int, comment_id: Int, content: String) {
        val intent = Intent(context, CommentModifyActivity::class.java)
        val bundle = bundleOf("resource_id" to resource_id, "comment_id" to comment_id, "content" to content)
        intent.putExtras(bundle)
        Log.d(TAG, "bundle: $bundle")
        startActivity(intent)
    }
}