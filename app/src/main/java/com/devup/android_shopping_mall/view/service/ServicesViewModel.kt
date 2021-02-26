package com.devup.android_shopping_mall.view.service

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devup.android_shopping_mall.data.comments.model.AddCommentRequest
import com.devup.android_shopping_mall.data.comments.model.Comment
import com.devup.android_shopping_mall.data.comments.model.ServiceComments
import com.devup.android_shopping_mall.data.post.model.ServicesPostAddRequest
import com.devup.android_shopping_mall.data.post.source.repository.PostRepository
import com.devup.android_shopping_mall.data.services.model.ServicesPost
import com.devup.android_shopping_mall.data.services.model.ServicesPostsRequest
import com.devup.android_shopping_mall.data.services.source.repository.ServicesPostsRepository
import com.devup.android_shopping_mall.data.comments.source.repository.CommentsRepository
import com.devup.android_shopping_mall.data.user.source.repository.TokenRepository
import com.devup.android_shopping_mall.data.user.source.repository.UserRepository
import com.devup.android_shopping_mall.util.SingleLiveEvent
import com.devup.android_shopping_mall.util.TOKEN_EXPIRED
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ServicesViewModel(
    private val servicesPostsRepository: ServicesPostsRepository,
    private val postRepository: PostRepository,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
    private val commentsRepository: CommentsRepository
) : ViewModel() {

    private val TAG = "ServicesViewModel"

    private val _categortId = MutableLiveData<Int>()
    val categoryId: LiveData<Int> = _categortId
    private var page = 1
    private val limit = 10


    private val _servicesPostCount = MutableLiveData<Int>()
    val servicesPostCount: LiveData<Int> = _servicesPostCount

    private val _servicesPosts = MutableLiveData<List<ServicesPost?>>()
    val servicesPosts: LiveData<List<ServicesPost?>> = _servicesPosts

    private val _isExistData = MutableLiveData<Boolean>()
    val isExistData: LiveData<Boolean> = _isExistData

    private val _isExistReply = MutableLiveData<Boolean>()
    val isExistReply: LiveData<Boolean> = _isExistReply

    private val _openDetailActivity = MutableLiveData<Boolean>().apply { value = false }
    val openDetailActivity: LiveData<Boolean> = _openDetailActivity

    private val _isExistAuthor = MutableLiveData<Boolean>().apply { value = false }
    val isExistAuthor: LiveData<Boolean> = _isExistAuthor

    private val _isNextPage = MutableLiveData<Boolean>()
    val isNextPage: LiveData<Boolean> = _isNextPage

    private val _isLoadClear = MutableLiveData<Boolean>().apply { value = false }
    val isLoadClear: LiveData<Boolean> = _isLoadClear


    //글등록
    val type = MutableLiveData<Int>()
    val reason = MutableLiveData<Int>()
    val title = MutableLiveData<String>()
    val content = MutableLiveData<String>().apply { value = " " }

    private val _postId = MutableLiveData<Int>()
    val postId: LiveData<Int> = _postId

    private val _postDetails = MutableLiveData<ServicesPost>()
    val postDetails: LiveData<ServicesPost> = _postDetails

    private val _contentHTML = MutableLiveData<Spanned>()
    val contentHTML: LiveData<Spanned> = _contentHTML

    private val _commentCount = MutableLiveData<Int>()
    val commentCount: LiveData<Int> = _commentCount

    private val _comments = MutableLiveData<List<ServiceComments>>()
    val comments: LiveData<List<ServiceComments>> = _comments

    val _toastMessage = MutableLiveData<String>().apply { value = "" }
    val toastMessage: LiveData<String> = _toastMessage

    //에디터에 추가할 이미지
    private val _attachImageUrl = MutableLiveData<String>()
    val attachImageUrl: LiveData<String> = _attachImageUrl

    var _isExistImage = MutableLiveData<Boolean>().apply { value = false }
    val isExistImage: LiveData<Boolean> = _isExistImage

    private val _pickImage = SingleLiveEvent<Unit>()
    val pickImage: LiveData<Unit> get() = _pickImage


//    init {
//        getAccessToken()
//    }

    fun setCategoryId() {
        //categoriesRepository.getPostCategories()
    }

    private fun getServicesPostsRequest(categoryId: Int): ServicesPostsRequest {
        //page++
        return ServicesPostsRequest(categoryId, page, limit, null, null)
    }

    fun loadServicesPosts(categoryId: Int) {
        when (categoryId) {
            1, 2 -> {
                servicesPostsRepository.getServicesPosts(getServicesPostsRequest(categoryId),
                    onSuccess = { response ->
                        _servicesPostCount.value = response.post_count

                        if (response.posts.isNullOrEmpty()) {
                            if (servicesPosts.value.isNullOrEmpty()) {
                                Log.d(TAG, "servicesPosts.value.isNullOrEmpty()")
                                _isExistData.value = false
                            }
                        } else {
                            _isExistData.value = true
                            _isNextPage.value = response.is_next
                            _servicesPosts.value = servicesPosts.value?.plus(response.posts) ?: response.posts
                            //Log.d(TAG, "loadServicesPosts,servicesPosts.value.toString(): ${servicesPosts.value.toString()}")

                            for (servicesPost in response.posts) {

                                val con = servicesPost.content
                                val contentHTML: Spanned?
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    contentHTML = Html.fromHtml(con, Html.FROM_HTML_MODE_COMPACT)
                                } else {
                                    contentHTML = Html.fromHtml(con)
                                }
                                servicesPost.contentHTML = contentHTML
                            }
                        }
                    },
                    notSuccessStatus = { notSuccessStatus ->
                        Log.d(TAG, "notSuccessStatus of loadServicesPosts $notSuccessStatus")
                    },
                    onFailure = { failureError ->
                        Log.d(TAG, "onFailure of loadServicesPosts $failureError")
                    }
                )
            }

            else -> {
                servicesPostsRepository.getServicesAuthPosts(getServicesPostsRequest(categoryId),
                    onSuccess = { response ->
                        _servicesPostCount.value = response.post_count

                        if (response.posts.isNullOrEmpty()) {
                            if (servicesPosts.value.isNullOrEmpty()) {
                                Log.d(TAG, "servicesPosts.value.isNullOrEmpty()")
                                _isExistData.value = false
                            }
                        } else {
                            _isExistData.value = true
                            _isNextPage.value = response.is_next
                            _servicesPosts.value = servicesPosts.value?.plus(response.posts) ?: response.posts

                            for (servicesPost in response.posts) {
                                val con = servicesPost.content
                                val contentHTML: Spanned?
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    contentHTML = Html.fromHtml(con, Html.FROM_HTML_MODE_COMPACT)
                                } else {
                                    contentHTML = Html.fromHtml(con)
                                }
                                servicesPost.contentHTML = contentHTML
                            }
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
                    onFailure = { failureError ->
                        Log.d(TAG, "onFailure of loadServicesPosts $failureError")
                    }
                )
            }
        }
    }

    fun loadServicesPostsSearch(categoryId: Int, request: ServicesPostsRequest) {
        when (categoryId) {
            1, 2 -> {
                servicesPostsRepository.getServicesPosts(request,
                    onSuccess = { response ->
                        clearServicesPosts()

                        _servicesPostCount.value = response.post_count

                        if (response.posts.isNullOrEmpty()) {
                            if (servicesPosts.value.isNullOrEmpty()) {
                                Log.d(TAG, "servicesPosts.value.isNullOrEmpty()")
                                _isExistData.value = false
                            }
                        } else {
                            _isExistData.value = true
                            _isNextPage.value = response.is_next
                            _servicesPosts.value = servicesPosts.value?.plus(response.posts) ?: response.posts
                            //Log.d(TAG, "loadServicesPosts,servicesPosts.value.toString(): ${servicesPosts.value.toString()}")

                            for (servicesPost in response.posts) {

                                val con = servicesPost.content
                                val contentHTML: Spanned?
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    contentHTML = Html.fromHtml(con, Html.FROM_HTML_MODE_COMPACT)
                                } else {
                                    contentHTML = Html.fromHtml(con)
                                }
                                servicesPost.contentHTML = contentHTML
                            }
                        }
                    },
                    notSuccessStatus = { notSuccessStatus ->
                        Log.d(TAG, "notSuccessStatus of loadServicesPosts $notSuccessStatus")
                    },
                    onFailure = { failureError ->
                        Log.d(TAG, "onFailure of loadServicesPosts $failureError")
                    }
                )
            }

            else -> {
                servicesPostsRepository.getServicesAuthPosts(request,
                    onSuccess = { response ->
                        clearServicesPosts()

                        _servicesPostCount.value = response.post_count

                        if (response.posts.isNullOrEmpty()) {
                            if (servicesPosts.value.isNullOrEmpty()) {
                                Log.d(TAG, "servicesPosts.value.isNullOrEmpty()")
                                _isExistData.value = false
                            }
                        } else {
                            _isExistData.value = true
                            _isNextPage.value = response.is_next
                            _servicesPosts.value = servicesPosts.value?.plus(response.posts) ?: response.posts

                            for (servicesPost in response.posts) {


                                val con = servicesPost.content
                                val contentHTML: Spanned?
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    contentHTML = Html.fromHtml(con, Html.FROM_HTML_MODE_COMPACT)
                                } else {
                                    contentHTML = Html.fromHtml(con)
                                }
                                servicesPost.contentHTML = contentHTML
                            }
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
                    onFailure = { failureError ->
                        Log.d(TAG, "onFailure of loadServicesPosts $failureError")
                    }
                )
            }
        }
    }

    //리사이클러뷰에 데이터 추가
    fun loadMorePosts(categoryId: Int) {
        _isNextPage.value = false
        page++
        loadServicesPosts(categoryId)
    }


    private fun deleteAccessToken() {
        tokenRepository.deleteToken()
        getAccessToken()
    }

    private fun deleteProfile() {
        userRepository.deleteProfile()
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


    fun addServicesPost(servicesPostAddRequest: ServicesPostAddRequest) {
        when (servicesPostAddRequest.category_id) {
            3 -> { //문의
                postRepository.addServicesPost(
                    servicesPostAddRequest,
                    onSuccess = { response ->
                        openPostDetail(response.post_id)
                    },
                    notSuccessStatus = {

                    },
                    onFailure = {
                    }
                )
            }

            4 -> { //신고
                postRepository.addServicesPost(
                    servicesPostAddRequest,
                    onSuccess = {
                        // toast띄우기?
                        _toastMessage.value = "신고가 접수되었습니다.\n처리는 최대 1~3일정도 소요됩니다."

                    },
                    notSuccessStatus = {

                    },
                    onFailure = {
                    }
                )
            }
        }

    }

    fun clearServicesPosts() {
        _servicesPosts.value = null

        Log.d(TAG, "clearServicesPosts: ${servicesPosts.value}")
    }

    fun openPostDetail(postId: Int) {
        _postId.value = postId
        startDetailActivity()
    }

    private fun startDetailActivity() {
        _openDetailActivity.value = true
    }

    fun loadServicesPostDetails(resourceId: Int) {
        servicesPostsRepository.getServicesPostDetails(resourceId,
            onSuccess = { response ->
                _postDetails.value = response

                //HTML태그 변환
                val content = response.content
                val contentHTML: Spanned?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    contentHTML = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    contentHTML = Html.fromHtml(content)
                }
                _contentHTML.value = contentHTML
                _isLoadClear.value = true
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "loadServicesPostDetails, notSuccessStatus: $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "loadServicesPostDetails,failureError : $failureError")
            })
    }

    fun loadServicesComments(resourceId: Int) {
        commentsRepository.getServicesComments(
            resourceId,
            onSuccess = { response ->
                //if (response.)
                _isExistReply.value = true
                _comments.value = comments.value?.plus(response) ?: listOf(response)
            },
            onFailure = { failureError ->

                _isExistReply.value = false
                Log.d(TAG, "onFailure of loadComments $failureError")
            }
        )
    }

    fun clearServicesComments() {
        _comments.value = null
    }

    fun addComment(resourceId: Int, comment: String) {
        commentsRepository.addServicesComment(
            resourceId,
            AddCommentRequest(1, null, comment),
            onSuccess = { response ->
                loadServicesComments(resourceId)
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


    fun checkTitleInput(): Boolean {
        return !title.value.isNullOrBlank()
    }

    fun checkContentInput(): Boolean {
        return !content.value.isNullOrBlank()
    }

    fun checkTypeInput(): Boolean {
        return when (type.value) {
            0 -> false
            else -> true
        }
    }

    fun checkReasonInput(): Boolean {
        return when (reason.value) {
            0 -> false
            else -> true
        }
    }

    //이미지 업로드
    fun upLoadImage(file: File) {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("files", file.name, requestFile)

        val request: RequestBody = "service".toRequestBody("text/plain".toMediaTypeOrNull())

        postRepository.uploadFile(request, part,
            onSuccess = { response ->
                //업로드 성공
                Log.d(TAG, "upLoadImage, response: $response")
                _attachImageUrl.value = response[0].file_path
                _isExistImage.value = true
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "upLoadImage,notSuccessStatus: $notSuccessStatus")

            },
            onFailure = { failureError ->
                Log.d(TAG, "upLoadImage, 파일크기: ${file.length()}Bytes")
                Log.d(TAG, "upLoadImage, failureError: $failureError")

            }
        )
    }

}