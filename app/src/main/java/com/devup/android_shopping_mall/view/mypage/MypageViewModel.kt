package com.devup.android_shopping_mall.view.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devup.android_shopping_mall.data.community.model.Post
import com.devup.android_shopping_mall.data.community.model.PostsRequest
import com.devup.android_shopping_mall.data.community.source.repository.PostsRepository
import com.devup.android_shopping_mall.data.user.model.UserInfo
import com.devup.android_shopping_mall.data.user.source.repository.UserRepository

class MypageViewModel(
    private val userRepository: UserRepository,
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val TAG = "MypageViewModel"

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _userInfo = MutableLiveData<UserInfo>()
    val userInfo: LiveData<UserInfo> = _userInfo

    private val _postCount = MutableLiveData<Int>()
    val postCount: LiveData<Int> = _postCount

    private val _workspaceCount = MutableLiveData<Int>()
    val workspaceCount: LiveData<Int> = _workspaceCount

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _workspacePosts = MutableLiveData<List<Post>>()
    val workspacePosts: LiveData<List<Post>> = _workspacePosts

    val _loadWorkspaceFinish = MutableLiveData<Boolean>().apply { value = false }
    val loadWorkspaceFinish: LiveData<Boolean> = _loadWorkspaceFinish

    var page = 0
    private val limit = 4

    private val _isNextPage = MutableLiveData<Boolean>().apply { value = false }
    val isNextPage: LiveData<Boolean> = _isNextPage

    private val _openDetailActivity = MutableLiveData<Boolean>().apply { value = false }
    val openDetailActivity: LiveData<Boolean> = _openDetailActivity

    private val _postId = MutableLiveData<Int>()
    val postId: LiveData<Int> = _postId
    private val _categoryId = MutableLiveData<Int>()
    val categoryId: LiveData<Int> = _categoryId

    private val _isWorkspacePage = MutableLiveData<Boolean>().apply { value = false }
    val isWorkspacePage: LiveData<Boolean> = _isWorkspacePage

    //북마크

    private val _postCountBookmark = MutableLiveData<Int>()
    val postCountBookmark: LiveData<Int> = _postCountBookmark

    private val _workspaceCountBookmark = MutableLiveData<Int>()
    val workspaceCountBookmark: LiveData<Int> = _workspaceCountBookmark

    private val _postsBookmark = MutableLiveData<List<Post>>()
    val postsBookmark: LiveData<List<Post>> = _postsBookmark

    private val _workspacePostsBookmark = MutableLiveData<List<Post>>()
    val workspacePostsBookmark: LiveData<List<Post>> = _workspacePostsBookmark


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

    private fun getPostsRequest(categoryId: Int, type: String, userId: Int): PostsRequest {
        page++
        return PostsRequest(categoryId, page, limit, type, userId, null, null)
    }

    //사용자의 게시글 조회에 사용
    fun loadPosts(categoryId: Int, type: String, userId: Int) {
        //page = 0
        postsRepository.getPosts(getPostsRequest(categoryId, type, userId),
            onSuccess = { response ->
                when (type) {
                    "unitary" -> {
                        when (categoryId) {
                            //워크스페이스
                            5 -> {
                                _isWorkspacePage.value = true
                                _title.value = (userInfo.value?.profile_nickname ?: "") + "님이 작성한 워크스페이스"
                                _workspaceCount.value = response.post_count
                                if (response.posts.isNullOrEmpty()) {
                                    if (workspacePosts.value.isNullOrEmpty()) {
                                        Log.d(TAG, "posts.value.isNullOrEmpty()")
                                    }
                                } else {
                                    _isNextPage.value = response.is_next
                                    _workspacePosts.value = workspacePosts.value?.plus(response.posts) ?: response.posts
                                }

                                _loadWorkspaceFinish.value = true
                                //loadPosts(1,userId)

                            }
                            else -> {
                                _isWorkspacePage.value = false
                                _title.value = (userInfo.value?.profile_nickname ?: "") + "님이 작성한 게시글"
                                _postCount.value = response.post_count
                                if (response.posts.isNullOrEmpty()) {
                                    if (posts.value.isNullOrEmpty()) {
                                        Log.d(TAG, "posts.value.isNullOrEmpty()")
                                    }
                                } else {
                                    _isNextPage.value = response.is_next
                                    _posts.value = posts.value?.plus(response.posts) ?: response.posts
                                }
                                _loadWorkspaceFinish.value = false
                            }
                        }
                    }

                    "bookmark" -> {
                        when (categoryId) {
                            //워크스페이스
                            5 -> {
                                _isWorkspacePage.value = true
                                _title.value = "북마크한 워크스페이스"
                                _workspaceCount.value = response.post_count
                                if (response.posts.isNullOrEmpty()) {
                                    if (workspacePosts.value.isNullOrEmpty()) {
                                        Log.d(TAG, "posts.value.isNullOrEmpty()")
                                    }
                                } else {
                                    _isNextPage.value = response.is_next
                                    _workspacePosts.value = workspacePosts.value?.plus(response.posts) ?: response.posts
                                }

                                _loadWorkspaceFinish.value = true
                                //loadPosts(1,userId)

                            }
                            else -> {
                                _isWorkspacePage.value = false
                                _title.value = "북마크한 게시물"
                                _postCount.value = response.post_count
                                if (response.posts.isNullOrEmpty()) {
                                    if (posts.value.isNullOrEmpty()) {
                                        Log.d(TAG, "posts.value.isNullOrEmpty()")
                                    }
                                } else {
                                    _isNextPage.value = response.is_next
                                    _posts.value = posts.value?.plus(response.posts) ?: response.posts
                                }
                                _loadWorkspaceFinish.value = false
                            }
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

    fun loadMorePosts(categoryId: Int, type: String, userId: Int) {
        _isNextPage.value = false
        //page++
        loadPosts(categoryId, type, userId)
    }

    fun openPostDetail(categoryId: Int, postId: Int) {
        _postId.value = postId
        _categoryId.value = categoryId
        startDetailActivity()
    }

    private fun startDetailActivity() {
        _openDetailActivity.value = true
    }

    //목록 삭제
    fun clearPosts() {
        _posts.value = null
        _workspacePosts.value = null
    }

}