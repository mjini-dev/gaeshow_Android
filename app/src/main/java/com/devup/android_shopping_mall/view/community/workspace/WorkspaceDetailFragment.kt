package com.devup.android_shopping_mall.view.community.workspace

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
import android.widget.RelativeLayout
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
import com.devup.android_shopping_mall.databinding.FragmentWorkspaceDetailBinding
import com.devup.android_shopping_mall.view.comment.BoardCommentAdapter
import com.devup.android_shopping_mall.view.comment.CommentDepthActivity
import com.devup.android_shopping_mall.view.comment.CommentModifyActivity
import com.devup.android_shopping_mall.view.community.adapter.ListAdapter
import com.devup.android_shopping_mall.view.community.adapter.TagAdapter
import com.devup.android_shopping_mall.view.community.viewmodel.BoardDetailsViewModel
import com.devup.android_shopping_mall.view.service.ReportWriteActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_workspace_detail.*
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.*
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.btnAddComment
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.btnReport
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.etComment
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.ivBookmark
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.ivLiked
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.ivProfileImage
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.rvCommentList
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.rvTag
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.tvBookmarkCount
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.tvContent
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.tvLikedCount
import kotlinx.android.synthetic.main.fragment_workspace_detail.view.tvNickname
import org.koin.android.ext.android.inject

class WorkspaceDetailFragment : Fragment() {
    private val TAG = "WorkspaceDetailFragment"

    private val viewModel: BoardDetailsViewModel by inject()

    private lateinit var callback: OnBackPressedCallback
    private var resourceId: Int = 0
    private var categoryId: Int = 0
    lateinit var title: String

    val boardCommentAdapter = BoardCommentAdapter(viewModel, object :
        BoardCommentAdapter.PostItemClickListener {
        override fun onPostItemClick(position: Int) {
            observeIsExistAuthor(position)
        }

        override fun onModifyClick(position: Int) {
            val content = viewModel.comments.value?.get(position)?.content.toString()
            Log.d(TAG, "onModify , 수정하고싶은 내용: $content")
            val comment_id = viewModel.comments.value?.get(position)?.comment_id as Int

            modifyComment(resourceId, comment_id, content)
        }

        override fun onDeleteClick(comment_id: Int) {
            viewModel.deleteComment(resourceId, comment_id)
        }

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

    //프래그먼트 화면전환에 필요한 컨트롤러
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            resourceId = it.getInt("resource_id")
            categoryId = it.getInt("category_id")
            title = it.getString("title", "")
        }
        //resource_id = requireArguments().getInt("resource_id")
        Log.d(TAG, "resourceId $resourceId")
        Log.d(TAG, "categoryId $categoryId")
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
        val binding: FragmentWorkspaceDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_workspace_detail, container, false)
        val view = binding.root

        binding.apply {
            vm = viewModel
            lifecycleOwner = this@WorkspaceDetailFragment

            //viewModel.loadWorkspaceDetails(resourceId)

            if (viewModel.isExistAuthor.value!!) {
                //로그인 한 상태
                viewModel.loadWorkspaceDetailsAuth(resourceId)
            } else {
                //비로그인 상태
                viewModel.loadWorkspaceDetails(resourceId)
            }

            view.rvCommentList.adapter = boardCommentAdapter
            view.rvTag.adapter = TagAdapter(categoryId)
            view.ivAnotherImage.let {
                it.adapter = ListAdapter(
                    55,
                    object : ListAdapter.PostItemClickListener {
                        override fun onPostItemClick(postId: Int) {
                            viewModel.openPostDetail(postId)
                            Log.d(TAG, "onPostItemClick,postId: $postId")
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
        main.btnBackToolbar.visibility = View.VISIBLE
        main.btnCloseToolbar.visibility = View.GONE

        main.btnBackToolbar.setOnClickListener { navController.navigate(R.id.action_workspaceViewFragment_to_navigationWorkspace) }

        main.imageBtnDrawer.setOnClickListener { main.drawerLayout.openDrawer(GravityCompat.START) }

        main.imageBtnBell.isEnabled = true
        main.imageBtnBell.setOnClickListener {
            navController.navigate(R.id.action_workspaceViewFragment_to_notificationsFragment)
            Log.d(TAG, "onCreateView, main.imageBtnBell : ")
        }

        //워크스페이스 태그 표시하기
        val relative = view.findViewById<RelativeLayout>(R.id.relative)
        


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
                    viewModel.ratingsAdd("post", resourceId)
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
                    viewModel.bookmarksAddPost(resourceId)
                    //좋아요 카운트 플러스
                    val countAfter: Int = count + 1
                    view.tvBookmarkCount?.text = countAfter.toString()
                }
                observeBookmarkStatus()
            } else {
                alert(R.string.notice_login_str)
            }
        }

        //공유하기
        view.btnShare.setOnClickListener {
            val text = "[GAESHOW] ${viewModel.postDetails.value?.title}\nhttp://gaeshow.co.kr/workspaces.html?n=$resourceId"

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
                report(resourceId, "POST")
            } else {
                alert(R.string.notice_login_str)
            }
        }

        //댓글추가
        view.btnAddComment.setOnClickListener {
            if (!view.etComment.text.isNullOrBlank()) {
                viewModel.clearComments()
                viewModel.addComment(resourceId, view.etComment.text.toString())
            }
            view.etComment.text.clear()
            hideSoftInput()
        }

        //팔로우 클릭
        view.btnFollow.setOnClickListener {
            if (viewModel.isExistAuthor.value!!) {
                if (viewModel.followStatus.value!!) {
                    viewModel.followDelete(viewModel.followId.value!!)
                } else {
                    viewModel.followAdd(viewModel.postDetails.value!!.user_id)
                }
                observeFollowStatus()
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
            navController.navigate(R.id.action_workspaceViewFragment_to_userPageFragment, bundle)
        }

        view.tvNickname.setOnClickListener {
            val userId = viewModel.postDetails.value!!.user_id
            val profileNickname = viewModel.postDetails.value!!.profile_nickname

            val bundle = bundleOf("user_id" to userId, "profile_nickname" to profileNickname)
            navController.navigate(R.id.action_workspaceViewFragment_to_userPageFragment, bundle)
        }

        //무한스크롤
        view.ivAnotherImage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = view.ivAnotherImage.layoutManager

                // 다음 페이지가 있는 경우
                if (viewModel.isNextPage.value!!) {
                    val lastVisibleItem = (layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()

                    // 마지막으로 보여진 아이템 position 이
                    // 전체 아이템 개수보다 1개 모자란 경우, 데이터를 loadMore 한다
                    if (layoutManager.itemCount <= lastVisibleItem + 1) {
                        viewModel.loadMorePosts(categoryId, viewModel.postUserId.value!!, resourceId)
                    }
                }
            }
        })

        observeStartDetailActivity()
        observeContent()


        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigate(R.id.action_workspaceViewFragment_to_navigationWorkspace)
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
            viewModel.loadCommentsAuth(resourceId)
        } else {
            //비로그인 상태
            viewModel.loadComments(resourceId)
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
                    val bundle = bundleOf("comment" to comment, "resource_id" to resourceId)
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

    private fun observeFollowStatus() {
        viewModel.followStatus.observe(viewLifecycleOwner, Observer { follow ->
            if (follow) {
                view?.btnFollow?.setBackgroundResource(R.drawable.shape_stroke_black_radius_filled)
                view?.btnFollow?.setTextColor(resources.getColor(R.color.colorWhite))
            } else {
                view?.btnFollow?.setBackgroundResource(R.drawable.shape_stroke_black_radius)
                view?.btnFollow?.setTextColor(resources.getColor(R.color.colorBlack))
            }
        })
    }

    fun alert(strId: Int) {
        var dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(strId)
        dialog.setPositiveButton(R.string.ok_str, null)
        dialog.show()
    }

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
        navController.navigate(R.id.action_workspaceViewFragment_to_workspaceViewFragment, bundle)
        Log.d(TAG, "bundle Of resource_id $bundle")
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