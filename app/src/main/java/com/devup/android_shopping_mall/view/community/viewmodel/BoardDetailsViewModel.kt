package com.devup.android_shopping_mall.view.community.viewmodel

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devup.android_shopping_mall.data.categories.model.Category
import com.devup.android_shopping_mall.data.categories.source.repository.CategoriesRepository
import com.devup.android_shopping_mall.data.comments.model.*
import com.devup.android_shopping_mall.data.comments.source.repository.CommentsRepository
import com.devup.android_shopping_mall.data.community.model.*
import com.devup.android_shopping_mall.data.community.source.repository.PostsRepository
import com.devup.android_shopping_mall.data.post.model.PostBookmarksAddRequest
import com.devup.android_shopping_mall.data.post.model.PostRatingsAddRequest
import com.devup.android_shopping_mall.data.post.source.repository.PostRepository
import com.devup.android_shopping_mall.data.tag.Tag
import com.devup.android_shopping_mall.data.user.model.UserInfo
import com.devup.android_shopping_mall.data.user.source.repository.TokenRepository
import com.devup.android_shopping_mall.data.user.source.repository.UserRepository
import com.devup.android_shopping_mall.util.TOKEN_EXPIRED

class BoardDetailsViewModel(
    private val categoriesRepository: CategoriesRepository,
    private val postsRepository: PostsRepository,
    private val commentsRepository: CommentsRepository,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    private val TAG = "BoardDetailsViewModel"

    private val _postDetails = MutableLiveData<Post>()
    val postDetails: LiveData<Post> = _postDetails

    private val _postRecommendDetails = MutableLiveData<PostsRecommendsResponse>()
    val postRecommendDetails: LiveData<PostsRecommendsResponse> = _postRecommendDetails

    private val _postSalaryDetails = MutableLiveData<PostsSalaryResponse>()
    val postSalaryDetails: LiveData<PostsSalaryResponse> = _postSalaryDetails

    private val _postSalaryTop = MutableLiveData<List<Salaries>>()
    val postSalaryTop: LiveData<List<Salaries>> = _postSalaryTop

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _isNextPage = MutableLiveData<Boolean>()
    val isNextPage: LiveData<Boolean> = _isNextPage

    private val _userInfo = MutableLiveData<UserInfo>()
    val userInfo: LiveData<UserInfo> = _userInfo

    private val _openDetailActivity = MutableLiveData<Boolean>().apply { value = false }
    val openDetailActivity: LiveData<Boolean> = _openDetailActivity

    private val _postId = MutableLiveData<Int>()
    val postId: LiveData<Int> = _postId

    private val _bookmarkStatus = MutableLiveData<Boolean>().apply { value = false }
    val bookmarkStatus: LiveData<Boolean> = _bookmarkStatus

    private val _bookmarkId = MutableLiveData<Int>().apply { value = 0 }
    val bookmarkId: LiveData<Int> = _bookmarkId

    private val _ratingStatus = MutableLiveData<Boolean>().apply { value = false }
    val ratingStatus: LiveData<Boolean> = _ratingStatus
    private val _ratingId = MutableLiveData<Int>().apply { value = 0 }
    val ratingId: LiveData<Int> = _ratingId

    private val _followStatus = MutableLiveData<Boolean>().apply { value = false }
    val followStatus: LiveData<Boolean> = _followStatus
    private val _followId = MutableLiveData<Int>().apply { value = 0 }
    val followId: LiveData<Int> = _followId

    private val _ratingStatusComment = MutableLiveData<Boolean>().apply { value = false }
    val ratingStatusComment: LiveData<Boolean> = _ratingStatusComment
    private val _ratingIdComment = MutableLiveData<Int>().apply { value = 0 }
    val ratingIdComment: LiveData<Int> = _ratingIdComment

    private val _ratingStatusCommentDepth = MutableLiveData<Boolean>().apply { value = false }
    val ratingStatusCommentDepth: LiveData<Boolean> = _ratingStatusCommentDepth
    private val _ratingIdCommentDepth = MutableLiveData<Int>().apply { value = 0 }
    val ratingIdCommentDepth: LiveData<Int> = _ratingIdCommentDepth


    private val _tags = MutableLiveData<List<Tag>>()
    val tags: LiveData<List<Tag>> = _tags

    private var page = 0
    private val limit = 10

    private val _commentCount = MutableLiveData<Int>()
    val commentCount: LiveData<Int> = _commentCount

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments

    private val _commentsDepth = MutableLiveData<List<CommentDepth>>()
    val commentsDepth: LiveData<List<CommentDepth>> = _commentsDepth

    val _commentSingle = MutableLiveData<Comment>()
    val commentSingle: LiveData<Comment> = _commentSingle

    private val _isCommentModify = MutableLiveData<Boolean>().apply { value = false }
    val isCommentModify: LiveData<Boolean> = _isCommentModify

    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int> = _userId

    private val _postUserId = MutableLiveData<Int>()
    val postUserId: LiveData<Int> = _postUserId

    private val _isPostAuthor = MutableLiveData<Boolean>().apply { value = false }
    val isPostAuthor: LiveData<Boolean> = _isPostAuthor

    private val _isCommentAuthor = MutableLiveData<Boolean>().apply { value = false }
    val isCommentAuthor: LiveData<Boolean> = _isCommentAuthor

    private val _isCommentDepthAuthor = MutableLiveData<Boolean>().apply { value = false }
    val isCommentDepthAuthor: LiveData<Boolean> = _isCommentDepthAuthor

    private val _contentHTML = MutableLiveData<Spanned>()
    val contentHTML: LiveData<Spanned> = _contentHTML

    private val _isExistAuthor = MutableLiveData<Boolean>().apply { value = false }
    val isExistAuthor: LiveData<Boolean> = _isExistAuthor

    private val _isDeletedComment = MutableLiveData<Boolean>().apply { value = true }
    val isDeletedComment: LiveData<Boolean> = _isDeletedComment

    private val _isExistProfileExtra = MutableLiveData<Boolean>().apply { value = false }
    val isExistProfileExtra: LiveData<Boolean> = _isExistProfileExtra

    val _isTypeLanguage = MutableLiveData<Boolean>().apply { value = true }
    val isTypeLanguage: LiveData<Boolean> = _isTypeLanguage

    private val _type = MutableLiveData<String>()
    val type: LiveData<String> = _type

    private val _idesLanguages = MutableLiveData<List<Category>>()
    val idesLanguages: LiveData<List<Category>> = _idesLanguages

    private val _spinnerItem = MutableLiveData<List<String>>()
    val spinnerItem: LiveData<List<String>> = _spinnerItem

    val _isZeroRateCheck = MutableLiveData<Boolean>().apply { value = false }
    val isZeroRateCheck: LiveData<Boolean> = _isZeroRateCheck

    private val _isDeleteSuccess = MutableLiveData<Boolean>().apply { value = false }
    val isDeleteSuccess: LiveData<Boolean> = _isDeleteSuccess

    private val _isLoadClear = MutableLiveData<Boolean>().apply { value = false }
    val isLoadClear: LiveData<Boolean> = _isLoadClear

    var _isRateModify = MutableLiveData<Boolean>().apply { value = false }
    val isRateModify: LiveData<Boolean> = _isRateModify

    var _rate = MutableLiveData<Float?>()
    val rate: LiveData<Float?> = _rate

    var _advantage = MutableLiveData<String?>()
    val advantage: LiveData<String?> = _advantage

    var _disadvantage = MutableLiveData<String?>()
    val disadvantage: LiveData<String?> = _disadvantage

    init {
        getAccessToken()
    }

    //상세조회 로그인X
    fun loadPostDetails(resourceId: Int) {
        _postId.value = resourceId
        getUserId()
        //getAccessToken()
        postsRepository.getPostDetails(resourceId,
            onSuccess = { response ->

                _postDetails.value = response
                Log.d(TAG, "loadPostDetails, postDetails.value: ${postDetails.value}")

                _tags.value = response.tags
                Log.d(TAG, "loadPostDetails, response.tags: ${tags.value}")

                //HTML태그 변환
                val con = response.content
                val myCon: Spanned?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    myCon = Html.fromHtml(con, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    myCon = Html.fromHtml(con)
                }
                _contentHTML.value = myCon
                Log.d(TAG, "loadPostDetails, mycon:  ${myCon.toString()}")
                Log.d(TAG, "loadPostDetails, contentHTML.value:  ${contentHTML.value.toString()}")

                //게시글 작성자와 로그인한 사용자 일치여부 확인
                _isPostAuthor.value = response.user_id == _userId.value
                _isLoadClear.value = true
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of loadPostDetails $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadPostDetails $failureError")
            })
    }

    fun loadWorkspaceDetails(resourceId: Int) {
        _postId.value = resourceId
        getUserId()
        //getAccessToken()
        postsRepository.getPostDetails(resourceId,
            onSuccess = { response ->
                _postDetails.value = response
                _tags.value = response.tags

                //HTML태그 변환
                val con = response.content
                val myCon: Spanned?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    myCon = Html.fromHtml(con, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    myCon = Html.fromHtml(con)
                }
                _contentHTML.value = myCon

                //게시글 작성자와 로그인한 사용자 일치여부 확인
                _isPostAuthor.value = response.user_id == _userId.value

                //작성자Id
                _postUserId.value = response.user_id

                //작성자가 등록한 다른게시글 조회
                loadPosts(response.category_id, response.user_id, resourceId)
                getUserInfo(response.user_id)
                _isLoadClear.value = true

            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of loadPostDetails $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadPostDetails $failureError")
            })
    }

    //상세조회 로그인O
    fun loadPostDetailsAuth(resourceId: Int) {
        _postId.value = resourceId
        getUserId()
        //getAccessToken()
        postsRepository.getPostDetailsAuth(resourceId,
            onSuccess = { response ->

                _postDetails.value = response
                _tags.value = response.tags
                Log.d(TAG, "loadPostDetails, response.tags: ${tags.value}")

                //HTML태그 변환
                val con = response.content
                val myCon: Spanned?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    myCon = Html.fromHtml(con, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    myCon = Html.fromHtml(con)
                }
                _contentHTML.value = myCon
                Log.d(TAG, "loadPostDetails, mycon:  ${myCon.toString()}")
                Log.d(TAG, "loadPostDetails, contentHTML.value:  ${contentHTML.value.toString()}")

                //게시글 작성자와 로그인한 사용자 일치여부 확인
                _isPostAuthor.value = response.user_id == _userId.value

                //0이면 누르지않은 상태, 0이외의 숫자가 오면 누른상태
                _bookmarkStatus.value = response.bookmark_id != 0
                _ratingStatus.value = response.rating_id != 0

                _bookmarkId.value = response.bookmark_id
                _ratingId.value = response.rating_id
                _isLoadClear.value = true
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
                        //저장된 토큰 및 유저정보를 삭제하고, 비로그인이로 상세조회 진행
                        deleteAccessToken()
                        deleteProfile()
                        loadPostDetails(resourceId)
                    }
                    else -> Log.d(TAG, "notSuccessStatus of loadPostDetails $notSuccessStatus")
                }

            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadPostDetails $failureError")
            })
    }

    fun loadWorkspaceDetailsAuth(resourceId: Int) {
        _postId.value = resourceId
        getUserId()
        //getAccessToken()
        postsRepository.getPostDetailsAuth(resourceId,
            onSuccess = { response ->
                _postDetails.value = response
                _tags.value = response.tags

                //HTML태그 변환
                val con = response.content
                val myCon: Spanned?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    myCon = Html.fromHtml(con, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    myCon = Html.fromHtml(con)
                }
                _contentHTML.value = myCon

                //게시글 작성자와 로그인한 사용자 일치여부 확인
                _isPostAuthor.value = response.user_id == _userId.value
                //0이면 누르지않은 상태, 0이외의 숫자가 오면 누른상태
                _bookmarkStatus.value = response.bookmark_id != 0
                _ratingStatus.value = response.rating_id != 0

                _bookmarkId.value = response.bookmark_id
                _ratingId.value = response.rating_id

                //작성자Id
                _postUserId.value = response.user_id


                //작성자가 등록한 다른게시글 조회
                loadPosts(response.category_id, response.user_id, resourceId)
                getUserInfoAuth(response.user_id)
                _isLoadClear.value = true
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
                        //저장된 토큰 및 유저정보를 삭제하고, 비로그인이로 상세조회 진행
                        deleteAccessToken()
                        deleteProfile()
                        loadPostDetails(resourceId)
                    }
                    else -> Log.d(TAG, "notSuccessStatus of loadPostDetails $notSuccessStatus")
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadPostDetails $failureError")
            })
    }


    //ide, 언어 추천 상세조회
    fun loadRecommendDetails(type: String, unique_id: Int) {
        getUserId()
        getAccessToken()
        postsRepository.getPostRecommendsDetails(type, unique_id,
            onSuccess = { response ->
                _postRecommendDetails.value = response
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of loadPostDetails $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadPostDetails $failureError")
            })
    }

    fun loadRecommendDetailsSpinner(type: String, unique_id: Int) {
        getUserId()
        getAccessToken()
        clearComments()
        page = 0
        loadRecommendComments(type, unique_id)

        _postRecommendDetails.value = null

        postsRepository.getPostRecommendsDetails(type, unique_id,
            onSuccess = { response ->
                _postRecommendDetails.value = response
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of loadPostDetails $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadPostDetails $failureError")
            })
    }

    //연봉 상세조회
    fun loadSalaryDetails(job_filter: String, experience_years_filter: Int?) {
        getUserId()
        getAccessToken()
        postsRepository.getPostSalariesDetails(job_filter, experience_years_filter,
            onSuccess = { response ->
                _postSalaryDetails.value = response
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of loadPostDetails $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadPostDetails $failureError")
            })
    }

    //연봉 TOP3
    fun loadSalaryTop() {
        postsRepository.getSalariesTop(
            onSuccess = { response ->
//                _postSalaryTop.value = response
                if (response.jobs.isNullOrEmpty()) {
                    if (postSalaryTop.value.isNullOrEmpty()) {
                        Log.d(TAG, "loadSalaryTop: postSalaryTop.value.isNullOrEmpty")
                    }
                } else {
                    _postSalaryTop.value = postSalaryTop.value?.plus(response.jobs) ?: response.jobs

                }
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of loadPostDetails $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadPostDetails $failureError")
            })
    }


    private fun getCommentsRequest(): CommentsRequest {
        page++
        return CommentsRequest(page, limit)
    }

    //댓글조회 로그인X
    fun loadComments(resourceId: Int) {
        commentsRepository.getComments(
            resourceId,
            //getCommentsRequest(),
            onSuccess = { response ->
                _commentCount.value = response.comment_count
                if (response.comments.isNullOrEmpty()) {
                    if (comments.value.isNullOrEmpty()) {
                        Log.d(TAG, "posts.value.isNullOrEmpty()")
                    }
                } else {
                    _comments.value = comments.value?.plus(response.comments) ?: response.comments
                    //Log.d(TAG, " posts.value ${comments.value} ")
                    for (comment in response.comments) {
                        _commentsDepth.value = commentsDepth.value?.plus(comment.comments)


                        //Log.d(TAG, " commentsDepth.value ${comment.comments} ")

//                        Log.d(TAG, "loadComments,comment.user_id: ${comment.profile_nickname}")
//                        if(comment.profile_nickname.equals("null")) {
//                            _isDeletedComment.value = true
//                        } else { _isDeletedComment.value = false }
                    }


                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadComments $failureError")
            }
        )
    }

    fun loadRecommendComments(type: String, unique_id: Int) {
        page = 0
        commentsRepository.getPostRecommendsComments(
            type,
            unique_id,
            getCommentsRequest(),
            onSuccess = { response ->
                _commentCount.value = response.comment_count
                if (response.comments.isNullOrEmpty()) {
                    if (comments.value.isNullOrEmpty()) {
                        Log.d(TAG, "posts.value.isNullOrEmpty()")
                    }
                } else {
                    _comments.value = comments.value?.plus(response.comments) ?: response.comments
                    //Log.d(TAG, " posts.value ${comments.value} ")
                    for (comment in response.comments) {
                        //댓글 작성자와 로그인한 사용자 일치여부 확인
                        _isCommentAuthor.value = comment.user_id == _userId.value
                        comment.isCommentAuthor = isCommentAuthor.value as Boolean

                        _commentsDepth.value = commentsDepth.value?.plus(comment.comments)

                        if (!comment.comments.isNullOrEmpty()) {
                            for (commentDepth in comment.comments) {
                                _isCommentDepthAuthor.value = commentDepth.user_id == _userId.value
                                commentDepth.isCommentAuthor = isCommentDepthAuthor.value as Boolean
                            }
                        }
                    }
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadComments $failureError")
            }
        )
    }

    //댓글조회 로그인O
    fun loadCommentsAuth(resourceId: Int) {
        commentsRepository.getCommentsAuth(
            resourceId,
            onSuccess = { response ->
                _commentCount.value = response.comment_count
                if (response.comments.isNullOrEmpty()) {
                    if (comments.value.isNullOrEmpty()) {
                        Log.d(TAG, "posts.value.isNullOrEmpty()")
                    }
                } else {
                    _comments.value = comments.value?.plus(response.comments) ?: response.comments
                    //Log.d(TAG, " posts.value ${comments.value} ")
                    for (comment in response.comments) {

                        //댓글 작성자와 로그인한 사용자 일치여부 확인
                        _isCommentAuthor.value = comment.user_id == _userId.value
                        comment.isCommentAuthor = isCommentAuthor.value as Boolean

                        //댓글 평가(좋아요 여부)
                        // 0이면 누르지않은 상태, 0이외의 숫자가 오면 누른상태
                        _ratingStatusComment.value = comment.rating_id != 0
                        _ratingIdComment.value = comment.rating_id

                        _commentsDepth.value = commentsDepth.value?.plus(comment.comments)

                        if (!comment.comments.isNullOrEmpty()) {
                            for (commentDepth in comment.comments) {
                                _isCommentDepthAuthor.value = commentDepth.user_id == _userId.value
                                commentDepth.isCommentAuthor = isCommentDepthAuthor.value as Boolean

                                //댓글 평가(좋아요 여부)
                                // 0이면 누르지않은 상태, 0이외의 숫자가 오면 누른상태
                                _ratingStatusCommentDepth.value = commentDepth.rating_id != 0
                                _ratingIdCommentDepth.value = commentDepth.rating_id
                            }
                        }
                    }
                }
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
                        //저장된 토큰 및 유저정보를 삭제하고, 비로그인이로 상세조회 진행
                        deleteAccessToken()
                        deleteProfile()
                        loadComments(resourceId)
                    }
                    else -> Log.d(TAG, "notSuccessStatus of loadPostDetails $notSuccessStatus")
                }

            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadCommentsAuth $failureError")
            }
        )
    }

    fun loadMoreRecommendComments(type: String, unique_id: Int) {
        _isNextPage.value = false
        loadRecommendComments(type, unique_id)
    }

    //댓글 목록 삭제
    fun clearComments() {
        _comments.value = null
        _commentsDepth.value = null
    }

    //댓글 추가
    fun addComment(resourceId: Int, comment: String) {
        commentsRepository.addComment(
            resourceId,
            AddCommentRequest(1, null, comment),
            onSuccess = { response ->
                if (_isExistAuthor.value!!) {
                    loadCommentsAuth(resourceId)
                } else {
                    loadComments(resourceId)
                }

            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    //저장된 토큰값과 서버에서 보낸 토큰값이 다르다면, 저장된 토큰을 삭제하고 재발급한다.
                    TOKEN_EXPIRED -> {
                        deleteAccessToken()
                        deleteProfile()
                    }
                    else -> Log.d(TAG, "notSuccessStatus of loadServicesPosts $notSuccessStatus")
                }
            },
            onFailure = {
            }
        )
    }

    //대댓글 추가
    fun addCommentDepth(resourceId: Int, parentId: Int, comment: String) {
        commentsRepository.addComment(
            resourceId,
            AddCommentRequest(2, parentId, comment),
            onSuccess = { response ->
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    //저장된 토큰값과 서버에서 보낸 토큰값이 다르다면, 저장된 토큰을 삭제하고 재발급한다.
                    TOKEN_EXPIRED -> {
                        deleteAccessToken()
                        deleteProfile()
                    }
                    else -> Log.d(TAG, "notSuccessStatus of loadServicesPosts $notSuccessStatus")
                }
            },
            onFailure = {
            }
        )
    }

    //댓글 수정
    fun modifyComment(resourceId: Int, commentId: Int, request: AddCommentRequest) {
        commentsRepository.modifyComment(resourceId, commentId, request,
            onSuccess = {
                //수정 성공
                _isCommentModify.value = true
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
//                        deleteToken()
//                        deleteProfile()
//                        loadProfile()
                        Log.d(TAG, "modifyComment: $notSuccessStatus")
                    }
                    else -> Log.d(TAG, "modifyComment: $notSuccessStatus ")
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of modifyComment $failureError")

            })

    }

    //ide추천 댓글(평가) 추가
    fun addRecommendComment(request: AddRecommendCommentRequest) {
        commentsRepository.addPostRecommendsComment(
            request,
            onSuccess = { response ->
                //openPostDetail(response.comment_id)
                openPostDetail(response.comment_id)
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    //저장된 토큰값과 서버에서 보낸 토큰값이 다르다면, 저장된 토큰을 삭제하고 재발급한다.
                    TOKEN_EXPIRED -> {
                        deleteAccessToken()
                        deleteProfile()

                    }
                    else -> Log.d(TAG, "notSuccessStatus of loadServicesPosts $notSuccessStatus")
                }
            },
            onFailure = {
            }
        )
    }

    //ide추천 댓글(평가) 수정
    fun modifyPostRecommendsComment(resourceId: Int, request: AddRecommendCommentRequest) {
        commentsRepository.modifyPostRecommendsComment(
            resourceId,
            request,
            onSuccess = {
                //
                openPostDetail(resourceId)
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    //저장된 토큰값과 서버에서 보낸 토큰값이 다르다면, 저장된 토큰을 삭제하고 재발급한다.
                    TOKEN_EXPIRED -> {
                        deleteAccessToken()
                        deleteProfile()

                    }
                    else -> Log.d(TAG, "notSuccessStatus of loadServicesPosts $notSuccessStatus")
                }
            },
            onFailure = {
            }
        )
    }

    //댓글삭제
    fun deleteComment(resourceId: Int, commentId: Int) {
        commentsRepository.deleteComment(resourceId, commentId,
            onSuccess = {
                //댓글목록 지우고 댓글 로드
                clearComments()
                loadCommentsAuth(resourceId)
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
                        Log.d(TAG, "deletePost: $notSuccessStatus")
                    }
                    else -> Log.d(TAG, "deletePost: $notSuccessStatus ")
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of deletePost $failureError")

            })
    }

    //댓글삭제
    fun deletePostRecommendsComment(commentId: Int, type: String, uniqueId: Int) {
        commentsRepository.deletePostRecommendsComment(commentId,
            onSuccess = {
                //댓글목록 지우고 댓글 로드
                clearComments()
                loadRecommendComments(type, uniqueId)
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
                        Log.d(TAG, "deletePost: $notSuccessStatus")
                    }
                    else -> Log.d(TAG, "deletePost: $notSuccessStatus ")
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of deletePost $failureError")

            })
    }

    private fun getUserId() {
        userRepository.getUserProfile(
            onProfileExist = { userProfile ->
                _userId.value = userProfile.user_id
            },
            onProfileNotExist = {
                Log.d(TAG, "getUserId: onProfileNotExist")
            }
        )
    }

    fun getAccessToken() {
        tokenRepository.getAccessToken(
            accessTokenExisted = { token ->
                _isExistAuthor.value = true
                Log.d(TAG, "getAccessToken: ${isExistAuthor.value}")
            },
            accessTokenNotExist = {
                _isExistAuthor.value = false
            }
        )
    }

    fun deleteAccessToken() {
        tokenRepository.deleteToken()
        getAccessToken()
    }

    private fun deleteProfile() {
        userRepository.deleteProfile()
    }


    private fun getPostsRequest(categoryId: Int, userId: Int): PostsRequest {
        page++
        return PostsRequest(categoryId, page, limit, "unitary", userId, null, null)
    }

    //워크스페이스 상세조회시 사용자의 다른 게시글 조회에 사용
    fun loadPosts(categoryId: Int, userId: Int, resourceId: Int) {
        postsRepository.getPosts(getPostsRequest(categoryId, userId),
            onSuccess = { response ->
                if (response.posts.isNullOrEmpty()) {
                    if (posts.value.isNullOrEmpty()) {
                        Log.d(TAG, "posts.value.isNullOrEmpty()")
                    }
                } else {
                    _isNextPage.value = response.is_next
                    //_posts.value = posts.value?.plus(response.posts) ?: response.posts

                    for (post in response.posts) {
                        if (post.post_id != resourceId) {
                            _posts.value = posts.value?.plus(post) ?: listOf(post)
                        }
                    }
                }
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of loadPosts $notSuccessStatus")

            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadPosts $failureError")
            }
        )
    }

    fun loadMorePosts(categoryId: Int, userId: Int, resourceId: Int) {
        _isNextPage.value = false
        //page++
        loadPosts(categoryId, userId, resourceId)
    }

    //사용자 정보 가져오기
    fun getUserInfo(userId: Int) {
        userRepository.getOtherUserInfo(userId,
            onSuccess = { response ->
                _userInfo.value = response
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "getUserInfo,getUserInfo : $notSuccessStatus ")
            },
            onFailure = { failureError ->
                Log.d(TAG, "getUserInfo,getUserInfo: $failureError")
            }
        )
    }

    //사용자 정보 가져오기_로그인ㅇ
    private fun getUserInfoAuth(userId: Int) {
        userRepository.getOtherUserInfoAuth(userId,
            onSuccess = { response ->
                _userInfo.value = response

                _followStatus.value = response.following_id != 0
                _followId.value = response.following_id
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "getUserInfo,getUserInfo : $notSuccessStatus ")
            },
            onFailure = { failureError ->
                Log.d(TAG, "getUserInfo,getUserInfo: $failureError")
            }
        )
    }

    fun openPostDetail(postId: Int) {
        _postId.value = postId
        startDetailActivity()
    }

    private fun startDetailActivity() {
        _openDetailActivity.value = true
    }

    fun getIdeList() {
        categoriesRepository.getIdeCategories(
            onSuccess = { ideCategories ->
                _type.value = "ide"
                _idesLanguages.value = ideCategories

                _spinnerItem.value = null
                for (sp_item in idesLanguages.value!!) {
                    _spinnerItem.value = spinnerItem.value?.plus(sp_item.en_name) ?: listOf(sp_item.en_name)
                }
                Log.d(TAG, "getIdeList,itemL.value: ${spinnerItem.value}")
            },
            onFailure = {

            })
    }

    fun getLanguageList() {
        categoriesRepository.getLanguageCategories(
            onSuccess = { languageCategories ->
                _type.value = "language"
                _idesLanguages.value = languageCategories

                _spinnerItem.value = null
                for (sp_item in idesLanguages.value!!) {
                    _spinnerItem.value = spinnerItem.value?.plus(sp_item.en_name) ?: listOf(sp_item.en_name)
                }


                Log.d(TAG, "getLanguageList,itemL.value: ${spinnerItem.value}")
            },
            onFailure = {

            })
    }

    //게시글 삭제
    fun deletePost(resourceId: Int) {
        postRepository.deletePost(resourceId,
            onSuccess = {
                //목록으로 이동
                _isDeleteSuccess.value = true
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
                        Log.d(TAG, "deletePost: $notSuccessStatus")
                    }
                    else -> Log.d(TAG, "deletePost: $notSuccessStatus ")
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of deletePost $failureError")

            })

    }


    private fun getPostsRatingsAddRequest(type: String, resourceId: Int): PostRatingsAddRequest {

        return PostRatingsAddRequest(type, resourceId, 1)
    }

    //평가(좋아요) 추가
    fun ratingsAdd(type: String, resourceId: Int) {
        when (type) {
            "post" -> {
                postRepository.ratingsAddPost(getPostsRatingsAddRequest(type, resourceId),
                    onSuccess = { response ->
                        _ratingId.value = response.rating_id
                        _ratingStatus.value = true
                    },
                    notSuccessStatus = { notSuccessStatus ->
                        when (notSuccessStatus) {
                            TOKEN_EXPIRED -> {
                                Log.d(TAG, "ratingsAddPost,notSuccessStatus: $notSuccessStatus")
                            }
                            else -> Log.d(TAG, "ratingsAddPost,notSuccessStatus: $notSuccessStatus ")
                        }

                    },
                    onFailure = { failureError ->
                        Log.d(TAG, "onFailure of ratingsAddPost $failureError")

                    })
            }

            "comment" -> {
                postRepository.ratingsAddPost(getPostsRatingsAddRequest(type, resourceId),
                    onSuccess = { response ->
                        _ratingIdComment.value = response.rating_id
                        _ratingStatusComment.value = true
                        loadCommentsAuth(_postId.value!!)
                    },
                    notSuccessStatus = { notSuccessStatus ->
                        when (notSuccessStatus) {
                            TOKEN_EXPIRED -> {
                                Log.d(TAG, "ratingsAddPost,notSuccessStatus: $notSuccessStatus")
                            }
                            else -> Log.d(TAG, "ratingsAddPost,notSuccessStatus: $notSuccessStatus ")
                        }

                    },
                    onFailure = { failureError ->
                        Log.d(TAG, "onFailure of ratingsAddPost $failureError")

                    })
            }
        }

    }

    //평가(좋아요) 삭제
    fun ratingsDelete(type: String, ratingId: Int) {
        when (type) {
            "post" -> {
                postRepository.ratingsDeletePost(ratingId,
                    onSuccess = {

                        _ratingStatus.value = false
                    },
                    notSuccessStatus = { notSuccessStatus ->
                        when (notSuccessStatus) {
                            TOKEN_EXPIRED -> {
                                Log.d(TAG, "ratingsDeletePost,notSuccessStatus: $notSuccessStatus")
                            }
                            else -> Log.d(TAG, "ratingsDeletePost,notSuccessStatus: $notSuccessStatus ")
                        }
                    },
                    onFailure = { failureError ->
                        Log.d(TAG, "onFailure of ratingsDeletePost $failureError")
                    })
            }
            "comment" -> {
                postRepository.ratingsDeletePost(ratingId,
                    onSuccess = {
                        loadCommentsAuth(_postId.value!!)
                        _ratingStatusComment.value = false
                    },
                    notSuccessStatus = { notSuccessStatus ->
                        when (notSuccessStatus) {
                            TOKEN_EXPIRED -> {
                                Log.d(TAG, "ratingsDeletePost,notSuccessStatus: $notSuccessStatus")
                            }
                            else -> Log.d(TAG, "ratingsDeletePost,notSuccessStatus: $notSuccessStatus ")
                        }
                    },
                    onFailure = { failureError ->
                        Log.d(TAG, "onFailure of ratingsDeletePost $failureError")
                    })
            }
        }
    }


    private fun getPostBookmarksAddRequest(resourceId: Int): PostBookmarksAddRequest {
        return PostBookmarksAddRequest("post", resourceId)
    }

    //북마크 추가
    fun bookmarksAddPost(resourceId: Int) {
        postRepository.bookmarksAddPost(getPostBookmarksAddRequest(resourceId),
            onSuccess = { response ->
                _bookmarkId.value = response.bookmark_id
                _bookmarkStatus.value = true
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
                        Log.d(TAG, "bookmarksAddPost,notSuccessStatus: $notSuccessStatus")
                    }
                    else -> Log.d(TAG, "bookmarksAddPost,notSuccessStatus: $notSuccessStatus ")
                }

            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of bookmarksAddPost $failureError")

            })
    }

    //북마크 삭제
    fun bookmarksDeletePost(bookmarkId: Int) {
        postRepository.bookmarksDeletePost(bookmarkId,
            onSuccess = {
                _bookmarkStatus.value = false
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
                        Log.d(TAG, "bookmarksDeletePost,notSuccessStatus: $notSuccessStatus")
                    }
                    else -> Log.d(TAG, "bookmarksDeletePost,notSuccessStatus: $notSuccessStatus ")
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of bookmarksDeletePost $failureError")

            })
    }

    //팔로우 추가
    fun followAdd(userId: Int) {
        userRepository.addFollows(userId,
            onSuccess = { response ->
                _followId.value = response.follow_id
                _followStatus.value = true
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
                        Log.d(TAG, "bookmarksAddPost,notSuccessStatus: $notSuccessStatus")
                    }
                    else -> Log.d(TAG, "bookmarksAddPost,notSuccessStatus: $notSuccessStatus ")
                }

            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of bookmarksAddPost $failureError")

            })
    }

    //북마크 삭제
    fun followDelete(resourceId: Int) {
        userRepository.deleteFollows(resourceId,
            onSuccess = {
                _followStatus.value = false
            },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
                        Log.d(TAG, "bookmarksDeletePost,notSuccessStatus: $notSuccessStatus")
                    }
                    else -> Log.d(TAG, "bookmarksDeletePost,notSuccessStatus: $notSuccessStatus ")
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of bookmarksDeletePost $failureError")

            })
    }

}