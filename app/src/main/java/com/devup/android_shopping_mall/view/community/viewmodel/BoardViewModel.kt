package com.devup.android_shopping_mall.view.community.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devup.android_shopping_mall.data.categories.model.Category
import com.devup.android_shopping_mall.data.categories.source.repository.CategoriesRepository
import com.devup.android_shopping_mall.data.community.model.Post
import com.devup.android_shopping_mall.data.community.model.PostsRequest
import com.devup.android_shopping_mall.data.community.source.repository.PostsRepository
import com.devup.android_shopping_mall.data.tag.Tag
import com.devup.android_shopping_mall.data.user.source.repository.TokenRepository

class BoardViewModel(
    private val categoriesRepository: CategoriesRepository,
    private val postsRepository: PostsRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val TAG = "BoardViewModel"

    private val _categortId = MutableLiveData<Int>()
    val categoryId: LiveData<Int> = _categortId
    private var page = 0
    private val limit = 10

    private val _postCount = MutableLiveData<Int>()
    val postCount: LiveData<Int> = _postCount

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _tags = MutableLiveData<List<Tag>>()
    val tags: LiveData<List<Tag>> = _tags

    private val _openDetailActivity = MutableLiveData<Boolean>().apply { value = false }
    val openDetailActivity: LiveData<Boolean> = _openDetailActivity

    private val _postId = MutableLiveData<Int>()
    val postId: LiveData<Int> = _postId

    private val _isExistAuthor = MutableLiveData<Boolean>().apply { value = false }
    val isExistAuthor: LiveData<Boolean> = _isExistAuthor

    private val _isNextPage = MutableLiveData<Boolean>()
    val isNextPage: LiveData<Boolean> = _isNextPage

    //ide
    private val _idesLanguages = MutableLiveData<List<Category>>()
    val idesLanguages: LiveData<List<Category>> = _idesLanguages

    private val _type = MutableLiveData<String>().apply { value = "language" }
    val type: LiveData<String> = _type

    private val _isTypeLanguage = MutableLiveData<Boolean>().apply { value = true }
    val isTypeLanguage: LiveData<Boolean> = _isTypeLanguage


    init {
        //loadPosts()
        getAccessToken()
    }

    fun setCategoryId() {
        //categoriesRepository.getPostCategories()
    }

    private fun getPostsRequest(categoryId: Int): PostsRequest {
        page++
        return PostsRequest(categoryId, page, limit, null, null, null, null)
    }

    fun loadPosts(categoryId: Int) {
        postsRepository.getPosts(getPostsRequest(categoryId),
            onSuccess = { response ->
                _postCount.value = response.post_count
                if (response.posts.isNullOrEmpty()) {
                    if (posts.value.isNullOrEmpty()) {
                        Log.d(TAG, "posts.value.isNullOrEmpty()")
                    }
                } else {
                    _isNextPage.value = response.is_next
                    _posts.value = posts.value?.plus(response.posts) ?: response.posts
                    //Log.d(TAG, " posts.value ${posts.value} ")
                    Log.d(TAG, "loadPosts: ${response.posts[0].thumbnail}")

                    for (post in response.posts) {
                        _tags.value = tags.value?.plus(post.tags) ?: post.tags
                    }
                    //Log.d(TAG, " tags.value ${tags.value} ")
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

    fun loadMorePosts(categoryId: Int) {
        _isNextPage.value = false
        //page++
        loadPosts(categoryId)
    }

    fun openPostDetail(postId: Int) {
        _postId.value = postId
        startDetailActivity()
    }

    private fun startDetailActivity() {
        _openDetailActivity.value = true
    }

    fun getAccessToken() {
        tokenRepository.getAccessToken(
            accessTokenExisted = { token ->
                _isExistAuthor.value = true
            },
            accessTokenNotExist = {
                _isExistAuthor.value = false
            }
        )
    }

    fun getIdeList() {
        categoriesRepository.getIdeCategories(
            onSuccess = { ideCategories ->
                _idesLanguages.value = ideCategories
                _type.value = "ide"
                _isTypeLanguage.value = false
            },
            onFailure = {

            })
    }

    fun getLanguageList() {
        categoriesRepository.getLanguageCategories(
            onSuccess = { languageCategories ->
                _idesLanguages.value = languageCategories
                _type.value = "language"
                _isTypeLanguage.value = true
            },
            onFailure = {

            })
    }


}