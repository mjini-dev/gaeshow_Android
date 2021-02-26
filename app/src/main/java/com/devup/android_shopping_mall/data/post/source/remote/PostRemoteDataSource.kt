package com.devup.android_shopping_mall.data.post.source.remote

import com.devup.android_shopping_mall.data.post.model.*
import com.devup.android_shopping_mall.data.uploadfile.UploadFileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface PostRemoteDataSource {

    fun addPost(
        request: PostAddRequest,
        onSuccess: (response: PostAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun addServicesPost(
        request: ServicesPostAddRequest,
        onSuccess: (response: PostAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun uploadFile(
        request: RequestBody,
        file: MultipartBody.Part,
        onSuccess: (response: List<UploadFileResponse>) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun modifyPost(
        resourceId: Int,
        request: PostAddRequest,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun deletePost(
        resourceId: Int,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun ratingsAddPost(
        request: PostRatingsAddRequest,
        onSuccess: (response: PostRatingsAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun ratingsDeletePost(
        resourceId: Int,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun bookmarksAddPost(
        request: PostBookmarksAddRequest,
        onSuccess: (response: PostBookmarksAddResponse) -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

    fun bookmarksDeletePost(
        resourceId: Int,
        onSuccess: () -> Unit,
        notSuccessStatus: (errorCode: Int) -> Unit,
        onFailure: (e: Throwable) -> Unit
    )

}