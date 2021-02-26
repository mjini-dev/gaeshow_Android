package com.devup.android_shopping_mall.view.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devup.android_shopping_mall.data.community.model.Post
import com.devup.android_shopping_mall.data.community.model.PostsRequest
import com.devup.android_shopping_mall.data.community.source.repository.PostsRepository
import com.devup.android_shopping_mall.data.user.model.UserInfo
import com.devup.android_shopping_mall.data.user.source.repository.UserRepository

class SearchViewModel(
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val TAG = "SearchViewModel"


    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _workspacePosts = MutableLiveData<List<Post>>()
    val workspacePosts: LiveData<List<Post>> = _workspacePosts

    var page = 0
    private val limit = 10

    private val _isNextPage = MutableLiveData<Boolean>().apply { value = false }
    val isNextPage: LiveData<Boolean> = _isNextPage

    private val _openDetailActivity = MutableLiveData<Boolean>().apply { value = false }
    val openDetailActivity: LiveData<Boolean> = _openDetailActivity

    private val _postId = MutableLiveData<Int>()
    val postId: LiveData<Int> = _postId

    var _categoryId = MutableLiveData<Int>().apply { value = 1 }
    val categoryId: LiveData<Int> = _categoryId

    var _searchType  = MutableLiveData<String>()
    val searchType: LiveData<String> = _searchType

    var _searchWord  = MutableLiveData<String>()
    val searchWord: LiveData<String> = _searchWord

    private val _isWorkspacePage = MutableLiveData<Boolean>().apply { value = false }
    val isWorkspacePage: LiveData<Boolean> = _isWorkspacePage


    private fun getPostsRequest(categoryId: Int, searchType: String, searchWord: String): PostsRequest {
        page++
        return PostsRequest(categoryId, page, limit, null, null, searchType, searchWord)
    }

    //사용자의 게시글 조회에 사용
    fun loadPosts(categoryId: Int, searchType: String, searchWord: String) {
        //page = 0
        postsRepository.getPosts(getPostsRequest(categoryId, searchType, searchWord),
            onSuccess = { response ->
                when (categoryId) {
                    //워크스페이스
                    5 -> {
                        _isWorkspacePage.value = true

                        if (response.posts.isNullOrEmpty()) {
                            if (workspacePosts.value.isNullOrEmpty()) {
                                Log.d(TAG, "posts.value.isNullOrEmpty()")
                            }
                        } else {
                            _isNextPage.value = response.is_next
                            _workspacePosts.value = workspacePosts.value?.plus(response.posts) ?: response.posts
                        }
                        Log.d(TAG, "loadPosts: ${workspacePosts.value}")
                    }
                    else -> {
                        _isWorkspacePage.value = false
                        if (response.posts.isNullOrEmpty()) {
                            if (posts.value.isNullOrEmpty()) {
                                Log.d(TAG, "posts.value.isNullOrEmpty()")
                            }
                        } else {
                            _isNextPage.value = response.is_next
                            _posts.value = posts.value?.plus(response.posts) ?: response.posts
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

    fun loadMorePosts(categoryId: Int, searchType: String, searchWord: String) {
        _isNextPage.value = false
        //page++
        loadPosts(categoryId, searchType, searchWord)
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