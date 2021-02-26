package com.devup.android_shopping_mall.view.community.viewmodel

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devup.android_shopping_mall.data.community.model.Post
import com.devup.android_shopping_mall.data.community.source.repository.PostsRepository
import com.devup.android_shopping_mall.data.post.model.PostAddRequest
import com.devup.android_shopping_mall.data.post.source.repository.PostRepository
import com.devup.android_shopping_mall.data.tag.Tag
import com.devup.android_shopping_mall.util.SingleLiveEvent
import com.devup.android_shopping_mall.util.TOKEN_EXPIRED
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class BoardWriteViewModel(
    private val postRepository: PostRepository,
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val TAG = "BoardWriteViewModel"

    private val _tags = MutableLiveData<List<Tag>>()
    val tags: LiveData<List<Tag>> = _tags

    private val _isPostAuthor = MutableLiveData<Boolean>().apply { value = false }
    val isPostAuthor: LiveData<Boolean> = _isPostAuthor

    private val _openDetailActivity = MutableLiveData<Boolean>().apply { value = false }
    val openDetailActivity: LiveData<Boolean> = _openDetailActivity

    private val _postId = MutableLiveData<Int>()
    val postId: LiveData<Int> = _postId

    private val _etcTags = MutableLiveData<List<String?>>()
    val etcTags: LiveData<List<String?>> = _etcTags

    private val _platformTags = MutableLiveData<List<String?>>()
    val platformTags: LiveData<List<String?>> = _platformTags

    private val _languageTags = MutableLiveData<List<String?>>()
    val languageTags: LiveData<List<String?>> = _languageTags

    private val _ideTags = MutableLiveData<List<String?>>()
    val ideTags: LiveData<List<String?>> = _ideTags

    var _isPostModify = MutableLiveData<Boolean>().apply { value = false }
    val isPostModify: LiveData<Boolean> = _isPostModify

    private val _postDetails = MutableLiveData<Post>()
    val postDetails: LiveData<Post> = _postDetails

    private val _contentHTML = MutableLiveData<Spanned>()
    val contentHTML: LiveData<Spanned> = _contentHTML

    //에디터에 추가할 이미지
    private val _attachImageUrl = MutableLiveData<String>()
    val attachImageUrl: LiveData<String> = _attachImageUrl

    var _isExistImage = MutableLiveData<Boolean>().apply { value = false }
    val isExistImage: LiveData<Boolean> = _isExistImage

    private val _pickImage = SingleLiveEvent<Unit>()
    val pickImage: LiveData<Unit> get() = _pickImage



    fun addPost(postAddRequest: PostAddRequest) {
        postRepository.addPost(
            postAddRequest,
            onSuccess = { response ->
                openPostDetail(response.post_id)
            },
            notSuccessStatus = {

            },
            onFailure = {
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

    fun addTag(type: String, tagInput: String) {
        when (type) {
            "etc" -> {
                _etcTags.value = etcTags.value?.plus(tagInput) ?: listOf(tagInput)
            }
            "platform" -> {
                _platformTags.value = platformTags.value?.plus(tagInput) ?: listOf(tagInput)
            }
            "language" -> {
                _languageTags.value = languageTags.value?.plus(tagInput) ?: listOf(tagInput)
            }
            "ide" -> {
                _ideTags.value = ideTags.value?.plus(tagInput) ?: listOf(tagInput)
            }
        }
    }

    fun deleteTag(type: String, position: Int) {
        when (type) {
            "etc" -> {
                _etcTags.value = etcTags.value?.filterIndexed { index, s -> index != position }
            }
            "platform" -> {
                _platformTags.value = platformTags.value?.filterIndexed { index, s -> index != position }
            }
            "language" -> {
                _languageTags.value = languageTags.value?.filterIndexed { index, s -> index != position }
            }
            "ide" -> {
                _ideTags.value = ideTags.value?.filterIndexed { index, s -> index != position }
            }
        }

    }

    fun requestTagList(): List<Tag> {
        return mutableListOf<Tag>().apply {

            for (etcTag in etcTags.value ?: listOf()) {
                this.add(Tag("etc", etcTag, null, null, null))
            }

            for (etcTag in platformTags.value ?: listOf()) {
                this.add(Tag("platform", etcTag, null, null, null))
            }

            for (etcTag in languageTags.value ?: listOf()) {
                this.add(Tag("language", etcTag, null, null, null))
            }

            for (etcTag in ideTags.value ?: listOf()) {
                this.add(Tag("ide", etcTag, null, null, null))
            }

        }
    }

    fun loadPostDetails(resourceId: Int) {
        //getAccessToken()
        postsRepository.getPostDetails(resourceId,
            onSuccess = { response ->
                _postDetails.value = response
                _tags.value = response.tags
                Log.d(TAG, "loadPostDetails, response.tags: ${tags.value}")

                for (tag in response.tags) {
                    //_etcTags.value = etcTags.value?.plus(tag.name) ?: listOf(tag.name)
                    when(tag.type) {
                        "etc" -> _etcTags.value = etcTags.value?.plus(tag.name) ?: listOf(tag.name)
                        "platform" -> _platformTags.value = platformTags.value?.plus(tag.name) ?: listOf(tag.name)
                        "language" -> _languageTags.value = languageTags.value?.plus(tag.name) ?: listOf(tag.name)
                        "ide" -> _ideTags.value = ideTags.value?.plus(tag.name) ?: listOf(tag.name)
                    }
                }



                //HTML태그 변환
                val con = response.content
                val myCon: Spanned?
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    myCon = Html.fromHtml(con, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    myCon = Html.fromHtml(con)
                }
                _contentHTML.value = myCon
            },
            notSuccessStatus = { notSuccessStatus ->
                Log.d(TAG, "notSuccessStatus of loadPostDetails $notSuccessStatus")
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of loadPostDetails $failureError")
            })
    }

    fun modifyPost( resourceId: Int, postAddRequest: PostAddRequest) {
        postRepository.modifyPost(resourceId,postAddRequest,
        onSuccess = {
            //상세페이지로 이동
            openPostDetail(resourceId)
        },
            notSuccessStatus = { notSuccessStatus ->
                when (notSuccessStatus) {
                    TOKEN_EXPIRED -> {
//                        deleteToken()
//                        deleteProfile()
//                        loadProfile()
                        Log.d(TAG, "modifyPost: $notSuccessStatus")
                    }
                    else -> Log.d(TAG, "modifyPost: $notSuccessStatus ")
                }
            },
            onFailure = { failureError ->
                Log.d(TAG, "onFailure of modifyPost $failureError")

            })

    }

    //이미지 업로드
    fun upLoadImage(file: File) {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("files", file.name, requestFile)

        val request: RequestBody = "post".toRequestBody("text/plain".toMediaTypeOrNull())

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