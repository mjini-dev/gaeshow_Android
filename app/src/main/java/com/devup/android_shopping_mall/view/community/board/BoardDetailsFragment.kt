package com.devup.android_shopping_mall.view.community.board

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
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
import com.devup.android_shopping_mall.R
import com.devup.android_shopping_mall.databinding.FragmentBoardDetailsBinding
import com.devup.android_shopping_mall.view.comment.BoardCommentAdapter
import com.devup.android_shopping_mall.view.comment.CommentDepthActivity
import com.devup.android_shopping_mall.view.comment.CommentModifyActivity
import com.devup.android_shopping_mall.view.community.adapter.TagAdapter
import com.devup.android_shopping_mall.view.community.viewmodel.BoardDetailsViewModel
import com.devup.android_shopping_mall.view.service.ReportWriteActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_board_details.*
import kotlinx.android.synthetic.main.fragment_board_details.view.*
import kotlinx.android.synthetic.main.fragment_board_details.view.btnAddComment
import kotlinx.android.synthetic.main.fragment_board_details.view.btnReport
import kotlinx.android.synthetic.main.fragment_board_details.view.btnShare
import kotlinx.android.synthetic.main.fragment_board_details.view.etComment
import kotlinx.android.synthetic.main.fragment_board_details.view.ivBookmark
import kotlinx.android.synthetic.main.fragment_board_details.view.ivLiked
import kotlinx.android.synthetic.main.fragment_board_details.view.ivProfileImage
import kotlinx.android.synthetic.main.fragment_board_details.view.rvCommentList
import kotlinx.android.synthetic.main.fragment_board_details.view.rvTag
import kotlinx.android.synthetic.main.fragment_board_details.view.tvBookmarkCount
import kotlinx.android.synthetic.main.fragment_board_details.view.tvContent
import kotlinx.android.synthetic.main.fragment_board_details.view.tvLikedCount
import kotlinx.android.synthetic.main.fragment_board_details.view.tvNickname
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.*
import org.koin.android.ext.android.inject

class BoardDetailsFragment : Fragment() {
    private val TAG = "BoardDetailsFragment"

    private val viewModel: BoardDetailsViewModel by inject()

    private val boardCommentAdapter = BoardCommentAdapter(viewModel, object :
        BoardCommentAdapter.PostItemClickListener {
        //대댓글달기
        override fun onPostItemClick(position: Int) {
            observeIsExistAuthor(position)
        }

        //댓글 수정
        override fun onModifyClick(position: Int) {
            val content = viewModel.comments.value?.get(position)?.content.toString()
            Log.d(TAG, "onModify , 수정하고싶은 내용: $content")
            val comment_id = viewModel.comments.value?.get(position)?.comment_id as Int

            modifyComment(resource_id, comment_id, content)
        }

        //댓글 삭제
        override fun onDeleteClick(comment_id: Int) {
            viewModel.deleteComment(resource_id, comment_id)
        }

        //평가(좋아요)
        override fun onRatingClick(position: Int) {
            //viewModel.comments.value[position].rating_id 0이면 안누른거, 0이아니면 누른거
            if (viewModel.isExistAuthor.value!!) {
                if (viewModel.comments.value?.get(position)?.rating_id ?: 0 != 0) {
                    //좋아요 삭제
                    viewModel.ratingsDelete("comment", viewModel.comments.value?.get(position)?.rating_id as Int)
                } else {
                    viewModel.ratingsAdd("comment", viewModel.comments.value?.get(position)?.comment_id as Int)
                }
                viewModel.clearComments()
                //로그인 한 상태
                //viewModel.loadCommentsAuth(resource_id)
            } else {
                alert(R.string.notice_login_str)
            }


        }

        //신고
        override fun onReportClick(comment_id: Int) {
            if (viewModel.isExistAuthor.value!!) {
                report(comment_id, "POST_COMMENT")
            } else {
                alert(R.string.notice_login_str)
            }
        }
    })

    private lateinit var callback: OnBackPressedCallback
    private var resource_id: Int = 0
    private var category_id: Int = 0
    lateinit var title: String

    //프래그먼트 화면전환에 필요한 컨트롤러
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            resource_id = it.getInt("resource_id")
            category_id = it.getInt("category_id")
            title = it.getString("title", "")
        }
        //resource_id = requireArguments().getInt("resource_id")
        Log.d(TAG, "resource_id $resource_id")
        Log.d(TAG, "category_id $category_id")
    }


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
        //val view = inflater.inflate(R.layout.fragment_board_details, container, false)
        val binding: FragmentBoardDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_board_details, container, false)
        val view = binding.root

        //liveData를 활용하기위해 설정
        //설정하지 않으면 라이브데이터 관찰할 때 마다 data(xml)이 refresh되지않음
        binding.apply {
            vm = viewModel
            lifecycleOwner = this@BoardDetailsFragment
            if (viewModel.isExistAuthor.value!!) {
                //로그인 한 상태
                viewModel.loadPostDetailsAuth(resource_id)
            } else {
                //비로그인 상태
                viewModel.loadPostDetails(resource_id)
            }

            //viewModel.loadComments(resource_id)
            view.rvCommentList.adapter = boardCommentAdapter
            //view.rvTag.adapter = TagAdapter(category_id)
            view.rvTag.adapter = TagAdapter(category_id)
        }

        view.tvBoard.text = title
        val main = activity as AppCompatActivity
        val actionBar: ActionBar = main.supportActionBar!!
        main.tvTitleToolbar.text = getText(R.string.navi_community_str)
//----- 커뮤니티 -네비게이션 드로어 open 버튼
        actionBar.setDisplayHomeAsUpEnabled(false)
        main.imageBtnDrawer.visibility = View.VISIBLE
        main.btnBackToolbar.visibility = View.VISIBLE
        main.btnCloseToolbar.visibility = View.GONE
        main.imageBtnDrawer.setOnClickListener { main.drawerLayout.openDrawer(GravityCompat.START) }

        main.imageBtnBell.isEnabled = true
        main.imageBtnBell.setOnClickListener {
            navController.navigate(R.id.action_boardViewFragment_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }

         //뒤로가기버튼
         main.btnBackToolbar.setOnClickListener {
             when (category_id) {
                 2 -> navController.navigate(R.id.action_boardViewFragment_to_navigationBoard)
                 3 -> navController.navigate(R.id.action_boardViewFragment_to_navigationBadmouse)
                 4 -> navController.navigate(R.id.action_boardViewFragment_to_navigationTips)
                 8 -> navController.navigate(R.id.action_boardViewFragment_to_navigationPortfolio)
             }
             Log.d(TAG, "onCreateView, main.btnBackToolbar : ")
         }

        //평가(좋아요) 클릭
        view.ivLiked.setOnClickListener {
            if (viewModel.isExistAuthor.value!!) {
                val count = view.tvLikedCount?.text.toString().toInt()
                if (viewModel.ratingStatus.value!!) {
                    //좋아요 삭제
                    viewModel.ratingsDelete("post", viewModel.ratingId.value!!)
                    //좋아요 카운트 빼기
                    val countAfter: Int = count - 1
                    view.tvLikedCount?.text = countAfter.toString()
                } else {
                    viewModel.ratingsAdd("post", resource_id)
                    //좋아요 카운트 플러스
                    val countAfter: Int = count + 1
                    view.tvLikedCount?.text = countAfter.toString()
                }
                observeRatingStatus()
            } else {
                alert(R.string.notice_login_str)
            }

        }

        //북마크 클릭
        view.ivBookmark.setOnClickListener {
            if (viewModel.isExistAuthor.value!!) {
                val count = view.tvBookmarkCount?.text.toString().toInt()
                if (viewModel.bookmarkStatus.value!!) {
                    //좋아요 삭제
                    viewModel.bookmarksDeletePost(viewModel.bookmarkId.value!!)
                    //좋아요 카운트 빼기
                    val countAfter: Int = count - 1
                    view.tvBookmarkCount?.text = countAfter.toString()
                } else {
                    viewModel.bookmarksAddPost(resource_id)
                    //좋아요 카운트 플러스
                    val countAfter: Int = count + 1
                    view.tvBookmarkCount?.text = countAfter.toString()
                }
                observeBookmarkStatus()
            } else {
                alert(R.string.notice_login_str)
            }
        }

        //프로필이미지, 닉네임 클릭 -> 유저페이지로 이동동
        //ivProfileImage, tvNickname
        view.ivProfileImage.setOnClickListener {
            val userId = viewModel.postDetails.value!!.user_id
            val profileNickname = viewModel.postDetails.value!!.profile_nickname

            val bundle = bundleOf("user_id" to userId, "profile_nickname" to profileNickname)
            navController.navigate(R.id.action_boardViewFragment_to_userPageFragment, bundle)
        }

        view.tvNickname.setOnClickListener {
            val userId = viewModel.postDetails.value!!.user_id
            val profileNickname = viewModel.postDetails.value!!.profile_nickname

            val bundle = bundleOf("user_id" to userId, "profile_nickname" to profileNickname)
            navController.navigate(R.id.action_boardViewFragment_to_userPageFragment, bundle)
        }

        //공유하기
        view.btnShare.setOnClickListener {

            var mid = ""
            when(category_id) {
                2-> mid = "boardview"
                3-> mid = "badmouseview"
                4-> mid = "tipsview"
                8-> mid = "portfolioview"
            }
            val text = "[GAESHOW] ${viewModel.postDetails.value?.title}\nhttp://gaeshow.co.kr/$mid.html?n=$resource_id"

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "공유하기")
            startActivity(shareIntent)
        }

        //신고하기
        view.btnReport.setOnClickListener {
            if (viewModel.isExistAuthor.value!!) {
                report(resource_id, "POST")
            } else {
                alert(R.string.notice_login_str)
            }
        }

        //수정하기
        view.btnModify.setOnClickListener {
            val bundle = bundleOf("category_id" to category_id, "title" to title, "resource_id" to resource_id)
            when (category_id) {
                8 -> navController.navigate(R.id.action_boardViewFragment_to_portfolioWriteFragment, bundle)
                else -> navController.navigate(R.id.action_boardViewFragment_to_boardWriteFragment, bundle)
            }
            //navController.navigate(R.id.action_boardViewFragment_to_boardWriteFragment, bundle)
        }

        //삭제하기
        view.btnDelete.setOnClickListener {
            //삭제 확인 다이얼로그
            var alertDialog = AlertDialog.Builder(requireContext()).apply {
                setTitle(R.string.delete_info_str)
                setMessage("")
                setNegativeButton(R.string.cancel_str, null)
                setPositiveButton(R.string.ok_str) { dialog, which ->
                    viewModel.deletePost(resource_id)
                }
            }
            alertDialog.show()
        }

        //댓글추가
        view.btnAddComment.setOnClickListener {
            if (!view.etComment.text.isNullOrBlank()) {
                viewModel.clearComments()
                viewModel.addComment(resource_id, view.etComment.text.toString())
            }
            view.etComment.text.clear()
            hideSoftInput()
        }

        observeIsDeleteSuccess()
        observeContent()

        /*   //val tvContent : WebView = view.findViewById(R.id.tvContent)
           val tvContent : WebView = view.tvContent
           //viewModel.postDetails.value?.content?.let { tvContent.evaluateJavascript(it, ValueCallback { t -> Log.d(TAG, "observeContent: $t") }) }
           viewModel.postDetails.value?.content?.let { tvContent.loadData(it,"text/html; charset=utf-8", "UTF-8") }
           tvContent.webViewClient = HelloWebViewClient()*/

        return view
    }
//-----onCreateView종료

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (category_id) {
                    2 -> navController.navigate(R.id.action_boardViewFragment_to_navigationBoard)
                    3 -> navController.navigate(R.id.action_boardViewFragment_to_navigationBadmouse)
                    4 -> navController.navigate(R.id.action_boardViewFragment_to_navigationTips)
                    8 -> navController.navigate(R.id.action_boardViewFragment_to_navigationPortfolio)
                }
                Log.d(TAG, "OnBackPressed")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        if (viewModel.isExistAuthor.value!!) {
            //로그인 한 상태
            viewModel.loadCommentsAuth(resource_id)
        } else {
            //비로그인 상태
            viewModel.loadComments(resource_id)
        }
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

    private fun hideSoftInput() {
        val main = activity as AppCompatActivity
        val imm: InputMethodManager = main.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etComment.windowToken, 0);
    }

    fun observeIsExistAuthor(position: Int) {
        viewModel.isExistAuthor.observe(viewLifecycleOwner, Observer { isExistAuthor ->
            if (isExistAuthor) {
                var comment = viewModel.comments.value?.get(position)
                viewModel._commentSingle.value = comment

                activity?.let {
                    val intent = Intent(context, CommentDepthActivity::class.java)
                    val bundle = bundleOf("comment" to comment, "resource_id" to resource_id)
                    intent.putExtras(bundle)
                    Log.d(TAG, "bundle: $bundle")
                    startActivity(intent)
                }
            } else {
                alert(R.string.notice_login_str)
            }
        })
    }

    private fun observeRatingStatus() {
        viewModel.ratingStatus.observe(viewLifecycleOwner, Observer { like ->
            if (like) {
                view?.ivLiked?.setBackgroundResource(R.drawable.ic_like_clicked_3x)
            } else {
                view?.ivLiked?.setBackgroundResource(R.drawable.ic_like_df_3x)
            }
        })
    }

    private fun observeBookmarkStatus() {
        viewModel.bookmarkStatus.observe(viewLifecycleOwner, Observer { bookmarked ->
            if (bookmarked) {
                view?.ivBookmark?.setBackgroundResource(R.drawable.ic_bookmark_clicked3x)
            } else {
                view?.ivBookmark?.setBackgroundResource(R.drawable.ic_bookmark_df3x)
            }
        })
    }

    private fun observeIsDeleteSuccess() {
        viewModel.isDeleteSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                navigateAction(category_id)
            }
        })
    }

    fun navigateAction(category_id: Int) {
        when (category_id) {
            2 -> navController.navigate(R.id.action_boardViewFragment_to_navigationBoard)
            3 -> navController.navigate(R.id.action_boardViewFragment_to_navigationBadmouse)
            4 -> navController.navigate(R.id.action_boardViewFragment_to_navigationTips)
            8 -> navController.navigate(R.id.action_boardViewFragment_to_navigationPortfolio)
        }
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

    fun report(uniqueId: Int, reportType: String) {
        val intent = Intent(context, ReportWriteActivity::class.java)
        val bundle = bundleOf("uniqueId" to uniqueId, "reportType" to reportType)
        intent.putExtras(bundle)
        Log.d(TAG, "bundle: $bundle")
        startActivity(intent)
    }

    private fun observeContent() {
        viewModel.isLoadClear.observe(viewLifecycleOwner, Observer { load ->
            if (load) {
                //웹뷰에 링크넣기
                val tvContent: WebView? = view?.tvContent
                Log.d(TAG, "observeContent,viewModel.postDetails.value?.content.toString(): ${viewModel.postDetails.value?.content.toString()}")
                //viewModel.postDetails.value?.content?.let { tvContent.evaluateJavascript(it, ValueCallback { t -> Log.d(TAG, "observeContent: $t") }) }
                viewModel.postDetails.value?.content.toString().let {
                    tvContent?.loadData(it, "text/html; charset=utf-8", "UTF-8")
                }
                if (tvContent != null) {
                    tvContent.webViewClient = HelloWebViewClient()
                }

            }
        }
        )
    }

    private class HelloWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            view.loadUrl(url!!)
            return true
        }
    }


}